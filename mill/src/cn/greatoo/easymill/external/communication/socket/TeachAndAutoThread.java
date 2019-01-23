package cn.greatoo.easymill.external.communication.socket;

import cn.greatoo.easymill.cnc.CNCMachine;
import cn.greatoo.easymill.cnc.EWayOfOperating;
import cn.greatoo.easymill.db.util.CNCHandler;
import cn.greatoo.easymill.db.util.DBHandler;
import cn.greatoo.easymill.entity.Coordinates;
import cn.greatoo.easymill.entity.Program;
import cn.greatoo.easymill.process.AbstractStep;
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
public class TeachAndAutoThread extends AbstractStep implements Runnable {

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
		initOffset();
	}

	@Override
	public void run() {
		int wSize = DBHandler.getInstance().getStatckerBuffer().get(0).getAmount();
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
			putToTableStep.putToTable(program, robot, cncMachine, teached, wIndex, view);
			
			//===示教、自动化结束===重置设备
			FinishStep.finish(robot, cncMachine, teached, view);
			wIndex++;
		}
	}
	

	private void initOffset() {
		Coordinates  unLoadStackerOffset = program.getUnloadstacker().getOffset();
		Coordinates  loadCNCOffset = program.getUnloadstacker().getOffset();
		Coordinates  unLoadCNCOffset = program.getUnloadstacker().getOffset();
		Coordinates  loadStackerOffset = program.getUnloadstacker().getOffset();
		if(unLoadStackerOffset.getX() != 0 || unLoadStackerOffset.getY() != 0 || unLoadStackerOffset.getZ() != 0 
				|| unLoadStackerOffset.getW() != 0 || unLoadStackerOffset.getP() != 0 || unLoadStackerOffset.getR() != 0) {
			setUnloadStackerRelativeTeachedOffset(unLoadStackerOffset);
		}
		if(loadCNCOffset.getX() != 0 || loadCNCOffset.getY() != 0 || loadCNCOffset.getZ() != 0 
				|| loadCNCOffset.getW() != 0 || loadCNCOffset.getP() != 0 || loadCNCOffset.getR() != 0) {
			setLoadCNCRelativeTeachedOffset(loadCNCOffset);
		}
		if(unLoadCNCOffset.getX() != 0 || unLoadCNCOffset.getY() != 0 || unLoadCNCOffset.getZ() != 0 
				|| unLoadCNCOffset.getW() != 0 || unLoadCNCOffset.getP() != 0 || unLoadCNCOffset.getR() != 0) {
			setUnloadCNCRelativeTeachedOffset(unLoadCNCOffset);
		}
		if(loadStackerOffset.getX() != 0 || loadStackerOffset.getY() != 0 || loadStackerOffset.getZ() != 0 
				|| loadStackerOffset.getW() != 0 || loadStackerOffset.getP() != 0 || loadStackerOffset.getR() != 0) {
			setLoadStackerRelativeTeachedOffset(loadStackerOffset);
		}		
	}
	
	public static Controller getView() {
		return view;
	}

}
