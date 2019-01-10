package cn.greatoo.easymill.db.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import cn.greatoo.easymill.entity.Clamping;

public class ClampingHandler {

	Connection conn = DBHandler.getInstance().getConnection();

	public long saveClamping(Clamping clamping) {
		long index = -1;
		try {
			String sql = "insert into Clamping(name,relativePosition,smoothToPoint,smoothFromPoint,height,defaultHeight,imageURL,"
					+ "type,clampingType) " + "value(?,?,?,?,?,?,?,?,?)";
			PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
			stmt.setString(1, clamping.getName());
			stmt.setInt(2, clamping.getRelativePosition());
			stmt.setInt(3, clamping.getSmoothToPoint());
			stmt.setInt(4, clamping.getSmoothFromPoint());
			stmt.setDouble(5, clamping.getHeight());
			stmt.setDouble(6, clamping.getDefaultHeight());
			stmt.setString(7, clamping.getImageUrl());
			stmt.setInt(8, 1);
			stmt.setInt(9, clamping.getId());
			stmt.executeUpdate();
			ResultSet results = stmt.getGeneratedKeys();// 这一句代码就是得到插入的记录的id
			while (results.next()) {
				index = results.getLong(1);
			}			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return index;
	}

	public void updateClamping(Clamping clamping) {
		String sql = "update Clamping set name=?,relativePosition=?,smoothToPoint=?,smoothFromPoint=?,height=?,defaultHeight=?,imageURL=?,"
				+ "type=?,clampingType=? WHERE id = ?";
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, clamping.getName());
			stmt.setInt(2, clamping.getRelativePosition());
			stmt.setInt(3, clamping.getSmoothToPoint());
			stmt.setInt(4, clamping.getSmoothFromPoint());
			stmt.setDouble(5, clamping.getHeight());
			stmt.setDouble(6, clamping.getDefaultHeight());
			stmt.setString(7, clamping.getImageUrl());
			stmt.setInt(8, 1);
			stmt.setInt(9, clamping.getId());
			stmt.setInt(10, clamping.getId());
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Clamping selectClamping(int id) {
		String sql = "select * from Clamping where id = ?";
		Clamping clamping = new Clamping();
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			ResultSet results = stmt.executeQuery();
			int i = 0;
			while (results.next()) {
				clamping.setId(results.getInt(i++));
				clamping.setName(results.getString(i++)); 
				
				clamping.setRelativePosition(results.getInt(i++));
				clamping.setSmoothToPoint(results.getInt(i++));
				clamping.setSmoothFromPoint(results.getInt(i++));
				
				clamping.setHeight((float) results.getDouble(i++));
				clamping.setDefaultHeight((float) results.getDouble(i++));				
				clamping.setImageUrl(results.getString(i++));
				
				clamping.setType(results.getInt(i++));
				clamping.setClampingType(results.getInt(i++));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return clamping;
	}
}
