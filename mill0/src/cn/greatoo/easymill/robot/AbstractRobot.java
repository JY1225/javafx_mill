package cn.greatoo.easymill.robot;

import java.util.HashSet;
import java.util.Set;

import cn.greatoo.easymill.entity.Coordinates;
import cn.greatoo.easymill.external.communication.socket.AbstractCommunicationException;
import cn.greatoo.easymill.external.communication.socket.RobotSocketCommunication;
import cn.greatoo.easymill.external.communication.socket.SocketConnection;
import cn.greatoo.easymill.process.ProcessExecutor;

public abstract class AbstractRobot {
	private static final String EXCEPTION_DISCONNECTED_WHILE_WAITING = "AbstractRobot.disconnectedWhileWaiting";
	private  SocketConnection socketConnection;
	private  Set<RobotAlarm> alarms;
	private  double xrest, yrest, zrest;
	private  RobotAlarm robotTimeout;
	private  int speed;
	private  boolean stopAction;
	private  static Object syncObject;
	private  static int currentStatus;
	private  boolean statusChanged;
	private  boolean teachingNeeded;
	private  int id;
	private  String name;
	private  float payload; 
	private boolean running;
		
	public AbstractRobot(String name,float payload,final SocketConnection socketConnection) {
		this.socketConnection = socketConnection;
		this.name = name;
		this.payload = payload;
		alarms = new HashSet<RobotAlarm>();
		currentStatus = 0;
		xrest = -1;
		yrest = -1;
		zrest = -1;
		speed = 10;
		stopAction = false;
		statusChanged = false;
		teachingNeeded = false;
		syncObject = new Object();
		running = true;
	}
//	public GripperBody getGripperBody() {
//		final Set<GripperHead> gripperHeads = new HashSet<GripperHead>();
//		GripperBody activeGripperBody = new GripperBody("name", "description", gripperHeads);
//		return activeGripperBody;
//	}
	
	public abstract void updateStatusRestAndAlarms() throws AbstractCommunicationException, InterruptedException;
	public abstract void restartProgram() throws AbstractCommunicationException, InterruptedException;
	public abstract void reset() throws AbstractCommunicationException, InterruptedException;
	public abstract Coordinates getPosition() throws AbstractCommunicationException, InterruptedException;
	public abstract void sendSpeed(int speedPercentage) throws AbstractCommunicationException, InterruptedException;
	public abstract void writeRegister(int registerNr, int value) throws AbstractCommunicationException,  InterruptedException;
	public abstract void continueProgram() throws AbstractCommunicationException, InterruptedException;
	public abstract void abort() throws AbstractCommunicationException, InterruptedException;
	public abstract void recalculateTCPs() throws AbstractCommunicationException, InterruptedException;	
	public abstract boolean isConnected();
	public abstract void disconnect();
	public abstract void moveToHome() throws AbstractCommunicationException,  InterruptedException;
	public abstract void moveToChangePoint() throws AbstractCommunicationException,  InterruptedException;
	public abstract void moveToCustomPosition() throws AbstractCommunicationException,  InterruptedException;
	public abstract void enableMovement(boolean flag) throws AbstractCommunicationException,  InterruptedException;
	public abstract void continuePutTillAtLocation(boolean isTeachingNeeded) throws AbstractCommunicationException,  InterruptedException, RobotActionException;
	public abstract void continuePutTillClampAck(boolean isTeachingNeeded) throws AbstractCommunicationException,  InterruptedException, RobotActionException;
	public abstract void continuePutTillIPPoint() throws AbstractCommunicationException,  InterruptedException, RobotActionException;
	public abstract void finalizePut() throws AbstractCommunicationException,  InterruptedException;
	
	public abstract void continuePickTillAtLocation(boolean isTeachingNeeded) throws AbstractCommunicationException,  InterruptedException, RobotActionException;
	public abstract void continuePickTillUnclampAck(boolean isTeachingNeeded) throws AbstractCommunicationException,  InterruptedException, RobotActionException;
	public abstract void continuePickTillIPPoint() throws AbstractCommunicationException,  InterruptedException, RobotActionException;
	public abstract void finalizePick() throws AbstractCommunicationException,  InterruptedException;	
	public abstract void continueMoveTillAtLocation() throws AbstractCommunicationException,  InterruptedException;
	public abstract void continueMoveTillWait() throws AbstractCommunicationException,  InterruptedException;
	public abstract void performIOAction() throws AbstractCommunicationException,  InterruptedException;
	public abstract void continueMoveWithPieceTillIPPoint() throws AbstractCommunicationException,  InterruptedException;
	public abstract void continueMoveWithoutPieceTillIPPoint() throws AbstractCommunicationException,  InterruptedException;
	public abstract void finalizeMovePiece() throws AbstractCommunicationException,  InterruptedException;
	
