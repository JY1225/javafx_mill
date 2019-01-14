package cn.greatoo.easymill.db.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

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
    
	private void deleteCoordinate(Integer coordinateId) throws SQLException {
		PreparedStatement stmtDeleteCoordinates = conn.prepareStatement("delete from coordinates where id=?");
		stmtDeleteCoordinates.setInt(1, coordinateId);
		stmtDeleteCoordinates.executeUpdate();
	}	
	
    public void deleteCoordinates(final Coordinates coordinates) throws SQLException {
        if (coordinates.getId() > 0) {
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM COORDINATES WHERE ID = ?");
            stmt.setInt(1, coordinates.getId());
            stmt.executeUpdate();
            for (Map<Integer, Coordinates> buffer : DBHandler.getInstance().getCoordinatesBuffer().values()) {
                buffer.remove(coordinates.getId());
            }
            coordinates.setId(0);
        }
    }
     
    public Coordinates getCoordinatesById(final int processFlowId, final int coordinatesId) throws SQLException {
        Coordinates coordinates = null;
        if (processFlowId != 0) {
            Map<Integer, Coordinates> buffer = DBHandler.getInstance().getCoordinatesBuffer().get(processFlowId);
            if (buffer != null) {
                coordinates = buffer.get(coordinatesId);
                if (coordinates != null) {
                    return coordinates;
                }
            } else {
                Map<Integer, Coordinates> newBuffer = new HashMap<Integer, Coordinates>();
                DBHandler.getInstance().getCoordinatesBuffer().put(processFlowId, newBuffer);
            }
        }
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM COORDINATES WHERE ID = ?");
        stmt.setInt(1, coordinatesId);
        ResultSet results = stmt.executeQuery();
        if (results.next()) {
            float x = results.getFloat("X");
            float y = results.getFloat("Y");
            float z = results.getFloat("Z");
            float w = results.getFloat("W");
            float p = results.getFloat("P");
            float r = results.getFloat("R");
            coordinates = new Coordinates(x, y, z, w, p, r);
            coordinates.setId(coordinatesId);
        }
        stmt.close();
        if (processFlowId != 0) {
        	DBHandler.getInstance().getCoordinatesBuffer().get(processFlowId).put(coordinatesId, coordinates);
        }
        return coordinates;
    }
}
