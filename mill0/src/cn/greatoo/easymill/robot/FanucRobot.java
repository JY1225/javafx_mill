package cn.greatoo.easymill.robot;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cn.greatoo.easymill.entity.Clamping;
import cn.greatoo.easymill.entity.Coordinates;
import cn.greatoo.easymill.entity.Gripper;
import cn.greatoo.easymill.entity.Smooth;
import cn.greatoo.easymill.entity.WorkPiece;
import cn.greatoo.easymill.external.communication.socket.AbstractCommunicationException;
import cn.greatoo.easymill.external.communication.socket.RobotSocketCommunication;
import cn.greatoo.easymill.external.communication.socket.SocketConnection;
import cn.greatoo.easymill.external.communication.socket.SocketDisconnectedException;
import cn.greatoo.easymill.external.communication.socket.SocketResponseTimedOutException;
import cn.greatoo.easymill.external.communication.socket.SocketWrongResponseException;
import cn.greatoo.easymill.util.RobotConstants;

public class FanucRobot extends AbstractRobot{
	public static FanucRobot INSTANCE = null;
	private static RobotSocketCommunication fanucRobotCommunication;

    private static final int WRITE_VALUES_TIMEOUT = 2 * 5000;
    private static final int MOVE_TO_LOCATION_TIMEOUT = 3 * 60 * 1000;
    private static final int CLAMP_ACK_REQUEST_TIMEOUT = 10 * 1000;
    private static final int MOVE_TO_IPPOINT_TIMEOUT = 3 * 60 * 1000;
    private static final int MOVE_FINISH_TIMEOUT = 3 * 60 * 1000;
    private static final int ASK_POSITION_TIMEOUT = 50000;
    private static final int ASK_STATUS_TIMEOUT = 2 * 5 * 1000;
    private static final int TEACH_TIMEOUT = 10 * 60 * 1000;

    private static final int WRITE_REGISTER_TIMEOUT = 2 * 5000;
    private static final int IOACTION_TIMEOUT = 2 * 60 * 1000;

    private static final List<Integer> VALID_USERFRAMES = Arrays.asList(1, 3, 4, 6, 8, 9, 11, 12, 13, 14, 15);

    private static final String HEAD_A_ID = "A";
    private static final String HEAD_B_ID = "B";

    private DecimalFormat df;
    private DecimalFormat df2;

    private static Logger logger = LogManager.getLogger(FanucRobot.class.getName());

    public FanucRobot(String name,float payload,final SocketConnection socketConnection) {
    	super(name,payload,socketConnection);
        fanucRobotCommunication = new RobotSocketCommunication(socketConnection, this);;
        df = new DecimalFormat("#.###");
        df2 = new DecimalFormat("#");
        df.setDecimalSeparatorAlwaysShown(false);
        DecimalFormatSymbols custom = new DecimalFormatSymbols();
        custom.setDecimalSeparator('.');
        df.setDecimalFormatSymbols(custom);
    }

    @Override
    public void sendSpeed(final int speedPercentage) throws SocketDisconnectedException, SocketResponseTimedOutException, InterruptedException, SocketWrongResponseException {
        fanucRobotCommunication.writeValue(RobotConstants.COMMAND_SET_SPEED, RobotConstants.RESPONSE_SET_SPEED, WRITE_VALUES_TIMEOUT, speedPercentage + "");
    }
    
