package cn.greatoo.easymill.db.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import cn.greatoo.easymill.entity.Coordinates;

public class CoordinatesHandler {


	Connection conn = DBHandler.getInstance().getConnection();

	public long saveCoordinates(Coordinates Coordinates) {
		long index = -1;
		try {
			String sql = "insert into Coordinates(x,y,z,w,p,r) value(?,?,?,?,?,?)";
			PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
			stmt.setDouble(1, Coordinates.getX());
			stmt.setDouble(2, Coordinates.getY());
			stmt.setDouble(3, Coordinates.getZ());
			stmt.setDouble(4, Coordinates.getW());
			stmt.setDouble(5, Coordinates.getP());
			stmt.setDouble(6, Coordinates.getR());			
			stmt.executeUpdate();
			ResultSet results = stmt.getGeneratedKeys();// 这一句代码就是得到插入的记录的id
			while (results.next()) {
				index = results.getLong(1);
			}
			return index;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return index;
	}

	public void updateCoordinates(Coordinates Coordinates) {
		String sql = "update Coordinates set name=?,relativePosition=?,smoothToPoint=?,smoothFromPoint=?,height=?,defaultHeight=?,imageURL=?,"
				+ "type=?,CoordinatesType=?";
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, Coordinates.getName());
			stmt.setInt(2, Coordinates.getRelativePosition());
			stmt.setInt(3, Coordinates.getSmoothToPoint());
			stmt.setInt(4, Coordinates.getSmoothFromPoint());
			stmt.setDouble(5, Coordinates.getHeight());
			stmt.setDouble(6, Coordinates.getDefaultHeight());
			stmt.setString(7, Coordinates.getImageUrl());
			stmt.setInt(8, 1);
			stmt.setInt(9, Coordinates.getId());
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Coordinates selectCoordinates(int id) {
		String sql = "select * from Coordinates where id = ?";
		Coordinates Coordinates = new Coordinates();
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			ResultSet results = stmt.executeQuery();
			int i = 0;
			while (results.next()) {
				Coordinates.setId(results.getInt(i++));
				Coordinates.setName(results.getString(i++)); 
				
				Coordinates.setRelativePosition(results.getInt(i++));
				Coordinates.setSmoothToPoint(results.getInt(i++));
				Coordinates.setSmoothFromPoint(results.getInt(i++));
				
				Coordinates.setHeight((float) results.getDouble(i++));
				Coordinates.setDefaultHeight((float) results.getDouble(i++));				
				Coordinates.setImageUrl(results.getString(i++));
				
				Coordinates.setType(results.getInt(i++));
				Coordinates.setCoordinatesType(results.getInt(i++));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return Coordinates;
	}

}
