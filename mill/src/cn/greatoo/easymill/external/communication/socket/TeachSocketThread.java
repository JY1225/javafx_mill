package cn.greatoo.easymill.external.communication.socket;

import java.util.Arrays;
import java.util.List;

import javax.swing.JOptionPane;

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
<<<<<<< Upstream, based on branch 'master' of http://jy@192.168.1.73:1000/r/mill/v1.0.git
import javafx.scene.control.Alert;
=======
>>>>>>> e5cfef5 commit

/**
 * 示教线程
 *
 */
public class TeachSocketThread extends Controller implements Runnable {
	private static final List<Integer> VALID_USERFRAMES = Arrays.asList(1, 3, 4, 6, 8, 9, 11, 12, 13, 14, 15);

	// private RobotSocketCommunication roboSocketConnection;
	private CNCSocketCommunication cncSocketConnection;
	private boolean isAlive;
	private FanucRobot roboSocketConnection;

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
				
				roboSocketConnection.restartProgram();// 重启流程

				int[] values = new int[1];
				values[0] = 0;
				int startingRegisterNr = 58;
				//  IPC to DI (CNC): write: WW58;01;0;
				cncSocketConnection.writeRegisters(startingRegisterNr, values);
				//  IPC to DI (CNC): write: WW58;01;0;
				cncSocketConnection.writeRegisters(startingRegisterNr, values);

				Gripper gripper = new Gripper("name", Type.TWOPOINT, 192, "description", "");
				final String headId = "A";
				final GripperHead gHeadA = new GripperHead("jyA", null, gripper);
				final GripperHead gHeadB = new GripperHead("jyB", null, gripper);
				int serviceType = 5;
				boolean gripInner = false;
				//  IPC write to Robot: 75;5;2;192;192;0; //
				roboSocketConnection.writeServiceGripperSet(headId, gHeadA, gHeadB, serviceType, gripInner);
				roboSocketConnection.recalculateTCPs();
<<<<<<< Upstream, based on branch 'master' of http://jy@192.168.1.73:1000/r/mill/v1.0.git
				int speed = 60;
=======
				int speed = 100;
				//回home点
>>>>>>> e5cfef5 commit
				roboSocketConnection.moveToHome(speed);

				// IPC to DI (CNC): write: WW58;01;0;
				cncSocketConnection.writeRegisters(startingRegisterNr, values);
				startingRegisterNr = 18;
				values = new int[2];
				values[0] = 1;
				values[1] = 16;
				// IPC to DI (CNC): write: WW18;02;1;16;
				cncSocketConnection.writeRegisters(startingRegisterNr, values);

				serviceType = 12;
				roboSocketConnection.writeServiceGripperSet(headId, gHeadA, gHeadB, serviceType, gripInner);

				boolean freeAfterService = false;
				final int serviceHandlingPPMode = 48;

