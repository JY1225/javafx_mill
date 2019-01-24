package cn.greatoo.easymill.external.communication.socket;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cn.greatoo.easymill.cnc.CNCMachine;
import cn.greatoo.easymill.db.util.CNCHandler;
import cn.greatoo.easymill.db.util.RobotHandler;
import cn.greatoo.easymill.robot.FanucRobot;
import cn.greatoo.easymill.ui.alarms.AlarmListenThread;
import cn.greatoo.easymill.ui.main.Controller;
import cn.greatoo.easymill.util.ButtonStyleChangingThread;
import cn.greatoo.easymill.util.ThreadManager;
import javafx.application.Platform;
import javafx.scene.control.Button;

public class StatusChangeThread implements Runnable {

	private FanucRobot robot;
	private CNCMachine cncMachine;
	private double previousXRest,previousYRest,previousZRest;
	private int rpreviousStatus;
	private boolean alive;
	private AlarmListenThread alarmListenThread;
	private boolean isRunning = true;
	private Map<Integer, Integer> previousStatus;
	private Set<Integer> previousActiveMCodes;

	public StatusChangeThread(final Button AlarmButton, final int duration, ButtonStyleChangingThread changingThread) {
		this.alarmListenThread = new AlarmListenThread(AlarmButton, duration, changingThread);	
		alarmListenThread.run();
		this.previousStatus = new HashMap<Integer, Integer>();
		this.previousActiveMCodes = new HashSet<Integer>();
		this.alive = true;		
		connCNC();
		connRobo();
		conn();
	}

	@Override
	public void run() {
		while (alive) {
			try {	
				alarmListenThread.run();
				if (robot != null) {
					robot.updateStatusRestAndAlarms();
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
								@Override public void run() {
									String str = " (移动 (" + xrest + ", " + yrest + ", "+ zrest + ")"+ ")";
									TeachAndAutoThread.getView().setMessege(Controller.getMessege()+str);							
									//System.out.println("xrest: "+xrest+"; yrest: "+yrest+"; zrest: "+zrest);
								} 
							});
						}					
					} 					
				}

				if (cncMachine != null) {
					//更新状态和MCode
					cncMachine.updateStatusAndAlarms();
					boolean statusChanged = false;
					// cncMachine.statusChanged();
					for (int statusRegister : previousStatus.keySet()) {
						if (cncMachine.getStatus(statusRegister) != previousStatus.get(statusRegister)) {
							statusChanged = true;
						}
					}
					Set<Integer> activeMCodes = new HashSet<Integer>();
					activeMCodes = cncMachine.getMCodeAdapter().getActiveMCodes();
					//如果状态变或者MCode变通知线程继续进行
					if ((statusChanged) || (!previousActiveMCodes.containsAll(activeMCodes))
							|| (!activeMCodes.containsAll(previousActiveMCodes))) {

						cncMachine.statusChanged();
					}
					this.previousStatus = new HashMap<Integer, Integer>(cncMachine.getStatusMap());
					this.previousActiveMCodes = new HashSet<Integer>(activeMCodes);
				}

				Thread.sleep(250);

			} catch (InterruptedException | SocketResponseTimedOutException | SocketDisconnectedException | SocketWrongResponseException e) {
				interrupted();
			} 
		}
	}

	private void conn() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (isRunning) {
					if (alarmListenThread != null && robot != null && cncMachine != null) {
						boolean isRobotConn = robot.isConnected();
						alarmListenThread.setIsRobotConn(isRobotConn);
						if (!isRobotConn) {
							connRobo();
						}
						boolean isCNCConn = cncMachine.isConnected();
						alarmListenThread.setIsCNCConn(isCNCConn);
						if (!isCNCConn) {
							isCNCConn = connCNC();
						}
						if (isCNCConn && isRobotConn) {
							alarmListenThread.setIsRobotConn(isRobotConn);
							alarmListenThread.setIsCNCConn(isCNCConn);
							isRunning = false;
						}
					}
					try {
						Thread.sleep(500);
					} catch (Exception e) {

					}
				}
			}
		}).start();
	}

	protected boolean connCNC() {
		try {
			cncMachine = (CNCMachine) CNCHandler.getCNCMillingMachine();
			if(cncMachine != null) {
				cncMachine.indicateOperatorRequested(false);
				cncMachine.indicateOperatorRequested(false);
			}
			return true;
		} catch (SocketResponseTimedOutException | SocketDisconnectedException
				| SocketWrongResponseException | InterruptedException e) {
			return false;
		}
	}

	protected boolean connRobo() {
		try {
			robot = (FanucRobot)RobotHandler.getRobot();
			if(robot != null) {
				robot.restartProgram();
			}
			return true;
		} catch (SocketDisconnectedException | SocketResponseTimedOutException
				| SocketWrongResponseException | InterruptedException e) {
			return false;
		}
	}

	public void interrupted() {
		alive = false;
	}

}
