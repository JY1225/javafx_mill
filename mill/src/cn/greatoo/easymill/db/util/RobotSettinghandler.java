package cn.greatoo.easymill.db.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import cn.greatoo.easymill.entity.Gripper;
import cn.greatoo.easymill.entity.Gripper.Type;
import cn.greatoo.easymill.entity.WorkPiece.Material;
import cn.greatoo.easymill.external.communication.socket.SocketConnection;
import cn.greatoo.easymill.entity.RobotSetting;
import cn.greatoo.easymill.entity.WorkPiece;

public class RobotSettinghandler {
	
	Connection conn = DBHandler.getInstance().getConnection();

	public void saveRobotSetting(final RobotSetting robotSetting) throws SQLException {
		conn.setAutoCommit(false);
		PreparedStatement stmt = conn.prepareStatement("INSERT INTO ROBOTSETTING (RELEASEBEFOREMACHINE, SOCKETCONNECTION, PAYLOAD) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
		stmt.setBoolean(1, robotSetting.isReleaseBeforeMachine());
		stmt.setInt(2, robotSetting.getSocketConnection().getId());
		stmt.setFloat(3, robotSetting.getPlayload());				
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
		if ((!robotSetting.isReleaseBeforeMachine()==releaseBeforeMachine) || (!robotSetting.getSocketConnection().equals(socketConnection)) || (robotSetting.getPlayload() != playload)) {
			PreparedStatement stmt = conn.prepareStatement("UPDATE ROBOTSETTING SET RELEASEBEFOREMACHINE = ?, SOCKETCONNECTION = ?, PAYLOAD = ? WHERE ID = ?");
			stmt.setBoolean(1, releaseBeforeMachine);
			stmt.setInt(2, socketConnection.getId());
			stmt.setFloat(3, playload);
			stmt.setInt(4, robotSetting.getId());

			stmt.executeUpdate();
			robotSetting.setReleaseBeforeMachine(releaseBeforeMachine);
            robotSetting.setSocketConnection(socketConnection);
            robotSetting.setPlayload(playload);
		}
		conn.commit();
		conn.setAutoCommit(true);
		// TODO updating of gripper head compatibility
	}
    public RobotSetting getRobotSettingById(final int programId, final int robotSettingId) throws SQLException {
    	RobotSetting robotSetting = DBHandler.getInstance().getRobotSettingBuffer().get(programId); 
        if (robotSetting != null) {
            return robotSetting;
        }
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM ROBOTSETTING WHERE ID = ?");
        stmt.setInt(1, robotSettingId);
        ResultSet results = stmt.executeQuery();
        if (results.next()) {
            boolean ReleaseBeforeMachine = results.getBoolean("RELEASEBEFOREMACHINE");
            int socketConnectionId = results.getInt("SOCKETCONNECTION");
            SocketConnection socketConnection = DBHandler.getInstance().getSocketConnectionById(socketConnectionId);
            float payload = results.getFloat("PAYLOAD");    
            robotSetting = new RobotSetting(ReleaseBeforeMachine, socketConnection, payload);
            robotSetting.setId(robotSettingId);
        }
        stmt.close();
        DBHandler.getInstance().getRobotSettingBuffer().put(robotSettingId, robotSetting);
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
