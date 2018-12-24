package cn.greatoo.easymill.external.communication.socket;

import cn.greatoo.easymill.cnc.CNCMachine;
import cn.greatoo.easymill.cnc.DeviceActionException;
import cn.greatoo.easymill.cnc.EWayOfOperating;
import cn.greatoo.easymill.cnc.MCodeAdapter;
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
public class TeachSocketThread extends Controller implements Runnable {

	// private RobotSocketCommunication roboSocketConnection;
	//private CNCSocketCommunication cncSocketConnection;
	private boolean isAlive;
	private FanucRobot robot;
	private CNCMachine cncMachine; 
	private EWayOfOperating wayOfOperating;

	public TeachSocketThread(RobotSocketCommunication roboSocketConnection,
			CNCSocketCommunication cncSocketConnection) {
		this.robot = FanucRobot.getInstance(roboSocketConnection);		 		
		//MCodeAdapter mCodeAdapter = DBHandler.getInstance().getMCodeAdapter(1);
		this.cncMachine = CNCMachine.getInstance(cncSocketConnection, DBHandler.getInstance().getMCodeAdapter(1), wayOfOperating);// DBHandler.getInstance().getCNCMillingMachine(1,cncSocketConnection);//new CNCMachine(cncSocketConnection, mCodeAdapter, wayOfOperating.M_CODES);				
		//this.cncSocketConnection = cncSocketConnection;
		this.isAlive = true;
	}

