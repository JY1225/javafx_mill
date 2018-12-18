package cn.greatoo.easymill.robot;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cn.greatoo.easymill.entity.GripperHead;
import cn.greatoo.easymill.external.communication.socket.AbstractCommunicationException;
import cn.greatoo.easymill.external.communication.socket.RobotSocketCommunication;
import cn.greatoo.easymill.external.communication.socket.SocketDisconnectedException;
import cn.greatoo.easymill.external.communication.socket.SocketResponseTimedOutException;
import cn.greatoo.easymill.external.communication.socket.SocketWrongResponseException;
import cn.greatoo.easymill.util.Clamping;
import cn.greatoo.easymill.util.Coordinates;
import cn.greatoo.easymill.util.RobotConstants;
import cn.greatoo.easymill.workpiece.IWorkPieceDimensions;
import cn.greatoo.easymill.workpiece.RectangularDimensions;
import cn.greatoo.easymill.workpiece.WorkPiece;
import cn.greatoo.easymill.workpiece.WorkPiece.Dimensions;

public class FanucRobot extends AbstractRobot{
	private RobotSocketCommunication fanucRobotCommunication;

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

    public FanucRobot(final RobotSocketCommunication socketConnection) {
    	super(socketConnection);
        this.fanucRobotCommunication = socketConnection;
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
    
    public List<String> askStatusRest() throws SocketDisconnectedException, SocketResponseTimedOutException, SocketWrongResponseException, InterruptedException {
        List<String> values = fanucRobotCommunication.readValues(RobotConstants.COMMAND_ASK_STATUS, RobotConstants.RESPONSE_ASK_STATUS, ASK_STATUS_TIMEOUT);
        int errorId = Integer.parseInt(values.get(0));
        int controllerValue = Integer.parseInt(values.get(1));
        int controllerString = Integer.parseInt(values.get(2));//机器人当前状态
        double xRest = Float.parseFloat(values.get(3));
        double yRest = Float.parseFloat(values.get(4));
        double zRest = Float.parseFloat(values.get(5));
        setAlarms(RobotAlarm.parseFanucRobotAlarms(errorId, controllerValue, getRobotTimeout()));//报警机器人各种错误
        setStatus(controllerString);//机器人当前状态
        setRestValues(xRest, yRest, zRest);
        return values;
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
    public void writeServiceGripperSet(final String headId, final GripperHead gHeadA, final GripperHead gHeadB, final int serviceType,
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
        values.add("" + (int) Math.floor(gHeadA.getGripper().getHeight()));		// a height
        if (gHeadB != null) {
            values.add("" + (int) Math.floor(gHeadB.getGripper().getHeight()));		// b height
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
    
    public void writeServiceHandlingSet(int Speed, final boolean freeAfterService, final int serviceHandlingPPMode, final IWorkPieceDimensions dimensions, 
    		final float weight2, int approachType,WorkPiece wp1,WorkPiece wp2)
            throws SocketDisconnectedException, SocketResponseTimedOutException, InterruptedException, SocketWrongResponseException {
        List<String> values = new ArrayList<String>();
        // free after this service ; shape WP ;  WP length ; WP width ; WP height ;
        //;  ; dx correction P1 ; dy correction P1 ; dx correction P2 ; dy correction P2 ; dW correction ;
        //    dP correction ; robot speed ; payload 1 ; payload 2 ; PP mode ; positioning type (approach)
        if (freeAfterService) {				// free after this service
            values.add("1");
        } else {
            values.add("0");
        }
        if (dimensions instanceof RectangularDimensions) {
            values.add("1");	// select shape (Box - 1)
            values.add(df.format(Math.max(dimensions.getDimension(Dimensions.LENGTH), dimensions.getDimension(Dimensions.WIDTH))));	// WP length (WP diameter)
            values.add(df.format(Math.min(dimensions.getDimension(Dimensions.LENGTH), dimensions.getDimension(Dimensions.WIDTH))));	// WP width
            values.add(df.format(dimensions.getDimension(Dimensions.HEIGHT)));	// WP height
        } else {
            values.add("2");	// select shape (Round - 2)
            values.add(df.format(dimensions.getDimension(Dimensions.DIAMETER)));	// WP length (WP diameter)
            values.add(df.format(dimensions.getDimension(Dimensions.DIAMETER)));	// WP width
            values.add(df.format(dimensions.getDimension(Dimensions.HEIGHT)));	// WP height
        }
        values.add("0");					// gripped height

        if ((Speed < 5) || (Speed > 100)) {
            throw new IllegalStateException("The current speed value: [" + Speed + "] is illegal.");
        }
        float payLoad1 = 0.0f;
        float payLoad2 = 0.0f;
        if (wp1 != null) {
            payLoad1 = wp1.getWeight() * 10;
            payLoad2 = wp1.getWeight() * 10;
        }
        if (wp2 != null) {
            payLoad1 += wp2.getWeight() * 10;
            payLoad2 += wp2.getWeight() * 10;
        }
        payLoad2 = payLoad2 + (weight2 * 10);
        values.add(Speed + "");						// robot speed
        values.add(df2.format(Math.ceil(payLoad1)));		// payload 1
        values.add(df2.format(Math.ceil(payLoad2)));		// payload 2
        values.add("" + serviceHandlingPPMode);				// PP mode
        values.add("" + approachType);				// positioning type (approach empty/top/bottom)
        logger.debug("Writing service handling set: " + values);
        fanucRobotCommunication.writeValues(RobotConstants.COMMAND_WRITE_SERVICE_HANDLING, RobotConstants.RESPONSE_WRITE_SERVICE_HANDLING, WRITE_VALUES_TIMEOUT, values);
    }
    
    public void writeServicePointSet(final int workArea, final Coordinates location, final Coordinates smoothPoint,final int smoothPointZ, final IWorkPieceDimensions dimensions,
            final Clamping clamping, final int approachType,final float zSafePlane) throws SocketDisconnectedException, SocketResponseTimedOutException, InterruptedException, SocketWrongResponseException {
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
        
        values.add("" + df.format((clamping.getHeight() + clamping.getRelativePosition().getZ())));	// clamp height (we need to include the relative position, height is measured from z = 0)
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
	public void updateStatusRestAndAlarms() throws AbstractCommunicationException, InterruptedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void moveToHome() throws AbstractCommunicationException, InterruptedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void continuePutTillAtLocation() throws AbstractCommunicationException, InterruptedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void continuePutTillClampAck() throws AbstractCommunicationException, InterruptedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void continuePutTillIPPoint() throws AbstractCommunicationException, InterruptedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void finalizePut() throws AbstractCommunicationException, InterruptedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void continuePickTillAtLocation() throws AbstractCommunicationException, InterruptedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void continuePickTillUnclampAck() throws AbstractCommunicationException, InterruptedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void continuePickTillIPPoint() throws AbstractCommunicationException, InterruptedException {
		// TODO Auto-generated method stub
		
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
}
