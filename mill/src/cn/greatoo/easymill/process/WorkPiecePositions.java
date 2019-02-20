package cn.greatoo.easymill.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cn.greatoo.easymill.db.util.DBHandler;
import cn.greatoo.easymill.entity.Clamping;
import cn.greatoo.easymill.entity.Coordinates;
import cn.greatoo.easymill.entity.Program;
import cn.greatoo.easymill.entity.Stacker;
import cn.greatoo.easymill.entity.WorkPiece;

public class WorkPiecePositions {

	public static List<Coordinates> coordinatesList = new ArrayList<>();
	private boolean isCornerLength = false;
	private boolean isCornerWidth = false;
	private Stacker stacker = DBHandler.getInstance().getStatckerBuffer().get(0);
	private Program program;
	public WorkPiecePositions(Program program) {
		this.program = program;
	}
	
	public int getWorkPieceAmount() {
		return getMaxHorizontalAmount(program.getUnloadstacker().getWorkPiece(), program.getOrientation()) * getMaxVerticalAmount(program.getUnloadstacker().getWorkPiece(), program.getOrientation());		
	}
	
	public  void initStackingPositions(final WorkPiece dimensions) {
		coordinatesList.clear();
		//横向可以放的工件数
		int amountHorizontal = getMaxHorizontalAmount(dimensions, program.getOrientation());//9
		//纵向可以放的工件数
		int amountVertical = getMaxVerticalAmount(dimensions, program.getOrientation());// 3
		int orientation = (int) program.getOrientation();
		switch (orientation) {
		case 0:
			initializeRawWorkPiecePositionsHorizontal(dimensions, amountHorizontal,amountVertical);
			break;
		case 45:
			initializeRawWorkPiecePositionsTilted(dimensions, amountHorizontal,amountVertical);
			break;
		case 90:
			initializeRawWorkPiecePositionsDeg90(dimensions, amountHorizontal,amountVertical);
			break;
		default:
			break;
		}
	}
	//0度
	private List<Coordinates> initializeRawWorkPiecePositionsHorizontal(final WorkPiece dimensions, int amountHorizontal, int amountVertical){						
		//工件长占多少列
		int amountOfStudsWorkPiece = getNumberOfStudsPerWorkPiece(dimensions.getLength(), true);
		//工件宽占多少行
		int amountOfStudsWorkPieceVertical = getNumberOfStudsPerWorkPiece(dimensions.getWidth(), false);

		//纵向可以放的工件数
		for (int i = 0; i < amountVertical; i++) {
			//横向可以放的工件数
			for (int j = 0; j < amountHorizontal; j++) {
				int amountOfStudsLeft = j * amountOfStudsWorkPiece;
				int amountOfStudsBottom = 1 + i * amountOfStudsWorkPieceVertical;
				double xBottomLeft = stacker.getHorizontalPadding()
						+ (amountOfStudsLeft) * stacker.getHorizontalHoleDistance() + stacker.getStudDiameter() / 2;
				double yBottomLeft = stacker.getVerticalPaddingBottom()
						+ (amountOfStudsBottom - 1) * stacker.getVerticalHoleDistance() + stacker.getStudDiameter() / 2;
				float x = (float) xBottomLeft + dimensions.getLength() / 2;
				float y = (float) yBottomLeft + dimensions.getWidth() / 2;
				Coordinates stPos = new Coordinates(x, y, 0, 0, 0, DBHandler.getInstance().getStatckerBuffer().get(0).getHorizontalR());
				coordinatesList.add(stPos);
			}
		}
		return coordinatesList;	
	}
	
	//45度
	private void initializeRawWorkPiecePositionsTilted(final WorkPiece dimensions, int amountHorizontal, int amountVertical){		
		int amountOfStudsLeftFirst = getAmountOfStudsLeft(dimensions.getLength(), dimensions.getWidth());
		double a = stacker.getHorizontalHoleDistance()/(Math.sqrt(2)) - stacker.getStudDiameter()/2;
		double b = stacker.getHorizontalHoleDistance()/(Math.sqrt(2));
		int amountOfStudsLeftOther = getNbStudsLeftOther(dimensions.getLength(), dimensions.getWidth(), a, b);
		int amountOfStudsVertical = getVerticalIndex(dimensions.getLength(), dimensions.getWidth(), program.getOrientation());
		
		for (int i = 0; i < amountVertical; i++) {
			for (int j = 0; j < amountHorizontal; j++) {
				int amountOfStudsLeft = amountOfStudsLeftFirst + j * amountOfStudsLeftOther;
				int amountOfStudsBottom = 1 + i * amountOfStudsVertical;
				double adjustment = (stacker.getHorizontalHoleDistance()/2 - stacker.getStudDiameter()/Math.sqrt(2));
				double xBottom = stacker.getHorizontalPadding() + (amountOfStudsLeft - 1)*stacker.getHorizontalHoleDistance() + stacker.getHorizontalHoleDistance()/2;
				double yBottom = stacker.getVerticalPaddingBottom() - adjustment + (amountOfStudsBottom - 1)*stacker.getVerticalHoleDistance();
				double extraX = (dimensions.getLength()/Math.sqrt(2) - dimensions.getWidth()/Math.sqrt(2))/2;
				double extraY = (dimensions.getLength()/Math.sqrt(2) + dimensions.getWidth()/Math.sqrt(2))/2;
				float x = (float) (xBottom + extraX);
				float y = (float) (yBottom + extraY);
				Coordinates stPos = new Coordinates(x, y, 0, 0, 0, DBHandler.getInstance().getStatckerBuffer().get(0).getTiltedR());
				coordinatesList.add(stPos);
			}
		}
	}
	
