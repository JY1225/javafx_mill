package cn.greatoo.easymill.process;

import cn.greatoo.easymill.cnc.CNCMachine;
import cn.greatoo.easymill.entity.Program;
import cn.greatoo.easymill.external.communication.socket.SocketDisconnectedException;
import cn.greatoo.easymill.external.communication.socket.SocketResponseTimedOutException;
import cn.greatoo.easymill.external.communication.socket.SocketWrongResponseException;
import cn.greatoo.easymill.robot.FanucRobot;

/**
 * 
 * 示教，自动化准备工作
 *
 */
public class PrepareStep extends AbstractStep {

	public void prepareStep(Program program, FanucRobot robot, boolean teached, CNCMachine cncMachine)
			throws SocketResponseTimedOutException, SocketDisconnectedException, SocketWrongResponseException,
			InterruptedException {
		final String headId = program.getLoadCNC().getGripperHead().getName();
		int serviceType = 5;
		boolean gripInner = false;
		checkProcessExecutorStatus(robot, cncMachine);
		robot.writeServiceGripperSet(headId, program.getLoadCNC().getGripper(), program.getUnloadCNC().getGripper(),
				serviceType, gripInner);// 75
		robot.recalculateTCPs();// 64
		if (teached) {
			checkProcessExecutorStatus(robot, cncMachine);
			robot.moveToHome(robot.getSpeed());// 71
		}
		checkProcessExecutorStatus(robot, cncMachine);
		//cncMachine.indicateOperatorRequested(false);// 58
		cncMachine.prepareForProcess(1);// 18
	}

}
