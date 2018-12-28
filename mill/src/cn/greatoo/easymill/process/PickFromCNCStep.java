package cn.greatoo.easymill.process;

import cn.greatoo.easymill.cnc.CNCMachine;
import cn.greatoo.easymill.cnc.DeviceActionException;
import cn.greatoo.easymill.entity.Gripper;
import cn.greatoo.easymill.entity.Gripper.Type;
import cn.greatoo.easymill.entity.GripperHead;
import cn.greatoo.easymill.external.communication.socket.AbstractCommunicationException;
import cn.greatoo.easymill.robot.FanucRobot;
import cn.greatoo.easymill.robot.RobotActionException;
import cn.greatoo.easymill.ui.main.Controller;
import cn.greatoo.easymill.util.Clamping;
import cn.greatoo.easymill.util.Coordinates;
import cn.greatoo.easymill.workpiece.IWorkPieceDimensions;
import cn.greatoo.easymill.workpiece.RectangularDimensions;
import cn.greatoo.easymill.workpiece.WorkPiece;
import cn.greatoo.easymill.workpiece.WorkPiece.Material;

public class PickFromCNCStep {

	public static void pickFromCNC(FanucRobot robot, CNCMachine cncMachine, boolean teached, Controller view) {
		try {
			Gripper gripper = new Gripper("name", Type.TWOPOINT, 190, "description", "");
			final String headId = "A";
			final GripperHead gHeadA = new GripperHead("jyA", null, gripper);
			final GripperHead gHeadB = new GripperHead("jyB", null, gripper);
			int serviceType = 12;
			boolean gripInner = false;
			robot.writeServiceGripperSet(headId, gHeadA, gHeadB, serviceType, gripInner);
			boolean freeAfterService = true;
			final int serviceHandlingPPMode = 16;
			final IWorkPieceDimensions dimensions = new RectangularDimensions(200, 170, 21);
			float weight2 = 16;
			int approachType = 1;
			WorkPiece wp1 = new WorkPiece(WorkPiece.Type.FINISHED, dimensions, Material.AL, 2.4f);
			WorkPiece wp2 = null;
			robot.writeServiceHandlingSet(robot.getSpeed(), freeAfterService, serviceHandlingPPMode, dimensions,
					weight2, approachType, wp1, wp2);
			int workArea = 3;
			Coordinates location = new Coordinates(0f, 0f, 0, 0, 0, 90);
			Coordinates smoothPoint = new Coordinates(0f, 0f, 0, 0, 0, 90);
			String name = "A";
			float defaultHeight = 0;
			Coordinates relativePosition = new Coordinates(1, 1, 0, 1, 1, 1);
			Coordinates smoothToPoint = null;
			Coordinates smoothFromPoint = null;
			String imageURL = "";
			Clamping clamping = new Clamping(Clamping.Type.CENTRUM, name, defaultHeight, relativePosition,
					smoothToPoint, smoothFromPoint, imageURL);
			approachType = 1;
			float zSafePlane = 21;
			float smoothPointZ = 20.5f;
			robot.writeServicePointSet(workArea, location, smoothPoint, smoothPointZ, dimensions, clamping,
					approachType, zSafePlane);
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
