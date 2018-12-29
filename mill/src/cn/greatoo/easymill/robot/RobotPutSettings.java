package cn.greatoo.easymill.robot;

import cn.greatoo.easymill.cnc.SimpleWorkArea;
import cn.greatoo.easymill.entity.GripperHead;
import cn.greatoo.easymill.workpiece.WorkPiece;
import cn.greatoo.easymill.util.Coordinates;

public abstract class RobotPutSettings extends AbstractRobotActionSettings {		
	private boolean doRobotAirblow;
	private boolean releaseBeforeMachine;
	private ApproachType approachType;
	private boolean turnInMachine = true;
//	private boolean turnInMachine = false;
	private boolean isTIMPut = true;
	//private boolean isTIMPut = false;

	public RobotPutSettings(final AbstractRobot robot, final SimpleWorkArea workArea, final GripperHead gripperHead, final Coordinates smoothPoint, final Coordinates location, final boolean doRobotAirblow, 
			final boolean releaseBeforeMachine, final boolean gripInner) {
		super(robot, workArea, gripperHead, smoothPoint, location, gripInner);
		this.doRobotAirblow = doRobotAirblow;
		this.releaseBeforeMachine = releaseBeforeMachine;
		this.approachType = ApproachType.TOP;
	}
	
	public boolean isRobotAirblow() {
		return doRobotAirblow;
	}

	public void setRobotAirblow(final boolean robotAirblow) {
		this.doRobotAirblow = robotAirblow;
	}

	public boolean isReleaseBeforeMachine() {
		return releaseBeforeMachine;
	}

	public void setReleaseBeforeMachine(final boolean releaseBeforeMachine) {
		this.releaseBeforeMachine = releaseBeforeMachine;
	}
	
	public ApproachType getApproachType() {
		return this.approachType;
	}
	
	public void setApproachType(ApproachType type) {
		this.approachType = type;
	}

}