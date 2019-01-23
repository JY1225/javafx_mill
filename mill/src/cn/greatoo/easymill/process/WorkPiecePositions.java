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
		int amountOfStudsWorkPiece = (int) Math.ceil((dimensions.getWidth()/stacker.getHorizontalHoleDistance())+1);
		//工件长占多少行
		int amountOfStudsWorkPieceVertical = (int) Math.ceil((dimensions.getLength()/ stacker.getVerticalHoleDistance())+1);
		//横向可以放的工件数
		int amountHorizontal = Math.round((stacker.getHorizontalHoleAmount()-1)/(float)amountOfStudsWorkPiece);
		//纵向可以放的工件数
		int amountVertical = Math.round((stacker.getVerticalHoleAmount()-1)/(float)amountOfStudsWorkPieceVertical);
		
		for (int i = 0; i < amountVertical; i++) {// 2
			for (int j = 0; j < amountHorizontal; j++) {// 4
				int amountOfStudsLeft = j * amountOfStudsWorkPiece;// 6
				int amountOfStudsBottom = 1 + i * amountOfStudsWorkPieceVertical;// 4
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

		// c(137.5, 133.5, 0.0, 0.0, 0.0, 90.0)
		Coordinates c = new Coordinates(coordinatesList.get(index));
//		if (amount > 0) {
//			c.setZ((amount - 1) * getWorkPiece().getDimensions().getZSafe());
//		} 
		c.offset(DBHandler.getInstance().getClampBuffer().get(0).getRelativePosition());
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
	private static Clamping.ClampingType type = DBHandler.getInstance().getClampBuffer().get(0).getClampingType();
	public static Coordinates getPutLocation(Clamping clamp) {		
		Coordinates c = new Coordinates(DBHandler.getInstance().getClampBuffer().get(0).getRelativePosition());
		if (clamp.getClampingType() == Clamping.ClampingType.LENGTH) {
			if (type != clamp.getClampingType()) {
				c.setR(c.getR() + DBHandler.getInstance().getStatckerBuffer().get(0).getOrientation());
			} else {
				c.setR(c.getR());
			}
		}else {
			if (type != clamp.getClampingType()) {
				c.setR(c.getR());
			} else {
				c.setR(c.getR() + DBHandler.getInstance().getStatckerBuffer().get(0).getOrientation());
			}
		}
		type = clamp.getClampingType();
		return c;
	}
		
	public List<Coordinates> getCoordinatesList() {
		return coordinatesList;
	}

	public void setCoordinatesList(List<Coordinates> coordinatesList) {
		this.coordinatesList = coordinatesList;
	}
}