    @Override
    public void updateStatusRestAndAlarms() throws SocketDisconnectedException, SocketResponseTimedOutException, SocketWrongResponseException, InterruptedException  {
        List<String> values = fanucRobotCommunication.readValues(RobotConstants.COMMAND_ASK_STATUS, RobotConstants.RESPONSE_ASK_STATUS, ASK_STATUS_TIMEOUT);
        int errorId = Integer.parseInt(values.get(0));
        int controllerValue = Integer.parseInt(values.get(1));
        int controllerString = Integer.parseInt(values.get(2));
        double xRest = Float.parseFloat(values.get(3));
        double yRest = Float.parseFloat(values.get(4));
        double zRest = Float.parseFloat(values.get(5));
        setAlarms(RobotAlarm.parseFanucRobotAlarms(errorId, controllerValue, getRobotTimeout()));//报警机器人各种错误
        setStatus(controllerString);//机器人当前状态
        setRestValues(xRest, yRest, zRest);
    }
    @Override
    public Coordinates getPosition() throws SocketDisconnectedException, SocketResponseTimedOutException,  InterruptedException, SocketWrongResponseException {
        Coordinates position = fanucRobotCommunication.getPosition(ASK_POSITION_TIMEOUT);
        return position;
    }

    @Override
    public void continueProgram() throws SocketDisconnectedException, SocketResponseTimedOutException, InterruptedException, SocketWrongResponseException {
        fanucRobotCommunication.writeCommand(RobotConstants.COMMAND_CONTINUE, RobotConstants.RESPONSE_CONTINUE, WRITE_VALUES_TIMEOUT);
    }

    @Override
    public void abort() throws InterruptedException, AbstractCommunicationException {
   
        fanucRobotCommunication.writeCommand(RobotConstants.COMMAND_ABORT, RobotConstants.RESPONSE_ABORT, WRITE_VALUES_TIMEOUT);
        restartProgram();
        sendSpeed(this.getSpeed());
    }

    @Override
    public void disconnect() {
        fanucRobotCommunication.disconnect();
    }

    @Override
    public void restartProgram() throws SocketDisconnectedException, SocketResponseTimedOutException, InterruptedException, SocketWrongResponseException {
        
        fanucRobotCommunication.writeCommand(RobotConstants.COMMAND_RESTART_PROGRAM, RobotConstants.RESPONSE_RESTART_PROGRAM, WRITE_VALUES_TIMEOUT);
        
    }

    @Override
    public void reset() throws SocketDisconnectedException, SocketResponseTimedOutException, InterruptedException, SocketWrongResponseException {
        fanucRobotCommunication.writeCommand(RobotConstants.COMMAND_RESET, RobotConstants.RESPONSE_RESET, WRITE_VALUES_TIMEOUT);
    }

    @Override
    public void writeRegister(final int registerNr, final int value) throws SocketDisconnectedException, SocketResponseTimedOutException,  InterruptedException, SocketWrongResponseException {
        List<String> values = new ArrayList<String>();
        values.add(registerNr + "");
        values.add(value + "");
        fanucRobotCommunication.writeValues(RobotConstants.COMMAND_WRITE_REGISTER, RobotConstants.RESPONSE_WRITE_REGISTER, WRITE_REGISTER_TIMEOUT, values);
    }

    public void openGripperA() throws SocketDisconnectedException, SocketResponseTimedOutException, InterruptedException, SocketWrongResponseException {
        List<String> values = new ArrayList<String>();
        values.add(2 + "");
        values.add(0 + "");
        fanucRobotCommunication.writeValues(RobotConstants.COMMAND_GRIPPER_ACTION, RobotConstants.RESPONSE_GRIPPER_ACTION, WRITE_VALUES_TIMEOUT, values);
    }

    public void closeGripperA() throws SocketDisconnectedException, SocketResponseTimedOutException, InterruptedException, SocketWrongResponseException {
        List<String> values = new ArrayList<String>();
        values.add(2 + "");
        values.add(1 + "");
        fanucRobotCommunication.writeValues(RobotConstants.COMMAND_GRIPPER_ACTION, RobotConstants.RESPONSE_GRIPPER_ACTION, WRITE_VALUES_TIMEOUT, values);
    }

    public void openGripperB() throws SocketDisconnectedException, SocketResponseTimedOutException, InterruptedException, SocketWrongResponseException {
        List<String> values = new ArrayList<String>();
        values.add(3 + "");
        values.add(0 + "");
        fanucRobotCommunication.writeValues(RobotConstants.COMMAND_GRIPPER_ACTION, RobotConstants.RESPONSE_GRIPPER_ACTION, WRITE_VALUES_TIMEOUT, values);
    }

