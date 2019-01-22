package cn.greatoo.easymill.process;

import cn.greatoo.easymill.entity.Clamping;
import cn.greatoo.easymill.entity.Coordinates;
import cn.greatoo.easymill.entity.WorkPiece;
import cn.greatoo.easymill.util.TeachedCoordinatesCalculator;

public abstract class AbstractStep {
	private static Coordinates unloadStackerRelativeTeachedOffset;
	private static Coordinates loadCNCRelativeTeachedOffset;
	private static Coordinates unloadCNCRelativeTeachedOffset;
	private static Coordinates loadStackerRelativeTeachedOffset;

	public AbstractStep() {
		unloadStackerRelativeTeachedOffset = null;
	}
	
	protected void initSafeTeachedOffset(int step, WorkPiece workPiece, Clamping clampping, final Coordinates originalPosition) {
		float extraOffsetX = 0;
		float extraOffsetY = 0;
		float extraOffsetZ = 0;
		if (originalPosition.getZ() + workPiece.getHeight() < 
				clampping.getRelativePosition().getZ() 
					+ clampping.getHeight()) {
			extraOffsetZ = (clampping.getRelativePosition().getZ() 
						+ clampping.getHeight()) 
						- (originalPosition.getZ() + workPiece.getHeight());
			}
		switch (step) {
		case 1:
			setUnloadStackerRelativeTeachedOffset(TeachedCoordinatesCalculator.calculateRelativeTeachedOffset(originalPosition, new Coordinates(extraOffsetX, extraOffsetY, extraOffsetZ, 0, 0, 0)));
			break;
		case 2:
			setLoadCNCRelativeTeachedOffset(TeachedCoordinatesCalculator.calculateRelativeTeachedOffset(originalPosition, new Coordinates(extraOffsetX, extraOffsetY, extraOffsetZ, 0, 0, 0)));
			break;
		case 3:
			setUnloadCNCRelativeTeachedOffset(TeachedCoordinatesCalculator.calculateRelativeTeachedOffset(originalPosition, new Coordinates(extraOffsetX, extraOffsetY, extraOffsetZ, 0, 0, 0)));
			break;
		case 4:
			setLoadStackerRelativeTeachedOffset(TeachedCoordinatesCalculator.calculateRelativeTeachedOffset(originalPosition, new Coordinates(extraOffsetX, extraOffsetY, extraOffsetZ, 0, 0, 0)));
			break;
		default:
			break;
		}
	}

	public Coordinates getUnloadStackerRelativeTeachedOffset() {
		return unloadStackerRelativeTeachedOffset;
	}

	public void setUnloadStackerRelativeTeachedOffset(Coordinates unloadStackerRelativeTeachedOffset) {
		this.unloadStackerRelativeTeachedOffset = unloadStackerRelativeTeachedOffset;
	}

	public Coordinates getLoadCNCRelativeTeachedOffset() {
		return loadCNCRelativeTeachedOffset;
	}

	public void setLoadCNCRelativeTeachedOffset(Coordinates loadCNCRelativeTeachedOffset) {
		this.loadCNCRelativeTeachedOffset = loadCNCRelativeTeachedOffset;
	}

	public Coordinates getUnloadCNCRelativeTeachedOffset() {
		return unloadCNCRelativeTeachedOffset;
	}

	public void setUnloadCNCRelativeTeachedOffset(Coordinates unloadCNCRelativeTeachedOffset) {
		this.unloadCNCRelativeTeachedOffset = unloadCNCRelativeTeachedOffset;
	}

	public Coordinates getLoadStackerRelativeTeachedOffset() {
		return loadStackerRelativeTeachedOffset;
	}

	public void setLoadStackerRelativeTeachedOffset(Coordinates loadStackerRelativeTeachedOffset) {
		this.loadStackerRelativeTeachedOffset = loadStackerRelativeTeachedOffset;
	}	
	
}
