package cn.greatoo.easymill.entity;

public class UserFrame {

	private int id; 
	private String processName;
	private Program.Step step;
	private String name;
	private int number;
	private float zSafeDistance;
	private Coordinates location;
	
	public UserFrame(String processName, Program.Step step, final int number, final String name, final float zSafeDistance, final Coordinates location) {
		this.processName = processName;
		this.step = step;
		this.number = number;
		this.name = name;
		this.zSafeDistance = zSafeDistance;
		this.location = location;
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

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(final int number) {
		this.number = number;
	}

	public float getzSafeDistance() {
		return zSafeDistance;
	}

	public void setzSafeDistance(final float zSafeDistance) {
		this.zSafeDistance = zSafeDistance;
	}

	public Coordinates getLocation() {
		return location;
	}

	public void setLocation(final Coordinates location) {
		this.location = location;
	}

}
