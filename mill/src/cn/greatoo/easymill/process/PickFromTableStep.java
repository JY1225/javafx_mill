package cn.greatoo.easymill.process;

import cn.greatoo.easymill.cnc.CNCMachine;
import cn.greatoo.easymill.db.util.DBHandler;
import cn.greatoo.easymill.entity.Coordinates;
import cn.greatoo.easymill.entity.Program;
import cn.greatoo.easymill.external.communication.socket.AbstractCommunicationException;
import cn.greatoo.easymill.robot.FanucRobot;
import cn.greatoo.easymill.robot.RobotActionException;
import cn.greatoo.easymill.ui.main.Controller;
import cn.greatoo.easymill.util.RobotConstants;
import cn.greatoo.easymill.util.TeachedCoordinatesCalculator;

/**
 * ===从table抓取工件===机器人抓取工件，回到原点
 *
 */
public class PickFromTableStep extends AbstractStep{

	@SuppressWarnings("static-access")
	public void pickFromTable(Program program, FanucRobot robot, CNCMachine cncMachine, boolean teached, int wIndex, Controller view) {
		
		try {			

			int serviceType = RobotConstants.SERVICE_GRIPPER_SERVICE_TYPE_PICK;//12;			
			boolean gripInner = true;
			//75
			robot.writeServiceGripperSet(program.getUnloadstacker().getGripperHead().getName(), program.getUnloadstacker().getGripper(),
					program.getUnloadCNC().getGripper(), serviceType, program.getUnloadstacker().getGripperHead().isGripperInner());
			boolean freeAfterService = false;
			int serviceHandlingPPMode = RobotConstants.SERVICE_HANDLING_PP_MODE_ORDER_12;
			if(teached) {
				serviceHandlingPPMode = serviceHandlingPPMode | RobotConstants.SERVICE_HANDLING_PP_MODE_TEACH;
			}

			int approachType = 1;
			float payLoad1 = 0;
			float payLoad2 = program.getUnloadstacker().getWorkPiece().getWeight();
			//76
			robot.writeServiceHandlingSet(robot.getSpeed(), freeAfterService, serviceHandlingPPMode, 
					program.getUnloadstacker().getWorkPiece(), approachType, payLoad1, payLoad2);
			//----------------------------------------------------
			WorkPiecePositions wpositions = new WorkPiecePositions();
			wpositions.initializeRawWorkPiecePositionsDeg90(program.getUnloadstacker().getWorkPiece());
			//(92.5, 107.5, 0.0, 0.0, 0.0, 90.0)
			Coordinates originalPosition = wpositions.getPickLocation(wIndex);
			Coordinates position = null;
			if (teached) {
				position = new Coordinates(originalPosition);
				if (getRelativeTeachedOffset() == null) {
					//初始化安全示教偏移
					initSafeTeachedOffset(originalPosition);
				}
				//计算绝对偏移(-1.5599976, 1.9199982, 2.45, 0.0, 0.0, 0.0)
				Coordinates absoluteOffset = TeachedCoordinatesCalculator.calculateAbsoluteOffset(position, getRelativeTeachedOffset());
				//(90.94, 109.42, 2.45, 0.0, 0.0, 90.0)
				position.offset(absoluteOffset);
			}else {
				position = originalPosition;
			}
			//-----------------------------------------------------------
			int workArea = 1;
			//Coordinates location = new Coordinates(90.94f, 109.42f, 2.45f, 0, 0, 90);//
			approachType = 1;
			float zSafePlane = 0;
			float wh = program.getUnloadstacker().getWorkPiece().getHeight();
			float sh = DBHandler.getInstance().getStatckerBuffer().get(0).getStudHeight_Stacker();
			if(wh >= sh) {
				zSafePlane = 2*wh;
			}else {
				zSafePlane = wh + sh;
			}
			//77
			robot.writeServicePointSet(workArea, position, program.getUnloadstacker().getSmooth(), 
					DBHandler.getInstance().getUserFrameBuffer().get(1).getzSafeDistance(), program.getUnloadstacker().getWorkPiece(), 
					DBHandler.getInstance().getClampBuffer().get(0),
					approachType, zSafePlane);
			robot.startService();
			view.statusChanged(new StatusChangedEvent(StatusChangedEvent.PICK_FROM_TABLE));
			if (teached) {
				view.statusChanged(new StatusChangedEvent(StatusChangedEvent.EXECUTE_TEACHED));
				robot.continuePickTillAtLocation(true);
				view.statusChanged(new StatusChangedEvent(StatusChangedEvent.TEACHING_NEEDED));
				robot.continuePickTillUnclampAck(true);
				view.statusChanged(new StatusChangedEvent(StatusChangedEvent.TEACHING_FINISHED));
				Coordinates robotPosition = robot.getPosition(); // 90.94f, 109.42f, 2.45f, 0, 0, 90
			} else {				
				robot.continuePickTillAtLocation(false);
				robot.continuePickTillUnclampAck(false);
			}

			robot.continuePickTillIPPoint();
		} catch (InterruptedException | AbstractCommunicationException | RobotActionException e) {
			e.printStackTrace();
		}
	}
}
