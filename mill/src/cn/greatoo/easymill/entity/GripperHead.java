package cn.greatoo.easymill.entity;

public class GripperHead {
	private int id;
	private String name;
	private boolean gripperInner;
	
	public GripperHead(String name, boolean gripperInner) {
		this.name = name;
		this.gripperInner = gripperInner;
	}

	public GripperHead() {
		// TODO Auto-generated constructor stub
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isGripperInner() {
		return gripperInner;
	}

	public void setGripperInner(boolean gripperInner) {
		this.gripperInner = gripperInner;
	}
	
}
