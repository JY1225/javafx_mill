package cn.greatoo.easymill.process;

import cn.greatoo.easymill.cnc.CNCMachine;
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

public class PutToTableStep extends AbstractStep {

	public void putToTable(Program program, FanucRobot robot, CNCMachine cncMachine,
			WorkPiecePositions workPiecePositions, boolean teached, int wIndex, Controller view)
			throws InterruptedException, AbstractCommunicationException, RobotActionException {

		int serviceType = 13;
		// 75
		checkProcessExecutorStatus(robot, cncMachine);
		robot.writeServiceGripperSet(program.getUnloadCNC().getGripperHead().getName(),
				program.getUnloadCNC().getGripper(), program.getUnloadCNC().getGripper(), serviceType,
				program.getUnloadCNC().getGripperHead().isGripperInner());
		boolean freeAfterService = true;
		int serviceHandlingPPMode = RobotConstants.SERVICE_HANDLING_PP_MODE_ORDER_12;
		if (teached) {
			serviceHandlingPPMode = serviceHandlingPPMode | RobotConstants.SERVICE_HANDLING_PP_MODE_TEACH;
		}

		// float weight2 = -program.getUnloadCNC().getWorkPiece().getWeight();
		int approachType = 1;
		float payLoad1 = program.getFinishedWorkPiece().getWeight() * 10;
		float payLoad2 = 0;
		// 76
		checkProcessExecutorStatus(robot, cncMachine);
		robot.writeServiceHandlingSet(robot.getSpeed(), freeAfterService, serviceHandlingPPMode,
				program.getFinishedWorkPiece(), approachType, payLoad1, payLoad2);
		// -----------------------------------
		int workArea = 1;
		float zSafePlane = 0;
		Clamping clamping = DBHandler.getInstance().getClampBuffer().get(0);
		checkProcessExecutorStatus(robot, cncMachine);
		Coordinates originalPosition = workPiecePositions.getPickLocation(wIndex);
		;
		Coordinates position = new Coordinates(originalPosition);
		if (getLoadStackerRelativeTeachedOffset() == null) {
			initSafeTeachedOffset(4, program, clamping, originalPosition);
		}
		Coordinates absoluteOffset = TeachedCoordinatesCalculator.calculateAbsoluteOffset(position,
				getLoadStackerRelativeTeachedOffset());
		position.offset(absoluteOffset);
		float wh = program.getFinishedWorkPiece().getHeight();
		float sh = program.getStudHeight_Workpiece();
		if (wh >= sh) {
			zSafePlane = 2 * wh;
		} else {
			zSafePlane = wh + sh;
		}
		float clampHeight = sh;
		// 77
		checkProcessExecutorStatus(robot, cncMachine);
		robot.writeServicePointSet(workArea, position, program.getLoadstacker().getSmooth(),
				DBHandler.getInstance().getUserFrameBuffer().get(1).getzSafeDistance(), program.getFinishedWorkPiece(),
				clampHeight, approachType, zSafePlane);
		// --------------------------------------------------
		checkProcessExecutorStatus(robot, cncMachine);
		robot.startService();
		view.statusChanged(new StatusChangedEvent(StatusChangedEvent.PUT_TO_TABLE));
		if (teached) {
			view.statusChanged(new StatusChangedEvent(StatusChangedEvent.EXECUTE_TEACHED));
			checkProcessExecutorStatus(robot, cncMachine);
			robot.continuePutTillAtLocation(true);
			view.statusChanged(new StatusChangedEvent(StatusChangedEvent.TEACHING_NEEDED));
			checkProcessExecutorStatus(robot, cncMachine);
			robot.continuePutTillClampAck(true);
			view.statusChanged(new StatusChangedEvent(StatusChangedEvent.TEACHING_FINISHED));
			checkProcessExecutorStatus(robot, cncMachine);
			Coordinates robotPosition = robot.getPosition();
			Coordinates relTeachedOffset = TeachedCoordinatesCalculator.calculateRelativeTeachedOffset(originalPosition,
					robotPosition.calculateOffset(originalPosition));
			setLoadStackerRelativeTeachedOffset(relTeachedOffset);
			updateProgramOffset(program.getLoadstacker().getOffset(), relTeachedOffset);
		} else {
			checkProcessExecutorStatus(robot, cncMachine);
			robot.continuePutTillAtLocation(false);// 50,2
			checkProcessExecutorStatus(robot, cncMachine);
			robot.continuePutTillClampAck(false);
		}
		checkProcessExecutorStatus(robot, cncMachine);
		robot.continuePutTillIPPoint();// 50,8

	}
}
