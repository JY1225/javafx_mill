package cn.greatoo.easymill.entity;

import cn.greatoo.easymill.entity.Clamping.ClampingType;

public class Step implements Cloneable {

	private int id;
	private Gripper gripper;
	private WorkPiece workPiece;
	private int userFrame;
	private Smooth smooth;
	private Coordinates offset;
	private GripperHead gripperHead;

	public Step(GripperHead gripperHead,Gripper gripper, WorkPiece workPiece, int userFrame,
		Smooth smooth, Coordinates offset) {
		this.gripperHead = gripperHead;
		this.gripper = gripper;
		this.workPiece = workPiece;
		this.userFrame = userFrame;
		this.smooth = smooth;
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

	public int getUserFrame() {
		return userFrame;
	}

	public void setUserFrame(int userFrame) {
		this.userFrame = userFrame;
	}

	public Smooth getSmooth() {
		return smooth;
	}

	public void setSmooth(Smooth smooth) {
		this.smooth = smooth;
	}

	public Coordinates getOffset() {
		return offset;
	}

	public void setOffset(Coordinates offset) {
		this.offset = offset;
	}

	public GripperHead getGripperHead() {
		return gripperHead;
	}

	public void setGripperHead(GripperHead gripperHead) {
		this.gripperHead = gripperHead;
	}
	
}
