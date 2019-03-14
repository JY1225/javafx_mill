package cn.greatoo.easymill.entity;

public class RobotSetting {

	private int id;
	private boolean releaseBeforeMachine;
		
	
	public RobotSetting(boolean releaseBeforeMachine) {
		this.releaseBeforeMachine =releaseBeforeMachine;
	}
	
	public RobotSetting() {
		// TODO Auto-generated constructor stub
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public boolean isReleaseBeforeMachine() {
		return releaseBeforeMachine;
	}

	public void setReleaseBeforeMachine(boolean releaseBeforeMachine) {
		this.releaseBeforeMachine = releaseBeforeMachine;
	}
	
	
}
