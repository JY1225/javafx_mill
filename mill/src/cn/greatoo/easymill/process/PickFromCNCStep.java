package cn.greatoo.easymill.process;

import cn.greatoo.easymill.cnc.CNCMachine;
import cn.greatoo.easymill.cnc.DeviceActionException;
import cn.greatoo.easymill.db.util.DBHandler;
import cn.greatoo.easymill.entity.Clamping;
import cn.greatoo.easymill.entity.Coordinates;
import cn.greatoo.easymill.entity.Program;
import cn.greatoo.easymill.external.communication.socket.AbstractCommunicationException;
import cn.greatoo.easymill.external.communication.socket.TeachAndAutoThread;
import cn.greatoo.easymill.robot.FanucRobot;
import cn.greatoo.easymill.robot.RobotActionException;
import cn.greatoo.easymill.ui.main.Controller;
import cn.greatoo.easymill.util.RobotConstants;
import cn.greatoo.easymill.util.TeachedCoordinatesCalculator;
import javafx.application.Platform;

public class PickFromCNCStep extends AbstractStep{

	@SuppressWarnings({ "unused" })
	public void pickFromCNC(Program program, FanucRobot robot, CNCMachine cncMachine, boolean teached, Controller view) {
		try {
			int serviceType = RobotConstants.SERVICE_GRIPPER_SERVICE_TYPE_PICK;//12;
			boolean gripInner = false;
			//75
			checkProcessExecutorStatus(robot,cncMachine);
			robot.writeServiceGripperSet(program.getUnloadCNC().getGripperHead().getName(), 
					program.getLoadCNC().getGripper(), program.getUnloadCNC().getGripper(), serviceType,
					program.getUnloadCNC().getGripperHead().isGripperInner());
			boolean freeAfterService = false;
			int serviceHandlingPPMode = RobotConstants.SERVICE_HANDLING_PP_MODE_ORDER_12;
			if(teached) {
				serviceHandlingPPMode = serviceHandlingPPMode | RobotConstants.SERVICE_HANDLING_PP_MODE_TEACH;
			}
			float weight2 = program.getUnloadCNC().getWorkPiece().getWeight();
			int approachType = 1;
			float payLoad1 = 0;
			float payLoad2 = program.getUnloadCNC().getWorkPiece().getWeight() * 10;
			//76
			checkProcessExecutorStatus(robot,cncMachine);
			robot.writeServiceHandlingSet(robot.getSpeed(), freeAfterService, serviceHandlingPPMode,
					program.getUnloadCNC().getWorkPiece(), approachType, payLoad1, payLoad2);
			
			//-----------------------------------------------------
			int workArea = 3;
			Clamping clamping = DBHandler.getInstance().getClampBuffer().get(0);
			checkProcessExecutorStatus(robot,cncMachine);
			Coordinates originalPosition = new WorkPiecePositions(program).getPutLocation(clamping);
			Coordinates position = new Coordinates(originalPosition);
			if (getUnloadCNCRelativeTeachedOffset() == null) {
				//初始化安全示教偏移
				initSafeTeachedOffset(3,program,clamping,originalPosition);
			}
			//计算绝对偏移(-1.5599976, 1.9199982, 2.45, 0.0, 0.0, 0.0)
			Coordinates absoluteOffset = TeachedCoordinatesCalculator.calculateAbsoluteOffset(position, getUnloadCNCRelativeTeachedOffset());
			//(90.94, 109.42, 2.45, 0.0, 0.0, 90.0)
			position.offset(absoluteOffset);			
			float zSafePlane = clamping.getHeight() + program.getUnloadCNC().getWorkPiece().getHeight() + clamping.getRelativePosition().getZ();
			float clampHeight = clamping.getHeight()  + clamping.getRelativePosition().getZ();

			//77
			checkProcessExecutorStatus(robot,cncMachine);
			robot.writeServicePointSet(workArea, position, program.getUnloadCNC().getSmooth(),
					DBHandler.getInstance().getUserFrameBuffer().get(3).getzSafeDistance(), program.getUnloadCNC().getWorkPiece(), 
					clampHeight, approachType, zSafePlane);
			//-------------------------------------------------				
			checkProcessExecutorStatus(robot,cncMachine);
			cncMachine.prepareForPick(false, 0, 1);
			checkProcessExecutorStatus(robot,cncMachine);
			robot.startService();	
			view.statusChanged(new StatusChangedEvent(StatusChangedEvent.PICK_FROM_CNC));
			if (teached) {
				view.statusChanged(new StatusChangedEvent(StatusChangedEvent.EXECUTE_TEACHED));
				checkProcessExecutorStatus(robot,cncMachine);
				robot.continuePickTillAtLocation(true);
				view.statusChanged(new StatusChangedEvent(StatusChangedEvent.TEACHING_NEEDED));
				checkProcessExecutorStatus(robot,cncMachine);
				robot.continuePickTillUnclampAck(true);
				view.statusChanged(new StatusChangedEvent(StatusChangedEvent.TEACHING_FINISHED));
				checkProcessExecutorStatus(robot,cncMachine);
				Coordinates robotPosition = robot.getPosition();
				Coordinates relTeachedOffset = TeachedCoordinatesCalculator.calculateRelativeTeachedOffset(originalPosition, robotPosition.calculateOffset(originalPosition));
				setUnloadCNCRelativeTeachedOffset(relTeachedOffset);
				updateProgramOffset(program.getUnloadCNC().getOffset(), relTeachedOffset);
			} else {
				checkProcessExecutorStatus(robot,cncMachine);
				robot.continuePickTillAtLocation(false);// 50,1
				checkProcessExecutorStatus(robot,cncMachine);
				robot.continuePickTillUnclampAck(false);
			}
			checkProcessExecutorStatus(robot,cncMachine);
			cncMachine.releasePiece();// 22;18
			checkProcessExecutorStatus(robot,cncMachine);
			robot.continuePickTillIPPoint();// 50,4
			checkProcessExecutorStatus(robot,cncMachine);
			cncMachine.pickFinished(0, true);// 22;53;19

		} catch (InterruptedException | AbstractCommunicationException | RobotActionException
				| DeviceActionException e) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					TeachAndAutoThread.getView().setMessege("程序未启动！");
				}
			});
		}
	}
	
	
}
