package cn.greatoo.easymill.process;

import cn.greatoo.easymill.entity.Clamping;
import cn.greatoo.easymill.entity.Coordinates;
import cn.greatoo.easymill.entity.Program;
import cn.greatoo.easymill.entity.WorkPiece;
import cn.greatoo.easymill.util.TeachedCoordinatesCalculator;

public abstract class AbstractStep {
	private Coordinates relativeTeachedOffset;

	public AbstractStep() {
		relativeTeachedOffset = null;
	}
	
	protected void initSafeTeachedOffset(WorkPiece workPiece, Clamping clampping, final Coordinates originalPosition) {
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
		
		setRelativeTeachedOffset(TeachedCoordinatesCalculator.calculateRelativeTeachedOffset(originalPosition, new Coordinates(extraOffsetX, extraOffsetY, extraOffsetZ, 0, 0, 0)));
	}
	
	public Coordinates getRelativeTeachedOffset() {
		return relativeTeachedOffset;
	}

	public void setRelativeTeachedOffset(Coordinates relativeTeachedOffset) {
		this.relativeTeachedOffset = relativeTeachedOffset;
	}
	
}
