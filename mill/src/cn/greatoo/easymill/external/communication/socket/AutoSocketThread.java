package cn.greatoo.easymill.external.communication.socket;

import javax.swing.JOptionPane;

import cn.greatoo.easymill.entity.Gripper;
import cn.greatoo.easymill.entity.Gripper.Type;
import cn.greatoo.easymill.entity.GripperHead;
import cn.greatoo.easymill.robot.FanucRobot;
import cn.greatoo.easymill.ui.main.Controller;
import cn.greatoo.easymill.util.Clamping;
import cn.greatoo.easymill.util.Coordinates;
import cn.greatoo.easymill.workpiece.IWorkPieceDimensions;
import cn.greatoo.easymill.workpiece.RectangularDimensions;
import cn.greatoo.easymill.workpiece.WorkPiece;
import cn.greatoo.easymill.workpiece.WorkPiece.Material;
import javafx.scene.control.Button;

/**
 * 自动化线程
 *
 */
public class AutoSocketThread extends Controller implements Runnable {

	// private RobotSocketCommunication roboSocketConnection;
	private CNCSocketCommunication cncSocketConnection;
	private boolean isAlive;
	FanucRobot roboSocketConnection;
	private Button startBt;
	
	public AutoSocketThread(RobotSocketCommunication roboSocketConnection,
			CNCSocketCommunication cncSocketConnection,Button startBt) {
		this.roboSocketConnection = new FanucRobot(roboSocketConnection);
		this.cncSocketConnection = cncSocketConnection;
		this.startBt =startBt;
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
				cncSocketConnection.writeRegisters(startingRegisterNr, values);
				cncSocketConnection.writeRegisters(startingRegisterNr, values);

				Gripper gripper = new Gripper("name", Type.TWOPOINT, 192, "description", "");
				final String headId = "A";
				final GripperHead gHeadA = new GripperHead("jyA", null, gripper);
				final GripperHead gHeadB = new GripperHead("jyB", null, gripper);
				int serviceType = 5;
				boolean gripInner = false;
				// 设置抓爪信息 
				roboSocketConnection.writeServiceGripperSet(headId, gHeadA, gHeadB, serviceType, gripInner);
				//计算TCPS
				roboSocketConnection.recalculateTCPs();
				int speed = 100;
				//回home点
				roboSocketConnection.moveToHome(speed);

				cncSocketConnection.writeRegisters(startingRegisterNr, values);
				startingRegisterNr = 18;
				values = new int[2];
				values[0] = 1;
				values[1] = 16;
				cncSocketConnection.writeRegisters(startingRegisterNr, values);
				serviceType = 12;
				//设置抓爪信息
				roboSocketConnection.writeServiceGripperSet(headId, gHeadA, gHeadB, serviceType, gripInner);

				boolean freeAfterService = false;
				final int serviceHandlingPPMode = 48;

				final IWorkPieceDimensions dimensions = new RectangularDimensions(180, 160, 30);
				final float weight2 = 16;
				int approachType = 1;
				WorkPiece wp1 = new WorkPiece(WorkPiece.Type.FINISHED, dimensions, Material.AL, 2.4f);
				WorkPiece wp2 = null;
				//设置机器人搬运信息
				roboSocketConnection.writeServiceHandlingSet(10, freeAfterService, serviceHandlingPPMode,
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
				//设置机器人位置信息
				roboSocketConnection.writeServicePointSet(workArea, location, smoothPoint, smoothPointZ, dimensions,
						clamping, approachType, zSafePlane);
				//发送开始命令
				roboSocketConnection.startService();
				//获取pick权限
				roboSocketConnection.writeCommand(1);//机器人动
				//设置速度
				roboSocketConnection.sendSpeed(speed);				
				//pick
				roboSocketConnection.writeCommand(4);
		

				//设置抓爪信息
				serviceType = 13;
				roboSocketConnection.writeServiceGripperSet(headId, gHeadA, gHeadB, serviceType, gripInner);
				//设置机器人搬运信息
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
				approachType = 1;
				zSafePlane = 239;
				smoothPointZ = 5;
				//设置机器人位置信息
				roboSocketConnection.writeServicePointSet(workArea, location, smoothPoint, smoothPointZ, dimensions,
						clamping, approachType, zSafePlane);

				//开始命令
				roboSocketConnection.startService();

				startingRegisterNr = 39;
				int amount = 1;
				cncSocketConnection.readRegisters(startingRegisterNr, amount);
				startingRegisterNr = 40;
				amount = 1;
				cncSocketConnection.readRegisters(startingRegisterNr, amount);
				startingRegisterNr = 40;
				amount = 1;
				cncSocketConnection.readRegisters(startingRegisterNr, amount);
				startingRegisterNr = 42;
				amount = 1;
				cncSocketConnection.readRegisters(startingRegisterNr, amount);
				startingRegisterNr = 22;
				amount = 1;
				cncSocketConnection.readRegisters(startingRegisterNr, amount);

				startingRegisterNr = 24;
				values = new int[1];
				values[0] = 0;
				cncSocketConnection.writeRegisters(startingRegisterNr, values);
				startingRegisterNr = 18;
				values = new int[2];
				values[0] = 37;
				values[1] = 1;
				cncSocketConnection.writeRegisters(startingRegisterNr, values);

				//获取Put 权限
				roboSocketConnection.writeCommand(2);				

				startingRegisterNr = 22;
				amount = 1;
				cncSocketConnection.readRegisters(startingRegisterNr, amount);

				startingRegisterNr = 18;
				values = new int[2];
				values[0] = 37;
				values[1] = 4;
				//选择 工作区域 ，选择机床夹具 write: WW18;02;37;4;
				cncSocketConnection.writeRegisters(startingRegisterNr, values);

				//put释放
				roboSocketConnection.writeCommand(8);
				//回到原点
				roboSocketConnection.moveToHome(speed);
				//回到原点
				roboSocketConnection.moveToHome(speed);

				startingRegisterNr = 19;
				values = new int[1];
				values[0] = 64;
				cncSocketConnection.writeRegisters(startingRegisterNr, values);

				// 设置抓爪信息
				gripInner = false;
				serviceType = 12;
				roboSocketConnection.writeServiceGripperSet(headId, gHeadA, gHeadB, serviceType, gripInner);

				// 设置机器人搬运信息
				freeAfterService = true;
				roboSocketConnection.writeServiceHandlingSet(speed, freeAfterService, serviceHandlingPPMode, dimensions,
						weight2, approachType, wp1, wp2);

				// 设置位置信息
				roboSocketConnection.writeServicePointSet(workArea, location, smoothPoint, smoothPointZ, dimensions,
						clamping, approachType, zSafePlane);

				// 开始
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

				// 获取PUT权限 
				roboSocketConnection.writeCommand(1);

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

				//获取JAWS_CHANGED_ACK权限
				roboSocketConnection.writeCommand(4);

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


				//设置机器人夹爪信息
				gripInner = false;
				serviceType = 13;
				roboSocketConnection.writeServiceGripperSet(headId, gHeadA, gHeadB, serviceType, gripInner);

				// 设置机器人搬运信息
				freeAfterService = true;
				roboSocketConnection.writeServiceHandlingSet(speed, freeAfterService, serviceHandlingPPMode, dimensions,
						weight2, approachType, wp1, wp2);

				// 设置机器人位置信息
				location = new Coordinates(97.5f, 87.5f, 16, 0, 0, 90);
				name = "A";
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
				
				//发送Start命令
				roboSocketConnection.startService();
				
				//获取PICK_RELEASE_ACK权限
				roboSocketConnection.writeCommand(2);
				
				while(roboSocketConnection.getStatus() != 0) {					
					roboSocketConnection.updateStatusRestAndAlarms();//  IPC write to Robot: 22；COMMAND_ASK_STATUS					
				}
				
				//获取MOVEWAIT_CONTINUE权限
				roboSocketConnection.writeCommand(8);
				//回到原点
				roboSocketConnection.moveToHome(speed);
				//回到原点
				roboSocketConnection.moveToHome(speed);
				//终止
				roboSocketConnection.abort();
				startBt.setDisable(false);
				isAlive = false;
			} catch (SocketDisconnectedException e) {
				e.printStackTrace();
			} catch (SocketResponseTimedOutException e) {
				e.printStackTrace();
			} catch (SocketWrongResponseException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (AbstractCommunicationException e) {
				e.printStackTrace();
			} 
		}
	}

}
