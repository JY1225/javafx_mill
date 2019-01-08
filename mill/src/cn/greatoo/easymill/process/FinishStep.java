package cn.greatoo.easymill.process;

import cn.greatoo.easymill.cnc.CNCMachine;
import cn.greatoo.easymill.cnc.DeviceActionException;
import cn.greatoo.easymill.external.communication.socket.AbstractCommunicationException;
import cn.greatoo.easymill.robot.FanucRobot;
import cn.greatoo.easymill.robot.RobotActionException;
import cn.greatoo.easymill.ui.main.Controller;

public class FinishStep extends AbstractProcessStep{

	public static void finish(FanucRobot robot, CNCMachine cncMachine, boolean teached, Controller view) {
		try {
			if(teached) {
				view.statusChanged(new StatusChangedEvent(StatusChangedEvent.ENDED));
			}else {
				robot.moveToHome(robot.getSpeed());//71
				cncMachine.indicateAllProcessed();//58
				robot.moveToHome(robot.getSpeed());//71
				view.statusChanged(new StatusChangedEvent(StatusChangedEvent.FINISHED));
			}
		}catch (InterruptedException | AbstractCommunicationException | DeviceActionException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void executeStep(int processId, ProcessExecutor executor)
			throws AbstractCommunicationException, RobotActionException, DeviceActionException, InterruptedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ProcessStepType getType() {
		// TODO Auto-generated method stub
		return null;
	}
}
