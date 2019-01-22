package cn.greatoo.easymill.db.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import cn.greatoo.easymill.entity.Gripper;
import cn.greatoo.easymill.entity.Gripper.Type;

public class Gripperhandler {
	
	private static final int GRIPPER_TYPE_TWOPOINT = 1;
	private static final int GRIPPER_TYPE_VACUUM = 2;
	Connection conn = DBHandler.getInstance().getConnection();
	private CoordinatesHandler coordinatesHandler;

	public void saveGripper(final Gripper gripper) throws SQLException {
		conn.setAutoCommit(false);
		PreparedStatement stmt = conn.prepareStatement("INSERT INTO GRIPPER (NAME, HEIGHT, FIXEDHEIGHT, SELECTGRIPPER, GRIPPERINNER, IMAGEURL,TYPE) VALUES (?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		stmt.setString(1, gripper.getName());
		stmt.setFloat(2, gripper.getHeight());
		stmt.setBoolean(3, gripper.isFixedHeight());
		stmt.setString(4, gripper.getSelectGripper());
		stmt.setString(5, gripper.getImageUrl());
		stmt.setBoolean(6, gripper.isGripperInner());
		
		int typeInt = GRIPPER_TYPE_TWOPOINT;
		if (gripper.getType() == Type.TWOPOINT) {
			typeInt = GRIPPER_TYPE_TWOPOINT;
		} else if (gripper.getType() == Type.VACUUM) {
			typeInt = GRIPPER_TYPE_VACUUM;
		}
		stmt.setInt(7, typeInt);

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
	public void updateGripper(final Gripper gripper, final String name, final float height, final boolean fixedHeight,
			final String selectGripper, final boolean gripperInner, final String imgUrl, final Gripper.Type type ) throws SQLException {
		conn.setAutoCommit(false);
		if ((!gripper.getName().equals(name)) || (!gripper.getImageUrl().equals(imgUrl)) || (gripper.getHeight() != height)
				|| (gripper.isFixedHeight() != fixedHeight) || (gripper.getType() != type)) {
			PreparedStatement stmt = conn.prepareStatement("UPDATE GRIPPER SET NAME = ?, HEIGHT = ?, FIXEDHEIGHT = ?, SELECTGRIPPER = ?, GRIPPERINNER = ?, IMAGEURL = ?, TYPE = ? WHERE ID = ?");
			stmt.setString(1, name);
			stmt.setFloat(2, height);
			stmt.setBoolean(3, fixedHeight);
			stmt.setString(4, selectGripper);
			stmt.setBoolean(5, gripperInner);
			stmt.setString(6, imgUrl);
			int gripperTypeId = GRIPPER_TYPE_TWOPOINT;
			if (type == Type.TWOPOINT) {
				gripperTypeId = GRIPPER_TYPE_TWOPOINT;
			} else if (type == Type.VACUUM) {
				gripperTypeId = GRIPPER_TYPE_VACUUM;
			} else {
				throw new IllegalArgumentException("Unknown gripper type: " + type);
			}
			stmt.setInt(7, gripperTypeId);
			stmt.setInt(8, gripper.getId());
			stmt.executeUpdate();
			gripper.setName(name);
			gripper.setImageUrl(imgUrl);
			gripper.setHeight(height);
			gripper.setFixedHeight(fixedHeight);
			gripper.setType(type);
			gripper.setGripperInner(gripperInner);
			gripper.setSelectGripper(selectGripper);
		}
		conn.commit();
		conn.setAutoCommit(true);
		// TODO updating of gripper head compatibility
	}
		
	public Gripper getGripperById(final int id) throws SQLException {
		Gripper gripper = null;
		gripper = DBHandler.getInstance().getGrippersBuffer().get(id);
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
				gripper = new Gripper(name, type, height, selectGripper, gripperInner, imageUrl);
				gripper.setFixedHeight(fixedHeight);
				gripper.setId(id);
			}
			DBHandler.getInstance().getGrippersBuffer().put(id, gripper);
		}
		return gripper;
	}
	
	
	public void deleteGripper(final Gripper gripper) throws SQLException {
		conn.setAutoCommit(false);
		try {
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
