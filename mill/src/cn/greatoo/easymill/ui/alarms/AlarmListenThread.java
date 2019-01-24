package cn.greatoo.easymill.ui.alarms;

import cn.greatoo.easymill.util.ButtonStyleChangingThread;
import javafx.application.Platform;
import javafx.scene.control.Button;

public class AlarmListenThread {
	private static final String CSS_CLASS_HEADER_BUTTON = "header-button";
	private static final String CSS_CLASS_ALARMS_PRESENT = "alarms-present";
	public ButtonStyleChangingThread changingThread;

	public  boolean isCNCConn = false;
	public  boolean isRobotConn = false;
	private Button alarm;


	public AlarmListenThread(final Button AlarmButton, final int duration, ButtonStyleChangingThread changingThread) {
		this.alarm = AlarmButton;
		this.changingThread = changingThread;

	}

	public void run() {
		Platform.runLater(new Thread() {
			@Override
			public void run() {

				AlarmView.getInstance().isRoboConn(isRobotConn);
				AlarmView.getInstance().isCNCConn(isCNCConn);
				if (isCNCConn && isRobotConn) {
					changingThread.setRunning(false);
					alarm.getStyleClass().clear();
					alarm.getStyleClass().add(CSS_CLASS_HEADER_BUTTON);
				}
			}

		});		
	}

	public void setIsRobotConn(boolean isRobotCon) {
		isRobotConn = isRobotCon;
	}

	public boolean getIsRobotConn() {
		return isRobotConn;
	}

	public void setIsCNCConn(boolean isCNCCon) {
		isCNCConn = isCNCCon;
	}

	public boolean getIsCNCConn() {
		return isCNCConn;
	}

	public void indicateAlarmsPresent(final boolean alarmsPresent) {
		if (alarmsPresent) {
			changingThread.setRunning(true);
		} else {
			changingThread.setRunning(false);
			alarm.getStyleClass().remove(CSS_CLASS_ALARMS_PRESENT);
		}
	}
}
