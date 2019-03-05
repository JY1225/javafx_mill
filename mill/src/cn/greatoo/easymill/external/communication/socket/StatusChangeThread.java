package cn.greatoo.easymill.external.communication.socket;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cn.greatoo.easymill.cnc.CNCMachine;
import cn.greatoo.easymill.cnc.CNCMachineAlarm;
import cn.greatoo.easymill.db.util.CNCHandler;
import cn.greatoo.easymill.db.util.RobotHandler;
import cn.greatoo.easymill.robot.FanucRobot;
import cn.greatoo.easymill.robot.RobotAlarm;
import cn.greatoo.easymill.ui.alarms.AlarmListenThread;
import cn.greatoo.easymill.ui.general.NotificationBox;
import cn.greatoo.easymill.ui.main.Controller;
import cn.greatoo.easymill.util.ButtonStyleChangingThread;
import javafx.application.Platform;
import javafx.scene.control.Button;
import cn.greatoo.easymill.util.Translator;

public class StatusChangeThread implements Runnable {

	private FanucRobot robot;
	private CNCMachine cncMachine;
	private double previousXRest, previousYRest, previousZRest;
	private int rpreviousStatus;
	private boolean alive;
	private boolean rwasConnected;
	private boolean cwasConnected;
	private AlarmListenThread alarmListenThread;
	private boolean isRunning = true;
	private Map<Integer, Integer> previousStatus;
	private Set<Integer> previousActiveMCodes;
	private static final String NOT_CONNECTED_TO = "AlarmsPopUpPresenter.notConnectedTo";

	public StatusChangeThread(final Button AlarmButton, final int duration, ButtonStyleChangingThread changingThread) {
		this.alarmListenThread = new AlarmListenThread(AlarmButton, duration, changingThread);
		this.previousStatus = new HashMap<Integer, Integer>();
		this.previousActiveMCodes = new HashSet<Integer>();
		this.alive = true;
		this.rwasConnected = false;
		this.cwasConnected = false;
		connCNC();
		connRobo();
	}

	@Override
	public void run() {
		while (alive) {
			try {
				alarmListenThread.run();
				conn();
				StringBuilder alarmStrings = new StringBuilder();
				if (robot != null) {					
					if (robot.isConnected()) {
						if(TeachAndAutoThread.getView().notificationBox.getLblAlarmMessage().getText().equals(Translator.getTranslation(NOT_CONNECTED_TO) + " " + robot.getName() + "。")) {
							TeachAndAutoThread.getView().hideNotification();
						}
						if (!rwasConnected) {
							robot.restartProgram();
							rwasConnected = true;
						}
						robot.updateStatusRestAndAlarms();
						Set<RobotAlarm> robotAlarm = robot.getAlarms();
						for (RobotAlarm r : robotAlarm) {
							alarmStrings.append(r.getLocalizedMessage() + "。 ");
						}
						int statu = robot.getStatus();
						if (statu != rpreviousStatus) {
							rpreviousStatus = statu;
							robot.statusChanged();
						}
						double xrest = robot.getXRest();
						double yrest = robot.getYRest();
						double zrest = robot.getZRest();
						if (zrest != previousZRest || xrest != previousXRest || yrest != previousYRest) {
							if (xrest > 1 || yrest > 1 || zrest > 1) {
								Platform.runLater(new Runnable() {
									@Override
									public void run() {
										String str = " (移动 (" + xrest + ", " + yrest + ", " + zrest + ")" + ")";
										TeachAndAutoThread.getView().setMessege(Controller.getMessege() + str);
									}
								});
							}
						}
					} else {
						if (rwasConnected) {
							rwasConnected = false;
						}
						alarmStrings.append(Translator.getTranslation(NOT_CONNECTED_TO) + " " + robot.getName() + "。");
					}					
				}

				if (cncMachine != null) {
					if (cncMachine.isConnected()) {
						if(TeachAndAutoThread.getView().notificationBox.getLblAlarmMessage().getText().equals(Translator.getTranslation(NOT_CONNECTED_TO) + " " + cncMachine.getName() + "。")) {
							TeachAndAutoThread.getView().hideNotification();
						}
						
						if (!cwasConnected) {
							cncMachine.indicateOperatorRequested(false);
							cncMachine.indicateOperatorRequested(false);
							cwasConnected = true;
						}
						// 更新状态和MCode
						cncMachine.updateStatusAndAlarms();
						Set<CNCMachineAlarm> cncAlarm = cncMachine.getAlarms();
						for (CNCMachineAlarm c : cncAlarm) {
							alarmStrings.append(c.getLocalizedMessage() + "。 ");
						}
						
						boolean statusChanged = false;
						for (int statusRegister : previousStatus.keySet()) {
							if (cncMachine.getStatus(statusRegister) != previousStatus.get(statusRegister)) {
								statusChanged = true;
							}
						}
						Set<Integer> activeMCodes = new HashSet<Integer>();
						activeMCodes = cncMachine.getMCodeAdapter().getActiveMCodes();
						// 如果状态变或者MCode变通知线程继续进行
						if ((statusChanged) || (!previousActiveMCodes.containsAll(activeMCodes))
								|| (!activeMCodes.containsAll(previousActiveMCodes))) {

							cncMachine.statusChanged();

						}
						this.previousStatus = new HashMap<Integer, Integer>(cncMachine.getStatusMap());
						this.previousActiveMCodes = new HashSet<Integer>(activeMCodes);
					} else {
						if (cwasConnected) {
							cwasConnected = false;
						}
						alarmStrings
								.append(Translator.getTranslation(NOT_CONNECTED_TO) + " " + cncMachine.getName() + "。");
					}					
				}
				if (TeachAndAutoThread.getView() != null && alarmStrings.length() > 0) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							TeachAndAutoThread.getView().showNotification(alarmStrings.toString(),
									NotificationBox.Type.WARNING);								
						}
					});
				}
				Thread.sleep(250);

			} catch (InterruptedException | SocketResponseTimedOutException | SocketDisconnectedException
					| SocketWrongResponseException e) {
				//interrupted();
			}
		}
	}

	private void conn() {

		if (alarmListenThread != null && robot != null && cncMachine != null) {
			boolean isRobotConn = robot.isConnected();
			alarmListenThread.setIsRobotConn(isRobotConn);
			if (!isRobotConn) {
				connRobo();
			}
			boolean isCNCConn = cncMachine.isConnected();
			alarmListenThread.setIsCNCConn(isCNCConn);
			if (!isCNCConn) {
				connCNC();
			}
			if (isCNCConn && isRobotConn) {
				alarmListenThread.setIsRobotConn(isRobotConn);
				alarmListenThread.setIsCNCConn(isCNCConn);
				isRunning = false;
			}
		}
	}

	protected void connCNC() {
		cncMachine = (CNCMachine) CNCHandler.getCNCMillingMachine();
	}

	protected void connRobo() {
		robot = (FanucRobot) RobotHandler.getRobot();
	}

	public void interrupted() {
		alive = false;
	}

}
