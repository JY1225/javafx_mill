package cn.greatoo.easymill.entity;

import cn.greatoo.easymill.external.communication.socket.SocketConnection;

public class RobotSetting {

	private int id;
	private boolean releaseBeforeMachine;
	private SocketConnection socketConnection;
	private float Playload;
		
	
	public RobotSetting(boolean releaseBeforeMachine, SocketConnection socketConnection, float Playload) {
		this.releaseBeforeMachine =releaseBeforeMachine;
		this.socketConnection = socketConnection;
		this.Playload = Playload;
	}
	
	public RobotSetting() {
		// TODO Auto-generated constructor stub
	}

	public int getId() {
		return id;
	}


	public SocketConnection getSocketConnection() {
		return socketConnection;
	}

	public void setSocketConnection(SocketConnection socketConnection) {
		this.socketConnection = socketConnection;
	}

	public float getPlayload() {
		return Playload;
	}

	public void setPlayload(float playload) {
		Playload = playload;
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