    public void closeGripperB() throws SocketDisconnectedException, SocketResponseTimedOutException, InterruptedException, SocketWrongResponseException {
        List<String> values = new ArrayList<String>();
        values.add(3 + "");
        values.add(1 + "");
        fanucRobotCommunication.writeValues(RobotConstants.COMMAND_GRIPPER_ACTION, RobotConstants.RESPONSE_GRIPPER_ACTION, WRITE_VALUES_TIMEOUT, values);
    }
    public void moveToHome(int speed) throws SocketDisconnectedException, SocketResponseTimedOutException, InterruptedException, SocketWrongResponseException {
       
        fanucRobotCommunication.writeValue(RobotConstants.COMMAND_TO_HOME, RobotConstants.RESPONSE_TO_HOME, WRITE_VALUES_TIMEOUT, "" + speed);
    }  

    public void writeServiceGripperSet(final String headId, final Gripper gA, final Gripper gB, final int serviceType,
            final boolean gripInner) throws SocketDisconnectedException, SocketResponseTimedOutException, InterruptedException, SocketWrongResponseException {
        List<String> values = new ArrayList<String>();
        boolean a = false;
        if (headId.equals(HEAD_A_ID)) {
            a = true;
        } else if (headId.equals(HEAD_B_ID)) {
            a = false;
        } else {
            throw new IllegalArgumentException("Gripper head id should be " + HEAD_A_ID + " or " + HEAD_B_ID + ".");
        }
        // service type ; grip select ; a height ; b height ; inner/outer gripper type ;
        values.add("" + serviceType);	// service type
        if (a) {						// Selected gripper (2 = A - 3 = B - 4 = C)
            values.add("2");
        } else {
            values.add("3");
        }
        values.add("" + (int) Math.floor(gA.getHeight()));		// a height
        if (gB != null) {
            values.add("" + (int) Math.floor(gB.getHeight()));		// b height
        } else {
            values.add("0");		// b height
        }
        // inner/outer gripper type
        if(a) {
            if (gripInner) {
                // inner A
                values.add(RobotConstants.SERVICE_GRIPPER_SERVICE_GRP_INNER_A+"");
            } else {
                // outer A
                values.add("0");
            }
        } else {
            if (gripInner) {
                // inner B
                values.add(RobotConstants.SERVICE_GRIPPER_SERVICE_GRP_INNER_B+"");
            } else {
                // outer B
                values.add("0");
            }
        }
        logger.debug("Writing service gripper set: " + values);
        fanucRobotCommunication.writeValues(RobotConstants.COMMAND_WRITE_SERVICE_GRIPPER, RobotConstants.RESPONSE_WRITE_SERVICE_GRIPPER, WRITE_VALUES_TIMEOUT, values);
    }
    
    public void writeServiceHandlingSet(int Speed, final boolean freeAfterService, final int serviceHandlingPPMode, 
    		final WorkPiece wp, int approachType,float payLoad1,float payLoad2)
            throws SocketDisconnectedException, SocketResponseTimedOutException, InterruptedException, SocketWrongResponseException {
        List<String> values = new ArrayList<String>();

        if (freeAfterService) {				
            values.add("1");//Go to home after service
        } else {
            values.add("0");//Wait in IP point after service
        }
        if (wp.getShape() == WorkPiece.WorkPieceShape.CUBIC) {//工件是否是矩形
            values.add("1");	
            values.add(df.format(Math.max(wp.getLength(), wp.getWidth())));	// WP length (WP diameter)
            values.add(df.format(Math.min(wp.getLength(), wp.getWidth())));	// WP width
            values.add(df.format(wp.getHeight()));	
        } else {
            values.add("2");	
            values.add(df.format(wp.getDiameter()));	
            values.add(df.format(wp.getDiameter()));	
            values.add(df.format(wp.getHeight()));	
        }
        values.add("0");					// gripped height

        if ((Speed < 5) || (Speed > 100)) {
            throw new IllegalStateException("The current speed value: [" + Speed + "] is illegal.");
        }

        values.add(Speed + "");						// robot speed
        values.add(df2.format(Math.ceil(payLoad1)));		// payload 1
        values.add(df2.format(Math.ceil(payLoad2)));		// payload 2
        values.add("" + serviceHandlingPPMode);				// PP mode
        values.add("" + approachType);				// positioning type (approach empty/top/bottom)
        logger.debug("Writing service handling set: " + values);
        fanucRobotCommunication.writeValues(RobotConstants.COMMAND_WRITE_SERVICE_HANDLING, RobotConstants.RESPONSE_WRITE_SERVICE_HANDLING, WRITE_VALUES_TIMEOUT, values);
    }
    
