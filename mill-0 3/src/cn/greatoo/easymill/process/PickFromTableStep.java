package cn.greatoo.easymill.process;

import cn.greatoo.easymill.cnc.CNCMachine;
import cn.greatoo.easymill.entity.Gripper;
import cn.greatoo.easymill.entity.GripperHead;
import cn.greatoo.easymill.entity.Gripper.Type;
import cn.greatoo.easymill.external.communication.socket.AbstractCommunicationException;
import cn.greatoo.easymill.external.communication.socket.SocketDisconnectedException;
import cn.greatoo.easymill.external.communication.socket.SocketResponseTimedOutException;
import cn.greatoo.easymill.external.communication.socket.SocketWrongResponseException;
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

/**
 *从table抓取工件
 *
 */
public class PickFromTableStep {

	public static void pickFromTable(FanucRobot robot, CNCMachine cncMachine, boolean teached, Controller view) {
		//===从table抓取工件==================================================================================
		try {
		Gripper gripper = new Gripper("name", Type.TWOPOINT, 190, "description", "");
		final String headId = "A";
		final GripperHead gHeadA = new GripperHead("jyA", null, gripper);
		final GripperHead gHeadB = new GripperHead("jyB", null, gripper);
		int serviceType = 12;
		boolean gripInner = true;
		view.statusChanged(new StatusChangedEvent(StatusChangedEvent.STARTED, 1, Mode.TEACH));
		robot.writeServiceGripperSet(headId, gHeadA, gHeadB, serviceType, gripInner);
		boolean freeAfterService = false;
		final int serviceHandlingPPMode = 16;
		final IWorkPieceDimensions dimensions = new RectangularDimensions(200, 170, 21);
		float weight2 = 16;
		if(teached) {
			weight2 = 48;
		}
		int approachType = 1;
		WorkPiece wp1 = new WorkPiece(WorkPiece.Type.FINISHED, dimensions, Material.AL, 2.4f);
		WorkPiece wp2 = null;
		robot.writeServiceHandlingSet(robot.getSpeed(), freeAfterService, serviceHandlingPPMode,
				dimensions, weight2, approachType, wp1, wp2);
		int workArea = 1;
		Coordinates location = new Coordinates(90.94f, 109.42f, 2.45f, 0, 0, 90);
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
		robot.writeServicePointSet(workArea, location, smoothPoint, smoothPointZ, dimensions,
				clamping, approachType, zSafePlane);
		robot.startService();
		Coordinates robotPosition;
		if(teached) {
			view.statusChanged(new StatusChangedEvent(StatusChangedEvent.EXECUTE_TEACHED, 1,Mode.TEACH));			
			robot.continuePickTillAtLocation(true);
			view.statusChanged(new StatusChangedEvent(StatusChangedEvent.TEACHING_NEEDED, 1,Mode.TEACH));
			robot.continuePickTillUnclampAck(true);
			view.statusChanged(new StatusChangedEvent(StatusChangedEvent.TEACHING_FINISHED, 1,Mode.TEACH));
			robotPosition = robot.getPosition(); //97.5, 87.5, 0.0, 0.0, 0.0, 90.0				
		}else {
			view.statusChanged(new StatusChangedEvent(StatusChangedEvent.EXECUTE_NORMAL, 1,Mode.AUTO));
			robot.continuePickTillAtLocation(false);
			robot.continuePickTillUnclampAck(false);
		}
						
		robot.continuePickTillIPPoint();
	}catch (InterruptedException | AbstractCommunicationException | RobotActionException e) {
		e.printStackTrace();
	}
	}
}
