package cn.greatoo.easymill.db.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import cn.greatoo.easymill.entity.Gripper;
import cn.greatoo.easymill.entity.Gripper.Type;

public class Gripperhandler {
	
	private static final int GRIPPER_TYPE_TWOPOINT = 1;
	private static final int GRIPPER_TYPE_VACUUM = 2;
	static Connection conn = DBHandler.getInstance().getConnection();

	public static void saveGripper(final Gripper gripper) throws SQLException {
		conn.setAutoCommit(false);
		PreparedStatement stmt = conn.prepareStatement("INSERT INTO GRIPPER (NAME, HEIGHT, FIXEDHEIGHT, IMAGEURL,TYPE) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		stmt.setString(1, gripper.getName());
		stmt.setFloat(2, gripper.getHeight());
		stmt.setBoolean(3, gripper.isFixedHeight());
		stmt.setString(4, gripper.getImageUrl());	
		int typeInt = GRIPPER_TYPE_TWOPOINT;
		if (gripper.getType() == Type.TWOPOINT) {
			typeInt = GRIPPER_TYPE_TWOPOINT;
		} else if (gripper.getType() == Type.VACUUM) {
			typeInt = GRIPPER_TYPE_VACUUM;
		}
		stmt.setInt(5, typeInt);

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
			PreparedStatement stmt = conn.prepareStatement("UPDATE GRIPPER SET NAME = ?, HEIGHT = ?, FIXEDHEIGHT = ?, IMAGEURL = ?, TYPE = ? WHERE ID = ?");
			stmt.setString(1, gripper.getName());
			stmt.setFloat(2, gripper.getHeight());
			stmt.setBoolean(3, gripper.isFixedHeight());
			stmt.setString(4, gripper.getImageUrl());
			int typeInt = GRIPPER_TYPE_TWOPOINT;
			if (gripper.getType() == Type.TWOPOINT) {
				typeInt = GRIPPER_TYPE_TWOPOINT;
			} else if (gripper.getType() == Type.VACUUM) {
				typeInt = GRIPPER_TYPE_VACUUM;
			} else {
				throw new IllegalArgumentException("Unknown gripper type: " +gripper.getType() );
			}
			stmt.setInt(5, typeInt);
			stmt.setInt(6, gripper.getId());
			stmt.executeUpdate();
	}
		
	
	public static Gripper getGripperById(final int id) throws SQLException {
		Gripper gripper = null;
		if (gripper == null) {
			PreparedStatement stmt = conn.prepareStatement("SELECT * FROM GRIPPER WHERE ID = ?");
			stmt.setInt(1, id);
			ResultSet results = stmt.executeQuery();
			if (results.next()) {
				float height = results.getFloat("HEIGHT");
				boolean fixedHeight = results.getBoolean("FIXEDHEIGHT");
				String name = results.getString("NAME");
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
				gripper = new Gripper(name, type, height, imageUrl);
				gripper.setFixedHeight(fixedHeight);
				gripper.setId(id);
			}
		}
		return gripper;
	}
	
    public static Gripper getGripperByName(final String gripperName) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM GRIPPER WHERE NAME = ?");
        stmt.setString(1, gripperName);
        ResultSet results = stmt.executeQuery();
        Gripper gripper = null;
        if (results.next()) {
            int id = results.getInt("ID");
			float height = results.getFloat("HEIGHT");
			boolean fixedHeight = results.getBoolean("FIXEDHEIGHT");
			String name = results.getString("NAME");
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
			gripper = new Gripper(name, type, height, imageUrl);
			gripper.setFixedHeight(fixedHeight);
			gripper.setId(id);
        }        
        return gripper;
    }
	public static int getGripperIdByName(String name, Gripper gripper) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement("SELECT * FROM GRIPPER WHERE NAME = ?");
		stmt.setString(1, name);
		ResultSet results = stmt.executeQuery();
		if (results.next()) {
			int id = results.getInt("id");
			gripper.setId(id);
		}			
		return gripper.getId();
	}
	
	public static List<Gripper> getAllGripper(){
		List<Gripper> list = new ArrayList<>();		
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement("SELECT * FROM GRIPPER");		
		ResultSet results = stmt.executeQuery();
		while (results.next()) {
			float height = results.getFloat("HEIGHT");
			boolean fixedHeight = results.getBoolean("FIXEDHEIGHT");
			String name = results.getString("NAME");
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
			Gripper gripper = new Gripper(name, type, height, imageUrl);
			gripper.setFixedHeight(fixedHeight);
			gripper.setId(results.getInt("ID"));
			DBHandler.getInstance().getGriperBuffer().add(gripper);
			list.add(gripper);
		}
		} catch (SQLException e) {			
			e.printStackTrace();
		}
		return list;
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
