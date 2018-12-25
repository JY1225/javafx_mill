package cn.greatoo.easymill.external.communication.socket;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cn.greatoo.easymill.cnc.CNCMachine;
import cn.greatoo.easymill.db.util.DBHandler;
import cn.greatoo.easymill.process.StatusChangedEvent;
import cn.greatoo.easymill.process.StatusChangedEvent.Mode;
import cn.greatoo.easymill.robot.FanucRobot;
import cn.greatoo.easymill.ui.alarms.AlarmListenThread;
import cn.greatoo.easymill.ui.teach.TeachMainViewController;
import cn.greatoo.easymill.util.ButtonStyleChangingThread;
import javafx.application.Platform;
import javafx.scene.control.Button;

public class RobotStatusChangeThread implements Runnable {

	private FanucRobot robot;
	private CNCMachine cncMachine;
	public static CNCSocketCommunication cncSocketConnection;
	public static RobotSocketCommunication roboSocketConnection;
	private int rpreviousStatus;
	private int cpreviousStatus;
	private boolean alive;
	private AlarmListenThread alarmListenThread;
	private static SocketConnection robotSocket;
	private static SocketConnection cncSocket;
	private boolean isRunning = true;
	private Map<Integer, Integer> previousStatus;
	private Set<Integer> previousActiveMCodes;
	
	public RobotStatusChangeThread(final Button AlarmButton,
			final int duration, ButtonStyleChangingThread changingThread) {						
		this.alarmListenThread = new AlarmListenThread(AlarmButton,duration,changingThread);
		this.previousStatus = new HashMap<Integer, Integer>();
		this.previousActiveMCodes = new HashSet<Integer>();
		this.alive = true;	
		connCNC("127.0.0.1",2010);
		connRobo("127.0.0.1",2001);
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
					//System.out.println("statu = "+ statu);
					if (statu != rpreviousStatus) {						
						rpreviousStatus = statu;						
						robot.statusChanged();						
					}
					double xrest = robot.getXRest();
					double yrest = robot.getYRest();
					double zrest = robot.getZRest();
					//System.out.println("xrest: "+xrest+"; yrest: "+yrest+"; zrest: "+zrest);
				}
				
				
				if (cncMachine != null) {					
					cncMachine.updateStatusAndAlarms();	
					boolean statusChanged = false;
					//cncMachine.statusChanged();	
					for(int statusRegister: previousStatus.keySet()) {
						if (cncMachine.getStatus(statusRegister) != previousStatus.get(statusRegister)) {
							statusChanged = true;
						}
					}
					Set<Integer> activeMCodes = new HashSet<Integer>();
					activeMCodes = cncMachine.getMCodeAdapter().getActiveMCodes();
					if ((statusChanged) || (!previousActiveMCodes.containsAll(activeMCodes)) 
							|| (!activeMCodes.containsAll(previousActiveMCodes))) {

						cncMachine.statusChanged();	
					}
					this.previousStatus = new HashMap<Integer, Integer>(cncMachine.getStatusMap());
					this.previousActiveMCodes = new HashSet<Integer>(activeMCodes);
			}		
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {					
					interrupted();
				}
			} catch (InterruptedException e) {
				System.out.println("statu = ");
				e.printStackTrace();
			}catch (AbstractCommunicationException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void conn() {	
		new Thread(new Runnable() {
			@Override
			public void run() {								
				while (isRunning) {
					if(alarmListenThread != null) {
					boolean isRobotConn = robotSocket.isConnected();					
					alarmListenThread.setIsRobotConn(isRobotConn);
					if (!isRobotConn) {
						connRobo("127.0.0.1",2001);
					}
					boolean isCNCConn = cncSocket.isConnected();
					alarmListenThread.setIsCNCConn(isCNCConn);
					if (!isCNCConn) {
						isCNCConn = connCNC("127.0.0.1",2000);
					}
					if(isCNCConn && isRobotConn) {
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
	protected boolean connCNC(String ip,int port) {
		try {
			cncSocket = new SocketConnection(SocketConnection.Type.CLIENT, "CNC_CONN_THREAD", ip, port);
			cncSocket.connect();
			cncSocketConnection = new CNCSocketCommunication(cncSocket);
			cncMachine = (CNCMachine) DBHandler.getInstance().getCNCMillingMachine(1,cncSocketConnection);
			return true;
		} catch (IOException e) {
			cncSocket.disconnect();
			return false;
		}
	}

	protected boolean connRobo(String ip,int port) {
		try {
			robotSocket = new SocketConnection(SocketConnection.Type.CLIENT, "ROBO_CONN_THREAD", ip, port);
			robotSocket.connect();
			roboSocketConnection = new RobotSocketCommunication(robotSocket);	
			robot = FanucRobot.getInstance(roboSocketConnection);
			return true;
		} catch (IOException e) {
			robotSocket.disconnect();
			return false;
		}
	}
	
	
	public void interrupted() {
		alive = false;
	}

}
