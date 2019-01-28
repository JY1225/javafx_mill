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

public class PutToTableStep extends AbstractStep{

	@SuppressWarnings("static-access")
	public void putToTable(Program program, FanucRobot robot, CNCMachine cncMachine, boolean teached, int wIndex, Controller view) {
		try {

			int serviceType = 13;	
			//75
			robot.writeServiceGripperSet(program.getLoadstacker().getGripperHead().getName(), 
					program.getLoadCNC().getGripper(), program.getUnloadCNC().getGripper(), serviceType, 
					program.getLoadstacker().getGripperHead().isGripperInner());
			boolean freeAfterService = true;
			int serviceHandlingPPMode = RobotConstants.SERVICE_HANDLING_PP_MODE_ORDER_12;
			if(teached) {
				serviceHandlingPPMode = serviceHandlingPPMode | RobotConstants.SERVICE_HANDLING_PP_MODE_TEACH;
			}

			float weight2 = -program.getLoadstacker().getWorkPiece().getWeight();
			int approachType = 1;
			float payLoad1 = program.getLoadstacker().getWorkPiece().getWeight() * 10;
			float payLoad2 = 0;
			//76
			robot.writeServiceHandlingSet(robot.getSpeed(), freeAfterService, serviceHandlingPPMode,
					program.getLoadstacker().getWorkPiece(), approachType,  payLoad1, payLoad2);
			//-----------------------------------
			int workArea = 1;
			float zSafePlane = 0;
			Clamping clamping = DBHandler.getInstance().getClampBuffer().get(0);
			Coordinates originalPosition = WorkPiecePositions.getPickLocation(wIndex);;
			Coordinates position = new Coordinates(originalPosition);
			if (getLoadStackerRelativeTeachedOffset() == null) {
				initSafeTeachedOffset(4,program.getUnloadstacker().getWorkPiece(),clamping,originalPosition);
			}			
			Coordinates absoluteOffset = TeachedCoordinatesCalculator.calculateAbsoluteOffset(position, getLoadStackerRelativeTeachedOffset());			
			position.offset(absoluteOffset);		
			float wh = program.getUnloadstacker().getWorkPiece().getHeight();
			float sh = DBHandler.getInstance().getStatckerBuffer().get(0).getStudHeight_Workpiece();
			if(wh >= sh) {
				zSafePlane = 2*wh;	
			}else {
				zSafePlane = wh + sh;							
			}
			float clampHeight = sh;
			//77
			robot.writeServicePointSet(workArea, position, program.getLoadstacker().getSmooth(), 
					DBHandler.getInstance().getUserFrameBuffer().get(1).getzSafeDistance(), program.getLoadstacker().getWorkPiece(), 
					clampHeight,
					approachType, zSafePlane);
			//--------------------------------------------------
			robot.startService();
			view.statusChanged(new StatusChangedEvent(StatusChangedEvent.PUT_TO_TABLE));
			if(teached) {
				view.statusChanged(new StatusChangedEvent(StatusChangedEvent.EXECUTE_TEACHED));
				robot.continuePutTillAtLocation(true);
				view.statusChanged(new StatusChangedEvent(StatusChangedEvent.TEACHING_NEEDED));
				robot.continuePutTillClampAck(true);
				view.statusChanged(new StatusChangedEvent(StatusChangedEvent.TEACHING_FINISHED));
				Coordinates robotPosition = robot.getPosition();
				Coordinates relTeachedOffset = TeachedCoordinatesCalculator.calculateRelativeTeachedOffset(originalPosition, robotPosition.calculateOffset(originalPosition));
				setLoadStackerRelativeTeachedOffset(relTeachedOffset);
				updateProgramOffset(program.getLoadstacker().getOffset(), relTeachedOffset);
			}else {
				robot.continuePutTillAtLocation(false);//50,2
				robot.continuePutTillClampAck(false);
			}
			
			robot.continuePutTillIPPoint();//50,8
		}catch (InterruptedException | AbstractCommunicationException | RobotActionException e) {
			e.printStackTrace();
		}
	}
}
