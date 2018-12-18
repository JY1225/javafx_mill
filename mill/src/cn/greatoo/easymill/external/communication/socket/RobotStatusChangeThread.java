package cn.greatoo.easymill.external.communication.socket;

import java.util.HashMap;
import java.util.Map;

import cn.greatoo.easymill.robot.FanucRobot;
import cn.greatoo.easymill.ui.teach.TeachMainContentViewController;
import cn.greatoo.easymill.ui.teach.TeachMainViewController;
import javafx.application.Platform;

public class RobotStatusChangeThread implements Runnable {

	FanucRobot roboSocketConnection;
	private int previousStatus;
	private boolean alive;
	private boolean wasConnected;
	private Map<Integer, String> map = new HashMap<Integer, String>();

	public RobotStatusChangeThread(RobotSocketCommunication roboSocketConnection) {
		this.roboSocketConnection = new FanucRobot(roboSocketConnection);
		this.alive = true;
		this.wasConnected = false;
		map.put(0, "");
		map.put(1, "Pick request release");
		map.put(2, "Put request grab");
		map.put(4, "Pick finished");
		map.put(8, "Put finished");
		map.put(16, "Pick out of machine");
		map.put(32, "Put out of machine");
		map.put(64, "Caliber move finished");
		map.put(128, "Docking finished");
		map.put(256, "In jaw change position");
		map.put(512, "Robot moved the bar");
		map.put(1024, "Waiting for teaching");
		map.put(2048, "Teaching finished");
		map.put(2049, "Pick request release,Teaching finished");
		map.put(2050, "Put request grab,Teaching finished");
		map.put(4096, "Waiting for action to be performed");
		map.put(8192, "Move finished");
		map.put(16384, "Präge action finished");
		map.put(32768, "Caliber picked");
	}

	@Override
	public void run() {
		while (alive) {
			try {
				if (roboSocketConnection != null) {
					roboSocketConnection.askStatusRest();
					int statu = roboSocketConnection.getStatus();
					System.out.println("statu = "+ statu);
					if (statu != previousStatus) {
						System.out.println("statu = "+ statu);
						previousStatus = statu;
						// 显示界面
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								TeachMainViewController.teachMainContentViewController.setMessege(map.get(statu));
							}
						});
					}
				}
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
					interrupted();
				}
			} catch (SocketDisconnectedException e) {
				e.printStackTrace();
			} catch (SocketResponseTimedOutException e) {
				e.printStackTrace();
			} catch (SocketWrongResponseException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void interrupted() {
		alive = false;
	}

}
