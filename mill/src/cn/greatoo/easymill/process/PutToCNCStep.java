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
/**
 * 
 * ===put工件到机床===机器人put工件到机床，回到原点，机床关门加工工件，加工完成后打开门
 *
 */
public class PutToCNCStep extends AbstractStep{

	public void putToCNC(Program program, FanucRobot robot, CNCMachine cncMachine, boolean teached, Controller view) {
		try {
			Clamping Clampping =DBHandler.getInstance().getClampBuffer().get(0);
			//===put工件到机床=========================================================================================================			
			int serviceType = RobotConstants.SERVICE_GRIPPER_SERVICE_TYPE_PUT;//13;	
			//75
			checkProcessExecutorStatus(robot,cncMachine);
			robot.writeServiceGripperSet(program.getUnloadstacker().getGripperHead().getName(), program.getUnloadstacker().getGripper(),
					program.getUnloadstacker().getGripper(), serviceType, program.getUnloadstacker().getGripperHead().isGripperInner());
			boolean freeAfterService = true;
			int serviceHandlingPPMode = RobotConstants.SERVICE_HANDLING_PP_MODE_ORDER_12;
			if(teached) {
				serviceHandlingPPMode = serviceHandlingPPMode | RobotConstants.SERVICE_HANDLING_PP_MODE_TEACH;
			}
			int approachType = 1;
			float payLoad1 = program.getUnloadstacker().getWorkPiece().getWeight() * 10;
			float payLoad2 = 0;
			//76
			checkProcessExecutorStatus(robot,cncMachine);
			robot.writeServiceHandlingSet(robot.getSpeed(), freeAfterService, serviceHandlingPPMode, 
					program.getUnloadstacker().getWorkPiece(), approachType, payLoad1, payLoad2);
			//-----------------------------------------
			checkProcessExecutorStatus(robot,cncMachine);
			Coordinates originalPosition = new WorkPiecePositions(program).getPutLocation(Clampping);
			Coordinates position = new Coordinates(originalPosition);
			if (getLoadCNCRelativeTeachedOffset() == null) {
				//初始化安全示教偏移
				initSafeTeachedOffset(2,program,Clampping,originalPosition);
			}
			//计算绝对偏移(-1.5599976, 1.9199982, 2.45, 0.0, 0.0, 0.0)
			Coordinates absoluteOffset = TeachedCoordinatesCalculator.calculateAbsoluteOffset(position, getLoadCNCRelativeTeachedOffset());
			//(90.94, 109.42, 2.45, 0.0, 0.0, 90.0)
			position.offset(absoluteOffset);
			int workArea = DBHandler.getInstance().getUserFrameBuffer().get(3).getNumber();	
			Clamping clamping = DBHandler.getInstance().getClampBuffer().get(0);
			float zSafePlane = clamping.getHeight() + program.getUnloadstacker().getWorkPiece().getHeight() + clamping.getRelativePosition().getZ();
			float clampHeight = clamping.getHeight()  + clamping.getRelativePosition().getZ();
			//77
			checkProcessExecutorStatus(robot,cncMachine);
			robot.writeServicePointSet(workArea, position, program.getLoadCNC().getSmooth(),
					DBHandler.getInstance().getUserFrameBuffer().get(3).getzSafeDistance(), program.getUnloadstacker().getWorkPiece(),
					clampHeight, approachType, zSafePlane);	
			//------------------------------------------------------
			checkProcessExecutorStatus(robot,cncMachine);
			robot.startService();
			view.statusChanged(new StatusChangedEvent(StatusChangedEvent.PUT_TO_CNC));
			checkProcessExecutorStatus(robot,cncMachine);
			cncMachine.prepareForPut(false, 0,0);
			
			if(teached) {
				view.statusChanged(new StatusChangedEvent(StatusChangedEvent.EXECUTE_TEACHED));
				checkProcessExecutorStatus(robot,cncMachine);
				robot.continuePutTillAtLocation(true);
				view.statusChanged(new StatusChangedEvent(StatusChangedEvent.TEACHING_NEEDED));
				checkProcessExecutorStatus(robot,cncMachine);
				robot.continuePutTillClampAck(true);
				view.statusChanged(new StatusChangedEvent(StatusChangedEvent.TEACHING_FINISHED));
				checkProcessExecutorStatus(robot,cncMachine);
				Coordinates robotPosition = robot.getPosition();
				Coordinates relTeachedOffset = TeachedCoordinatesCalculator.calculateRelativeTeachedOffset(originalPosition, robotPosition.calculateOffset(originalPosition));
				setLoadCNCRelativeTeachedOffset(relTeachedOffset);
				updateProgramOffset(program.getLoadCNC().getOffset(), relTeachedOffset);
			}else {				
				checkProcessExecutorStatus(robot,cncMachine);
				robot.continuePutTillAtLocation(false);	
				checkProcessExecutorStatus(robot,cncMachine);
				robot.continuePutTillClampAck(false);
			}
			checkProcessExecutorStatus(robot,cncMachine);
			cncMachine.grabPiece();
			checkProcessExecutorStatus(robot,cncMachine);
			robot.continuePutTillIPPoint();
			checkProcessExecutorStatus(robot,cncMachine);
			cncMachine.pickFinished(0,false);			
			view.statusChanged(new StatusChangedEvent(StatusChangedEvent.CNC_PROCESSING));			
		}catch (InterruptedException | AbstractCommunicationException | RobotActionException | DeviceActionException e) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					TeachAndAutoThread.getView().setMessege("程序未启动！");
				}
			});
		}
	}
}
