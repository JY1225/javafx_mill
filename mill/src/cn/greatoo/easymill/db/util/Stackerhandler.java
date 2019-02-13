package cn.greatoo.easymill.db.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Set;

import org.apache.derby.impl.sql.compile.GetCurrentConnectionNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cn.greatoo.easymill.entity.Stacker;
import cn.greatoo.easymill.entity.Clamping;
import cn.greatoo.easymill.entity.Coordinates;
import cn.greatoo.easymill.entity.Smooth;

public class Stackerhandler {
	
	static Connection conn = DBHandler.getInstance().getConnection();

	public static void SaveStacker(Stacker stacker) throws SQLException {
		if (stacker.getId()<=0) {
		PreparedStatement stmt = conn.prepareStatement("INSERT INTO STACKER (HORIZONTALHOLEAMOUNT, VERTICALHOLEAMOUNT, "
				+ "HOLEDIAMETER, STUDDIAMETER, HORIZONTALPADDING, "
				+ "VERTICALPADDINGTOP, VERTICALPADDINGBOTTOM, HORIZONTALHOLEDISTANCE, INTERFERENCEDISTANCE, OVERFLOWPERCENTAGE,"
				+ " HORIZONTAL_R, TILTED_R, MAX_OVERFLOW, MIN_OVERLAP, MAX_UNDERFLOW, VERTICALHOLEDISTANCE"
				+ ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		stmt.setInt(1, stacker.getHorizontalHoleAmount());
		stmt.setInt(2, stacker.getVerticalHoleAmount());
		stmt.setFloat(3, stacker.getHoleDiameter());
		stmt.setFloat(4, stacker.getStudDiameter());
		stmt.setFloat(5, stacker.getHorizontalPadding());
		stmt.setFloat(6, stacker.getVerticalPaddingTop());
		stmt.setFloat(7, stacker.getVerticalPaddingBottom());
		stmt.setFloat(8, stacker.getHorizontalHoleDistance());
        stmt.setFloat(9, stacker.getInterferenceDistance());
		stmt.setFloat(10, stacker.getOverflowPercentage());
		stmt.setFloat(11, stacker.getHorizontalR());
		stmt.setFloat(12, stacker.getTiltedR());
		stmt.setFloat(13, stacker.getMaxOverflow());
		stmt.setFloat(14, stacker.getMinOverlap());
		stmt.setFloat(15, stacker.getMaxUnderflow());
		stmt.setFloat(16, stacker.getVerticalHoleDistance());	
		stmt.executeUpdate();
		ResultSet resultSet = stmt.getGeneratedKeys();
		if (resultSet.next()) {
			stacker.setId(resultSet.getInt(1));
		}
		}
		else {
			updateStacker(stacker);			
		}
		if (stacker.getSmoothto() != null) {
			SmoothHandler.saveSmooth(stacker.getSmoothto());
		}
		if (stacker.getSmoothfrom() != null) {
			SmoothHandler.saveSmooth(stacker.getSmoothfrom());
		}

	}
	
	
	public static void updateStacker(Stacker stacker) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement("UPDATE STACKER SET HORIZONTALHOLEAMOUNT = ?, VERTICALHOLEAMOUNT = ?, HOLEDIAMETER = ?, " +
				"STUDDIAMETER = ?, HORIZONTALPADDING = ?, VERTICALPADDINGTOP = ?, VERTICALPADDINGBOTTOM = ?, HORIZONTALHOLEDISTANCE = ?, VERTICALHOLEDISTANCE = ?,"
				+ " INTERFERENCEDISTANCE = ?, " +
				" OVERFLOWPERCENTAGE = ?, HORIZONTAL_R = ?, TILTED_R = ?, MAX_OVERFLOW = ?, MIN_OVERLAP = ?, MAX_UNDERFLOW = ?"
				+ "WHERE ID = ?");
		stmt.setInt(1, stacker.getHorizontalHoleAmount());
		stmt.setInt(2, stacker.getVerticalHoleAmount());
		stmt.setFloat(3, stacker.getHoleDiameter());
		stmt.setFloat(4, stacker.getStudDiameter());
		stmt.setFloat(5, stacker.getHorizontalPadding());
		stmt.setFloat(6, stacker.getVerticalPaddingTop());
		stmt.setFloat(7, stacker.getVerticalPaddingBottom());
		stmt.setFloat(8, stacker.getHorizontalHoleDistance());
		stmt.setFloat(9, stacker.getVerticalHoleDistance());
        stmt.setFloat(10, stacker.getInterferenceDistance());
		stmt.setFloat(11, stacker.getOverflowPercentage());
		stmt.setFloat(12, stacker.getHorizontalR());
		stmt.setFloat(13, stacker.getTiltedR());
		stmt.setFloat(14, stacker.getMaxOverflow());
		stmt.setFloat(15, stacker.getMinOverlap());
		stmt.setFloat(16, stacker.getMaxUnderflow());				
		stmt.setInt(17, stacker.getId());
		stmt.executeUpdate();

		if(stacker.getSmoothto() != null) {
			SmoothHandler.saveSmooth(stacker.getSmoothto());
		}
		if(stacker.getSmoothfrom() != null) {
			SmoothHandler.saveSmooth(stacker.getSmoothfrom());
		}

	}
	
