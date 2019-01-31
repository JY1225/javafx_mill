package cn.greatoo.easymill.process;

import cn.greatoo.easymill.cnc.CNCMachine;
import cn.greatoo.easymill.cnc.DeviceActionException;
import cn.greatoo.easymill.external.communication.socket.AbstractCommunicationException;
import cn.greatoo.easymill.external.communication.socket.TeachAndAutoThread;
import cn.greatoo.easymill.robot.FanucRobot;
import cn.greatoo.easymill.ui.main.Controller;
import javafx.application.Platform;

public class FinishStep extends AbstractStep{

	public void finish(FanucRobot robot, CNCMachine cncMachine, boolean teached, Controller view) {
		try {
			if(teached) {
				view.statusChanged(new StatusChangedEvent(StatusChangedEvent.ENDED));
			}else {
				checkProcessExecutorStatus(robot,cncMachine);
				robot.moveToHome(robot.getSpeed());//71
				checkProcessExecutorStatus(robot,cncMachine);
				cncMachine.indicateAllProcessed();//58
				checkProcessExecutorStatus(robot,cncMachine);
				robot.moveToHome(robot.getSpeed());//71
				view.statusChanged(new StatusChangedEvent(StatusChangedEvent.FINISHED));
			}
		}catch (InterruptedException | AbstractCommunicationException | DeviceActionException e) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					TeachAndAutoThread.getView().setMessege("程序未启动！");
				}
			});
		}
	}
}
