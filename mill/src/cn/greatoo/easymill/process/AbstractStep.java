package cn.greatoo.easymill.process;

import cn.greatoo.easymill.entity.Coordinates;
import cn.greatoo.easymill.util.TeachedCoordinatesCalculator;
import eu.robojob.millassist.external.device.processing.reversal.ReversalUnit;
import eu.robojob.millassist.external.robot.AbstractRobotActionSettings.ApproachType;

public abstract class AbstractStep {
	private Coordinates relativeTeachedOffset;

	public AbstractStep() {
		relativeTeachedOffset = null;
	}
	
	protected void initSafeTeachedOffset(final Coordinates originalPosition) {
		float extraOffsetX = 0;
		float extraOffsetY = 0;
		float extraOffsetZ = 0;
		if ((getDevice() instanceof ReversalUnit) && !(getRobotSettings().getApproachType().equals(ApproachType.TOP))) {
			if (getRobotSettings().getApproachType().equals(ApproachType.BOTTOM)) {
				extraOffsetZ = - ((ReversalUnit) getDevice()).getStationHeight();
			} else if (getRobotSettings().getApproachType().equals(ApproachType.FRONT)) {
				extraOffsetX = ((ReversalUnit) getDevice()).getStationLength() - originalPosition.getX();
			} else if (getRobotSettings().getApproachType().equals(ApproachType.FRONT)) {
				extraOffsetY = - ((ReversalUnit) getDevice()).getStationFixtureWidth();
			}
		} else {
			if (originalPosition.getZ() + getRobotSettings().getWorkPiece().getDimensions().getZSafe() < 
					getDeviceSettings().getWorkArea().getWorkAreaManager().getActiveClamping(true, getDeviceSettings().getWorkArea().getSequenceNb()).getRelativePosition().getZ() 
					+ getDeviceSettings().getWorkArea().getWorkAreaManager().getActiveClamping(true, getDeviceSettings().getWorkArea().getSequenceNb()).getHeight()) {
				extraOffsetZ = (getDeviceSettings().getWorkArea().getWorkAreaManager().getActiveClamping(true, getDeviceSettings().getWorkArea().getSequenceNb()).getRelativePosition().getZ() 
						+ getDeviceSettings().getWorkArea().getWorkAreaManager().getActiveClamping(true, getDeviceSettings().getWorkArea().getSequenceNb()).getHeight()) 
						- (originalPosition.getZ() + getRobotSettings().getWorkPiece().getDimensions().getZSafe());
			}
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
