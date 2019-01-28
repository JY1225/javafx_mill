package cn.greatoo.easymill.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cn.greatoo.easymill.db.util.DBHandler;
import cn.greatoo.easymill.entity.Clamping;
import cn.greatoo.easymill.entity.Coordinates;
import cn.greatoo.easymill.entity.Stacker;
import cn.greatoo.easymill.entity.WorkPiece;

public class WorkPiecePositions {

	static List<Coordinates> coordinatesList = new ArrayList<>();

	public static List<Coordinates> initializeRawWorkPiecePositionsDeg90(final WorkPiece dimensions) {		
		
		Stacker stacker = DBHandler.getInstance().getStatckerBuffer().get(0);
		//工件宽占多少行50/35
		int amountOfStudsWorkPiece = getNumberOfStudsPerWorkPiece(dimensions.getWidth(), true);//4
		//工件长占多少行
		int amountOfStudsWorkPieceVertical = getNumberOfStudsPerWorkPiece(dimensions.getLength(), false);//3
		//横向可以放的工件数
		int amountHorizontal = Math.round((stacker.getHorizontalHoleAmount()-1)/(float)amountOfStudsWorkPiece);
		//纵向可以放的工件数
		int amountVertical = Math.round((stacker.getVerticalHoleAmount()-1)/(float)amountOfStudsWorkPieceVertical);
		
		for (int i = 0; i < amountVertical; i++) {// 2
			for (int j = 0; j < amountHorizontal; j++) {// 4
				int amountOfStudsLeft = j * amountOfStudsWorkPiece;// 0
				int amountOfStudsBottom = 1 + i * amountOfStudsWorkPieceVertical;// 1
				// horizontalHoleDistance=35 verticalHoleDistance = 70 studDiameter=15
				// getHorizontalPadding()=45 getVerticalPaddingBottom()=26
				double xBottomLeft = stacker.getHorizontalPadding()
						+ (amountOfStudsLeft) * stacker.getHorizontalHoleDistance() + stacker.getStudDiameter() / 2;
				double yBottomLeft = stacker.getVerticalPaddingBottom()
						+ (amountOfStudsBottom - 1) * stacker.getVerticalHoleDistance() + stacker.getStudDiameter() / 2;
				float x = (float) xBottomLeft + dimensions.getWidth() / 2;
				float y = (float) yBottomLeft + dimensions.getLength() / 2;
				Coordinates stPos = new Coordinates(x, y, 0, 0, 0, stacker.getOrientation());
				coordinatesList.add(stPos);
			}
		}
		return coordinatesList;
	}

	public static Coordinates getPickLocation(int index) {

		// c(137.5, 133.5, 0.0, 0.0, 0.0, 90.0)(120.0, 131.0, 0.0, 0.0, 0.0, 90.0)
		Coordinates c = new Coordinates(coordinatesList.get(index));
//		if (amount > 0) {
//			c.setZ((amount - 1) * getWorkPiece().getDimensions().getZSafe());
//		} 
		//c.offset(DBHandler.getInstance().getClampBuffer().get(0).getRelativePosition());???????????
		float x = DBHandler.getInstance().getStatckerBuffer().get(0).getHorizontalPadding();
		float y = DBHandler.getInstance().getStatckerBuffer().get(0).getVerticalPaddingBottom();
		c.offset(new Coordinates(-x,-y,0,0,0,0));
		// (92.5, 107.5, 0.0, 0.0, 0.0, 90.0)
		return c;

	}
	public Coordinates getRelativeTeachedOffset(int index){
		Coordinates c = getRelativePosition(index);
		return c;
		
	}
	public Coordinates getRelativePosition(final int coordinatesId){		
        Coordinates coordinates = null;
        try {
        PreparedStatement stmt = DBHandler.getInstance().getConnection().prepareStatement("SELECT * FROM COORDINATES WHERE ID = ?");		
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
        //stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
        return coordinates;
    }
	
	public static Coordinates getPutLocation(Clamping clamp) {		
		Coordinates c = new Coordinates(DBHandler.getInstance().getClampBuffer().get(0).getRelativePosition());
		if (clamp.getClampingType() == Clamping.ClampingType.LENGTH) {
			if (DBHandler.getInstance().getStatckerBuffer().get(0).getOrientation() == 90) {
				c.setR(c.getR() + 90);
			} else {
				c.setR(c.getR());
			}
		}else {
			if (DBHandler.getInstance().getStatckerBuffer().get(0).getOrientation() == 90) {
				c.setR(c.getR());
			} else {
				c.setR(c.getR() + 90);
			}
		}
		return c;
	}
		
	private static int getNumberOfStudsPerWorkPiece(final double dimension, final boolean isHorizontal) {
		Stacker stacker = DBHandler.getInstance().getStatckerBuffer().get(0);
		float holeDistance;
		int amountOfStudsWorkPiece;
		
		// The workpiece is aligned to its left-most (or lower) stud, so it is in fact shifted with studDiameter/2 to the right (or up). 
		double remainingWorkPieceDimension = dimension + (stacker.getStudDiameter() /2);
		
		if(isHorizontal) {
			//Initial value - 2 studs needed
			amountOfStudsWorkPiece = 2;
			holeDistance = stacker.getHorizontalHoleDistance();
			// The first 2 studs are default. This means we can subtract the length of the horizontalHoleDistance already from the initial
			// length of the workpiece since this distance is already covered by studs.
			remainingWorkPieceDimension -= holeDistance;
		} else {
			//Initial value - 1 stud needed
			amountOfStudsWorkPiece = 1;
			holeDistance = stacker.getVerticalHoleDistance();
		}		
		// for real small work-pieces - smaller than the distance between 2 studs
		if (remainingWorkPieceDimension < 0) {
			remainingWorkPieceDimension = 0;
		}		
		// for each time the horizontal hole distance fits in the remaining length, the amount of horizontal studs is incremented
		while (remainingWorkPieceDimension > holeDistance) {
			remainingWorkPieceDimension -= holeDistance;
			amountOfStudsWorkPiece++;
		}		
		// When the remainingLength is less than the distance between 2 studs, we still have a small piece of the workpiece 
		// that we did not take into account. The remaining distance is the space between the next stud and the end of the piece
		// (the distance to the nextWorkpiece - including the first left-most stud of the next workpiece)
		double distanceToNextWorkPiece = holeDistance - (stacker.getStudDiameter() / 2) - remainingWorkPieceDimension;
		// If the distance between 2 successive workpieces becomes too small (safe distance - interferenceDistance), we include 1 more stud
		if (distanceToNextWorkPiece < 5) {
			remainingWorkPieceDimension = 0;
			amountOfStudsWorkPiece++;
		}
		return amountOfStudsWorkPiece;
	}
	public List<Coordinates> getCoordinatesList() {
		return coordinatesList;
	}

	public void setCoordinatesList(List<Coordinates> coordinatesList) {
		this.coordinatesList = coordinatesList;
	}
}
