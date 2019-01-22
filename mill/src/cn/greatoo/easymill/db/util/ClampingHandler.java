package cn.greatoo.easymill.db.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


import cn.greatoo.easymill.entity.Clamping;
import cn.greatoo.easymill.entity.Clamping.ClampingType;
import cn.greatoo.easymill.entity.Clamping.Type;
import cn.greatoo.easymill.entity.Coordinates;
import cn.greatoo.easymill.entity.Gripper;
import cn.greatoo.easymill.entity.Smooth;

public class ClampingHandler {

	static Connection conn = DBHandler.getInstance().getConnection();

	private static final int CLAMPING_TYPE_CENTRUM = 1;
	private static final int CLAMPING_TYPE_FIXED_XP = 2;
	private static final int CLAMPING_TYPE_NONE = 3;
	private static final int CLAMPING_TYPE_FIXED_XM = 4;
	private static final int CLAMPING_TYPE_FIXED_YP = 5;
	private static final int CLAMPING_TYPE_FIXED_YM = 6;
	
	public static void saveClamping(final Clamping clamping) throws SQLException {
		conn.setAutoCommit(false);
		CoordinatesHandler.saveCoordinates(clamping.getRelativePosition());
		SmoothHandler.saveSmooth(clamping.getSmoothToPoint());
		SmoothHandler.saveSmooth(clamping.getSmoothFromPoint());
		int typeInt = 0;
		if (clamping.getType() == Type.CENTRUM) {
			typeInt = CLAMPING_TYPE_CENTRUM;
		} else if (clamping.getType() == Type.FIXED_XP) {
			typeInt = CLAMPING_TYPE_FIXED_XP;
		} else if (clamping.getType() == Type.NONE) {
			typeInt = CLAMPING_TYPE_NONE;
		} else if (clamping.getType() == Type.FIXED_XM) {
			typeInt = CLAMPING_TYPE_FIXED_XM;
		} else if (clamping.getType() == Type.FIXED_YP) {
			typeInt = CLAMPING_TYPE_FIXED_YP;
		} else if (clamping.getType() == Type.FIXED_YM) {
			typeInt = CLAMPING_TYPE_FIXED_YM;
		} else {
			throw new IllegalStateException("Unknown clamping type: " + clamping.getType());
		}
		PreparedStatement stmt = conn.prepareStatement("INSERT INTO CLAMPING (NAME, RELATIVEPOSITION, SMOOTHTOPOINT, SMOOTHFROMPOINT, HEIGHT, DEFAULTHEIGHT, IMAGEURL,CLAMPINGTYPE,TYPE) " +
				"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		stmt.setString(1, clamping.getName());
		stmt.setInt(2, clamping.getRelativePosition().getId());
		stmt.setInt(3, clamping.getSmoothToPoint().getId());
		stmt.setInt(4, clamping.getSmoothFromPoint().getId());
		stmt.setFloat(5, clamping.getHeight());
		stmt.setFloat(6, clamping.getDefaultHeight());
		stmt.setString(7, clamping.getImageUrl());
		stmt.setInt(8, clamping.getClampingType().getId());
		stmt.setInt(9, typeInt);
		try {
			stmt.executeUpdate();
			ResultSet resultSet = stmt.getGeneratedKeys();
			if (resultSet.next()) {
				clamping.setId(resultSet.getInt(1));
				conn.commit();
			}
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			conn.setAutoCommit(true);
		}
	}	
	public static void updateClamping(final Clamping clamping) throws SQLException {
		conn.setAutoCommit(false);
		Coordinates relPos = clamping.getRelativePosition();
		
		CoordinatesHandler.saveCoordinates(relPos);

	    Smooth smoothTo = clamping.getSmoothToPoint();

	    SmoothHandler.saveSmooth(smoothTo);

		Smooth smoothFrom = clamping.getSmoothFromPoint();

		SmoothHandler.saveSmooth(smoothFrom);
		
		PreparedStatement stmt = conn.prepareStatement("UPDATE CLAMPING " +
				"SET NAME = ?, TYPE = ?, HEIGHT = ?, IMAGEURL = ?, DEFAULTHEIGHT = ?, CLAMPINGTYPE = ? WHERE ID = ?");
		stmt.setString(1, clamping.getName());
		int typeInt = 0;
		Clamping.Type type = clamping.getType();
		if ( type== Type.CENTRUM) {
			typeInt = CLAMPING_TYPE_CENTRUM;
		} else if (type == Type.FIXED_XP) {
			typeInt = CLAMPING_TYPE_FIXED_XP;
		} else if (type == Type.NONE) {
			typeInt = CLAMPING_TYPE_NONE;
		} else if (type == Type.FIXED_XM) {
			typeInt = CLAMPING_TYPE_FIXED_XM;
		} else if (type == Type.FIXED_YP) {
			typeInt = CLAMPING_TYPE_FIXED_YP;
		} else if (type == Type.FIXED_YM) {
			typeInt = CLAMPING_TYPE_FIXED_YM;
		} else {
			throw new IllegalStateException("Unknown clamping type: " + type);
		}
		stmt.setInt(2, typeInt);
		stmt.setFloat(3, clamping.getHeight());
		stmt.setString(4, clamping.getImageUrl());
		stmt.setFloat(5, clamping.getDefaultHeight());
		stmt.setFloat(6, clamping.getClampingType().getId());
		stmt.setInt(7, clamping.getId());
		stmt.executeUpdate();
		
		conn.commit();
		conn.setAutoCommit(true);
	}
			
	private Clamping getClampingById(final int clampingId) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement("SELECT * FROM CLAMPING WHERE ID = ?");
		stmt.setInt(1, clampingId);
		ResultSet results = stmt.executeQuery();
		Clamping clamping = null;
		if (results.next()) {
			//(NAME, TYPE, RELATIVEPOSITION,CLAMPINFTYPE " +
			//"SMOOTHTOPOINT, SMOOTHFROMPOINT, IMAGEURL, HEIGHT, DEFAULTHEIGHT)
			int type = results.getInt("TYPE");
			int clampingType = results.getInt("CLAMPINGTYPE");
			int relativePositionId = results.getInt("RELATIVEPOSITION");
			Coordinates relativePosition = CoordinatesHandler.getCoordinatesById(0, relativePositionId);
			int smoothToId = results.getInt("SMOOTHTOPOINT");
			Smooth smoothTo = SmoothHandler.getSmoothById(0, smoothToId);
			int smoothFromId = results.getInt("SMOOTHFROMPOINT");
			Smooth smoothFrom = SmoothHandler.getSmoothById(0, smoothFromId);
			float height = results.getFloat("HEIGHT");
			float defaultHeight = results.getFloat("DEFAULTHEIGHT");
			String imageUrl = results.getString("IMAGEURL");
			String name = results.getString("NAME");

			switch(type) {
				case CLAMPING_TYPE_CENTRUM:
					clamping = new Clamping(Clamping.Type.CENTRUM, ClampingType.getTypeById(clampingType), name, defaultHeight, relativePosition, smoothTo,smoothFrom, imageUrl);
					break;
				case CLAMPING_TYPE_FIXED_XP:
					clamping = new Clamping(Clamping.Type.FIXED_XP, ClampingType.getTypeById(clampingType), name, defaultHeight, relativePosition, smoothTo,smoothFrom, imageUrl);
					break;
				case CLAMPING_TYPE_NONE:
					clamping = new Clamping(Clamping.Type.NONE, ClampingType.getTypeById(clampingType), name, defaultHeight, relativePosition, smoothTo,smoothFrom, imageUrl);
					break;
				case CLAMPING_TYPE_FIXED_XM:
					clamping = new Clamping(Clamping.Type.FIXED_XM, ClampingType.getTypeById(clampingType), name, defaultHeight, relativePosition, smoothTo,smoothFrom, imageUrl);
					break;
				case CLAMPING_TYPE_FIXED_YP:
					clamping = new Clamping(Clamping.Type.FIXED_YP, ClampingType.getTypeById(clampingType), name, defaultHeight, relativePosition, smoothTo,smoothFrom, imageUrl);
					break;
				case CLAMPING_TYPE_FIXED_YM:
					clamping = new Clamping(Clamping.Type.FIXED_YM, ClampingType.getTypeById(clampingType), name, defaultHeight, relativePosition, smoothTo,smoothFrom, imageUrl);
					break;
				default:
					throw new IllegalStateException("Unknown clamping type: [" + type + "].");
			}
			clamping.setId(clampingId);
		}
		return clamping;
	}
	