	public void interruptCurrentAction() {
		setRobotTimeout(null);
		stopAction = true;
		try {
			abort();
		} catch (AbstractCommunicationException | InterruptedException e) {
			if (isConnected()) {
				e.printStackTrace();
			}
		}
		synchronized (syncObject) {
			syncObject.notifyAll();
		}
		setRunning(false);
	}
	public void checkProcessExecutorStatus() throws InterruptedException {
		if (isRunning()) {
			return;
		} else {
			throw new InterruptedException("Executor stopped running.");
		}
	}
	
	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public int getStatus() {
		return currentStatus;
	}
	
	public void setStatus(final int status) {
		currentStatus = status;
	}
	
	public double getXRest() {
		return xrest;
	}
	
	public double getYRest() {
		return yrest;
	}
	
	public double getZRest() {
		return zrest;
	}
	
	public void setRestValues(final double xres, final double yres, final double zres) {
		xrest = xres;
		yrest = yres;
		zrest = zres;
	}
	
	public void setSpeed(final int speedPercentage) throws AbstractCommunicationException, InterruptedException {
		if ((speedPercentage < 0) || (speedPercentage > 100) || !((speedPercentage == 5) || (speedPercentage == 10) || (speedPercentage == 25) || (speedPercentage == 50) || (speedPercentage == 75) || (speedPercentage == 100))) {
			throw new IllegalArgumentException("Illegal speed value: " + speedPercentage + ", should be between 0 and 100");
		}
		speed = speedPercentage;
		sendSpeed(speedPercentage);
	}
	
	public int getSpeed() {
		return speed;
	}
	
	public Set<RobotAlarm> getAlarms() {
		return alarms;
	}
	
	public void setAlarms(final Set<RobotAlarm> alarm) {
		alarms = alarm;
	}
	public void setRobotTimeout(final RobotAlarm robotTimeou) {
		robotTimeout = robotTimeou;
	}
	
	public RobotAlarm getRobotTimeout() {
		return robotTimeout;
	}
	public boolean isTeachingNeeded() {
		return teachingNeeded;
	}
	
	public void setTeachingNeeded(final boolean teachingNeede) {
		teachingNeeded = teachingNeede;
	}
	protected boolean waitForStatus(final int status, final long timeout) throws RobotActionException, InterruptedException {
		long waitedTime = 0;
		stopAction = false;
		// check status before we start
		if ((currentStatus & status) > 0) {
			return true;
		}
		// also check connection status
		if (!isConnected()) {
			throw new RobotActionException(this, EXCEPTION_DISCONNECTED_WHILE_WAITING);
		}
		while ((timeout == 0) || ((timeout > 0) && (waitedTime < timeout))) {
			// start waiting
			statusChanged = false;
			if ((timeout == 0) || ((timeout > 0) && (timeout > waitedTime))) {
				long timeBeforeWait = System.currentTimeMillis();
				synchronized (syncObject) {
					if ((currentStatus & status) > 0) {
						return true;
					}
					if (timeout > 0) {
						syncObject.wait(timeout - waitedTime);
					} else {
						syncObject.wait();
					}
				}
				// at this point the wait is finished, either by a notify (status changed, or request to stop), or by a timeout
				if (stopAction) {
					stopAction = false;
					throw new InterruptedException("Waiting for status: " + status + " got interrupted");
				}
				// just to be sure, check connection
				if (!isConnected()) {
					throw new RobotActionException(this, EXCEPTION_DISCONNECTED_WHILE_WAITING);
				}
				// check if status has changed
				if ((statusChanged) && ((currentStatus & status) > 0)) {
					statusChanged = false;
					return true;
				}
				// update waited time
				waitedTime += System.currentTimeMillis() - timeBeforeWait;
			} else {
				return false;
			}
		} 
		return false;
	}
	
	protected void waitForStatus(final int status) throws RobotActionException, InterruptedException {
		waitForStatus(status, 0);
	}
	
	public void statusChanged() {
		synchronized (syncObject) {
			statusChanged = true;
			syncObject.notifyAll();
		}
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public SocketConnection getSocketConnection() {
		return socketConnection;
	}
	public void setSocketConnection(SocketConnection socketConnection) {
		this.socketConnection = socketConnection;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public float getPayload() {
		return payload;
	}
	public void setPayload(float payload) {
		this.payload = payload;
	}
	
}
