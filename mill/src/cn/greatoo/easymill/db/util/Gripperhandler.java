package cn.greatoo.easymill.db.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.google.common.base.FinalizablePhantomReference;

import cn.greatoo.easymill.entity.Gripper;
import cn.greatoo.easymill.entity.Gripper.Type;

public class Gripperhandler {
	
	private static final int GRIPPER_TYPE_TWOPOINT = 1;
	private static final int GRIPPER_TYPE_VACUUM = 2;
	static Connection conn = DBHandler.getInstance().getConnection();
	private CoordinatesHandler coordinatesHandler;

	public static void saveGripper(final Gripper gripper) throws SQLException {
		conn.setAutoCommit(false);
		PreparedStatement stmt = conn.prepareStatement("INSERT INTO GRIPPER (NAME, HEIGHT, FIXEDHEIGHT, GRIPPERINNER, IMAGEURL,TYPE) VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		stmt.setString(1, gripper.getName());
		stmt.setFloat(2, gripper.getHeight());
		stmt.setBoolean(3, gripper.isFixedHeight());
		stmt.setBoolean(4, gripper.isGripperInner());
		stmt.setString(5, gripper.getImageUrl());	
		int typeInt = GRIPPER_TYPE_TWOPOINT;
		if (gripper.getType() == Type.TWOPOINT) {
			typeInt = GRIPPER_TYPE_TWOPOINT;
		} else if (gripper.getType() == Type.VACUUM) {
			typeInt = GRIPPER_TYPE_VACUUM;
		}
		stmt.setInt(6, typeInt);

		try {
			stmt.executeUpdate();
			ResultSet resultSet = stmt.getGeneratedKeys();
			if (resultSet.next()) {
				gripper.setId(resultSet.getInt(1));		
				conn.commit();
			}
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			conn.setAutoCommit(true);
		}
	}
	public static void updateGripper(final Gripper gripper) throws SQLException {
		conn.setAutoCommit(false);
			PreparedStatement stmt = conn.prepareStatement("UPDATE GRIPPER SET NAME = ?, HEIGHT = ?, FIXEDHEIGHT = ?, GRIPPERINNER = ?, IMAGEURL = ?, TYPE = ? WHERE ID = ?");
			stmt.setString(1, gripper.getName());
			stmt.setFloat(2, gripper.getHeight());
			stmt.setBoolean(3, gripper.isFixedHeight());
			stmt.setBoolean(4, gripper.isGripperInner());
			stmt.setString(5, gripper.getImageUrl());
			int typeInt = GRIPPER_TYPE_TWOPOINT;
			if (gripper.getType() == Type.TWOPOINT) {
				typeInt = GRIPPER_TYPE_TWOPOINT;
			} else if (gripper.getType() == Type.VACUUM) {
				typeInt = GRIPPER_TYPE_VACUUM;
			} else {
				throw new IllegalArgumentException("Unknown gripper type: " +gripper.getType() );
			}
			stmt.setInt(6, typeInt);
			stmt.setInt(7, gripper.getId());
			stmt.executeUpdate();
		conn.commit();
		conn.setAutoCommit(true);
		// TODO updating of gripper head compatibility
	}
		
	public Gripper getGripperById(final int id) throws SQLException {
		Gripper gripper = null;
		//gripper = DBHandler.getInstance().getGrippersBuffer().get(id);
		if (gripper == null) {
			PreparedStatement stmt = conn.prepareStatement("SELECT * FROM GRIPPER WHERE ID = ?");
			stmt.setInt(1, id);
			ResultSet results = stmt.executeQuery();
			if (results.next()) {
				float height = results.getFloat("HEIGHT");
				boolean fixedHeight = results.getBoolean("FIXEDHEIGHT");
				String name = results.getString("NAME");
				String selectGripper = results.getString("SELECTGRIPPER");
				boolean gripperInner = results.getBoolean("GRIPPERINNER");
				String imageUrl = results.getString("IMAGEURL");
				int typeId = results.getInt("TYPE");
				Gripper.Type type = Gripper.Type.TWOPOINT;
				if (typeId == GRIPPER_TYPE_TWOPOINT) {
					type = Gripper.Type.TWOPOINT;
				} else if (typeId == GRIPPER_TYPE_VACUUM) {
					type = Gripper.Type.VACUUM;
				} else {
					throw new IllegalArgumentException("Unkown gripper type id: " + typeId);
				}
				gripper = new Gripper(name, type, height, gripperInner, imageUrl);
				gripper.setFixedHeight(fixedHeight);
				gripper.setId(id);
			}
			//DBHandler.getInstance().getGrippersBuffer().put(id, gripper);
		}
		return gripper;
	}
	
	
	public void deleteGripper(final Gripper gripper) throws SQLException {
		conn.setAutoCommit(false);
		try {
			PreparedStatement stmt = conn.prepareStatement("DELETE FROM GRIPPERHEAD_GRIPPER WHERE GRIPPER = ?");
			stmt.setInt(1, gripper.getId());
			stmt.executeUpdate();
			PreparedStatement stmt2 = conn.prepareStatement("DELETE FROM GRIPPER WHERE ID = ?");
			stmt2.setInt(1, gripper.getId());
			stmt2.executeUpdate();
			conn.commit();
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			conn.setAutoCommit(true);
		}
	}
}
