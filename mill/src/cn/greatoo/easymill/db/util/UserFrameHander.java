package cn.greatoo.easymill.db.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.Attributes.Name;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cn.greatoo.easymill.entity.Coordinates;
import cn.greatoo.easymill.entity.UserFrame;
import cn.greatoo.easymill.db.util.CoordinatesHandler;

public class UserFrameHander {
	
	static Connection conn = DBHandler.getInstance().getConnection();
	private final static Logger LOGGER = LogManager.getLogger(UserFrameHander.class.getName());

	public static void saveUserFrame(final UserFrame userFrame) throws SQLException {
		conn.setAutoCommit(false);
		CoordinatesHandler.saveCoordinates(userFrame.getLocation());
		PreparedStatement stmt = conn.prepareStatement("INSERT INTO USERFRAME (NAME,NUMBER, ZSAFEDISTANCE, LOCATION) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		stmt.setString(1, userFrame.getName());
		stmt.setInt(2, userFrame.getNumber());
		stmt.setFloat(3, userFrame.getzSafeDistance());
		stmt.setInt(4, userFrame.getLocation().getId());

		stmt.executeUpdate();
		ResultSet resultSet = stmt.getGeneratedKeys();
		if (resultSet.next()) {
			userFrame.setId(resultSet.getInt(1));
		}
		conn.commit();
		conn.setAutoCommit(true);
	}
	
//	public static void updateUserFrame(final UserFrame userFrame) throws SQLException {
//		conn.setAutoCommit(false);
//		try{
//			PreparedStatement stmt = conn.prepareStatement("UPDATE USERFRAME SET NAME = ?, NUMBER = ?, ZSAFEDISTANCE = ? WHERE ID = ?");
//			String name =userFrame.getName();
//			stmt.setString(1,name);
//			int number = userFrame.getNumber();
//			stmt.setInt(2, number);
//			float zSafeDistance =userFrame.getzSafeDistance();
//			stmt.setFloat(3, zSafeDistance);
//			int locationId = userFrame.getLocation().getId();
//			stmt.setInt(4, locationId);
//			stmt.executeUpdate();
//			CoordinatesHandler.saveCoordinates(userFrame.getLocation());
//		conn.commit();
//		conn.setAutoCommit(true);
//		}catch (SQLException ex) {
//			LOGGER.log(Level.ERROR, "{}", ex);
//		}
//	
//	}
	
	public static void updateuserframe(final UserFrame userFrame) throws SQLException {
		conn.setAutoCommit(false);
		PreparedStatement stmt = conn.prepareStatement("UPDATE USERFRAME SET NAME = ?, NUMBER = ?, ZSAFEDISTANCE = ? WHERE ID = ?");
		stmt.setString(1, userFrame.getName());
		stmt.setInt(2, userFrame.getNumber());
		stmt.setFloat(3, userFrame.getzSafeDistance());
		stmt.setInt(4, userFrame.getId());
		stmt.executeUpdate();
		CoordinatesHandler.saveCoordinates(userFrame.getLocation());
		conn.commit();
		conn.setAutoCommit(true);
	}
	
    public static Set<UserFrame> getAllUserFrames() throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM USERFRAME");
        ResultSet results = stmt.executeQuery();
        Set<UserFrame> userFrames = new HashSet<UserFrame>();
        while (results.next()) {
            int num = results.getInt("NUMBER");
            userFrames.add(getUserFrameByName(num));
        }
        return userFrames;
    } 
    
    public static UserFrame getUserFrameByName(final int number) throws SQLException {
        UserFrame userFrame = DBHandler.getInstance().getUserFrameBuffer().get(number);
        if (userFrame != null) {
            return userFrame;
        }
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM USERFRAME WHERE NUMBER = ?");
        stmt.setInt(1, number);
        ResultSet results = stmt.executeQuery();
        while (results.next()) {
            int locationId = results.getInt("LOCATION");
            Coordinates location = CoordinatesHandler.getCoordinatesById(0, locationId);
            userFrame = new UserFrame(results.getString("NAME"), results.getInt("NUMBER"), results.getFloat("ZSAFEDISTANCE"), location);
            userFrame.setId(results.getInt("ID"));
        }
        stmt.close();
        DBHandler.getInstance().getUserFrameBuffer().put(number, userFrame);
        return userFrame;
    }

    public static UserFrame getUserFrameByName(final String userFrameName) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM USERFRAME WHERE NAME = ?");
        stmt.setString(1, userFrameName);
        ResultSet results = stmt.executeQuery();
        UserFrame uf = null;
        if (results.next()) {
            int id = results.getInt("ID");
            int number = results.getInt("NUMBER");
            float ZSAFEDISTANCE = results.getFloat("ZSAFEDISTANCE");
            int locationId = results.getInt("LOCATION");
            String name = results.getString("NAME");
            Coordinates location = CoordinatesHandler.getCoordinatesById(0, locationId);
            uf = new UserFrame(name, number, ZSAFEDISTANCE, location);
            uf.setId(id);
        }
        stmt.close();
        return uf;
    }
}
