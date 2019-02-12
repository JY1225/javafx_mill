package cn.greatoo.easymill.db.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cn.greatoo.easymill.external.communication.socket.SocketConnection;
import cn.greatoo.easymill.robot.AbstractRobot;
import cn.greatoo.easymill.robot.FanucRobot;

public class RobotHandler {
	private final static Logger LOGGER = LogManager.getLogger(RobotHandler.class.getName());
	private static Connection conn = DBHandler.getInstance().getConnection();
	 /**
     * JY
     * renturn AbstractRobot
     * 
     */
    public static AbstractRobot getRobot(){
    	AbstractRobot robot = null;
    	try {
		PreparedStatement stmt = conn.prepareStatement("SELECT * FROM ROBOT");
		//stmt.setInt(1, id);//1
		ResultSet results = stmt.executeQuery();		
		if (results.next()) {			
			int socketConnectionId = results.getInt("SOCKETCONNECTION");
			PreparedStatement stmt2 = conn.prepareStatement("SELECT * FROM SOCKETCONNECTION WHERE ID = ?");
			stmt2.setInt(1, socketConnectionId);
			ResultSet results2 = stmt2.executeQuery();	
			SocketConnection socketConnection = null;
			if (results2.next()) {
				socketConnection = new SocketConnection(SocketConnection.Type.CLIENT,results2.getString("NAME"),results2.getString("IPADDRESS"),results2.getInt("PORTNUMBER"));				
				socketConnection.setId(socketConnectionId);
			}			
			robot = FanucRobot.getInstance(results.getString("NAME"), results.getShort("PAYLOAD"), socketConnection);	
			robot.setId(results.getInt("ID"));
		}
    	}catch (SQLException ex) {
	          LOGGER.log(Level.ERROR, "{}", ex);
	      }
		return robot;
	}
    
    public static void saveRobot(AbstractRobot robot) throws SQLException{		
		if (robot.getId() <= 0) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO SOCKETCONNECTION(NAME, IPADDRESS, PORTNUMBER) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, robot.getSocketConnection().getName());
            stmt.setString(2, robot.getSocketConnection().getIpAddress());
            stmt.setFloat(3, robot.getSocketConnection().getPortNumber());
            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            int socketConnectionId = 0;
            if ((keys != null) && (keys.next())) {
            	socketConnectionId = keys.getInt(1);
            }
            PreparedStatement stmt2 = conn.prepareStatement("INSERT INTO ROBOT(NAME, PAYLOAD, SOCKETCONNECTION) VALUES (?, ?, ?)");
            stmt2.setString(1, robot.getName());
            stmt2.setFloat(2, robot.getPayload());
            stmt2.setInt(3, socketConnectionId);
            stmt2.executeUpdate();
        } else {
        	 PreparedStatement stmt3 = conn.prepareStatement("UPDATE SOCKETCONNECTION SET NAME = ?, IPADDRESS = ?, PORTNUMBER = ? WHERE ID = ?");
             stmt3.setString(1, robot.getSocketConnection().getName());
             stmt3.setString(2, robot.getSocketConnection().getIpAddress());
             stmt3.setFloat(3, robot.getSocketConnection().getPortNumber());
             stmt3.setInt(4, robot.getSocketConnection().getId());
             stmt3.executeUpdate();
             PreparedStatement stmt4 = conn.prepareStatement("UPDATE ROBOT SET NAME = ?, PAYLOAD = ? WHERE ID = ?");
             stmt4.setString(1, robot.getName());
             stmt4.setFloat(2, robot.getPayload());
             stmt4.setInt(3, robot.getId());
             stmt4.executeUpdate();
        }
	
	}
    
}