    public void writeServicePointSet(final int workArea, final Coordinates location, final Smooth smoothPoint,final float smoothPointZ, final WorkPiece dimensions,
            final float clampHeight, final int approachType,final float zSafePlane) throws SocketDisconnectedException, SocketResponseTimedOutException, InterruptedException, SocketWrongResponseException {
        List<String> values = new ArrayList<String>();
        // user frame id ; x destination ; y destination ; z destination ; w destination, p destination, r destination ; z-safe plane ; safety add z ; smooth x ; smooth y ; smooth z ;
        // approachStrategy ; clamp height

        values.add("" + workArea);			// select user frame

        values.add(df.format(location.getX()));		// destination x
        values.add(df.format(location.getY()));		// destination y
        values.add(df.format(location.getZ()));		// destination z
        values.add(df.format(location.getW()));		// destination w
        values.add(df.format(location.getP()));		// destination p
        values.add(df.format(location.getR()));		// destination r    90    

        values.add(df.format(zSafePlane));	// z-safe plane 60

        // Safety add Z: use UF value, compare to smooth and use the largest
   
        values.add(df.format(smoothPointZ)); //25
        
        values.add(df.format(smoothPoint.getX()));	// smooth x
        values.add(df.format(smoothPoint.getY()));	// smooth y
        values.add(df.format(smoothPoint.getZ()));	// smooth z
        //TODO review if this strategy is always safe
        // The approach strategy can be overwritten by the robot in case the height of the IP-point is lower than the reversal unit + height of workpiece
        
        values.add("" + approachType/*RobotConstants.SERVICE_POINT_XYZ_ALLOWED_XYZ*/);	// APPRCH_STRAT
        
        values.add("" + df.format(clampHeight));	// clamp height (we need to include the relative position, height is measured from z = 0)
        logger.debug("Writing service point: " + values);
        fanucRobotCommunication.writeValues(RobotConstants.COMMAND_WRITE_SERVICE_POINT, RobotConstants.RESPONSE_WRITE_SERVICE_POINT, WRITE_VALUES_TIMEOUT, values);
    }
    
    public void startService() throws SocketDisconnectedException, SocketResponseTimedOutException, SocketWrongResponseException, InterruptedException {
    	fanucRobotCommunication.writeValue(RobotConstants.COMMAND_START_SERVICE, RobotConstants.RESPONSE_START_SERVICE, WRITE_VALUES_TIMEOUT, "1");
    }
    
