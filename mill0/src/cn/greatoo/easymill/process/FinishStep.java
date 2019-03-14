package cn.greatoo.easymill.process;

import cn.greatoo.easymill.cnc.CNCMachine;
import cn.greatoo.easymill.cnc.DeviceActionException;
import cn.greatoo.easymill.external.communication.socket.SocketDisconnectedException;
import cn.greatoo.easymill.external.communication.socket.SocketResponseTimedOutException;
import cn.greatoo.easymill.external.communication.socket.SocketWrongResponseException;
import cn.greatoo.easymill.robot.FanucRobot;
import cn.greatoo.easymill.ui.main.Controller;

public class FinishStep extends AbstractStep {

	public void finish(FanucRobot robot, CNCMachine cncMachine, boolean teached, Controller view)
			throws SocketDisconnectedException, SocketResponseTimedOutException, SocketWrongResponseException,
			InterruptedException, DeviceActionException {

		if (teached) {
			view.statusChanged(new StatusChangedEvent(StatusChangedEvent.ENDED));
		} else {
			checkProcessExecutorStatus(robot, cncMachine);
			robot.moveToHome(robot.getSpeed());// 71
			checkProcessExecutorStatus(robot, cncMachine);
			cncMachine.indicateAllProcessed();// 58
			checkProcessExecutorStatus(robot, cncMachine);
			robot.moveToHome(robot.getSpeed());// 71
			view.statusChanged(new StatusChangedEvent(StatusChangedEvent.FINISHED));
		}

	}
}
