package cn.greatoo.easymill.process;

import cn.greatoo.easymill.cnc.CNCMachine;
import cn.greatoo.easymill.entity.Gripper;
import cn.greatoo.easymill.entity.GripperHead;
import cn.greatoo.easymill.external.communication.socket.SocketDisconnectedException;
import cn.greatoo.easymill.external.communication.socket.SocketResponseTimedOutException;
import cn.greatoo.easymill.external.communication.socket.SocketWrongResponseException;
import cn.greatoo.easymill.entity.Gripper.Type;
import cn.greatoo.easymill.robot.FanucRobot;
/**
 * 
 *示教，自动化准备工作
 *
 */
public class PrepareStep {

	public static void prepareStep(FanucRobot robot, boolean teached, CNCMachine cncMachine) {
		try {
			Gripper gripper = new Gripper("name", Type.TWOPOINT, 190, "description", "");
			final String headId = "A";
			final GripperHead gHeadA = new GripperHead("jyA", null, gripper);
			final GripperHead gHeadB = new GripperHead("jyB", null, gripper);
			int serviceType = 5;
			boolean gripInner = false;
			robot.writeServiceGripperSet(headId, gHeadA, gHeadB, serviceType, gripInner);// 75			
			robot.recalculateTCPs();// 64
			if(teached) {
				robot.moveToHome(robot.getSpeed());// 71
			}
			cncMachine.indicateOperatorRequested(false);// 58
			cncMachine.prepareForProcess(1);// 18
		} catch (SocketDisconnectedException | SocketResponseTimedOutException | SocketWrongResponseException
				| InterruptedException e) {
			e.printStackTrace();
		}
	}

}