    private void writeAirblowPointSet(final Clamping clamping, final AirblowSquare airblowSettings) throws SocketDisconnectedException, SocketResponseTimedOutException, InterruptedException, SocketWrongResponseException {
        Coordinates bottom = Coordinates.add(airblowSettings.getBottomCoord(), clamping.getRelativePosition());
        Coordinates top = Coordinates.add(airblowSettings.getTopCoord(), clamping.getRelativePosition());
        List<String> values = new ArrayList<String>();
        //XYZ
        values.add(df.format(bottom.getX()));
        values.add(df.format(bottom.getY()));
        values.add(df.format(bottom.getZ()));
        //WPR
        values.add("0");
        values.add("0");
        values.add("0");
        //XYZ
        values.add(df.format(top.getX()));
        values.add(df.format(top.getY()));
        values.add(df.format(bottom.getZ()));
        //WPR
        values.add("0");
        values.add("0");
        values.add("0");
        logger.debug("Writing airblow points: " + values);
        fanucRobotCommunication.writeValues(RobotConstants.COMMAND_WRITE_AIRBLOW, RobotConstants.RESPONSE_WRITE_AIRBLOW, WRITE_VALUES_TIMEOUT, values);
    }

    public void writeCommand(final int permission) throws SocketDisconnectedException, SocketResponseTimedOutException, InterruptedException, SocketWrongResponseException {
        List<String> values = new ArrayList<String>();
        values.add("" + permission);
        fanucRobotCommunication.writeValues(RobotConstants.COMMAND_SET_PERMISSIONS, RobotConstants.RESPONSE_SET_PERMISSIONS, WRITE_VALUES_TIMEOUT, values);
    }


    @Override
    public void moveToChangePoint() throws SocketDisconnectedException, SocketResponseTimedOutException,  InterruptedException, SocketWrongResponseException {
        fanucRobotCommunication.writeCommand(RobotConstants.COMMAND_JAW_CH, RobotConstants.RESPONSE_JAW_CH, WRITE_VALUES_TIMEOUT);
    }

    @Override
    public boolean isConnected() {
        return fanucRobotCommunication.isConnected();
    }

    @Override
    public void recalculateTCPs() throws SocketDisconnectedException, SocketResponseTimedOutException, InterruptedException, SocketWrongResponseException {
        logger.debug("About to recalculate TCPs.");
//        String name = this.getGripperBody().getGripperHeadByName(HEAD_A_ID).getName();
//        writeServiceGripperSet(this.getGripperBody().getGripperHeadByName(HEAD_A_ID).getName(), this.getGripperBody().getGripperHeadByName(HEAD_A_ID), this.getGripperBody().getGripperHeadByName(HEAD_B_ID), RobotConstants.SERVICE_GRIPPER_SERVICE_TYPE_JAW_CHANGE, false);
        fanucRobotCommunication.writeCommand(RobotConstants.COMMAND_RECALC_TCPS, RobotConstants.RESPONSE_RECALC_TCPS, WRITE_VALUES_TIMEOUT);
    }


    public RobotSocketCommunication getRobotSocketCommunication() {
        return this.fanucRobotCommunication;
    }

    @Override
    public void moveToCustomPosition() throws AbstractCommunicationException,  InterruptedException {
        fanucRobotCommunication.writeCommand(RobotConstants.COMMAND_TO_CUSTOM_POS, RobotConstants.RESPONSE_TO_CUSTOM_POS, WRITE_VALUES_TIMEOUT);
    }

    private List<String> getPositionValues(final Coordinates coord, final Config config) {
        List<String> values = new ArrayList<String>();
        values.add(df.format(coord.getX()));
        values.add(df.format(coord.getY()));
        values.add(df.format(coord.getZ()));
        values.add(df.format(coord.getW()));
        values.add(df.format(coord.getP()));
        values.add(df.format(coord.getR()));
        values.add(df.format(config.getCfgFlip()));
        values.add(df.format(config.getCfgUp()));
        values.add(df.format(config.getCfgFront()));
        values.add(df.format(config.getCfgTurn1()));
        values.add(df.format(config.getCfgTurn2()));
        values.add(df.format(config.getCfgTurn3()));
        return values;
    }

    @Override
    public void enableMovement(final boolean flag) throws AbstractCommunicationException,  InterruptedException {
        List<String> values = new ArrayList<String>();
        if (flag) {
            values.add("" + 1);
            logger.debug("start robot movement");
        } else {
            values.add("" + 0);
            logger.debug("stop robot movement");
        }
        fanucRobotCommunication.readValues(RobotConstants.COMMAND_MOTION_ENABLE, RobotConstants.RESPONSE_MOTION_ENABLE, WRITE_VALUES_TIMEOUT, values);
    }

