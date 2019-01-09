package cn.greatoo.easymill.entity;

public class RobotPutSetting {

	private int id;
	private String processName;
	private boolean releaseBeforeMachine;
	
	public RobotPutSetting() {
		
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


	public boolean isReleaseBeforeMachine() {
		return releaseBeforeMachine;
	}

	public void setReleaseBeforeMachine(boolean releaseBeforeMachine) {
		this.releaseBeforeMachine = releaseBeforeMachine;
	}
	
	
}
