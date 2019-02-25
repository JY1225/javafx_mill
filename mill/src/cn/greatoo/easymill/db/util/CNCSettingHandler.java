package cn.greatoo.easymill.db.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import cn.greatoo.easymill.entity.CNCSetting;
import cn.greatoo.easymill.entity.CNCSetting.ClampingType;

public class CNCSettingHandler {

	static Connection conn = DBHandler.getInstance().getConnection();

	public static void saveCNCSetting(final CNCSetting CNCSetting) throws SQLException {
		conn.setAutoCommit(false);
		if (CNCSetting.getId() <= 0) {
			PreparedStatement stmt = conn.prepareStatement("INSERT INTO CNCSETTING (CLAMPINGTYPE) VALUES (?)",
					Statement.RETURN_GENERATED_KEYS);
			stmt.setInt(1, CNCSetting.getClampingType().getId());
			try {
				stmt.executeUpdate();
				ResultSet resultSet = stmt.getGeneratedKeys();
				if (resultSet.next()) {
					CNCSetting.setId(resultSet.getInt(1));
					conn.commit();
				}
			} catch (SQLException e) {
				conn.rollback();
				throw e;
			} finally {
				conn.setAutoCommit(true);
			}
		} else {
			updateCNCSetting(CNCSetting);
		}
	}

	public static void updateCNCSetting(final CNCSetting CNCSetting) throws SQLException {
		try {
		conn.setAutoCommit(false);
		PreparedStatement stmt = conn.prepareStatement("UPDATE CNCSETTING SET CLAMPINGTYPE = ? WHERE ID = ?");
		stmt.setInt(1, CNCSetting.getClampingType().getId());
		stmt.setInt(2, CNCSetting.getId());
		stmt.executeUpdate();
		conn.commit();
		conn.setAutoCommit(true);
		}catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			conn.setAutoCommit(true);
		}
	}

	public static CNCSetting getCNCSettingById(final int CNCSettingId) throws SQLException {
		CNCSetting CNCSetting = null;
		PreparedStatement stmt = conn.prepareStatement("SELECT * FROM CNCSETTING WHERE ID = ?");
		stmt.setInt(1, CNCSettingId);
		ResultSet results = stmt.executeQuery();
		if (results.next()) {
			int clampingType = results.getInt("CLAMPINGTYPE");
			CNCSetting = new CNCSetting(ClampingType.getTypeById(clampingType));
			CNCSetting.setId(CNCSettingId);
		}
		return CNCSetting;
	}

	public void deleteCNCSetting(final CNCSetting CNCSetting) throws SQLException {
		conn.setAutoCommit(false);
		try {
			PreparedStatement stmt2 = conn.prepareStatement("DELETE FROM CNCSETTING WHERE ID = ?");
			stmt2.setInt(1, CNCSetting.getId());
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
