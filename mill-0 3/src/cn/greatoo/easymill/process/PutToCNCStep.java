package cn.greatoo.easymill.process;

import cn.greatoo.easymill.cnc.CNCMachine;
import cn.greatoo.easymill.cnc.DeviceActionException;
import cn.greatoo.easymill.entity.Gripper;
import cn.greatoo.easymill.entity.GripperHead;
import cn.greatoo.easymill.entity.Gripper.Type;
import cn.greatoo.easymill.external.communication.socket.AbstractCommunicationException;
import cn.greatoo.easymill.process.StatusChangedEvent.Mode;
import cn.greatoo.easymill.robot.FanucRobot;
import cn.greatoo.easymill.robot.RobotActionException;
import cn.greatoo.easymill.ui.main.Controller;
import cn.greatoo.easymill.util.Clamping;
import cn.greatoo.easymill.util.Coordinates;
import cn.greatoo.easymill.workpiece.IWorkPieceDimensions;
import cn.greatoo.easymill.workpiece.RectangularDimensions;
import cn.greatoo.easymill.workpiece.WorkPiece;
import cn.greatoo.easymill.workpiece.WorkPiece.Material;

public class PutToCNCStep {

	public static void putToCNC(FanucRobot robot, CNCMachine cncMachine, boolean teached, Controller view) {
		try {
			//===put工件到机床=========================================================================================================
			Gripper gripper = new Gripper("name", Type.TWOPOINT, 190, "description", "");
			final String headId = "A";
			final GripperHead gHeadA = new GripperHead("jyA", null, gripper);
			final GripperHead gHeadB = new GripperHead("jyB", null, gripper);
			int serviceType = 13;
			boolean gripInner = true;
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
			robot.writeServicePointSet(workArea, location, smoothPoint, smoothPointZ, dimensions,
					clamping, approachType, zSafePlane);
			robot.startService();
			
			cncMachine.prepareForPut(false, 0,0);
			
			if(teached) {
				view.statusChanged(new StatusChangedEvent(StatusChangedEvent.EXECUTE_TEACHED, 1,Mode.TEACH));
				robot.continuePutTillAtLocation(true);
				view.statusChanged(new StatusChangedEvent(StatusChangedEvent.TEACHING_NEEDED, 1,Mode.TEACH));
				robot.continuePutTillClampAck(true);
				view.statusChanged(new StatusChangedEvent(StatusChangedEvent.TEACHING_FINISHED, 1,Mode.TEACH));
				Coordinates robotPosition = robot.getPosition();
			}else {
				view.statusChanged(new StatusChangedEvent(StatusChangedEvent.EXECUTE_NORMAL, 1,Mode.AUTO));
				robot.continuePutTillAtLocation(false);
				robot.continuePutTillClampAck(false);
			}
			
			cncMachine.grabPiece();
			robot.continuePutTillIPPoint();
			view.statusChanged(new StatusChangedEvent(StatusChangedEvent.ENDED, 1,Mode.TEACH));

			cncMachine.pickFinished(0,false);
		}catch (InterruptedException | AbstractCommunicationException | RobotActionException | DeviceActionException e) {
			e.printStackTrace();
		}
	}
}
