package cn.greatoo.easymill.db.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cn.greatoo.easymill.entity.Coordinates;
import cn.greatoo.easymill.entity.UserFrame;

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
	
    public static Set<UserFrame> getAllUserFrames(){
        PreparedStatement stmt;
        Set<UserFrame> userFrames = null;
		try {
			stmt = conn.prepareStatement("SELECT * FROM USERFRAME");		
        ResultSet results = stmt.executeQuery();
        userFrames = new HashSet<UserFrame>();
        while (results.next()) {
            int num = results.getInt("NUMBER");
            userFrames.add(getUserFrameByName(num));
        }
		} catch (SQLException e) {			
			e.printStackTrace();
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
            Coordinates location = CoordinatesHandler.getCoordinatesById(locationId);
            userFrame = new UserFrame(results.getString("NAME"), results.getInt("NUMBER"), results.getFloat("ZSAFEDISTANCE"), location);
            userFrame.setId(results.getInt("ID"));
        }
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
            Coordinates location = CoordinatesHandler.getCoordinatesById(locationId);
            uf = new UserFrame(name, number, ZSAFEDISTANCE, location);
            uf.setId(id);
        }
        return uf;
    }
}
