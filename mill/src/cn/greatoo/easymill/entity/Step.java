package cn.greatoo.easymill.entity;

public class Step {

	private int id;
	private Gripper gripper;
	private WorkPiece workPiece;
	private UserFrame userFrame;
	private Smooth smooth;
	private RobotPutSetting robotPutSetting;
	
	public Step(Gripper gripper, WorkPiece workPiece, UserFrame userFrame,
			Smooth smooth, RobotPutSetting robotPutSetting) {
		this.gripper = gripper;
		this.workPiece = workPiece;
		this.userFrame = userFrame;
		this.smooth = smooth;
		this.robotPutSetting = robotPutSetting;
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

	public RobotPutSetting getRobotPutSetting() {
		return robotPutSetting;
	}

	public void setRobotPutSetting(RobotPutSetting robotPutSetting) {
		this.robotPutSetting = robotPutSetting;
	}
	
}
