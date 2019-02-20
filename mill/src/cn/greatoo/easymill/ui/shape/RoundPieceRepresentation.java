package cn.greatoo.easymill.ui.shape;

import cn.greatoo.easymill.entity.WorkPiece;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class RoundPieceRepresentation implements IDrawableObject {

	private WorkPiece workPiece;
	private static final String CSS_CLASS_WORKPIECE  = "workpiece";
	private static final String CSS_CLASS_FINISHED = "finished";
	
	public RoundPieceRepresentation(WorkPiece workPiece) {
		this.workPiece = workPiece;
	}
	
	@Override
	public Shape createShape() {
		float diameter = workPiece.getDiameter();
		Circle circle = (new Circle(diameter/2));
		circle.setCenterX(0);
		circle.setCenterY(0);
		circle.getStyleClass().add(CSS_CLASS_WORKPIECE);
		if (workPiece.getType().equals(WorkPiece.Type.FINISHED)) {
			circle.getStyleClass().add(CSS_CLASS_FINISHED);
		}
		return circle;
	}

	@Override
	public boolean needsMarkers() {
		return false;
	}

	@Override
	public float getXCorrection() {
		return 0;
	}

	@Override
	public float getYCorrection() {
		return 0;
	}

	@Override
	public Rectangle createMarker(boolean length) {
		return null;
	}

	@Override
	public float getXTranslationMarker() {
		return 0;
	}

	@Override
	public float getYTranslationMarker() {
		return 0;
	}
}