	//90度
	private List<Coordinates> initializeRawWorkPiecePositionsDeg90(final WorkPiece dimensions, int amountHorizontal, int amountVertical) {				
		//工件宽占多少列
		int amountOfStudsWorkPiece = getNumberOfStudsPerWorkPiece(dimensions.getWidth(), true);//4
		//工件长占多少行
		int amountOfStudsWorkPieceVertical = getNumberOfStudsPerWorkPiece(dimensions.getLength(), false);//3
		
		//纵向可以放的工件数
		for (int i = 0; i < amountVertical; i++) {// 2
			//横向可以放的工件数
			for (int j = 0; j < amountHorizontal; j++) {// 7
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
				//(112.5, 123.5, 0.0, 0.0, 0.0, 90.0)(252.5, 123.5, 0.0, 0.0, 0.0, 90.0)(392.5, 123.5, 0.0, 0.0, 0.0, 90.0)(532.5, 123.5, 0.0, 0.0, 0.0, 90.0)(672.5, 123.5, 0.0, 0.0, 0.0, 90.0)
				Coordinates stPos = new Coordinates(x, y, 0, 0, 0, program.getOrientation());
				coordinatesList.add(stPos);
			}
		}
		return coordinatesList;
	}

	protected static Coordinates getPickLocation(int index) {

		Coordinates c = new Coordinates(coordinatesList.get(index));
//		if (amount > 0) {
//			c.setZ((amount - 1) * getWorkPiece().getDimensions().getZSafe());
//		} 
		
		float x = DBHandler.getInstance().getStatckerBuffer().get(0).getHorizontalPadding();
		float y = DBHandler.getInstance().getStatckerBuffer().get(0).getVerticalPaddingBottom();
		c.offset(new Coordinates(-x,-y,0,0,0,0));
		return c;

	}
	private Coordinates getRelativeTeachedOffset(int index){
		Coordinates c = getRelativePosition(index);
		return c;
		
	}
	private Coordinates getRelativePosition(final int coordinatesId){		
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
	
	public  Coordinates getPutLocation(Clamping clamp) {		
		Coordinates c = new Coordinates(DBHandler.getInstance().getClampBuffer().get(0).getRelativePosition());
		if (clamp.getClampingType() == Clamping.ClampingType.LENGTH) {
			if (program.getOrientation() == 90) {
				c.setR(c.getR() + 90);
			} else {
				c.setR(c.getR());
			}
		}else {
			if (program.getOrientation() == 90) {
				c.setR(c.getR());
			} else {
				c.setR(c.getR() + 90);
			}
		}
		return c;
	}
	
	protected int getMaxVerticalAmount(final WorkPiece dimensions, final float orientation) {
		if (orientation == 0) {
			return getMaxVerticalAmountNormal(dimensions.getLength(), dimensions.getWidth(), orientation);			
		}
		if (orientation == 90) {
			return getMaxVerticalAmountNormal(dimensions.getWidth(), dimensions.getLength(), orientation);
		} 
		if (orientation == 45) {
			return getMaxVerticalAmountTilted(dimensions.getLength(), dimensions.getWidth(), orientation);
		}
		throw new IllegalArgumentException("Unknown work piece orientation");
	}
	protected int getMaxHorizontalAmount(final WorkPiece dimensions, final float orientation)  {
		if (orientation == 0) {
			return getMaxHorizontalAmountNormal(dimensions.getLength(), dimensions.getWidth(), orientation);			
		}
		if (orientation == 90) {
			return getMaxHorizontalAmountNormal(dimensions.getWidth(), dimensions.getLength(), orientation);
		} 
		if (orientation == 45) {
			return getMaxHorizontalAmountTilted(dimensions.getLength(), dimensions.getWidth(), orientation);
		}
		throw new IllegalArgumentException("Unknown work piece orientation");
	}
	private int getMaxVerticalAmountNormal(float length, float width, float orientation) {
		int amountOfStudsWorkPieceVertical = getNumberOfStudsPerWorkPiece(width, false);
		int remainingStudsTop = stacker.getVerticalHoleAmount() - 1;
		int maxVerticalIndex = (int) Math.floor(remainingStudsTop/amountOfStudsWorkPieceVertical);
		remainingStudsTop -= (amountOfStudsWorkPieceVertical*maxVerticalIndex);
		if(isSufficientStudsLeftTop(remainingStudsTop, orientation, isCornerLength || isCornerWidth)) {
			if(isOverFlowTopHorizontal(length, width, remainingStudsTop)) {
				maxVerticalIndex++;
			}
		}
		return maxVerticalIndex;
	}
	private int getMaxHorizontalAmountNormal(float length, float width, float orientation) {		
		int amountOfHorizontalStudsWorkPiece = getNumberOfStudsPerWorkPiece(length, true);//4
		int remainingStudsRight = stacker.getHorizontalHoleAmount() - 1;//26
		int maxHorizontalIndex = (int) Math.floor(remainingStudsRight/amountOfHorizontalStudsWorkPiece);//6
		remainingStudsRight -= amountOfHorizontalStudsWorkPiece*maxHorizontalIndex;//2
		if(isSufficientStudsLeftHorizontal(remainingStudsRight, orientation, (isCornerLength || isCornerWidth))) {
			if(isOverFlowRightHorizontal(length, width, remainingStudsRight)) {
				maxHorizontalIndex++;
			}
		}
		return maxHorizontalIndex;
	}
	private boolean isSufficientStudsLeftHorizontal(int remainingStudsRight, float orientation, boolean corner) {
		if (orientation == 0 || orientation == 90) {
			if (remainingStudsRight < 0) {
				return false;
			}
			//In case a corner is needed, we need at least 2 stud positions for the attachment of the cornerpieces (1 in the corner of the cornerpiece and one next to it)
			//If we do not have corners, we need at least 3 stud positions for stability (2 to support the workpiece and 1 left of the workpiece)
			if (((corner) && (remainingStudsRight <= 1)) || ((!corner) && (remainingStudsRight < 2))) {
				return false;
			}
		} else if (orientation == 45) {
			if (remainingStudsRight < 1) {
				return false;
			}
		}
		return true;
	}																		//2
	private boolean isOverFlowRightHorizontal(float length, float width, int remainingStuds) {
		//12.5				//120           //15                  //2            //35                                   //45
		double overflowR = length + stacker.getStudDiameter()/2 - remainingStuds * stacker.getHorizontalHoleDistance() - stacker.getHorizontalPadding();
		return isOverFlow(overflowR, length*width);
	}
	private int getMaxHorizontalAmountTilted(float length, float width, float orientation) {
		double a = stacker.getHorizontalHoleDistance()/(Math.sqrt(2)) - stacker.getStudDiameter()/2;
		double b = stacker.getHorizontalHoleDistance()/(Math.sqrt(2));
		
		int amountOfStudsLeftOther = getNbStudsLeftOther(length, width, a, b);
		
		// calculate amount of pieces horizontally by checking overflow to the right
		int maxHorizontalIndex = 0;
		int remainingStudsRight = stacker.getHorizontalHoleAmount() - getAmountOfStudsLeft(length, width);
		
		boolean ok = false;
		while (!ok) {
			if(isSufficientStudsLeftHorizontal(remainingStudsRight, orientation, isCornerLength || isCornerWidth)) {
				if (isOverFlowRightTilted(length, width, remainingStudsRight)) {
					maxHorizontalIndex++;
					remainingStudsRight -= amountOfStudsLeftOther;
				} else {
					ok = true;
				}
			} else {
				ok = true;
			}
		}
		return maxHorizontalIndex;
	}
	private int getMaxVerticalAmountTilted(float length, float width, float orientation) {
		boolean ok = false;
		int verticalRowIndex = getVerticalIndex(length, width, orientation);
		int remainingStudsTop = stacker.getVerticalHoleAmount() - 1;
		int maxVerticalIndex = 0;
		while (!ok) {
			if (isOverFlowTiltedTop(length, width, remainingStudsTop)) {
				maxVerticalIndex++;
				remainingStudsTop -= verticalRowIndex;
			} else {
				ok = true;
			}
		}
		return maxVerticalIndex;
	}
	private boolean isOverFlowTopHorizontal(float length, float width, int remainingStuds) {
		double overflowTop = width - remainingStuds * stacker.getVerticalHoleDistance() - stacker.getVerticalPaddingTop() + stacker.getStudDiameter() / 2;
		return isOverFlow(overflowTop, length*width);
	}
	private boolean isOverFlowTiltedTop(float length, float width, int remainingStuds) {
		double overflowTop = (width + length)/Math.sqrt(2) - (stacker.getHorizontalHoleDistance()/2 - stacker.getStudDiameter()/Math.sqrt(2)) - remainingStuds*stacker.getHorizontalHoleDistance()*2 - stacker.getVerticalPaddingTop();
		return isOverFlow(overflowTop, length*width);
	}
	
	private boolean isOverFlowRightTilted(float length, float width, int remainingStuds) {
		double overflowR = (length - (stacker.getHorizontalHoleDistance()/(Math.sqrt(2)) - stacker.getStudDiameter()/2) - (remainingStuds-1)*stacker.getHorizontalHoleDistance()*Math.sqrt(2) - stacker.getStudDiameter()/2) / Math.sqrt(2) - stacker.getHorizontalPadding();
		return isOverFlow(overflowR, length*width);
	}
	private  int getAmountOfStudsLeft(float length, float width) {
		
		double a = stacker.getHorizontalHoleDistance()/(Math.sqrt(2)) - stacker.getStudDiameter()/2;
		double b = stacker.getHorizontalHoleDistance()/(Math.sqrt(2));
		double c = stacker.getStudDiameter()/2;
		boolean ok = false;
		int n = 0;
		while(!ok) {
			double overflowHorL = (width - a - n*b - c) / Math.sqrt(2) - stacker.getHorizontalPadding();	// check the horizontal distance overflowing the stacker to the left
			if (isOverFlow(overflowHorL, length*width)) {	// if this distance is negative, or small enough, everything is ok
				ok = true;	
			} else {
				n++; // if not, we increase the amount of studs to the left
			}
		}
		return n + 1;
	}
	
	private  boolean isOverFlow(double remainingDistance, double surface) {		
		if((remainingDistance < 0) || (Math.pow(remainingDistance, 2)/surface < stacker.getOverflowPercentage())) {
			//12.5                  //0.5
			if(remainingDistance > stacker.getMaxOverflow()) {
				return false;
			}
			return true;
		}
		return false;
	}
	private  int getNbStudsLeftOther(float length, float width, double a, double b) {
		
		//Calculate the stud pieces to the left of the workpiece
		double extraSpace = 0;
		boolean isCornerLength = false;
		if (isCornerLength) {
			extraSpace = stacker.getStudDiameter();
		}
		// no corner piece used, or corner piece because of width
		int extraStudsLeft = 0;
		while (a + extraStudsLeft*b + extraSpace - width < stacker.getInterferenceDistance()) {
			extraStudsLeft++;
		}
		return (extraStudsLeft + 2);	// also added the aligning stud of this one and the wp to the left
	}
	
	private  int getVerticalIndex(float length, float width, float orientation) {
		
		float horizontalHoleDistance = stacker.getHorizontalHoleDistance();
		float studDiameter = stacker.getStudDiameter();
		float interferenceDistance = stacker.getInterferenceDistance();
		boolean ok = false;
		double a = horizontalHoleDistance/(Math.sqrt(2)) - studDiameter/2;
		double b = horizontalHoleDistance/(Math.sqrt(2));
		int amountOfStudsLeftOther = getNbStudsLeftOther(length, width, a, b);
		int verticalRowIndex = 1;
		while (!ok) {
			double boundingWidth = (verticalRowIndex * horizontalHoleDistance * 2 - studDiameter*Math.sqrt(2)) / Math.sqrt(2);
			double boundingLength = -1;
			double amountOfHoles = amountOfStudsLeftOther - 2;
			
			if (amountOfHoles%2 != 0) {
				boundingLength = (verticalRowIndex + (amountOfHoles - 1)/2 + 1) * Math.sqrt(2) * horizontalHoleDistance + horizontalHoleDistance / Math.sqrt(2);
			} else {
				boundingLength = (verticalRowIndex + amountOfHoles/2 + 1) * Math.sqrt(2) * horizontalHoleDistance - studDiameter;
			}
			if ((width + interferenceDistance < boundingWidth) && (length + interferenceDistance < boundingLength)) {
				ok = true;
			} else {
				verticalRowIndex++;
			}
		}
		return verticalRowIndex;
	}
	
	private boolean isSufficientStudsLeftTop(int remainingStudsTop, float orientation, boolean corner) {
		if (orientation == 0 || orientation == 90) {
			if (remainingStudsTop < 0) {
				return false;
			}
			if ((!corner) && (remainingStudsTop <= 1)) {
				return false;
			}
			return true;
		}
		if (orientation == 45) {
			if ((!corner) && (remainingStudsTop < 1)) {
				return false;
			}
			return true;
		}
		return false;
	}
	
	private  int getNumberOfStudsPerWorkPiece(final double dimension, final boolean isHorizontal) {
		
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
