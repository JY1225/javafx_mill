package cn.greatoo.easymill.process;

import cn.greatoo.easymill.cnc.CNCMachine;
import cn.greatoo.easymill.cnc.DeviceActionException;
import cn.greatoo.easymill.external.communication.socket.AbstractCommunicationException;
import cn.greatoo.easymill.robot.FanucRobot;
import cn.greatoo.easymill.ui.main.Controller;

public class FinishStep {

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
}