	private Stacker getStacker(final int id, final String name) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement("SELECT * FROM STACKER WHERE ID = ?");
		stmt.setInt(1, id);
		ResultSet results = stmt.executeQuery();
		Stacker stacker = null;
		if (results.next()) {
			int horizontalHoleAmount = results.getInt("HORIZONTALHOLEAMOUNT");
			int verticalHoleAmount = results.getInt("VERTICALHOLEAMOUNT");
			float holeDiameter = results.getFloat("HOLEDIAMETER");
			float studDiameter = results.getFloat("STUDDIAMETER");
			float horizontalPadding = results.getFloat("HORIZONTALPADDING");
			float verticalPaddingTop = results.getFloat("VERTICALPADDINGTOP");
			float verticalPaddingBottom = results.getFloat("VERTICALPADDINGBOTTOM");
			float horizontalHoleDistance = results.getFloat("HORIZONTALHOLEDISTANCE");
			float verticalHoleDistance = results.getFloat("VERTICALHOLEDISTANCE");
			float interferenceDistance = results.getFloat("INTERFERENCEDISTANCE");
			float overflowPercentage = results.getFloat("OVERFLOWPERCENTAGE");
			float horizontalR = results.getFloat("HORIZONTAL_R");
			float tiltedR = results.getFloat("TILTED_R");
			float maxOverflow = results.getFloat("MAX_OVERFLOW");
			float maxUnderflow = results.getFloat("MAX_UNDERFLOW");
			float minOverlap = results.getFloat("MIN_OVERLAP");			

			
			stacker = new Stacker(horizontalHoleAmount, verticalHoleAmount, holeDiameter, studDiameter, horizontalPadding, verticalPaddingTop, 
					verticalPaddingBottom, horizontalHoleDistance, verticalHoleDistance, interferenceDistance, overflowPercentage, horizontalR,
					tiltedR, maxOverflow, maxUnderflow, minOverlap);
			stacker.setId(id);
		}
		return stacker;
	}
	
	public static Stacker getStacker() throws SQLException {
		PreparedStatement stmt = conn.prepareStatement("SELECT * FROM STACKER");
		ResultSet results = stmt.executeQuery();
		Stacker stacker = null;
		if (results.next()) {
			int horizontalHoleAmount = results.getInt("HORIZONTALHOLEAMOUNT");
			int verticalHoleAmount = results.getInt("VERTICALHOLEAMOUNT");
			float holeDiameter = results.getFloat("HOLEDIAMETER");
			float studDiameter = results.getFloat("STUDDIAMETER");
			float horizontalPadding = results.getFloat("HORIZONTALPADDING");
			float verticalPaddingTop = results.getFloat("VERTICALPADDINGTOP");
			float verticalPaddingBottom = results.getFloat("VERTICALPADDINGBOTTOM");
			float horizontalHoleDistance = results.getFloat("HORIZONTALHOLEDISTANCE");
			float verticalHoleDistance = results.getFloat("VERTICALHOLEDISTANCE");
			float interferenceDistance = results.getFloat("INTERFERENCEDISTANCE");
			float overflowPercentage = results.getFloat("OVERFLOWPERCENTAGE");
			float horizontalR = results.getFloat("HORIZONTAL_R");
			float tiltedR = results.getFloat("TILTED_R");
			float maxOverflow = results.getFloat("MAX_OVERFLOW");
			float maxUnderflow = results.getFloat("MAX_UNDERFLOW");
			float minOverlap = results.getFloat("MIN_OVERLAP");			

			
			stacker = new Stacker(horizontalHoleAmount, verticalHoleAmount, holeDiameter, studDiameter, horizontalPadding, verticalPaddingTop, 
					verticalPaddingBottom, horizontalHoleDistance, verticalHoleDistance, interferenceDistance, overflowPercentage, horizontalR,
					tiltedR, maxOverflow, maxUnderflow, minOverlap);
			stacker.setId(results.getInt("ID"));
			DBHandler.getInstance().getStatckerBuffer().add(stacker);
		}
		return stacker;
	}
}
