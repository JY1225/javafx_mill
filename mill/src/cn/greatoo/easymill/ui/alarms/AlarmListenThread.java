package cn.greatoo.easymill.ui.alarms;

import java.io.IOException;

import cn.greatoo.easymill.external.communication.socket.CNCSocketCommunication;
import cn.greatoo.easymill.external.communication.socket.RobotSocketCommunication;
import cn.greatoo.easymill.external.communication.socket.RobotStatusChangeThread;
import cn.greatoo.easymill.external.communication.socket.SocketConnection;
import cn.greatoo.easymill.robot.FanucRobot;
import cn.greatoo.easymill.util.ButtonStyleChangingThread;
import cn.greatoo.easymill.util.ThreadManager;
import javafx.application.Platform;
import javafx.scene.control.Button;

public class AlarmListenThread implements Runnable {
	private static final String CSS_CLASS_HEADER_BUTTON = "header-button";
	private static final String CSS_CLASS_ALARMS_PRESENT = "alarms-present";
	public static ButtonStyleChangingThread changingThread;
	private int duration;

	private boolean isAlive;

	private Object syncObject;

	public static boolean isCNCConn = false;
	public static boolean isRobotConn = false;
	private Button alarm;
	public static CNCSocketCommunication cncSocketConnection;
	public static RobotSocketCommunication roboSocketConnection;
	private SocketConnection robotSocket;
	SocketConnection cncSocket;
	private boolean robocnn;
	private boolean cnccnn;
	private RobotStatusChangeThread robotStatusChangeThread;
	public AlarmListenThread(final Button AlarmButton, final int duration, ButtonStyleChangingThread changingThread) {
		this.alarm = AlarmButton;
		this.duration = duration;
		this.isAlive = true;
		this.changingThread = changingThread;
		conn();
		robocnn = !isRobotConn;
		cnccnn = !isCNCConn;
		
		//监听机器人状态
		robotStatusChangeThread = new RobotStatusChangeThread(roboSocketConnection);
		ThreadManager.submit(robotStatusChangeThread);
	}

	@Override
	public void run() {
		while (isAlive) {
			Platform.runLater(new Thread() {
				@Override
				public void run() {
					if (robocnn != isRobotConn) {
						AlarmView.getInstance().isRoboConn(isRobotConn);
						robocnn = isRobotConn;
					}					
				}

			});
			Platform.runLater(new Thread() {
				@Override
				public void run() {
					if (cnccnn != isCNCConn) {
						AlarmView.getInstance().isCNCConn(isCNCConn);
						cnccnn = isCNCConn;
					}
				}

			});
			Platform.runLater(new Thread() {
				@Override
				public void run() {
					if (isCNCConn && isRobotConn) {
						AlarmView.getInstance().isRoboConn(isRobotConn);
						AlarmView.getInstance().isCNCConn(isCNCConn);
						changingThread.setRunning(false);
						alarm.getStyleClass().clear();
						alarm.getStyleClass().add(CSS_CLASS_HEADER_BUTTON);
						isAlive = false;
					}			
				}
			});
			
			try {
				Thread.sleep(duration);
			} catch (InterruptedException e) {
				interrupted();
			}
		}
	}

	private void conn() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				connCNC("127.0.0.1",2010);
				connRobo("127.0.0.1",2001);
				while (true) {
					isRobotConn = robotSocket.isConnected();
					if (!isRobotConn) {
						connRobo("127.0.0.1",2001);
					}
					isCNCConn = cncSocket.isConnected();
					if (!isCNCConn) {
						connCNC("127.0.0.1",2000);
					}
					try {
						Thread.sleep(1000);
					} catch (Exception e) {

					}
				}
			}
		}).start();
	}

	public void interrupted() {
		this.isAlive = false;
		synchronized (syncObject) {
			syncObject.notify();
		}
	}

	public void indicateAlarmsPresent(final boolean alarmsPresent) {
		if (alarmsPresent) {
			changingThread.setRunning(true);
		} else {
			changingThread.setRunning(false);
			alarm.getStyleClass().remove(CSS_CLASS_ALARMS_PRESENT);
		}
	}

	protected boolean connCNC(String ip,int port) {
		try {
			cncSocket = new SocketConnection(SocketConnection.Type.CLIENT, "ROBO_CONN_THREAD", ip, port);
			cncSocket.connect();
			cncSocketConnection = new CNCSocketCommunication(cncSocket);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	protected boolean connRobo(String ip,int port) {
		try {
			robotSocket = new SocketConnection(SocketConnection.Type.CLIENT, "ROBO_CONN_THREAD", ip, port);
			robotSocket.connect();
			roboSocketConnection = new RobotSocketCommunication(robotSocket);	
			return true;
		} catch (IOException e) {
			return false;
		}
	}
}
