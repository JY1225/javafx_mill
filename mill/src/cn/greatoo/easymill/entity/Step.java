package cn.greatoo.easymill.entity;

import cn.greatoo.easymill.entity.Clamping.ClampingType;

public class Step implements Cloneable {

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
					
	public enum StepType {
		UNLOADSTACKER(1), LOADCNC(2), UNLOADCNC(3), LOADSTACKER(4);
		

		private int id;
		
		private StepType(int id) {
			this.id = id;
		}
		
		public int getId() {
			return this.id;
		}
		
		public int getIdTypeId() {
			return this.id;
		}
		public static ClampingType getTypeById(int id) throws IllegalStateException {
			for (ClampingType clampingType: ClampingType.values()) {
				if (clampingType.getId() == id) {
					return clampingType;
				}
			}
			throw new IllegalStateException("Unknown workpiece type: [" + id + "].");
		}

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

	public RobotSetting getRobotSetting() {
		return robotSetting;
	}

	public void setRobotSetting(RobotSetting robotSetting) {
		this.robotSetting = robotSetting;
	}

	public Coordinates getOffset() {
		return offset;
	}

	public void setOffset(Coordinates offset) {
		this.offset = offset;
	}
	
}
