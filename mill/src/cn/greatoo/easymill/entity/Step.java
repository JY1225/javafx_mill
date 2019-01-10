package cn.greatoo.easymill.entity;

public class Step {

	private int id;
	private Gripper gripper;
	private WorkPiece workPiece;
	private UserFrame userFrame;
	private Smooth smooth;
	private RobotSetting robotSetting;
	private Coordinates offset;

	public Step(Gripper gripper, WorkPiece workPiece, UserFrame userFrame,
		Smooth smooth, RobotSetting robotSetting, Coordinates offset) {
		this.gripper = gripper;
		this.workPiece = workPiece;
		this.userFrame = userFrame;
		this.smooth = smooth;
		this.robotSetting = robotSetting;
		this.offset = offset;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Gripper getGripper() {
		return gripper;
	}

	public void setGripper(Gripper gripper) {
		this.gripper = gripper;
	}

	public WorkPiece getWorkPiece() {
		return workPiece;
	}

	public void setWorkPiece(WorkPiece workPiece) {
		this.workPiece = workPiece;
	}

	public UserFrame getUserFrame() {
		return userFrame;
	}

	public void setUserFrame(UserFrame userFrame) {
		this.userFrame = userFrame;
	}

	public Smooth getSmooth() {
		return smooth;
	}

	public void setSmooth(Smooth smooth) {
		this.smooth = smooth;
	}

	public RobotSetting getRobotPutSetting() {
		return robotSetting;
	}

	public void setRobotPutSetting(RobotSetting robotPutSetting) {
		this.robotSetting = robotPutSetting;
	}

	public Coordinates getOffset() {
		return offset;
	}

	public void setOffset(Coordinates offset) {
		this.offset = offset;
	}
	
}