    public static Clamping getClampingByName(final String clampingName) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM CLAMPING WHERE NAME = ?");
        stmt.setString(1, clampingName);
        ResultSet results = stmt.executeQuery();
		Clamping clamping = null;
        if (results.next()) {
            int id = results.getInt("ID");
			int type = results.getInt("TYPE");
			int clampingType = results.getInt("CLAMPINGTYPE");
			int relativePositionId = results.getInt("RELATIVEPOSITION");
			Coordinates relativePosition = CoordinatesHandler.getCoordinatesById(0, relativePositionId);
			int smoothToId = results.getInt("SMOOTHTOPOINT");
			Smooth smoothTo = SmoothHandler.getSmoothById(0, smoothToId);
			int smoothFromId = results.getInt("SMOOTHFROMPOINT");
			Smooth smoothFrom = SmoothHandler.getSmoothById(0, smoothFromId);
			float height = results.getFloat("HEIGHT");
			float defaultHeight = results.getFloat("DEFAULTHEIGHT");
			String imageUrl = results.getString("IMAGEURL");
			String name = results.getString("NAME");
			switch(type) {
				case CLAMPING_TYPE_CENTRUM:
					clamping = new Clamping(Clamping.Type.CENTRUM, ClampingType.getTypeById(clampingType), name, defaultHeight, relativePosition, smoothTo,smoothFrom, imageUrl);
					break;
				case CLAMPING_TYPE_FIXED_XP:
					clamping = new Clamping(Clamping.Type.FIXED_XP, ClampingType.getTypeById(clampingType), name, defaultHeight, relativePosition, smoothTo,smoothFrom, imageUrl);
					break;
				case CLAMPING_TYPE_NONE:
					clamping = new Clamping(Clamping.Type.NONE, ClampingType.getTypeById(clampingType), name, defaultHeight, relativePosition, smoothTo,smoothFrom, imageUrl);
					break;
				case CLAMPING_TYPE_FIXED_XM:
					clamping = new Clamping(Clamping.Type.FIXED_XM, ClampingType.getTypeById(clampingType), name, defaultHeight, relativePosition, smoothTo,smoothFrom, imageUrl);
					break;
				case CLAMPING_TYPE_FIXED_YP:
					clamping = new Clamping(Clamping.Type.FIXED_YP, ClampingType.getTypeById(clampingType), name, defaultHeight, relativePosition, smoothTo,smoothFrom, imageUrl);
					break;
				case CLAMPING_TYPE_FIXED_YM:
					clamping = new Clamping(Clamping.Type.FIXED_YM, ClampingType.getTypeById(clampingType), name, defaultHeight, relativePosition, smoothTo,smoothFrom, imageUrl);
					break;
				default:
					throw new IllegalStateException("Unknown clamping type: [" + type + "].");
			}
			clamping.setId(id);
        }
        stmt.close();
		return clamping;
    }
	
    public static Clamping getClampings() throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM CLAMPING");
        ResultSet results = stmt.executeQuery();
		Clamping clamping = null;
        while (results.next()) {
            int id = results.getInt("ID");
			int type = results.getInt("TYPE");
			int clampingType = results.getInt("CLAMPINGTYPE");
			int relativePositionId = results.getInt("RELATIVEPOSITION");
			Coordinates relativePosition = CoordinatesHandler.getCoordinatesById(0, relativePositionId);
			int smoothToId = results.getInt("SMOOTHTOPOINT");
			Smooth smoothTo = SmoothHandler.getSmoothById(0, smoothToId);
			int smoothFromId = results.getInt("SMOOTHFROMPOINT");
			Smooth smoothFrom = SmoothHandler.getSmoothById(0, smoothFromId);
			float height = results.getFloat("HEIGHT");
			float defaultHeight = results.getFloat("DEFAULTHEIGHT");
			String imageUrl = results.getString("IMAGEURL");
			String name = results.getString("NAME");
			switch(type) {
				case CLAMPING_TYPE_CENTRUM:
					clamping = new Clamping(Clamping.Type.CENTRUM, ClampingType.getTypeById(clampingType), name, defaultHeight, relativePosition, smoothTo,smoothFrom, imageUrl);
					break;
				case CLAMPING_TYPE_FIXED_XP:
					clamping = new Clamping(Clamping.Type.FIXED_XP, ClampingType.getTypeById(clampingType), name, defaultHeight, relativePosition, smoothTo,smoothFrom, imageUrl);
					break;
				case CLAMPING_TYPE_NONE:
					clamping = new Clamping(Clamping.Type.NONE, ClampingType.getTypeById(clampingType), name, defaultHeight, relativePosition, smoothTo,smoothFrom, imageUrl);
					break;
				case CLAMPING_TYPE_FIXED_XM:
					clamping = new Clamping(Clamping.Type.FIXED_XM, ClampingType.getTypeById(clampingType), name, defaultHeight, relativePosition, smoothTo,smoothFrom, imageUrl);
					break;
				case CLAMPING_TYPE_FIXED_YP:
					clamping = new Clamping(Clamping.Type.FIXED_YP, ClampingType.getTypeById(clampingType), name, defaultHeight, relativePosition, smoothTo,smoothFrom, imageUrl);
					break;
				case CLAMPING_TYPE_FIXED_YM:
					clamping = new Clamping(Clamping.Type.FIXED_YM, ClampingType.getTypeById(clampingType), name, defaultHeight, relativePosition, smoothTo,smoothFrom, imageUrl);
					break;
				default:
					throw new IllegalStateException("Unknown clamping type: [" + type + "].");
			}
			clamping.setId(id);
			DBHandler.getInstance().getClampBuffer().add(clamping);
        }
        stmt.close();
		return clamping;
    }
    
	public void deleteClamping(final Clamping clamping) throws SQLException {
		conn.setAutoCommit(false);
		try {			
			PreparedStatement stmt = conn.prepareStatement("DELETE FROM CLAMPING WHERE ID = ?");
			stmt.setInt(1, clamping.getId());
			stmt.executeUpdate();
			conn.commit();
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			conn.setAutoCommit(true);
		}
	}
}
