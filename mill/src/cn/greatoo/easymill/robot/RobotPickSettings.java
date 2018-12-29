package cn.greatoo.easymill.robot;

import cn.greatoo.easymill.cnc.SimpleWorkArea;
import cn.greatoo.easymill.entity.GripperHead;
import cn.greatoo.easymill.workpiece.WorkPiece;
import cn.greatoo.easymill.util.Coordinates;

public abstract class RobotPickSettings extends AbstractRobotActionSettings{	
	private WorkPiece workPiece;
	private boolean doRobotAirblow;
	private ApproachType approachType;

	public RobotPickSettings(final AbstractRobot robot, final SimpleWorkArea workArea, final GripperHead gripperHead, final Coordinates smoothPoint, final Coordinates location, 
			final WorkPiece workPiece, final boolean doRobotAirblow, final boolean gripInner) {
		super(robot, workArea, gripperHead, smoothPoint, location, gripInner);
		this.workPiece = workPiece;
		this.doRobotAirblow = doRobotAirblow;
		this.approachType = ApproachType.TOP;
	}

	public WorkPiece getWorkPiece() {
		return workPiece;
	}

	public void setWorkPiece(final WorkPiece workPiece) {
		this.workPiece = workPiece;
	}

	public boolean isRobotAirblow() {
		return doRobotAirblow;
	}

	public void setRobotAirblow(final boolean robotAirblow) {
		this.doRobotAirblow = robotAirblow;
	}
	
	public void updateWorkPieceType() {
		if (workPiece.getType().equals(WorkPiece.Type.HALF_FINISHED)) {
			workPiece.setType(WorkPiece.Type.FINISHED);
		}
	}
	
	public ApproachType getApproachType() {
		return this.approachType;
	}
	
	public void setApproachType(ApproachType type) {
		this.approachType = type;
	}
	
	/**
	 * Check whether turn in machine is allowed.
	 * 
	 * @return In case the device from which we need to pick is a CNC machine, we check the value
	 * of turnInMachine allowed from the machine together with the option chosen at the CNC config. 
	 * Otherwise the result is always false.
	 */

}