	@Override
	public void moveToHome() throws AbstractCommunicationException, InterruptedException {
		// TODO Auto-generated method stub
		
	}

	 @Override
	    public void continuePutTillAtLocation(boolean isTeachingNeeded) throws AbstractCommunicationException, RobotActionException, InterruptedException {
	        writeCommand(RobotConstants.PERMISSIONS_COMMAND_PUT);	       
	        if (isTeachingNeeded) {
	            boolean waitingForTeachingNeeded = waitForStatus(RobotConstants.STATUS_AWAITING_TEACHING, MOVE_TO_LOCATION_TIMEOUT);
	            if (!waitingForTeachingNeeded) {
	                setRobotTimeout(new RobotAlarm(RobotAlarm.MOVE_TO_PUT_POSITION_TIMEOUT));
	                waitForStatus(RobotConstants.STATUS_AWAITING_TEACHING);
	                setRobotTimeout(null);
	            }
	        } else {
	            boolean waitingForRelease = waitForStatus(RobotConstants.STATUS_PUT_CLAMP_REQUEST, MOVE_TO_LOCATION_TIMEOUT);
	            if (!waitingForRelease) {
	                setRobotTimeout(new RobotAlarm(RobotAlarm.MOVE_TO_PUT_POSITION_TIMEOUT));
	                waitForStatus(RobotConstants.STATUS_PUT_CLAMP_REQUEST);
	                setRobotTimeout(null);
	            }
	        }
	    }

	 @Override
	    public void continuePutTillClampAck(boolean isTeachingNeeded) throws AbstractCommunicationException, RobotActionException, InterruptedException {	        
	        if (isTeachingNeeded) {
	            boolean waitingForRelease = waitForStatus(RobotConstants.STATUS_PUT_CLAMP_REQUEST, TEACH_TIMEOUT);
	            if (!waitingForRelease) {
	                setRobotTimeout(new RobotAlarm(RobotAlarm.TEACH_TIMEOUT));
	                waitForStatus(RobotConstants.STATUS_PUT_CLAMP_REQUEST);
	                setRobotTimeout(null);
	            }
	        } else {
	            boolean waitingForRelease = waitForStatus(RobotConstants.STATUS_PUT_CLAMP_REQUEST, CLAMP_ACK_REQUEST_TIMEOUT);
	            if (!waitingForRelease) {
	                setRobotTimeout(new RobotAlarm(RobotAlarm.CLAMP_ACK_REQUEST_TIMEOUT));
	                waitForStatus(RobotConstants.STATUS_PUT_CLAMP_REQUEST);
	                setRobotTimeout(null);
	            }
	        }
	    }

	 @Override
	    public void continuePutTillIPPoint() throws AbstractCommunicationException, RobotActionException, InterruptedException {	     
	        writeCommand(RobotConstants.PERMISSIONS_COMMAND_PUT_CLAMP_ACK);
	        boolean waitingForPickFinished = waitForStatus(RobotConstants.STATUS_PUT_OUT_OF_MACHINE, MOVE_FINISH_TIMEOUT);
	        if (!waitingForPickFinished) {
	            setRobotTimeout(new RobotAlarm(RobotAlarm.MOVE_TO_IPPOINT_PUT_TIMEOUT));
	            waitForStatus(RobotConstants.STATUS_PUT_OUT_OF_MACHINE);
	            setRobotTimeout(null);
	        }
	    }
	@Override
	public void finalizePut() throws AbstractCommunicationException, InterruptedException {
		// TODO Auto-generated method stub
		
	}

