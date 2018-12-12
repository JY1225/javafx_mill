package cn.greatoo.easymill.robot;

import java.util.HashSet;
import java.util.Set;

import cn.greatoo.easymill.entity.GripperBody;
import cn.greatoo.easymill.entity.GripperHead;
import cn.greatoo.easymill.external.communication.socket.AbstractCommunicationException;
import cn.greatoo.easymill.util.Coordinates;

public abstract class AbstractRobot {
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

}
