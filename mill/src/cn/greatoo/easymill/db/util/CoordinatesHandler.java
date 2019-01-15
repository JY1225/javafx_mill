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

}
