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
		PreparedStatement stmt = conn.prepareStatement("INSERT INTO ROBOTSETTING (RELEASEBEFOREMACHINE, SOCKETCONNECTION, PAYLOAD) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
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
	}
	
	public void updateRobotSetting(final RobotSetting robotSetting, final boolean releaseBeforeMachine, final SocketConnection socketConnection, final float playload) throws SQLException {
		conn.setAutoCommit(false);
		if ((!robotSetting.isReleaseBeforeMachine()==releaseBeforeMachine)) {
			PreparedStatement stmt = conn.prepareStatement("UPDATE ROBOTSETTING SET RELEASEBEFOREMACHINE = ?, SOCKETCONNECTION = ?, PAYLOAD = ? WHERE ID = ?");

			stmt.setBoolean(1, releaseBeforeMachine);
			stmt.setInt(2, socketConnection.getId());
			stmt.setFloat(3, playload);
			stmt.setInt(4, robotSetting.getId());

			stmt.executeUpdate();
			robotSetting.setReleaseBeforeMachine(releaseBeforeMachine);
		}
		conn.commit();
		conn.setAutoCommit(true);
		// TODO updating of gripper head compatibility
	}
    public RobotSetting getRobotSettingById(final int programId, final int robotSettingId) throws SQLException {
    	RobotSetting robotSetting = null; 
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM ROBOTSETTING WHERE ID = ?");
        stmt.setInt(1, robotSettingId);
        ResultSet results = stmt.executeQuery();
        if (results.next()) {
            boolean ReleaseBeforeMachine = results.getBoolean("RELEASEBEFOREMACHINE");    
            robotSetting = new RobotSetting(ReleaseBeforeMachine);
            robotSetting.setId(robotSettingId);
        }
        stmt.close();
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
