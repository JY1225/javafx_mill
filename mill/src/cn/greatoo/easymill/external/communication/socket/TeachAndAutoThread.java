package cn.greatoo.easymill.external.communication.socket;

import cn.greatoo.easymill.cnc.CNCMachine;
import cn.greatoo.easymill.cnc.DeviceActionException;
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
import cn.greatoo.easymill.process.WorkPiecePositions;
import cn.greatoo.easymill.robot.FanucRobot;
import cn.greatoo.easymill.robot.RobotActionException;
import cn.greatoo.easymill.ui.main.Controller;
import javafx.application.Platform;

/**
 * 示教线程
 *
 */
public class TeachAndAutoThread extends AbstractStep implements Runnable {
	public static boolean isFinishTeach;
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
	private PrepareStep prepareStep;
	private FinishStep finishStep;
	private WorkPiecePositions workPiecePositions;

	@SuppressWarnings("static-access")
	public TeachAndAutoThread(FanucRobot robot, CNCMachine cncMachine, boolean teached, Controller view) {
		isFinishTeach = false;
		this.programName = DBHandler.getInstance().getProgramName();
		this.program = DBHandler.getInstance().getProgramBuffer().get(programName);
		this.workPiecePositions = new WorkPiecePositions(program);
		workPiecePositions.initStackingPositions(program.getRawWorkPiece());
		this.robot = robot;
		this.cncMachine = cncMachine;
		this.teached = teached;
		this.view = view;
		prepareStep = new PrepareStep();
		pickFromTableStep = new PickFromTableStep();
		putToCNCStep = new PutToCNCStep();
		pickFromCNCStep = new PickFromCNCStep();
		putToTableStep = new PutToTableStep();
		finishStep = new FinishStep();
		robot.setRunning(true);
		cncMachine.setRunning(true);
		initOffset();
	}

	@Override
	public void run() {		
		int wSize = program.getAmount();
		int wIndex = 0;
		while (wIndex < wSize) {
			try {
				view.statusChanged(new StatusChangedEvent(StatusChangedEvent.STARTED));

				// 机器人回到原点，打开机床的门
				checkProcessExecutorStatus(robot, cncMachine);
				prepareStep.prepareStep(program, robot, teached, cncMachine);

				// ===从table抓取工件===机器人抓取工件，回到原点
				checkProcessExecutorStatus(robot, cncMachine);
				pickFromTableStep.pickFromTable(program, robot, cncMachine, workPiecePositions, teached, wIndex, view);

				// ===put工件到机床===机器人put工件到机床，回到原点，机床关门加工工件，加工完成后打开门
				checkProcessExecutorStatus(robot, cncMachine);
				putToCNCStep.putToCNC(program, robot, cncMachine, workPiecePositions, teached, view);

				// ===从机床pick工件出来===机器人抓取工件回到原点
				checkProcessExecutorStatus(robot, cncMachine);
				pickFromCNCStep.pickFromCNC(program, robot, cncMachine, workPiecePositions, teached, view);

				// ====把工件put到table===机器人put工件到卡盘，回到原点
				checkProcessExecutorStatus(robot, cncMachine);
				putToTableStep.putToTable(program, robot, cncMachine, workPiecePositions, teached, wIndex, view);

				// ===自动化结束===重置设备
				if(!teached && wIndex == wSize -1) {
					checkProcessExecutorStatus(robot, cncMachine);
					finishStep.finish(robot, cncMachine, teached, view);					
				}
				wIndex++;
				if (teached) {
					checkProcessExecutorStatus(robot, cncMachine);
					finishStep.finish(robot, cncMachine, teached, view);
					program.setHasTeach(true);
					isFinishTeach = true;
					wSize = 0;
				}else {
					view.statusChanged("FINISHED_WORKPIECE_ACOUNT;"+String.valueOf(wIndex));
				}				
				
			} catch (InterruptedException | AbstractCommunicationException | DeviceActionException | RobotActionException e) {
				wSize = 0;
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						TeachAndAutoThread.getView().setMessege("加工流程已停止！");
					}
				});
			}
		}		
	}

	private void initOffset() {
		Coordinates unLoadStackerOffset = program.getUnloadstacker().getOffset();
		Coordinates loadCNCOffset = program.getLoadCNC().getOffset();
		Coordinates unLoadCNCOffset = program.getUnloadCNC().getOffset();
		Coordinates loadStackerOffset = program.getLoadstacker().getOffset();
		if (program.isHasTeach()) {
			setUnloadStackerRelativeTeachedOffset(unLoadStackerOffset);
			setLoadCNCRelativeTeachedOffset(loadCNCOffset);
			setUnloadCNCRelativeTeachedOffset(unLoadCNCOffset);
			setLoadStackerRelativeTeachedOffset(loadStackerOffset);
		}
	}

	public static Controller getView() {
		return view;
	}

}
