package cn.greatoo.easymill.robot;

import java.util.HashSet;
import java.util.Set;

import cn.greatoo.easymill.entity.GripperBody;
import cn.greatoo.easymill.entity.GripperHead;
import cn.greatoo.easymill.external.communication.socket.AbstractCommunicationException;
import cn.greatoo.easymill.external.communication.socket.RobotSocketCommunication;
import cn.greatoo.easymill.util.Coordinates;

public abstract class AbstractRobot {
	private RobotSocketCommunication fanucRobotCommunication;
	private Set<RobotAlarm> alarms;
	private int currentStatus;
	private double xrest, yrest, zrest;
	private RobotAlarm robotTimeout;
	private int speed;
	public AbstractRobot(final RobotSocketCommunication socketConnection) {
		this.fanucRobotCommunication = socketConnection;
		this.alarms = new HashSet<RobotAlarm>();
		this.currentStatus = 0;
		this.xrest = -1;
		this.yrest = -1;
		this.zrest = -1;
		this.speed = 10;
	}
	public GripperBody getGripperBody() {
		final Set<GripperHead> gripperHeads = new HashSet<GripperHead>();
		GripperBody activeGripperBody = new GripperBody("name", "description", gripperHeads);
		return activeGripperBody;
	}
	
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
	
	//public abstract void initiatePut(RobotPutSettings putSettings, Clamping clamping) throws AbstractCommunicationException,  InterruptedException;
	public abstract void continuePutTillAtLocation() throws AbstractCommunicationException,  InterruptedException;
	public abstract void continuePutTillClampAck() throws AbstractCommunicationException,  InterruptedException;
	public abstract void continuePutTillIPPoint() throws AbstractCommunicationException,  InterruptedException;
	public abstract void finalizePut() throws AbstractCommunicationException,  InterruptedException;
	
	//public abstract void initiatePick(RobotPickSettings pickSettings, Clamping clamping) throws AbstractCommunicationException,  InterruptedException;
	public abstract void continuePickTillAtLocation() throws AbstractCommunicationException,  InterruptedException;
	public abstract void continuePickTillUnclampAck() throws AbstractCommunicationException,  InterruptedException;
	public abstract void continuePickTillIPPoint() throws AbstractCommunicationException,  InterruptedException;
	public abstract void finalizePick() throws AbstractCommunicationException,  InterruptedException;
	
	//public abstract void initiateMoveWithPiece(RobotPutSettings putSettings) throws AbstractCommunicationException,  InterruptedException;
	//public abstract void initiateMoveWithPieceNoAction(RobotPutSettings putSettings) throws AbstractCommunicationException,  InterruptedException;
	//public abstract void initiateMoveWithoutPieceNoAction(final RobotPutSettings putSettings) throws AbstractCommunicationException,  InterruptedException;
	public abstract void continueMoveTillAtLocation() throws AbstractCommunicationException,  InterruptedException;
	public abstract void continueMoveTillWait() throws AbstractCommunicationException,  InterruptedException;
	public abstract void performIOAction() throws AbstractCommunicationException,  InterruptedException;
	public abstract void continueMoveWithPieceTillIPPoint() throws AbstractCommunicationException,  InterruptedException;
	public abstract void continueMoveWithoutPieceTillIPPoint() throws AbstractCommunicationException,  InterruptedException;
	public abstract void finalizeMovePiece() throws AbstractCommunicationException,  InterruptedException;
	
//	public abstract void writeUserFrame(final RobotUserFrame userframe, final RobotPosition position) throws AbstractCommunicationException,  InterruptedException;
//	public abstract void readUserFrame(final RobotUserFrame userframe) throws AbstractCommunicationException,  InterruptedException;
//	public abstract void writeIPPoint(final RobotIPPoint ipPoint, final RobotPosition position) throws  AbstractCommunicationException,  InterruptedException;
//	public abstract void readIPPoint(final RobotIPPoint ipPoint) throws AbstractCommunicationException,  InterruptedException;
//	public abstract void writeRPPoint(final RobotRefPoint rpPoint, final RobotPosition position) throws  AbstractCommunicationException,  InterruptedException;
//	public abstract void readRPPoint(final RobotRefPoint rpPoint) throws AbstractCommunicationException,  InterruptedException;
//	public abstract void writeSpecialPoint(final RobotSpecialPoint specialPoint, final RobotPosition position) throws  AbstractCommunicationException,  InterruptedException;
//	public abstract void readSpecialPoint(final RobotSpecialPoint specialPoint) throws AbstractCommunicationException,  InterruptedException;
//	public abstract void writeToolFrame(final RobotToolFrame toolFrame, final RobotPosition position) throws AbstractCommunicationException,  InterruptedException;
//    public abstract void readToolFrame(final RobotToolFrame toolFrame) throws AbstractCommunicationException,  InterruptedException;
//    public abstract void readRegister(final RobotRegister register) throws AbstractCommunicationException,  InterruptedException;

	public int getStatus() {
		return currentStatus;
	}
	
	public void setStatus(final int status) {
		this.currentStatus = status;
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
	
	public void setRestValues(final double xrest, final double yrest, final double zrest) {
		this.xrest = xrest;
		this.yrest = yrest;
		this.zrest = zrest;
	}
	
	public void setSpeed(final int speedPercentage) throws AbstractCommunicationException, InterruptedException {
		if ((speedPercentage < 0) || (speedPercentage > 100) || !((speedPercentage == 5) || (speedPercentage == 10) || (speedPercentage == 25) || (speedPercentage == 50) || (speedPercentage == 75) || (speedPercentage == 100))) {
			throw new IllegalArgumentException("Illegal speed value: " + speedPercentage + ", should be between 0 and 100");
		}
		this.speed = speedPercentage;
		sendSpeed(speedPercentage);
	}
	
	public int getSpeed() {
		return speed;
	}
	
	public Set<RobotAlarm> getAlarms() {
		return alarms;
	}
	
	public void setAlarms(final Set<RobotAlarm> alarms) {
		this.alarms = alarms;
	}
	public void setRobotTimeout(final RobotAlarm robotTimeout) {
		this.robotTimeout = robotTimeout;
	}
	
	public RobotAlarm getRobotTimeout() {
		return robotTimeout;
	}
}
