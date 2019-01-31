package cn.greatoo.easymill.db.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import cn.greatoo.easymill.entity.GripperHead;

public class GripperHeadHandle {
	static Connection conn = DBHandler.getInstance().getConnection();

	public static GripperHead getGripperHeadById(final int gripperHeadId) throws SQLException {
		PreparedStatement stmt2 = conn.prepareStatement("SELECT * FROM GRIPPERHEAD WHERE ID = ?");
		stmt2.setInt(1, gripperHeadId);
		ResultSet results2 = stmt2.executeQuery();
		GripperHead gripperHead = new GripperHead();
		if (results2.next()) {
			gripperHead.setName(results2.getString("name"));
			gripperHead.setGripperInner(results2.getBoolean("GRIPPERINNER"));
			gripperHead.setId(gripperHeadId);
		}
		return gripperHead;
	}

	@SuppressWarnings("static-access")
	public static int saveGripperHead(GripperHead gripperHead) throws SQLException {
		try {
		if (gripperHead.getId() <= 0) {
			PreparedStatement stmt = conn.prepareStatement("INSERT INTO GRIPPERHEAD (NAME,GRIPPERINNER) VALUES (?,?)",Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, gripperHead.getName());		
			stmt.setBoolean(2, gripperHead.isGripperInner());		
				stmt.executeUpdate();
				ResultSet resultSet = stmt.getGeneratedKeys();
				if (resultSet.next()) {
					gripperHead.setId(resultSet.getInt(1));
					conn.commit();
				}			
		}else {
			PreparedStatement stmt = conn.prepareStatement(
					"UPDATE GRIPPERHEAD SET NAME = ? WHERE ID = ?");
			stmt.setString(1, gripperHead.getName());
			stmt.setFloat(2, gripperHead.getId());
			stmt.executeUpdate();
		}
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			conn.setAutoCommit(true);
		}
		return gripperHead.getId();
	}

}
