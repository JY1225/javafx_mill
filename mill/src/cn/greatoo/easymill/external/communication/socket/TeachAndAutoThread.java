package cn.greatoo.easymill.external.communication.socket;

import cn.greatoo.easymill.cnc.CNCMachine;
import cn.greatoo.easymill.cnc.DeviceActionException;
import cn.greatoo.easymill.cnc.EWayOfOperating;
import cn.greatoo.easymill.db.util.DBHandler;
import cn.greatoo.easymill.entity.Gripper;
import cn.greatoo.easymill.entity.Gripper.Type;
import cn.greatoo.easymill.entity.GripperHead;
import cn.greatoo.easymill.process.StatusChangedEvent;
import cn.greatoo.easymill.process.StatusChangedEvent.Mode;
import cn.greatoo.easymill.robot.FanucRobot;
import cn.greatoo.easymill.robot.RobotActionException;
import cn.greatoo.easymill.ui.main.Controller;
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

	private boolean teached;
	private boolean isAlive;
	private FanucRobot robot;
	private CNCMachine cncMachine; 
	private EWayOfOperating wayOfOperating;
	private Controller view;
	public TeachSocketThread(RobotSocketCommunication roboSocketConnection,
			CNCSocketCommunication cncSocketConnection, boolean teached, Controller view) {
		this.robot = FanucRobot.getInstance(roboSocketConnection);		 		
		this.cncMachine = CNCMachine.getInstance(cncSocketConnection, DBHandler.getInstance().getMCodeAdapter(1), wayOfOperating);// DBHandler.getInstance().getCNCMillingMachine(1,cncSocketConnection);//new CNCMachine(cncSocketConnection, mCodeAdapter, wayOfOperating.M_CODES);				
		this.teached = teached;
		this.isAlive = true;
		this.view = view;
	}

	@SuppressWarnings("unused")
	@Override
	public void run() {
		while (isAlive) {
			try {
				view.statusChanged(new StatusChangedEvent(StatusChangedEvent.PREPARE, 1, Mode.TEACH));
				
				Gripper gripper = new Gripper("name", Type.TWOPOINT, 190, "description", "");
				final String headId = "A";
				final GripperHead gHeadA = new GripperHead("jyA", null, gripper);
				final GripperHead gHeadB = new GripperHead("jyB", null, gripper);
				int serviceType = 5;
				boolean gripInner = false;
				robot.writeServiceGripperSet(headId, gHeadA, gHeadB, serviceType, gripInner);//75
				robot.recalculateTCPs();//64
				int speed = 100;
				if(teached) {
					robot.moveToHome(speed);//71
				}
				cncMachine.indicateOperatorRequested(false);//58
				cncMachine.prepareForProcess(1);//18
				
				//===从table抓取工件==================================================================================
				view.statusChanged(new StatusChangedEvent(StatusChangedEvent.STARTED, 1, Mode.TEACH));
				serviceType = 12;
				gripInner = true;
				robot.writeServiceGripperSet(headId, gHeadA, gHeadB, serviceType, gripInner);
				boolean freeAfterService = false;
				final int serviceHandlingPPMode = 16;
				final IWorkPieceDimensions dimensions = new RectangularDimensions(200, 170, 21);
				final float weight2 = 16;
				int approachType = 1;
				WorkPiece wp1 = new WorkPiece(WorkPiece.Type.FINISHED, dimensions, Material.AL, 2.4f);
				WorkPiece wp2 = null;
				robot.writeServiceHandlingSet(speed, freeAfterService, serviceHandlingPPMode,
						dimensions, weight2, approachType, wp1, wp2);
				int workArea = 1;
				Coordinates location = new Coordinates(90.94f, 109.42f, 2.45f, 0, 0, 90);
				Coordinates smoothPoint = new Coordinates(5f, 0f, 5, 0, 5, 90);
				String name = "A";
				float defaultHeight = 0;
				Coordinates relativePosition = new Coordinates(1, 1, 0, 1, 1, 1);
				Coordinates smoothToPoint = null;
				Coordinates smoothFromPoint = null;
				String imageURL = "";
				Clamping clamping = new Clamping(Clamping.Type.CENTRUM, name, defaultHeight, relativePosition,
						smoothToPoint, smoothFromPoint, imageURL);
				approachType = 1;
				float zSafePlane = 42;
				float smoothPointZ = 25;
				robot.writeServicePointSet(workArea, location, smoothPoint, smoothPointZ, dimensions,
						clamping, approachType, zSafePlane);
				robot.startService();
				Coordinates robotPosition;
				if(teached) {
					view.statusChanged(new StatusChangedEvent(StatusChangedEvent.EXECUTE_TEACHED, 1,Mode.TEACH));			
					robot.continuePickTillAtLocation(true);
					view.statusChanged(new StatusChangedEvent(StatusChangedEvent.TEACHING_NEEDED, 1,Mode.TEACH));
					robot.continuePickTillUnclampAck(true);
					view.statusChanged(new StatusChangedEvent(StatusChangedEvent.TEACHING_FINISHED, 1,Mode.TEACH));
					robotPosition = robot.getPosition(); //97.5, 87.5, 0.0, 0.0, 0.0, 90.0				
				}else {
					view.statusChanged(new StatusChangedEvent(StatusChangedEvent.EXECUTE_NORMAL, 1,Mode.AUTO));
					robot.continuePickTillAtLocation(false);
					robot.continuePickTillUnclampAck(false);
				}
								
				robot.continuePickTillIPPoint();
				//view.statusChanged(new StatusChangedEvent(StatusChangedEvent.ENDED, 0,Mode.TEACH));
				
				//===put工件到机床=========================================================================================================
				serviceType = 13;
				robot.writeServiceGripperSet(headId, gHeadA, gHeadB, serviceType, gripInner);
				freeAfterService = true;
				robot.writeServiceHandlingSet(speed, freeAfterService, serviceHandlingPPMode, dimensions,
						weight2, approachType, wp1, wp2);
				workArea = 3;
				location = new Coordinates(0f, 0f, 0, 0, 0, 90);
				smoothPoint = new Coordinates(0f, 0f, 0, 0, 0, 90);
				name = "A";
				defaultHeight = 0;
				relativePosition = new Coordinates(1, 1, 0, 1, 1, 1);
				smoothToPoint = null;
				smoothFromPoint = null;
				imageURL = "";
				clamping = new Clamping(Clamping.Type.CENTRUM, name, defaultHeight, relativePosition, smoothToPoint,
						smoothFromPoint, imageURL);
				approachType = 1;
				zSafePlane = 21;
				smoothPointZ = 20.5f;
				robot.writeServicePointSet(workArea, location, smoothPoint, smoothPointZ, dimensions,
						clamping, approachType, zSafePlane);
				robot.startService();
				
				cncMachine.prepareForPut(false, 0,0);
				
				if(teached) {
					view.statusChanged(new StatusChangedEvent(StatusChangedEvent.EXECUTE_TEACHED, 1,Mode.TEACH));
					robot.continuePutTillAtLocation(true);
					view.statusChanged(new StatusChangedEvent(StatusChangedEvent.TEACHING_NEEDED, 1,Mode.TEACH));
					robot.continuePutTillClampAck(true);
					view.statusChanged(new StatusChangedEvent(StatusChangedEvent.TEACHING_FINISHED, 1,Mode.TEACH));
					robotPosition = robot.getPosition();
				}else {
					view.statusChanged(new StatusChangedEvent(StatusChangedEvent.EXECUTE_NORMAL, 1,Mode.AUTO));
					robot.continuePutTillAtLocation(false);
					robot.continuePutTillClampAck(false);
				}
				
				cncMachine.grabPiece();
				robot.continuePutTillIPPoint();
				view.statusChanged(new StatusChangedEvent(StatusChangedEvent.ENDED, 1,Mode.TEACH));

				cncMachine.pickFinished(0,false);
				
				//===从机床pick工件出来============================================================================================================
				gripInner = false;
				serviceType = 12;
				robot.writeServiceGripperSet(headId, gHeadA, gHeadB, serviceType, gripInner);
				freeAfterService = true;
				robot.writeServiceHandlingSet(speed, freeAfterService, serviceHandlingPPMode, dimensions,
						weight2, approachType, wp1, wp2);
				robot.writeServicePointSet(workArea, location, smoothPoint, smoothPointZ, dimensions,
						clamping, approachType, zSafePlane);
				robot.startService();	
				
				cncMachine.prepareForPick(false, 0,1);

				if(teached) {
					view.statusChanged(new StatusChangedEvent(StatusChangedEvent.EXECUTE_TEACHED, 0,Mode.TEACH));			
					robot.continuePickTillAtLocation(true);				
					view.statusChanged(new StatusChangedEvent(StatusChangedEvent.TEACHING_NEEDED, 0,Mode.TEACH));
					robot.continuePickTillUnclampAck(true);
					view.statusChanged(new StatusChangedEvent(StatusChangedEvent.TEACHING_FINISHED, 0,Mode.TEACH));
					robotPosition = robot.getPosition();
				}else {
					view.statusChanged(new StatusChangedEvent(StatusChangedEvent.EXECUTE_NORMAL, 0,Mode.AUTO));
					robot.continuePickTillAtLocation(false);//50,1
					robot.continuePickTillUnclampAck(false);
				}
								
				cncMachine.releasePiece();//22;18
				robot.continuePickTillIPPoint();//50,4
				//view.statusChanged(new StatusChangedEvent(StatusChangedEvent.ENDED, 0,Mode.TEACH));

				//cncMachine.prepareForIntervention();
				cncMachine.pickFinished(0,true);//22;53;19
				//cncMachine.clearIndications();

				//====把工件put到table==========================================================================================================
				gripInner = false;
				serviceType = 13;
				robot.writeServiceGripperSet(headId, gHeadA, gHeadB, serviceType, gripInner);
				freeAfterService = true;
				robot.writeServiceHandlingSet(speed, freeAfterService, serviceHandlingPPMode, dimensions,
						weight2, approachType, wp1, wp2);
				location = new Coordinates(92.5f, 107.5f, 25, 0, 0, 90);
				name = "A";
				workArea =1;
				defaultHeight = -5;
				relativePosition = new Coordinates(5, 0, 5, 1, 1, 1);
				smoothToPoint = null;
				smoothFromPoint = null;
				imageURL = "";
				clamping = new Clamping(Clamping.Type.CENTRUM, name, defaultHeight, relativePosition, smoothToPoint,
						smoothFromPoint, imageURL);
				approachType = 1;
				zSafePlane = 42;
				smoothPointZ = 25;
				robot.writeServicePointSet(workArea, location, smoothPoint, smoothPointZ, dimensions,
						clamping, approachType, zSafePlane);
				robot.startService();
				
				if(teached) {
					view.statusChanged(new StatusChangedEvent(StatusChangedEvent.EXECUTE_TEACHED, 0,Mode.TEACH));
					robot.continuePutTillAtLocation(true);
					view.statusChanged(new StatusChangedEvent(StatusChangedEvent.TEACHING_NEEDED, 0,Mode.TEACH));
					robot.continuePutTillClampAck(true);
					view.statusChanged(new StatusChangedEvent(StatusChangedEvent.TEACHING_FINISHED, 0,Mode.TEACH));
					robotPosition = robot.getPosition();
				}else {
					view.statusChanged(new StatusChangedEvent(StatusChangedEvent.EXECUTE_NORMAL, 0,Mode.AUTO));
					robot.continuePutTillAtLocation(false);//50,2
					robot.continuePutTillClampAck(false);
				}
				
				robot.continuePutTillIPPoint();//50,8
				if(teached) {
					view.statusChanged(new StatusChangedEvent(StatusChangedEvent.ENDED, 0,Mode.TEACH));
				}else {
					robot.moveToHome(speed);//71
					cncMachine.indicateAllProcessed();//58
					robot.moveToHome(speed);//71
					view.statusChanged(new StatusChangedEvent(StatusChangedEvent.ENDED, 0,Mode.FINISHED));
				}
				isAlive = false;
			} catch (SocketDisconnectedException e) {
				e.printStackTrace();
			} catch (SocketResponseTimedOutException e) {
				e.printStackTrace();
			} catch (SocketWrongResponseException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}catch (AbstractCommunicationException | RobotActionException e) {
				e.printStackTrace();
			} catch (DeviceActionException e) {
				e.printStackTrace();
			}
		}
	}
	public boolean needsTeaching() {
		return true;
	}

}
