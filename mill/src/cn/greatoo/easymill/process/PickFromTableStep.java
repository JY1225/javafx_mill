package cn.greatoo.easymill.process;

import cn.greatoo.easymill.cnc.CNCMachine;
import cn.greatoo.easymill.entity.Clamping;
import cn.greatoo.easymill.entity.Coordinates;
import cn.greatoo.easymill.entity.Gripper;
import cn.greatoo.easymill.entity.Gripper.Type;
import cn.greatoo.easymill.entity.GripperHead;
import cn.greatoo.easymill.entity.WorkPiece;
import cn.greatoo.easymill.entity.WorkPiece.Material;
import cn.greatoo.easymill.external.communication.socket.AbstractCommunicationException;
import cn.greatoo.easymill.robot.FanucRobot;
import cn.greatoo.easymill.robot.RobotActionException;
import cn.greatoo.easymill.ui.main.Controller;
import cn.greatoo.easymill.util.RobotConstants;
import cn.greatoo.easymill.util.TeachedCoordinatesCalculator;
import cn.greatoo.easymill.workpiece.IWorkPieceDimensions;
import cn.greatoo.easymill.workpiece.RectangularDimensions;

/**
 * ===从table抓取工件===机器人抓取工件，回到原点
 *
 */
public class PickFromTableStep {

	public static void pickFromTable(FanucRobot robot, CNCMachine cncMachine, boolean teached, int wIndex, Controller view) {
		
		try {			
			Gripper gripper = new Gripper("name", Type.TWOPOINT, 190, "description", "");
			final String headId = "A";
			final GripperHead gHeadA = new GripperHead("jyA", null, gripper);
			final GripperHead gHeadB = new GripperHead("jyB", null, gripper);
			int serviceType = RobotConstants.SERVICE_GRIPPER_SERVICE_TYPE_PICK;//12;			
			boolean gripInner = true;
			
			robot.writeServiceGripperSet(headId, gHeadA, gHeadB, serviceType, gripInner);
			boolean freeAfterService = false;
			int serviceHandlingPPMode = RobotConstants.SERVICE_HANDLING_PP_MODE_ORDER_12;
			if(teached) {
				serviceHandlingPPMode = serviceHandlingPPMode | RobotConstants.SERVICE_HANDLING_PP_MODE_TEACH;
			}
			final IWorkPieceDimensions dimensions = new RectangularDimensions(200, 170, 21);
			float weight2 = 16;
			int approachType = 1;
			WorkPiece wp1 = new WorkPiece(WorkPiece.Type.FINISHED, dimensions, Material.AL, 2.4f);
			WorkPiece wp2 = null;
			
			robot.writeServiceHandlingSet(robot.getSpeed(), freeAfterService, serviceHandlingPPMode, dimensions,
					weight2, approachType, wp1, wp2);
			//----------------------------------------------------
			WorkPiecePositions wpositions = new WorkPiecePositions();
			wpositions.initializeRawWorkPiecePositionsDeg90((RectangularDimensions)dimensions);
			//(92.5, 107.5, 0.0, 0.0, 0.0, 90.0)
			Coordinates originalPosition = wpositions.getPickLocation(wIndex);
			Coordinates location = null;
			if (teached) {
				location = new Coordinates(originalPosition);
				//(1.9199982, 1.5599976, 2.45, 0.0, 0.0, 0.0)
				Coordinates c = wpositions.getRelativeTeachedOffset(745);
				//计算绝对偏移(-1.5599976, 1.9199982, 2.45, 0.0, 0.0, 0.0)
				Coordinates absoluteOffset = TeachedCoordinatesCalculator.calculateAbsoluteOffset(location, c);
				//(90.94, 109.42, 2.45, 0.0, 0.0, 90.0)
				location.offset(absoluteOffset);
			}else {
				location = originalPosition;
			}
			//-----------------------------------------------------------
			int workArea = 1;
			//Coordinates location = new Coordinates(90.94f, 109.42f, 2.45f, 0, 0, 90);//
			Coordinates smoothPoint = new Coordinates(5f, 0f, 5, 0, 5, 90);
			String name = "A";
			float defaultHeight = 0;
			Coordinates relativePosition = new Coordinates(1, 1, 0, 1, 1, 1);
			Coordinates smoothToPoint = null;
			Coordinates smoothFromPoint = null;
			String imageURL = "";
			Clamping clamping = new Clamping(Clamping.Type.CENTRUM, name, defaultHeight, relativePosition,
					smoothToPoint, smoothFromPoint, imageURL);
			approachType = 1;
			float zSafePlane = 42;
			float smoothPointZ = 25;
			
			robot.writeServicePointSet(workArea, location, smoothPoint, smoothPointZ, dimensions, clamping,
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
