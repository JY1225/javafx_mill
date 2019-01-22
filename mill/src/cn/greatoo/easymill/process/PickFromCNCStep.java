package cn.greatoo.easymill.process;

import cn.greatoo.easymill.cnc.CNCMachine;
import cn.greatoo.easymill.cnc.DeviceActionException;
import cn.greatoo.easymill.db.util.DBHandler;
import cn.greatoo.easymill.entity.Clamping;
import cn.greatoo.easymill.entity.Coordinates;
import cn.greatoo.easymill.entity.Program;
import cn.greatoo.easymill.external.communication.socket.AbstractCommunicationException;
import cn.greatoo.easymill.robot.FanucRobot;
import cn.greatoo.easymill.robot.RobotActionException;
import cn.greatoo.easymill.ui.main.Controller;
import cn.greatoo.easymill.util.RobotConstants;
import cn.greatoo.easymill.util.TeachedCoordinatesCalculator;

public class PickFromCNCStep extends AbstractStep{

	@SuppressWarnings("static-access")
	public void pickFromCNC(Program program, FanucRobot robot, CNCMachine cncMachine, boolean teached, Controller view) {
		try {
			int serviceType = RobotConstants.SERVICE_GRIPPER_SERVICE_TYPE_PICK;//12;
			boolean gripInner = false;
			//75
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
			float payLoad2 = program.getUnloadCNC().getWorkPiece().getWeight();
			//76
			robot.writeServiceHandlingSet(robot.getSpeed(), freeAfterService, serviceHandlingPPMode,
					program.getUnloadCNC().getWorkPiece(), approachType, payLoad1, payLoad2);
			
			//-----------------------------------------------------
			int workArea = 3;
			Clamping clamping = DBHandler.getInstance().getClampBuffer().get(0);
			Coordinates originalPosition = WorkPiecePositions.getPutLocation(clamping);
			Coordinates position = new Coordinates(originalPosition);
			if (getUnloadCNCRelativeTeachedOffset() == null) {
				//初始化安全示教偏移
				initSafeTeachedOffset(3,program.getUnloadstacker().getWorkPiece(),clamping,originalPosition);
			}
			//计算绝对偏移(-1.5599976, 1.9199982, 2.45, 0.0, 0.0, 0.0)
			Coordinates absoluteOffset = TeachedCoordinatesCalculator.calculateAbsoluteOffset(position, getUnloadCNCRelativeTeachedOffset());
			//(90.94, 109.42, 2.45, 0.0, 0.0, 90.0)
			position.offset(absoluteOffset);			
			float zSafePlane = clamping.getHeight() + program.getUnloadCNC().getWorkPiece().getHeight() + clamping.getRelativePosition().getZ();
			//77
			robot.writeServicePointSet(workArea, position, program.getUnloadCNC().getSmooth(),
					DBHandler.getInstance().getUserFrameBuffer().get(3).getzSafeDistance(), program.getUnloadCNC().getWorkPiece(), 
					clamping, approachType, zSafePlane);
			//-------------------------------------------------
			robot.startService();						
			cncMachine.prepareForPick(false, 0, 1);
			view.statusChanged(new StatusChangedEvent(StatusChangedEvent.PICK_FROM_CNC));
			if (teached) {
				view.statusChanged(new StatusChangedEvent(StatusChangedEvent.EXECUTE_TEACHED));
				robot.continuePickTillAtLocation(true);
				view.statusChanged(new StatusChangedEvent(StatusChangedEvent.TEACHING_NEEDED));
				robot.continuePickTillUnclampAck(true);
				view.statusChanged(new StatusChangedEvent(StatusChangedEvent.TEACHING_FINISHED));
				Coordinates robotPosition = robot.getPosition();
				Coordinates relTeachedOffset = TeachedCoordinatesCalculator.calculateRelativeTeachedOffset(originalPosition, robotPosition.calculateOffset(originalPosition));
				setUnloadCNCRelativeTeachedOffset(relTeachedOffset);
			} else {
				robot.continuePickTillAtLocation(false);// 50,1
				robot.continuePickTillUnclampAck(false);
			}

			cncMachine.releasePiece();// 22;18
			robot.continuePickTillIPPoint();// 50,4
			// view.statusChanged(new StatusChangedEvent(StatusChangedEvent.ENDED,
			// 0,Mode.TEACH));

			// cncMachine.prepareForIntervention();
			cncMachine.pickFinished(0, true);// 22;53;19
			// cncMachine.clearIndications();

		} catch (InterruptedException | AbstractCommunicationException | RobotActionException
				| DeviceActionException e) {
			e.printStackTrace();
		}
	}
	
	
}
