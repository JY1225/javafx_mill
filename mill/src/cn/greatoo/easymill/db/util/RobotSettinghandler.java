package cn.greatoo.easymill.db.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import cn.greatoo.easymill.entity.RobotSetting;
import cn.greatoo.easymill.external.communication.socket.SocketConnection;

public class RobotSettinghandler {

	static Connection conn = DBHandler.getInstance().getConnection();

	public static void saveRobotSetting(final RobotSetting robotSetting) throws SQLException {
		conn.setAutoCommit(false);
		if (robotSetting.getId() <= 0) {
			PreparedStatement stmt = conn.prepareStatement("INSERT INTO ROBOTSETTING (RELEASEBEFOREMACHINE) VALUES (?)",
					Statement.RETURN_GENERATED_KEYS);
			stmt.setBoolean(1, robotSetting.isReleaseBeforeMachine());
			try {
				stmt.executeUpdate();
				ResultSet resultSet = stmt.getGeneratedKeys();
				if (resultSet.next()) {
					robotSetting.setId(resultSet.getInt(1));
					conn.commit();
				}
			} catch (SQLException e) {
				conn.rollback();
				throw e;
			} finally {
				conn.setAutoCommit(true);
			}
		} else {
			updateRobotSetting(robotSetting);
		}
	}

	public static void updateRobotSetting(final RobotSetting robotSetting) throws SQLException {
		try {
		conn.setAutoCommit(false);
		PreparedStatement stmt = conn.prepareStatement("UPDATE ROBOTSETTING SET RELEASEBEFOREMACHINE = ? WHERE ID = ?");
		stmt.setBoolean(1, robotSetting.isReleaseBeforeMachine());
		stmt.setInt(2, robotSetting.getId());
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

	public static RobotSetting getRobotSettingById(final int robotSettingId) throws SQLException {
		RobotSetting robotSetting = null;
		PreparedStatement stmt = conn.prepareStatement("SELECT * FROM ROBOTSETTING WHERE ID = ?");
		stmt.setInt(1, robotSettingId);
		ResultSet results = stmt.executeQuery();
		if (results.next()) {
			boolean ReleaseBeforeMachine = results.getBoolean("RELEASEBEFOREMACHINE");
			robotSetting = new RobotSetting(ReleaseBeforeMachine);
			robotSetting.setId(robotSettingId);
		}
		return robotSetting;
	}

	public void deleteRobotSetting(final RobotSetting robotSetting) throws SQLException {
		conn.setAutoCommit(false);
		try {
			PreparedStatement stmt2 = conn.prepareStatement("DELETE FROM ROBOTSETTING WHERE ID = ?");
			stmt2.setInt(1, robotSetting.getId());
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
