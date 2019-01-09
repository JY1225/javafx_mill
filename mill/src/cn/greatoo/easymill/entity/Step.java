package cn.greatoo.easymill.entity;

public class Step {

	private int id;
	private String processName;
	private Process.Step step;
	private Gripper gripper;
	private WorkPiece workPiece;
	private UserFrame userFrame;
	private Smooth smooth;
	
	public Step(String processName, Process.Step step, Gripper gripper, WorkPiece workPiece, UserFrame userFrame,
			Smooth smooth) {
		this.processName = processName;
		this.step = step;
		this.gripper = gripper;
		this.workPiece = workPiece;
		this.userFrame = userFrame;
		this.smooth = smooth;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public Process.Step getStep() {
		return step;
	}

	public void setStep(Process.Step step) {
		this.step = step;
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
}
