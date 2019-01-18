package cn.greatoo.easymill.db.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;

import org.apache.derby.impl.sql.compile.GetCurrentConnectionNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cn.greatoo.easymill.entity.Stacker;
import cn.greatoo.easymill.entity.Clamping;
import cn.greatoo.easymill.entity.Coordinates;

public class Stackerhandler {
	
	static Connection conn = DBHandler.getInstance().getConnection();
	private final static Logger LOGGER = LogManager.getLogger(Stackerhandler.class.getName());

	public static void SaveStacker(Stacker stacker) throws SQLException {
		if (stacker.getId()<=0) {
		conn.setAutoCommit(false);
		PreparedStatement stmt = conn.prepareStatement("INSERT INTO STACKER (HORIZONTALHOLEAMOUNT, VERTICALHOLEAMOUNT, HOLEDIAMETER, STUDDIAMETER, HORIZONTALPADDING, VERTICALPADDINGTOP, VERTICALPADDINGBOTTOM, HORIZONTALHOLEDISTANCE, INTERFERENCEDISTANCE, OVERFLOWPERCENTAGE, HORIZONTALR, TILTEDR, MAXOVERFLOW, MINOVERLAP, MAXUNDERFLOW, VERTICALHOLEDISTANCE,"
				+ " ORIENTATION, LAYERS, AMOUNT, STUDHEIGHT_WORKPIECE) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
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
		stmt.setFloat(17, stacker.getOrientation());
		stmt.setFloat(18, stacker.getLayers());
		stmt.setFloat(19, stacker.getAmount());
		stmt.setFloat(20, stacker.getStudHeight_Workpiece());	
		stmt.executeUpdate();
		ResultSet resultSet = stmt.getGeneratedKeys();
		if (resultSet.next()) {
			stacker.setId(resultSet.getInt(1));
		}
		conn.commit();
		conn.setAutoCommit(true);
		}
		else {
			updateStacker(stacker);			
		}
		Coordinates smoothTo = new Coordinates();
		Coordinates smoothFrom = new Coordinates();
		//Coordinates smoothTo = Clamping.getSmoothToPoint();
		//Coordinates smoothFrom = Clamping.getSmoothFromPoint();
		smoothTo.setX(stacker.getSmoothto().getX());
		smoothTo.setY(stacker.getSmoothto().getY());
		smoothTo.setZ(stacker.getSmoothto().getZ());
		smoothFrom.setX(stacker.getSmoothfrom().getX());
		smoothFrom.setY(stacker.getSmoothfrom().getY());
		smoothFrom.setZ(stacker.getSmoothfrom().getZ());
		CoordinatesHandler.saveCoordinates(smoothTo);
		CoordinatesHandler.saveCoordinates(smoothFrom);
		conn.commit();
		conn.setAutoCommit(true);
		
		PreparedStatement stmt2 = conn.prepareStatement("UPDATE CLAMPING SET HEIGHT = ? WHERE ID = ?");
		stmt2.setFloat(1, stacker.getStudHeight_Stacker());
		stmt2.setInt(2, Clamping.getId());
		stmt2.execute();
		//Clamping.setDefaultHeight(stacker.getStudHeight_Stacker());
	}
	
	
	public static void updateStacker(Stacker stacker) throws SQLException {
		conn.setAutoCommit(false);
		PreparedStatement stmt = conn.prepareStatement("UPDATE STACKER SET HORIZONTALHOLEAMOUNT = ?, VERTICALHOLEAMOUNT = ?, HOLEDIAMETER = ?, " +
				"STUDDIAMETER = ?, HORIZONTALPADDING = ?, VERTICALPADDINGTOP = ?, VERTICALPADDINGBOTTOM = ?, HORIZONTALHOLEDISTANCE = ?, VERTICALHOLEDISTANCE = ?, INTERFERENCEDISTANCE = ?, " +
				" OVERFLOWPERCENTAGE = ?, HORIZONTAL_R = ?, TILTED_R = ?, MAX_OVERFLOW = ?, MIN_OVERLAP = ?, MAX_UNDERFLOW = ? "
				+ "ORIENTATION = ?, LAYERS = ?, AMOUNT = ?, STUDHEIGHT_WORKPIECE = ?"
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
		stmt.setFloat(17, stacker.getMaxUnderflow());
		stmt.setFloat(18, stacker.getMaxUnderflow());
		stmt.setFloat(19, stacker.getMaxUnderflow());
		stmt.setFloat(20, stacker.getMaxUnderflow());	
		stmt.setInt(21, stacker.getId());
		stmt.execute();

		Coordinates smoothTo = Clamping.getSmoothToPoint();
		Coordinates smoothFrom = Clamping.getSmoothFromPoint();
		smoothTo.setX(stacker.getSmoothto().getX());
		smoothTo.setY(stacker.getSmoothto().getY());
		smoothTo.setZ(stacker.getSmoothto().getZ());
		smoothFrom.setX(stacker.getSmoothfrom().getX());
		smoothFrom.setY(stacker.getSmoothfrom().getY());
		smoothFrom.setZ(stacker.getSmoothfrom().getZ());
		CoordinatesHandler.saveCoordinates(smoothTo);
		CoordinatesHandler.saveCoordinates(smoothFrom);
		conn.commit();
		conn.setAutoCommit(true);
		
		
		PreparedStatement stmt2 = conn.prepareStatement("UPDATE CLAMPING SET HEIGHT = ? WHERE ID = ?");
		stmt2.setFloat(1, stacker.getStudHeight_Stacker());
		stmt2.setInt(2, Clamping.getId());
		stmt2.execute();
		//Clamping.setDefaultHeight(stacker.getStudHeight_Stacker());
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
			float orientation = results.getFloat("ORIENTATION");		
			int layers = results.getInt("LAYERS");
			int amount = results.getInt("AMOUNT");
			float studHeight_Workpiece = results.getFloat("STUDHEIGHT_WORKPIECE");

			
			stacker = new Stacker(horizontalHoleAmount, verticalHoleAmount, holeDiameter, studDiameter, horizontalPadding, verticalPaddingTop, 
					verticalPaddingBottom, horizontalHoleDistance, verticalHoleDistance, interferenceDistance, overflowPercentage, horizontalR, tiltedR, maxOverflow, maxUnderflow, minOverlap,
					orientation,layers,amount,studHeight_Workpiece);
			stacker.setId(id);
		}
		return stacker;
	}
	
}
