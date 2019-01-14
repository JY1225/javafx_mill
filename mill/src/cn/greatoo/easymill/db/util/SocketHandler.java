package cn.greatoo.easymill.db.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import cn.greatoo.easymill.external.communication.socket.SocketConnection;

public class SocketHandler {
	Connection conn = DBHandler.getInstance().getConnection();

    public void saveCoordinates(SocketConnection socketConnection) throws SQLException {
        if (socketConnection.getId() <= 0) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO COORDINATES (X, Y, Z, W, P, R) VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            
            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            if ((keys != null) && (keys.next())) {
                //coordinates.setId(keys.getInt(1));
            }
        } else {
            PreparedStatement stmt = conn.prepareStatement("UPDATE COORDINATES SET X = ?, Y = ?, Z = ?, W = ?, P = ?, R = ? WHERE ID = ?");
            
            stmt.executeUpdate();
        }
    }
}