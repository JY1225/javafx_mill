package cn.greatoo.easymill.ui.alarms;

import java.io.IOException;

import cn.greatoo.easymill.external.communication.socket.SocketConnection;
import cn.greatoo.easymill.util.ButtonStyleChangingThread;
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
	private SocketConnection cncSocketConnection;
	private SocketConnection roboSocketConnection;
	private boolean robocnn;
	private boolean cnccnn;

	public AlarmListenThread(final Button AlarmButton, final int duration, ButtonStyleChangingThread changingThread) {
		this.alarm = AlarmButton;
		this.duration = duration;
		this.isAlive = true;
		this.changingThread = changingThread;
		conn();
		robocnn = !isRobotConn;
		cnccnn = !isCNCConn;
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
						changingThread.setRunning(!isCNCConn);
						cnccnn = isCNCConn;
					}
				}

			});
			if (isCNCConn && isRobotConn) {
				changingThread.setRunning(false);
				changingThread.setRunning(isCNCConn);
				alarm.getStyleClass().clear();
				alarm.getStyleClass().add(CSS_CLASS_HEADER_BUTTON);
			}
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
				connCNC();
				connRobo();
				while (true) {
					isRobotConn = roboSocketConnection.isConnected();
					//System.out.println("isRobotConn = " + isRobotConn);
					if (!isRobotConn) {
						connRobo();
					}
					isCNCConn = cncSocketConnection.isConnected();
					//System.out.println("isCNCConn = " + isCNCConn);
					if (!isCNCConn) {
						connCNC();
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

	protected boolean connCNC() {
		String ip = "127.0.0.1";
		int port = 2000;
		try {
			cncSocketConnection = new SocketConnection(SocketConnection.Type.CLIENT, "CNC_CONN_THREAD", ip, port);
			cncSocketConnection.connect();
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	protected boolean connRobo() {
		String ip = "127.0.0.1";
		int port = 2001;
		try {
			roboSocketConnection = new SocketConnection(SocketConnection.Type.CLIENT, "ROBO_CONN_THREAD", ip, port);
			roboSocketConnection.connect();
			return true;
		} catch (IOException e) {
			return false;
		}
	}
}
