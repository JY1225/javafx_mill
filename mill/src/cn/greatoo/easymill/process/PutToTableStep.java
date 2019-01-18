package cn.greatoo.easymill.process;

import cn.greatoo.easymill.cnc.CNCMachine;
import cn.greatoo.easymill.entity.Gripper;
import cn.greatoo.easymill.entity.Gripper.Type;
import cn.greatoo.easymill.entity.GripperHead;
import cn.greatoo.easymill.entity.Program;
import cn.greatoo.easymill.external.communication.socket.AbstractCommunicationException;
import cn.greatoo.easymill.robot.FanucRobot;
import cn.greatoo.easymill.robot.RobotActionException;
import cn.greatoo.easymill.ui.main.Controller;
import cn.greatoo.easymill.util.Clamping;
import cn.greatoo.easymill.util.Coordinates;
import cn.greatoo.easymill.util.RobotConstants;
import cn.greatoo.easymill.workpiece.IWorkPieceDimensions;
import cn.greatoo.easymill.workpiece.RectangularDimensions;
import cn.greatoo.easymill.workpiece.WorkPiece;
import cn.greatoo.easymill.workpiece.WorkPiece.Material;

public class PutToTableStep {

	public static void putToTable(Program program, FanucRobot robot, CNCMachine cncMachine, boolean teached, Controller view) {
		try {
			Gripper gripper = new Gripper("name", Type.TWOPOINT, 190, "description", "");
			final String headId = "A";
			final GripperHead gHeadA = new GripperHead("jyA", null, gripper);
			final GripperHead gHeadB = new GripperHead("jyB", null, gripper);
			int serviceType = 13;
			boolean gripInner = false;				
			robot.writeServiceGripperSet(headId, gHeadA, gHeadB, serviceType, gripInner);
			boolean freeAfterService = true;
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
			int workArea = 1;
			Coordinates location = new Coordinates(92.5f, 107.5f, 25, 0, 0, 90);
			Coordinates smoothPoint = new Coordinates(0f, 0f, 0, 0, 0, 90);
			String name = "A";
			float defaultHeight = -5;
			Coordinates relativePosition = new Coordinates(5, 0, 5, 1, 1, 1);
			Coordinates smoothToPoint = null;
			Coordinates smoothFromPoint = null;
			String imageURL = "";
			Clamping clamping = new Clamping(Clamping.Type.CENTRUM, name, defaultHeight, relativePosition,
					smoothToPoint, smoothFromPoint, imageURL);
			approachType = 1;
			float zSafePlane = 21;
			float smoothPointZ = 20.5f;
			clamping = new Clamping(Clamping.Type.CENTRUM, name, defaultHeight, relativePosition, smoothToPoint,
					smoothFromPoint, imageURL);
			approachType = 1;
			zSafePlane = 42;
			smoothPointZ = 25;
			robot.writeServicePointSet(workArea, location, smoothPoint, smoothPointZ, dimensions,
					clamping, approachType, zSafePlane);
			robot.startService();
			view.statusChanged(new StatusChangedEvent(StatusChangedEvent.PUT_TO_TABLE));
			if(teached) {
				view.statusChanged(new StatusChangedEvent(StatusChangedEvent.EXECUTE_TEACHED));
				robot.continuePutTillAtLocation(true);
				view.statusChanged(new StatusChangedEvent(StatusChangedEvent.TEACHING_NEEDED));
				robot.continuePutTillClampAck(true);
				view.statusChanged(new StatusChangedEvent(StatusChangedEvent.TEACHING_FINISHED));
				Coordinates robotPosition = robot.getPosition();
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