				final IWorkPieceDimensions dimensions = new RectangularDimensions(180, 160, 30);
				final float weight2 = 16;
				int approachType = 1;
				WorkPiece wp1 = new WorkPiece(WorkPiece.Type.FINISHED, dimensions, Material.AL, 2.4f);
				WorkPiece wp2 = null;
<<<<<<< Upstream, based on branch 'master' of http://jy@192.168.1.73:1000/r/mill/v1.0.git
				//  IPC write to Robot: 76;0;1;180;160;30;0;10;24;0;48;1;
				roboSocketConnection.writeServiceHandlingSet(10, freeAfterService, serviceHandlingPPMode,
=======
				//发送料架Pick的搬运信息: 76;0;1;180;160;30;0;10;24;0;48;1;
				roboSocketConnection.writeServiceHandlingSet(speed, freeAfterService, serviceHandlingPPMode,
>>>>>>> e5cfef5 commit
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
				//  IPC write to Robot: 77; 1;97.5;87.5;0;0;0;90;60;25.0;5;0;5;1;16;
				roboSocketConnection.writeServicePointSet(workArea, location, smoothPoint, smoothPointZ, dimensions,
						clamping, approachType, zSafePlane);
<<<<<<< Upstream, based on branch 'master' of http://jy@192.168.1.73:1000/r/mill/v1.0.git

				roboSocketConnection.startService();// IPC write to Robot: 51;1;
				roboSocketConnection.writeCommand(1);// IPC write to Robot: 50;1;
				roboSocketConnection.sendSpeed(100);//  IPC write to Robot: 67;100;

				/**
				 * 0-把机器人状态信息、位置等信息反馈给IPC
				 */
				while(roboSocketConnection.getStatus() != 0) {					
					roboSocketConnection.askStatusRest();//  IPC write to Robot: 22；COMMAND_ASK_STATUS					
				}
=======
				//发送开始命令
				roboSocketConnection.startService();
				//获取pick权限-PERMISSIONS1
				RobotStatusChangeThread.statusChanged(new StatusChangedEvent(StatusChangedEvent.EXECUTE_TEACHED, 0,Mode.TEACH));			
				roboSocketConnection.continuePickTillAtLocation(true);
				roboSocketConnection.sendSpeed(speed);
				RobotStatusChangeThread.statusChanged(new StatusChangedEvent(StatusChangedEvent.TEACHING_NEEDED, 0,Mode.TEACH));
				roboSocketConnection.continuePickTillUnclampAck(true);
				RobotStatusChangeThread.statusChanged(new StatusChangedEvent(StatusChangedEvent.TEACHING_FINISHED, 0,Mode.TEACH));
			
				//获取偏移位置信息70
				Coordinates robotPosition = roboSocketConnection.getPosition(); //97.5, 87.5, 0.0, 0.0, 0.0, 90.0				
>>>>>>> e5cfef5 commit
				
<<<<<<< Upstream, based on branch 'master' of http://jy@192.168.1.73:1000/r/mill/v1.0.git
				JOptionPane.showMessageDialog(null, "请把机器人移到正确位置，再继续进行示教！", "", JOptionPane.WARNING_MESSAGE);
				//应该等待把机器人调到正确位置后再执行
				Coordinates Coordinates = roboSocketConnection.getPosition(); 

				/**
				 * 2049-b00: Pick request release和 b11: Teaching finished
				 */
				while(roboSocketConnection.getStatus() != 2049) {					
					roboSocketConnection.askStatusRest();//  IPC write to Robot: 22；COMMAND_ASK_STATUS					
				}

				roboSocketConnection.writeCommand(4);// IPC write to Robot: 50;4; // COMMAND_SET_PERMISSIONS（4）
				
				/**
				 * 2048-b11: Teaching finished
				 */
				while(roboSocketConnection.getStatus() != 2048) {					
					roboSocketConnection.askStatusRest();//  IPC write to Robot: 22；COMMAND_ASK_STATUS					
				}

				//  IPC write to Robot: 75;13;2;192;192;0; //
=======
				//pick-PERMISSIONS_COMMAND_PICK_RELEASE_ACK-4
				roboSocketConnection.continuePickTillIPPoint();
				RobotStatusChangeThread.statusChanged(new StatusChangedEvent(StatusChangedEvent.ENDED, 0,Mode.TEACH));
				
				//设置抓爪信息
>>>>>>> e5cfef5 commit
				serviceType = 13;
				roboSocketConnection.writeServiceGripperSet(headId, gHeadA, gHeadB, serviceType, gripInner);
				//  IPC write to Robot: 76;1;1;180;160;30;0;100;24;0;48;1;
				freeAfterService = true;
				roboSocketConnection.writeServiceHandlingSet(speed, freeAfterService, serviceHandlingPPMode, dimensions,
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
				approachType = 1;// APPRCH_STRAT
				zSafePlane = 239;
				smoothPointZ = 5;
				//  IPC write to Robot: 77;3;432.5;135.5;209;0;0;0;239;5.0;0;0;0;1;209;
				roboSocketConnection.writeServicePointSet(workArea, location, smoothPoint, smoothPointZ, dimensions,
						clamping, approachType, zSafePlane);

				//  IPC write to Robot: 51;1; // COMMAND_START_SERVICE （1）
				roboSocketConnection.startService();

				startingRegisterNr = 39;
				int amount = 1;
				// readR: WR39013
				cncSocketConnection.readRegisters(startingRegisterNr, amount);
				startingRegisterNr = 40;
				amount = 1;
				// readR: WR40010
				cncSocketConnection.readRegisters(startingRegisterNr, amount);
				startingRegisterNr = 40;
				amount = 1;
				// readR: WR41010
				cncSocketConnection.readRegisters(startingRegisterNr, amount);
				startingRegisterNr = 42;
				amount = 1;
				// readR: WR42010
				cncSocketConnection.readRegisters(startingRegisterNr, amount);
				startingRegisterNr = 22;
				amount = 1;
				// readR: WR220116
				cncSocketConnection.readRegisters(startingRegisterNr, amount);

				startingRegisterNr = 24;
				values = new int[1];
				values[0] = 0;
				// write: WW24;01;0;
				cncSocketConnection.writeRegisters(startingRegisterNr, values);
				startingRegisterNr = 18;
				values = new int[2];
				values[0] = 37;
				values[1] = 1;
				// write: WW18;02;37;1;
				cncSocketConnection.writeRegisters(startingRegisterNr, values);

<<<<<<< Upstream, based on branch 'master' of http://jy@192.168.1.73:1000/r/mill/v1.0.git
				// IPC write to Robot: 50;2;
				roboSocketConnection.writeCommand(2);

				/**
				 * 0-返回机器人信息
				 */
				while(roboSocketConnection.getStatus() != 0) {					
					roboSocketConnection.askStatusRest();//  IPC write to Robot: 22；COMMAND_ASK_STATUS					
				}
				JOptionPane.showMessageDialog(null, "请把机器人移到正确位置，再继续进行示教！", "", JOptionPane.WARNING_MESSAGE);
				//  IPC write to Robot: 70； // COMMAND_ASK_POSITION（get destination Position）
				Coordinates = roboSocketConnection.getPosition();
=======
				//获取Put 权限PERMISSIONS_COMMAND_PUT2
				RobotStatusChangeThread.statusChanged(new StatusChangedEvent(StatusChangedEvent.EXECUTE_TEACHED, 1,Mode.TEACH));
				roboSocketConnection.continuePutTillAtLocation(true);
				RobotStatusChangeThread.statusChanged(new StatusChangedEvent(StatusChangedEvent.TEACHING_NEEDED, 1,Mode.TEACH));
				roboSocketConnection.continuePutTillClampAck(true);
				RobotStatusChangeThread.statusChanged(new StatusChangedEvent(StatusChangedEvent.TEACHING_FINISHED, 1,Mode.TEACH));
				//获取机器人位置信息
				robotPosition = roboSocketConnection.getPosition();
>>>>>>> e5cfef5 commit

<<<<<<< Upstream, based on branch 'master' of http://jy@192.168.1.73:1000/r/mill/v1.0.git
				/**
				 * b01: Put request grab和
				 * b11: Teaching finished
				 */
				while(roboSocketConnection.getStatus() != 2050) {					
					roboSocketConnection.askStatusRest();//IPC write to Robot: 22；COMMAND_ASK_STATUS					
				}
=======
>>>>>>> e5cfef5 commit

				startingRegisterNr = 22;
				amount = 1;
				// readR: WR220117
				cncSocketConnection.readRegisters(startingRegisterNr, amount);

				startingRegisterNr = 18;
				values = new int[2];
				values[0] = 37;
				values[1] = 4;
				// write: WW18;02;37;4;
				cncSocketConnection.writeRegisters(startingRegisterNr, values);

<<<<<<< Upstream, based on branch 'master' of http://jy@192.168.1.73:1000/r/mill/v1.0.git
				//  IPC write to Robot: 50;8; // COMMAND_SET_PERMISSIONS（8）
				roboSocketConnection.writeCommand(8);

				/**
				 * 2048-b11: Teaching finished
				 */
				while(roboSocketConnection.getStatus() != 2048) {					
					roboSocketConnection.askStatusRest();//  IPC write to Robot: 22；COMMAND_ASK_STATUS
				}
=======
				//put释放PERMISSIONS_COMMAND_PUT8
				roboSocketConnection.continuePutTillIPPoint();
				RobotStatusChangeThread.statusChanged(new StatusChangedEvent(StatusChangedEvent.ENDED, 1,Mode.TEACH));
>>>>>>> e5cfef5 commit

				startingRegisterNr = 19;
				values = new int[1];
				values[0] = 64;
				// write: WW19;01;64;
				cncSocketConnection.writeRegisters(startingRegisterNr, values);

				//  IPC write to Robot: 75;12;3;192;192;0; //
				gripInner = false;
				serviceType = 12;
				roboSocketConnection.writeServiceGripperSet(headId, gHeadA, gHeadB, serviceType, gripInner);

				//  IPC write to Robot: 76;1;1;180;160;30;0;100;24;0;48;1;
				freeAfterService = true;
				roboSocketConnection.writeServiceHandlingSet(speed, freeAfterService, serviceHandlingPPMode, dimensions,
						weight2, approachType, wp1, wp2);

				//  IPC write to Robot: 77;3;432.5;135.5;209;0;0;0;239;5.0;0;0;0;1;209;
				roboSocketConnection.writeServicePointSet(workArea, location, smoothPoint, smoothPointZ, dimensions,
						clamping, approachType, zSafePlane);

				//  IPC write to Robot: 51;1; // COMMAND_START_SERVICE （1）
				roboSocketConnection.startService();

				// readR: WR39013
				startingRegisterNr = 39;
				amount = 1;
				cncSocketConnection.readRegisters(startingRegisterNr, amount);
				// readR: WR40010
				startingRegisterNr = 40;
				amount = 1;
				cncSocketConnection.readRegisters(startingRegisterNr, amount);
				// readR: WR41010
				startingRegisterNr = 41;
				amount = 1;
				cncSocketConnection.readRegisters(startingRegisterNr, amount);
				// readR: WR42010
				startingRegisterNr = 42;
				amount = 1;
				cncSocketConnection.readRegisters(startingRegisterNr, amount);
				// readR: WR220184
				startingRegisterNr = 22;
				amount = 1;
				cncSocketConnection.readRegisters(startingRegisterNr, amount);

				// write: WW18;02;37;2;
				startingRegisterNr = 18;
				values = new int[2];
				values[0] = 37;
				values[1] = 2;
				cncSocketConnection.writeRegisters(startingRegisterNr, values);

<<<<<<< Upstream, based on branch 'master' of http://jy@192.168.1.73:1000/r/mill/v1.0.git
				//  IPC write to Robot: 50;1; // COMMAND_SET_PERMISSIONS（1）
				roboSocketConnection.writeCommand(1);
=======
				//  IPC write to Robot: 50;1; 
				RobotStatusChangeThread.statusChanged(new StatusChangedEvent(StatusChangedEvent.EXECUTE_TEACHED, 0,Mode.TEACH));			
				roboSocketConnection.continuePickTillAtLocation(true);				
				RobotStatusChangeThread.statusChanged(new StatusChangedEvent(StatusChangedEvent.TEACHING_NEEDED, 0,Mode.TEACH));
				roboSocketConnection.continuePickTillUnclampAck(true);
				RobotStatusChangeThread.statusChanged(new StatusChangedEvent(StatusChangedEvent.TEACHING_FINISHED, 0,Mode.TEACH));
>>>>>>> e5cfef5 commit

<<<<<<< Upstream, based on branch 'master' of http://jy@192.168.1.73:1000/r/mill/v1.0.git
				/**
				 * 0-返回机器人信息
				 */
				while(roboSocketConnection.getStatus() != 0) {					
					roboSocketConnection.askStatusRest();//  IPC write to Robot: 22；COMMAND_ASK_STATUS					
				}
				JOptionPane.showMessageDialog(null, "请把机器人移到正确位置，再继续进行示教！", "", JOptionPane.WARNING_MESSAGE);
=======

>>>>>>> e5cfef5 commit
				//  IPC write to Robot: 70； // COMMAND_ASK_POSITION（get destination Position）
				robotPosition = roboSocketConnection.getPosition();
				
<<<<<<< Upstream, based on branch 'master' of http://jy@192.168.1.73:1000/r/mill/v1.0.git
				/**
				 * 2049-b00: Pick request release和 b11: Teaching finished
				 */
				while(roboSocketConnection.getStatus() != 2049) {					
					roboSocketConnection.askStatusRest();//  IPC write to Robot: 22；COMMAND_ASK_STATUS					
				}
=======
>>>>>>> e5cfef5 commit

				// readR: WR220186
				startingRegisterNr = 22;
				amount = 1;
				cncSocketConnection.readRegisters(startingRegisterNr, amount);
				// write: WW18;02;37;8;
				startingRegisterNr = 18;
				values = new int[2];
				values[0] = 37;
				values[1] = 8;
				cncSocketConnection.writeRegisters(startingRegisterNr, values);

				//pick-PERMISSIONS_COMMAND_PICK_RELEASE_ACK-4
				roboSocketConnection.continuePickTillIPPoint();
				RobotStatusChangeThread.statusChanged(new StatusChangedEvent(StatusChangedEvent.ENDED, 0,Mode.TEACH));

				// write: WW58;01;5;
				startingRegisterNr = 58;
				values = new int[1];
				values[0] = 5;
				cncSocketConnection.writeRegisters(startingRegisterNr, values);

				// readR: WR220192
				startingRegisterNr = 22;
				amount = 1;
				cncSocketConnection.readRegisters(startingRegisterNr, amount);

				// readR: WR53011000
				startingRegisterNr = 53;
				amount = 1;
				cncSocketConnection.readRegisters(startingRegisterNr, amount);

				// write: WW19;01;32;
				startingRegisterNr = 19;
				values = new int[1];
				values[0] = 32;
				cncSocketConnection.writeRegisters(startingRegisterNr, values);

				// write: WW58;01;0;
				startingRegisterNr = 58;
				values = new int[1];
				values[0] = 0;
				cncSocketConnection.writeRegisters(startingRegisterNr, values);

<<<<<<< Upstream, based on branch 'master' of http://jy@192.168.1.73:1000/r/mill/v1.0.git
				/**
				 * 2048-b11: Teaching finished
				 */
				while(roboSocketConnection.getStatus() != 2048) {					
					roboSocketConnection.askStatusRest();//  IPC write to Robot: 22；COMMAND_ASK_STATUS					
				}
=======
>>>>>>> e5cfef5 commit

				//  IPC write to Robot: 75;13;3;192;192;0; //
				gripInner = false;
				serviceType = 13;
				roboSocketConnection.writeServiceGripperSet(headId, gHeadA, gHeadB, serviceType, gripInner);

				//  IPC write to Robot: 76;1;1;180;160;30;0;100;24;0;48;1;
				freeAfterService = true;
				roboSocketConnection.writeServiceHandlingSet(speed, freeAfterService, serviceHandlingPPMode, dimensions,
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
				roboSocketConnection.writeServicePointSet(workArea, location, smoothPoint, smoothPointZ, dimensions,
						clamping, approachType, zSafePlane);
				
				//	IPC write to Robot:  51;1;  // COMMAND_START_SERVICE （1）
				roboSocketConnection.startService();
				
<<<<<<< Upstream, based on branch 'master' of http://jy@192.168.1.73:1000/r/mill/v1.0.git
				//	IPC write to Robot:  50;2;  //  COMMAND_SET_PERMISSIONS（2）
				roboSocketConnection.writeCommand(2);
				
				// IPC write to Robot: 50;8;
				// roboSocketConnection.writeCommand(8);
				
				while(roboSocketConnection.getStatus() != 0) {					
					roboSocketConnection.askStatusRest();//  IPC write to Robot: 22；COMMAND_ASK_STATUS					
				}
				JOptionPane.showMessageDialog(null, "请把机器人移到正确位置，再继续进行示教！", "", JOptionPane.WARNING_MESSAGE);
=======
				//获取Put 权限PERMISSIONS_COMMAND_PUT2
				RobotStatusChangeThread.statusChanged(new StatusChangedEvent(StatusChangedEvent.EXECUTE_TEACHED, 1,Mode.TEACH));
				roboSocketConnection.continuePutTillAtLocation(true);
				RobotStatusChangeThread.statusChanged(new StatusChangedEvent(StatusChangedEvent.TEACHING_NEEDED, 1,Mode.TEACH));
				roboSocketConnection.continuePutTillClampAck(true);
				RobotStatusChangeThread.statusChanged(new StatusChangedEvent(StatusChangedEvent.TEACHING_FINISHED, 1,Mode.TEACH));

>>>>>>> e5cfef5 commit
				//	IPC write to Robot:  70； // COMMAND_ASK_POSITION（get destination Position）
<<<<<<< Upstream, based on branch 'master' of http://jy@192.168.1.73:1000/r/mill/v1.0.git
				Coordinates = roboSocketConnection.getPosition();
				
				while(roboSocketConnection.getStatus() != 0) {					
					roboSocketConnection.askStatusRest();//  IPC write to Robot: 22；COMMAND_ASK_STATUS
				}
				
				//	IPC write to Robot:  50;8;  //  COMMAND_SET_PERMISSIONS（8）
				roboSocketConnection.writeCommand(8);
				
				while(roboSocketConnection.getStatus() != 0) {					
					roboSocketConnection.askStatusRest();//  IPC write to Robot: 22；COMMAND_ASK_STATUS					
				}
=======
				robotPosition = roboSocketConnection.getPosition();
								
				//put释放PERMISSIONS_COMMAND_PUT8
				roboSocketConnection.continuePutTillIPPoint();
				RobotStatusChangeThread.statusChanged(new StatusChangedEvent(StatusChangedEvent.ENDED, 1,Mode.TEACH));


>>>>>>> e5cfef5 commit
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
			}
		}
	}
	public boolean needsTeaching() {
		return true;
	}
}
