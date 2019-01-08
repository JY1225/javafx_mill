package cn.greatoo.easymill.entity;

public class Process {			

	private int id;	
	private String name;
	private Step step;
	
	public Process(String name) {
		this.name = name;
	}
	public enum Step {
		UNLOADSTACKER, LOADCNC, UNLOADCNC, LOADSTACKER;
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

	public Step getStep() {
		return step;
	}

	public void setStep(Step step) {
		this.step = step;
	}
	
	
}
