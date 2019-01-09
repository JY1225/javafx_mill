package cn.greatoo.easymill.entity;

public class Gripper {
	
	public enum Type {
		TWOPOINT, VACUUM
	}
	
	private int id;
	private Type type;
	private String name;
	private String processName;
	private Program.Step step;
	private float height;
	private boolean fixedHeight;
	private String selectGripper;
	private boolean gripperInner;
	private String imageUrl;
	
	public Gripper() {
		
	}
	public Gripper(final String name, String processName, Program.Step step, final Type type, final float height, final String selectGripper, boolean gripperInner, final String imageUrl) {
		this.name = name;
		this.processName = processName;
		this.step = step;
		this.type = type;
		this.height = height;
		this.selectGripper = selectGripper;
		this.imageUrl = imageUrl;
		this.fixedHeight = false;
		this.gripperInner = gripperInner;
	}
	
	public int getId() {
		return id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public String getProcessName() {
		return processName;
	}
	public void setProcessName(String processName) {
		this.processName = processName;
	}
	public Program.Step getStep() {
		return step;
	}
	public void setStep(Program.Step step) {
		this.step = step;
	}
	public String getSelectGripper() {
		return selectGripper;
	}
	public void setSelectGripper(String selectGripper) {
		this.selectGripper = selectGripper;
	}
	public boolean isGripperInner() {
		return gripperInner;
	}
	public void setGripperInner(boolean gripperInner) {
		this.gripperInner = gripperInner;
	}
	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public Type getType() {
		return type;
	}

	public void setType(final Type type) {
		this.type = type;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(final float height) {
		this.height = height;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(final String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public boolean isFixedHeight() {
		return fixedHeight;
	}

	public void setFixedHeight(final boolean fixedHeight) {
		this.fixedHeight = fixedHeight;
	}

}