	@Override
    public void continuePickTillAtLocation(boolean isTeachingNeeded) throws AbstractCommunicationException, RobotActionException, InterruptedException {
        writeCommand(RobotConstants.PERMISSIONS_COMMAND_PICK);
        if (isTeachingNeeded) {
            boolean waitingForTeachingNeeded = waitForStatus(RobotConstants.STATUS_AWAITING_TEACHING, MOVE_TO_LOCATION_TIMEOUT);
            if (!waitingForTeachingNeeded) {
                setRobotTimeout(new RobotAlarm(RobotAlarm.MOVE_TO_PICK_POSITION_TIMEOUT));
                waitForStatus(RobotConstants.STATUS_AWAITING_TEACHING);
                setRobotTimeout(null);
            }
        } else {
            boolean waitingForRelease = waitForStatus(RobotConstants.STATUS_PICK_RELEASE_REQUEST, MOVE_TO_LOCATION_TIMEOUT);
            if (!waitingForRelease) {
                setRobotTimeout(new RobotAlarm(RobotAlarm.MOVE_TO_PICK_POSITION_TIMEOUT));
                waitForStatus(RobotConstants.STATUS_PICK_RELEASE_REQUEST);
                setRobotTimeout(null);
            }
        }
    }

	@Override
    public void continuePickTillUnclampAck(boolean isTeachingNeeded) throws AbstractCommunicationException, RobotActionException, InterruptedException {       
        if (isTeachingNeeded) {
            boolean waitingForRelease = waitForStatus(RobotConstants.STATUS_PICK_RELEASE_REQUEST, TEACH_TIMEOUT);
            if (!waitingForRelease) {
                setRobotTimeout(new RobotAlarm(RobotAlarm.TEACH_TIMEOUT));
                waitForStatus(RobotConstants.STATUS_PICK_RELEASE_REQUEST);
                setRobotTimeout(null);
            }
        } else {
            boolean waitingForRelease = waitForStatus(RobotConstants.STATUS_PICK_RELEASE_REQUEST, CLAMP_ACK_REQUEST_TIMEOUT);
            if (!waitingForRelease) {
                setRobotTimeout(new RobotAlarm(RobotAlarm.UNCLAMP_ACK_REQUEST_TIMEOUT));
                waitForStatus(RobotConstants.STATUS_PICK_RELEASE_REQUEST);
                setRobotTimeout(null);
            }
        }
    }

	@Override
    public void continuePickTillIPPoint() throws AbstractCommunicationException, RobotActionException, InterruptedException {
        writeCommand(RobotConstants.PERMISSIONS_COMMAND_PICK_RELEASE_ACK);
        boolean waitingForPickFinished = waitForStatus(RobotConstants.STATUS_PICK_OUT_OF_MACHINE, MOVE_TO_IPPOINT_TIMEOUT);
        if (!waitingForPickFinished) {
            setRobotTimeout(new RobotAlarm(RobotAlarm.MOVE_TO_IPPOINT_PICK_TIMEOUT));
            waitForStatus(RobotConstants.STATUS_PICK_OUT_OF_MACHINE);
            setRobotTimeout(null);
        }
    }

	@Override
	public void finalizePick() throws AbstractCommunicationException, InterruptedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void continueMoveTillAtLocation() throws AbstractCommunicationException, InterruptedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void continueMoveTillWait() throws AbstractCommunicationException, InterruptedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void performIOAction() throws AbstractCommunicationException, InterruptedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void continueMoveWithPieceTillIPPoint() throws AbstractCommunicationException, InterruptedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void continueMoveWithoutPieceTillIPPoint() throws AbstractCommunicationException, InterruptedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void finalizeMovePiece() throws AbstractCommunicationException, InterruptedException {
		// TODO Auto-generated method stub
		
	}
	
	
	public static FanucRobot getInstance(String name,float payload,final SocketConnection socketConnection) {
		if (INSTANCE == null && socketConnection != null) {
			INSTANCE = new FanucRobot(name,payload,socketConnection);
		}
		return INSTANCE;
	}
}
