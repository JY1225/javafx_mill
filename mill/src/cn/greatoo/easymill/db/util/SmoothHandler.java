package cn.greatoo.easymill.db.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import cn.greatoo.easymill.entity.Smooth;
import cn.greatoo.easymill.entity.Coordinates;
import cn.greatoo.easymill.entity.Smooth;

public class SmoothHandler {

	static Connection conn = DBHandler.getInstance().getConnection();

    public static void saveSmooth(final Smooth smooth) throws SQLException {
        if (smooth.getId() <= 0) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO SMOOTH (X, Y, Z) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            stmt.setFloat(1, smooth.getX());
            stmt.setFloat(2, smooth.getY());
            stmt.setFloat(3, smooth.getZ());
            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            if ((keys != null) && (keys.next())) {
            	smooth.setId(keys.getInt(1));
            }
        } else {
            PreparedStatement stmt = conn.prepareStatement("UPDATE SMOOTH SET X = ?, Y = ?, Z = ? WHERE ID = ?");
            stmt.setFloat(1, smooth.getX());
            stmt.setFloat(2, smooth.getY());
            stmt.setFloat(3, smooth.getZ());
            stmt.setInt(4, smooth.getId());
            stmt.executeUpdate();
        }
    }    
    
	private void deleteSmooth(Integer smoothId) throws SQLException {
		PreparedStatement stmtDeleteSmooth = conn.prepareStatement("delete from smooth where id=?");
		stmtDeleteSmooth.setInt(1, smoothId);
		stmtDeleteSmooth.executeUpdate();
	}	
	
    public void deleteCoordinates(final Smooth smooth) throws SQLException {
        if (smooth.getId() > 0) {
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM smooth WHERE ID = ?");
            stmt.setInt(1, smooth.getId());
            stmt.executeUpdate();
        }
    }
     
    public static Smooth getSmoothById(final int smoothId) throws SQLException {
        Smooth smooth = null;
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM smooth WHERE ID = ?");
        stmt.setInt(1, smoothId);
        ResultSet results = stmt.executeQuery();
        if (results.next()) {
            float x = results.getFloat("X");
            float y = results.getFloat("Y");
            float z = results.getFloat("Z");
            smooth = new Smooth(x, y, z);
            smooth.setId(smoothId);
        }
        return smooth;
    }
}