	@SuppressWarnings("unused")
	@Override
	public void run() {
		while (isAlive) {
			try {
				
				int[] values = new int[1];
				values[0] = 0;
				int startingRegisterNr = 58;
				cncMachine.indicateOperatorRequested(false);
				cncMachine.indicateOperatorRequested(false);				
				robot.restartProgram();// 重启流程
				Gripper gripper = new Gripper("name", Type.TWOPOINT, 192, "description", "");
				final String headId = "A";
				final GripperHead gHeadA = new GripperHead("jyA", null, gripper);
				final GripperHead gHeadB = new GripperHead("jyB", null, gripper);
				int serviceType = 5;
				boolean gripInner = false;
				robot.writeServiceGripperSet(headId, gHeadA, gHeadB, serviceType, gripInner);
				robot.recalculateTCPs();
				int speed = 100;
				//回home点
				robot.moveToHome(speed);

				// IPC to DI (CNC): write: WW58;01;0;
				cncMachine.indicateOperatorRequested(false);
				startingRegisterNr = 18;
				values = new int[2];
				values[0] = 1;
				values[1] = 16;
				// IPC to DI (CNC): write: WW18;02;1;16;
				cncMachine.prepareForProcess(1);
				serviceType = 12;
				//设置抓爪信息
				robot.writeServiceGripperSet(headId, gHeadA, gHeadB, serviceType, gripInner);

				boolean freeAfterService = false;
				final int serviceHandlingPPMode = 48;
				final IWorkPieceDimensions dimensions = new RectangularDimensions(180, 160, 30);
				final float weight2 = 16;
				int approachType = 1;
				WorkPiece wp1 = new WorkPiece(WorkPiece.Type.FINISHED, dimensions, Material.AL, 2.4f);
				WorkPiece wp2 = null;
				//发送料架Pick的搬运信息: 76;0;1;180;160;30;0;10;24;0;48;1;
				robot.writeServiceHandlingSet(speed, freeAfterService, serviceHandlingPPMode,
						dimensions, weight2, approachType, wp1, wp2);

				int workArea = 1;
				Coordinates location = new Coordinates(97.5f, 87.5f, 0, 0, 0, 90);
				Coordinates smoothPoint = new Coordinates(97.5f, 87.5f, 5, 0, 5, 90);
				String name = "A";
				float defaultHeight = 11;
				Coordinates relativePosition = new Coordinates(1, 1, 5, 1, 1, 1);
				Coordinates smoothToPoint = null;
				Coordinates smoothFromPoint = null;
				String imageURL = "";
				Clamping clamping = new Clamping(Clamping.Type.CENTRUM, name, defaultHeight, relativePosition,
						smoothToPoint, smoothFromPoint, imageURL);
				approachType = 1;// APPRCH_STRAT
				float zSafePlane = 60;
				int smoothPointZ = 25;
				//Pick的位置信息: 77; 1;97.5;87.5;0;0;0;90;60;25.0;5;0;5;1;16;
				robot.writeServicePointSet(workArea, location, smoothPoint, smoothPointZ, dimensions,
						clamping, approachType, zSafePlane);
				//发送开始命令
				robot.startService();				
				//获取pick权限-PERMISSIONS1
				RobotStatusChangeThread.statusChanged(new StatusChangedEvent(StatusChangedEvent.EXECUTE_TEACHED, 0,Mode.TEACH));			
				robot.continuePickTillAtLocation(true);
				RobotStatusChangeThread.statusChanged(new StatusChangedEvent(StatusChangedEvent.TEACHING_NEEDED, 0,Mode.TEACH));
				robot.continuePickTillUnclampAck(true);
				RobotStatusChangeThread.statusChanged(new StatusChangedEvent(StatusChangedEvent.TEACHING_FINISHED, 0,Mode.TEACH));
			
				//获取偏移位置信息70
				Coordinates robotPosition = robot.getPosition(); //97.5, 87.5, 0.0, 0.0, 0.0, 90.0				
				
				//pick-PERMISSIONS_COMMAND_PICK_RELEASE_ACK-4
				robot.continuePickTillIPPoint();
				RobotStatusChangeThread.statusChanged(new StatusChangedEvent(StatusChangedEvent.ENDED, 0,Mode.TEACH));
				
				//设置抓爪信息
				serviceType = 13;
				robot.writeServiceGripperSet(headId, gHeadA, gHeadB, serviceType, gripInner);
				//设置操作信息
				freeAfterService = true;
				robot.writeServiceHandlingSet(speed, freeAfterService, serviceHandlingPPMode, dimensions,
						weight2, approachType, wp1, wp2);

				workArea = 3;
				location = new Coordinates(432.5f, 135.5f, 209, 0, 0, 0);
				smoothPoint = new Coordinates(97f, 87.5f, 0, 0, 0, 90);
				name = "A";
				defaultHeight = 204;
				relativePosition = new Coordinates(1, 1, 5, 1, 1, 1);
				smoothToPoint = null;
				smoothFromPoint = null;
				imageURL = "";
				clamping = new Clamping(Clamping.Type.CENTRUM, name, defaultHeight, relativePosition, smoothToPoint,
						smoothFromPoint, imageURL);
				approachType = 1;
				zSafePlane = 239;
				smoothPointZ = 5;
				//设置位置信息
				robot.writeServicePointSet(workArea, location, smoothPoint, smoothPointZ, dimensions,
						clamping, approachType, zSafePlane);
				
				//开始命令
				robot.startService();
				values = new int[2];
				values[0] = 37;
				values[1] = 1;
				cncMachine.prepareForPut(false, 0,0);

				//获取Put 权限PERMISSIONS_COMMAND_PUT2
				RobotStatusChangeThread.statusChanged(new StatusChangedEvent(StatusChangedEvent.EXECUTE_TEACHED, 1,Mode.TEACH));
				robot.continuePutTillAtLocation(true);
				RobotStatusChangeThread.statusChanged(new StatusChangedEvent(StatusChangedEvent.TEACHING_NEEDED, 1,Mode.TEACH));
				robot.continuePutTillClampAck(true);
				RobotStatusChangeThread.statusChanged(new StatusChangedEvent(StatusChangedEvent.TEACHING_FINISHED, 1,Mode.TEACH));
				//获取机器人位置信息
				robotPosition = robot.getPosition();

				values = new int[2];
				values[0] = 37;
				values[1] = 4;
				cncMachine.grabPiece();

				//put释放PERMISSIONS_COMMAND_PUT8
				robot.continuePutTillIPPoint();
				RobotStatusChangeThread.statusChanged(new StatusChangedEvent(StatusChangedEvent.ENDED, 1,Mode.TEACH));

				startingRegisterNr = 19;
				values = new int[1];
				values[0] = 64;
				//  IPC_MC_FINISH_CMD: WW19;01;64;
				cncMachine.pickFinished(0,false);//finishMCode(0, 0);//writeRegisters(startingRegisterNr, values);

				// 设置抓爪信息
				gripInner = false;
				serviceType = 12;
				robot.writeServiceGripperSet(headId, gHeadA, gHeadB, serviceType, gripInner);

				// 设置操作信息
				freeAfterService = true;
				robot.writeServiceHandlingSet(speed, freeAfterService, serviceHandlingPPMode, dimensions,
						weight2, approachType, wp1, wp2);

				// 设置位置信息
				robot.writeServicePointSet(workArea, location, smoothPoint, smoothPointZ, dimensions,
						clamping, approachType, zSafePlane);

				// 开始
				robot.startService();

				values = new int[2];//18
				values[0] = 37;
				values[1] = 2;
				cncMachine.prepareForPick(false, 0,1);

				//  IPC write to Robot: 50;1; 
				RobotStatusChangeThread.statusChanged(new StatusChangedEvent(StatusChangedEvent.EXECUTE_TEACHED, 0,Mode.TEACH));			
				robot.continuePickTillAtLocation(true);				
				RobotStatusChangeThread.statusChanged(new StatusChangedEvent(StatusChangedEvent.TEACHING_NEEDED, 0,Mode.TEACH));
				robot.continuePickTillUnclampAck(true);
				RobotStatusChangeThread.statusChanged(new StatusChangedEvent(StatusChangedEvent.TEACHING_FINISHED, 0,Mode.TEACH));


				//  IPC write to Robot: 70；
				robotPosition = robot.getPosition();
				
				values = new int[2];
				values[0] = 37;
				values[1] = 8;
				cncMachine.releasePiece();

				//pick-PERMISSIONS_COMMAND_PICK_RELEASE_ACK-4
				robot.continuePickTillIPPoint();
				RobotStatusChangeThread.statusChanged(new StatusChangedEvent(StatusChangedEvent.ENDED, 0,Mode.TEACH));

				cncMachine.prepareForIntervention();

				cncMachine.pickFinished(0,true);

				cncMachine.clearIndications();


				//  IPC write to Robot: 75;13;3;192;192;0; //
				gripInner = false;
				serviceType = 13;
				robot.writeServiceGripperSet(headId, gHeadA, gHeadB, serviceType, gripInner);

				//  IPC write to Robot: 76;1;1;180;160;30;0;100;24;0;48;1;
				freeAfterService = true;
				robot.writeServiceHandlingSet(speed, freeAfterService, serviceHandlingPPMode, dimensions,
						weight2, approachType, wp1, wp2);

				//  IPC write to Robot: 77;1;97.5;87.5;16;0;0;90;60;25.0;5;0;5;1;16;
				location = new Coordinates(97.5f, 87.5f, 16, 0, 0, 90);
				name = "A";
				workArea =1;
				defaultHeight = 11;
				relativePosition = new Coordinates(1, 1, 5, 1, 1, 1);
				smoothToPoint = null;
				smoothFromPoint = null;
				imageURL = "";
				clamping = new Clamping(Clamping.Type.CENTRUM, name, defaultHeight, relativePosition, smoothToPoint,
						smoothFromPoint, imageURL);
				approachType = 1;// APPRCH_STRAT
				zSafePlane = 60;
				smoothPointZ = 25;
				robot.writeServicePointSet(workArea, location, smoothPoint, smoothPointZ, dimensions,
						clamping, approachType, zSafePlane);
				
				//	IPC write to Robot:  51;1; 
				robot.startService();
				
				//获取Put 权限PERMISSIONS_COMMAND_PUT2
				RobotStatusChangeThread.statusChanged(new StatusChangedEvent(StatusChangedEvent.EXECUTE_TEACHED, 1,Mode.TEACH));
				robot.continuePutTillAtLocation(true);
				RobotStatusChangeThread.statusChanged(new StatusChangedEvent(StatusChangedEvent.TEACHING_NEEDED, 1,Mode.TEACH));
				robot.continuePutTillClampAck(true);
				RobotStatusChangeThread.statusChanged(new StatusChangedEvent(StatusChangedEvent.TEACHING_FINISHED, 1,Mode.TEACH));

				//	IPC write to Robot:  70； 
				robotPosition = robot.getPosition();
								
				//put释放PERMISSIONS_COMMAND_PUT8
				robot.continuePutTillIPPoint();
				RobotStatusChangeThread.statusChanged(new StatusChangedEvent(StatusChangedEvent.ENDED, 1,Mode.TEACH));


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
