package cn.greatoo.easymill.external.communication.socket;

import cn.greatoo.easymill.cnc.CNCMachine;
import cn.greatoo.easymill.cnc.EWayOfOperating;
import cn.greatoo.easymill.db.util.DBHandler;
import cn.greatoo.easymill.process.FinishStep;
import cn.greatoo.easymill.process.PickFromCNCStep;
import cn.greatoo.easymill.process.PickFromTableStep;
import cn.greatoo.easymill.process.PrepareStep;
import cn.greatoo.easymill.process.PutToCNCStep;
import cn.greatoo.easymill.process.PutToTableStep;
import cn.greatoo.easymill.process.StatusChangedEvent;
import cn.greatoo.easymill.robot.FanucRobot;
import cn.greatoo.easymill.ui.main.Controller;

/**
 * 示教线程
 *
 */
public class TeachAndAutoThread implements Runnable {

	private boolean teached;
	private boolean isAlive;
	private FanucRobot robot;
	private CNCMachine cncMachine; 
	private EWayOfOperating wayOfOperating;
	private static Controller view;

	public TeachAndAutoThread(RobotSocketCommunication roboSocketConnection,
			CNCSocketCommunication cncSocketConnection, boolean teached, Controller view) {
		this.robot = FanucRobot.getInstance(roboSocketConnection);		 		
		this.cncMachine = CNCMachine.getInstance(cncSocketConnection, DBHandler.getInstance().getMCodeAdapter(1), wayOfOperating);// DBHandler.getInstance().getCNCMillingMachine(1,cncSocketConnection);//new CNCMachine(cncSocketConnection, mCodeAdapter, wayOfOperating.M_CODES);				
		this.teached = teached;
		this.isAlive = true;
		this.view = view;
	}

	@Override
	public void run() {
		while (isAlive) {
			view.statusChanged(new StatusChangedEvent(StatusChangedEvent.STARTED));
			//机器人回到原点，打开机床的门
			PrepareStep.prepareStep(robot, teached, cncMachine);
			
			//===从table抓取工件===机器人抓取工件，回到原点
			PickFromTableStep.pickFromTable(robot, cncMachine, teached, view);
			
			//===put工件到机床===机器人put工件到机床，回到原点，机床关门加工工件，加工完成后打开门
			PutToCNCStep.putToCNC(robot, cncMachine, teached, view);
			
			//===从机床pick工件出来===机器人抓取工件回到原点
			PickFromCNCStep.pickFromCNC(robot, cncMachine, teached, view);
			
			//====把工件put到table===机器人put工件到卡盘，回到原点
			PutToTableStep.putToTable(robot, cncMachine, teached, view);
			
			//===示教、自动化结束===重置设备
			FinishStep.finish(robot, cncMachine, teached, view);
			
			isAlive = false;
		}
	}
	public static Controller getView() {
		return view;
	}

}
