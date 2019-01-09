package cn.greatoo.easymill.entity;

public class RobotPutSetting {

	private int id;
	private boolean releaseBeforeMachine;
	
	public RobotPutSetting() {
		
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
