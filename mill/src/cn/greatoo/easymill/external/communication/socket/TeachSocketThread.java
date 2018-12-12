package cn.greatoo.easymill.external.communication.socket;

import java.util.Arrays;
import java.util.List;

import cn.greatoo.easymill.entity.Gripper;
import cn.greatoo.easymill.entity.Gripper.Type;
import cn.greatoo.easymill.entity.GripperHead;
import cn.greatoo.easymill.robot.FanucRobot;
import cn.greatoo.easymill.util.Clamping;
import cn.greatoo.easymill.util.Coordinates;
import cn.greatoo.easymill.workpiece.IWorkPieceDimensions;
import cn.greatoo.easymill.workpiece.RectangularDimensions;
import cn.greatoo.easymill.workpiece.WorkPiece;
import cn.greatoo.easymill.workpiece.WorkPiece.Material;

/**
 * 示教线程
 *
 */
public class TeachSocketThread implements Runnable {
	private static final List<Integer> VALID_USERFRAMES = Arrays.asList(1, 3, 4, 6, 8, 9, 11, 12, 13, 14, 15);

	//private RobotSocketCommunication roboSocketConnection;
	private CNCSocketCommunication cncSocketConnection;
	private boolean isAlive;
	FanucRobot roboSocketConnection;
	public TeachSocketThread(RobotSocketCommunication roboSocketConnection,
			CNCSocketCommunication cncSocketConnection) {
		this.roboSocketConnection = new FanucRobot(roboSocketConnection);
		this.cncSocketConnection = cncSocketConnection;
		this.isAlive = true;
	}

	@Override
	public void run() {
		while (isAlive) {
			try {
				roboSocketConnection.restartProgram();//重启流程
				
				int[] values = new int[2];
				values[0] = 1;
				values[1]=0;
				int startingRegisterNr = 58;
				cncSocketConnection.writeRegisters(startingRegisterNr,values);
				cncSocketConnection.writeRegisters(startingRegisterNr,values);
				
				Gripper gripper = new Gripper("name",Type.TWOPOINT, 192, "description", "");
				final String headId = "A";
				final GripperHead gHeadA = new GripperHead("jyA",null,gripper);
				final GripperHead gHeadB = new GripperHead("jyB",null,gripper);
				int serviceType = 5;
	            final boolean gripInner = true;
				roboSocketConnection.writeServiceGripperSet(headId, gHeadA, gHeadB, serviceType,
			            gripInner);
				roboSocketConnection.recalculateTCPs();
				int speed = 100;
				roboSocketConnection.moveToHome(speed);						
				
				cncSocketConnection.writeRegisters(startingRegisterNr,values);
				startingRegisterNr=18;
				int[] values2 = new int[3];
				values[0] = 2;
				values[1] = 1;
				values[2] = 16;
				cncSocketConnection.writeRegisters(startingRegisterNr,values2);
				
				serviceType = 12;
				roboSocketConnection.writeServiceGripperSet(headId, gHeadA, gHeadB, serviceType,
			            gripInner);
				
				final boolean freeAfterService = false;
				final int serviceHandlingPPMode = 48; 
				
				final IWorkPieceDimensions dimensions = new RectangularDimensions(180, 160, 30);
	    		final float weight2 = 16;
	    		int approachType = 1;
	    		WorkPiece wp1 = new WorkPiece(WorkPiece.Type.FINISHED, dimensions,Material.AL, 2.4f);
	    		WorkPiece wp2 = null;
				roboSocketConnection.writeServiceHandlingSet(speed,freeAfterService, serviceHandlingPPMode, dimensions, 
			    		weight2, approachType,wp1,wp2);
				
				final int workArea = 1;
				final Coordinates location = new Coordinates(97f, 87.5f, 0, 0, 0, 90); 
				final Coordinates smoothPoint = new Coordinates(97f, 87.5f, 5, 0, 5, 90);
				final String name = "A"; 
				final float defaultHeight = 10; 
				final Coordinates relativePosition= new Coordinates(1,1,5,1,1,1); 
				final Coordinates smoothToPoint= null; 
				final Coordinates smoothFromPoint = null; 
				final String imageURL="";
	            final Clamping clamping = new Clamping(Clamping.Type.CENTRUM, name, defaultHeight, relativePosition, smoothToPoint,
	            		smoothFromPoint, imageURL); 
	            approachType = 1;//APPRCH_STRAT
	            final float zSafePlane = 60;
				roboSocketConnection.writeServicePointSet(workArea, location, smoothPoint, dimensions,
			            clamping, approachType,zSafePlane);
				
				roboSocketConnection.startService();//IPC write to Robot:  51;1; 
				roboSocketConnection.writeCommand(1);//IPC write to Robot:  50;1;
				roboSocketConnection.sendSpeed(100);//	IPC write to Robot:  67;100;
				List<String> value = roboSocketConnection.askStatusRest();//	IPC write to Robot:  22；
				int controllerString = Integer.parseInt(value.get(2));
				if(controllerString == 0) {
					
				}
				
			} catch (SocketDisconnectedException e) {				
				e.printStackTrace();
			} catch (SocketResponseTimedOutException e) {				
				e.printStackTrace();
			} catch (SocketWrongResponseException e) {				
				e.printStackTrace();
			} catch (InterruptedException e) {				
				e.printStackTrace();
			}
		}
	}

}
