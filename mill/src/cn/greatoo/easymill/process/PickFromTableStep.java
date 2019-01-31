package cn.greatoo.easymill.process;

import cn.greatoo.easymill.cnc.CNCMachine;
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

/**
 * ===从table抓取工件===机器人抓取工件，回到原点
 *
 */
public class PickFromTableStep extends AbstractStep{

	@SuppressWarnings("static-access")
	public void pickFromTable(Program program, FanucRobot robot, CNCMachine cncMachine, boolean teached, int wIndex, Controller view) {		
		try {	
			WorkPiecePositions workPiecePositions = new WorkPiecePositions(DBHandler.getInstance().getStatckerBuffer().get(0));
			Clamping clamping =DBHandler.getInstance().getClampBuffer().get(0);
			int serviceType = RobotConstants.SERVICE_GRIPPER_SERVICE_TYPE_PICK;//12;			
			//75
			checkProcessExecutorStatus(robot,cncMachine);
			robot.writeServiceGripperSet(program.getUnloadstacker().getGripperHead().getName(), program.getUnloadstacker().getGripper(),
					program.getUnloadCNC().getGripper(), serviceType, program.getUnloadstacker().getGripperHead().isGripperInner());
			boolean freeAfterService = false;
			int serviceHandlingPPMode = RobotConstants.SERVICE_HANDLING_PP_MODE_ORDER_12;
			if(teached) {
				serviceHandlingPPMode = serviceHandlingPPMode | RobotConstants.SERVICE_HANDLING_PP_MODE_TEACH;
			}
			
			int approachType = 1;
			float payLoad1 = 0;
			float payLoad2 = program.getUnloadstacker().getWorkPiece().getWeight() * 10;
			//76
			checkProcessExecutorStatus(robot,cncMachine);
			robot.writeServiceHandlingSet(robot.getSpeed(), freeAfterService, serviceHandlingPPMode, 
					program.getUnloadstacker().getWorkPiece(), approachType, payLoad1, payLoad2);
			//----------------------------------------------------
			checkProcessExecutorStatus(robot,cncMachine);
			workPiecePositions.initStackingPositions(program.getUnloadstacker().getWorkPiece());
			Coordinates originalPosition = WorkPiecePositions.getPickLocation(wIndex);//(75.0, 105.0, 0.0, 0.0, 0.0, 90.0)			
			Coordinates position = new Coordinates(originalPosition);			
			if (getUnloadStackerRelativeTeachedOffset() == null) {///?????
				initSafeTeachedOffset(1,program.getUnloadstacker().getWorkPiece(),clamping,originalPosition);
			}
			Coordinates absoluteOffset = TeachedCoordinatesCalculator.calculateAbsoluteOffset(position, getUnloadStackerRelativeTeachedOffset());
			position.offset(absoluteOffset);
			
			//-----------------------------------------------------------
			int workArea = 1;
			approachType = 1;
			float zSafePlane = 0;
			float wh = program.getUnloadstacker().getWorkPiece().getHeight();
			float sh = DBHandler.getInstance().getStatckerBuffer().get(0).getStudHeight_Workpiece();
			if(wh >= sh) {
				zSafePlane = 2*wh;	
			}else {
				zSafePlane = wh + sh;							
			}
			float clampHeight = sh;
			//77
			checkProcessExecutorStatus(robot,cncMachine);
			robot.writeServicePointSet(workArea, position, program.getUnloadstacker().getSmooth(), 
					DBHandler.getInstance().getUserFrameBuffer().get(1).getzSafeDistance(), program.getUnloadstacker().getWorkPiece(), 
					clampHeight,
					approachType, zSafePlane);
			checkProcessExecutorStatus(robot,cncMachine);
			robot.startService();
			view.statusChanged(new StatusChangedEvent(StatusChangedEvent.PICK_FROM_TABLE));
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
				setUnloadStackerRelativeTeachedOffset(relTeachedOffset);
				updateProgramOffset(program.getUnloadstacker().getOffset(), relTeachedOffset);
				
			} else {		
				checkProcessExecutorStatus(robot,cncMachine);
				robot.continuePickTillAtLocation(false);
				checkProcessExecutorStatus(robot,cncMachine);
				robot.continuePickTillUnclampAck(false);
			}
			checkProcessExecutorStatus(robot,cncMachine);
			robot.continuePickTillIPPoint();
		} catch (InterruptedException | AbstractCommunicationException | RobotActionException e) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					TeachAndAutoThread.getView().setMessege("程序未启动！");
				}
			});
		}
	}
	
}
