package cn.greatoo.easymill.entity;

public class Smooth {

	private int id;
	private String processName;
	private Program.Step step;
	private float x;
	private float y;
	private float z;
	
	public Smooth(String processName, Program.Step step, float x, float y, float z) {
		this.processName = processName;
		this.step = step;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Smooth() {
		// TODO Auto-generated constructor stub
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

	public Program.Step getStep() {
		return step;
	}

	public void setStep(Program.Step step) {
		this.step = step;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
	}
	
}
