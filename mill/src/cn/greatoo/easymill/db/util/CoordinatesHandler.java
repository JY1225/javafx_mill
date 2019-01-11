package cn.greatoo.easymill.db.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import cn.greatoo.easymill.entity.Coordinates;

public class CoordinatesHandler {


	Connection conn = DBHandler.getInstance().getConnection();

    public void saveCoordinates(final Coordinates coordinates) throws SQLException {
        if (coordinates.getId() <= 0) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO COORDINATES (X, Y, Z, W, P, R) VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            stmt.setFloat(1, coordinates.getX());
            stmt.setFloat(2, coordinates.getY());
            stmt.setFloat(3, coordinates.getZ());
            stmt.setFloat(4, coordinates.getW());
            stmt.setFloat(5, coordinates.getP());
            stmt.setFloat(6, coordinates.getR());
            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            if ((keys != null) && (keys.next())) {
                coordinates.setId(keys.getInt(1));
            }
        } else {
            PreparedStatement stmt = conn.prepareStatement("UPDATE COORDINATES SET X = ?, Y = ?, Z = ?, W = ?, P = ?, R = ? WHERE ID = ?");
            stmt.setFloat(1, coordinates.getX());
            stmt.setFloat(2, coordinates.getY());
            stmt.setFloat(3, coordinates.getZ());
            stmt.setFloat(4, coordinates.getW());
            stmt.setFloat(5, coordinates.getP());
            stmt.setFloat(6, coordinates.getR());
            stmt.setInt(7, coordinates.getId());
            stmt.executeUpdate();
        }
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
