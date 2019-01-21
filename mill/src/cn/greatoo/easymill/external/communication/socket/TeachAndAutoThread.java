package cn.greatoo.easymill.external.communication.socket;

import cn.greatoo.easymill.cnc.CNCMachine;
import cn.greatoo.easymill.cnc.EWayOfOperating;
import cn.greatoo.easymill.db.util.CNCHandler;
import cn.greatoo.easymill.db.util.DBHandler;
import cn.greatoo.easymill.entity.Program;
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
	private FanucRobot robot;
	private CNCMachine cncMachine; 
	private static Controller view;
	private String programName;
	private Program program;
	private PickFromTableStep pickFromTableStep;
	private PutToCNCStep putToCNCStep;
	private PickFromCNCStep pickFromCNCStep;
	private PutToTableStep putToTableStep;
	public TeachAndAutoThread(FanucRobot robot,CNCMachine cncMachine,boolean teached, Controller view) {
		this.programName = DBHandler.getInstance().getProgramName();
		this.program = DBHandler.getInstance().getProgramBuffer().get(programName);
		this.robot = robot;		 		
		this.cncMachine = cncMachine;				
		this.teached = teached;
		this.view = view;
		pickFromTableStep = new PickFromTableStep();
		putToCNCStep = new PutToCNCStep();
		pickFromCNCStep = new PickFromCNCStep();
		putToTableStep = new PutToTableStep();
	}

	@Override
	public void run() {
		int wSize = 1;
		int wIndex = 0;
		while (wIndex < wSize) {
			
			view.statusChanged(new StatusChangedEvent(StatusChangedEvent.STARTED));
			//机器人回到原点，打开机床的门
			PrepareStep.prepareStep(program, robot, teached, cncMachine);
			
			//===从table抓取工件===机器人抓取工件，回到原点
			pickFromTableStep.pickFromTable(program, robot, cncMachine, teached, wIndex, view);
			
			//===put工件到机床===机器人put工件到机床，回到原点，机床关门加工工件，加工完成后打开门
			putToCNCStep.putToCNC(program, robot, cncMachine, teached, view);

			//===从机床pick工件出来===机器人抓取工件回到原点
			pickFromCNCStep.pickFromCNC(program, robot, cncMachine, teached, view);
			
			//====把工件put到table===机器人put工件到卡盘，回到原点
			putToTableStep.putToTable(program, robot, cncMachine, teached, view);
			
			//===示教、自动化结束===重置设备
			FinishStep.finish(robot, cncMachine, teached, view);
			wIndex++;
		}
	}
	public static Controller getView() {
		return view;
	}

}
