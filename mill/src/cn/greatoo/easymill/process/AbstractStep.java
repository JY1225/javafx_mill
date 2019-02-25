package cn.greatoo.easymill.process;

import cn.greatoo.easymill.cnc.CNCMachine;
import cn.greatoo.easymill.db.util.DBHandler;
import cn.greatoo.easymill.entity.Clamping;
import cn.greatoo.easymill.entity.Coordinates;
import cn.greatoo.easymill.entity.Program;
import cn.greatoo.easymill.entity.WorkPiece;
import cn.greatoo.easymill.robot.FanucRobot;
import cn.greatoo.easymill.util.TeachedCoordinatesCalculator;

public abstract class AbstractStep {
	private static Coordinates unloadStackerRelativeTeachedOffset;
	private static Coordinates loadCNCRelativeTeachedOffset;
	private static Coordinates unloadCNCRelativeTeachedOffset;
	private static Coordinates loadStackerRelativeTeachedOffset;


	protected void initSafeTeachedOffset(int step, Program program, Clamping clampping,
			final Coordinates originalPosition) {
		WorkPiece workPiece;
		float extraOffsetX = 0;
		float extraOffsetY = 0;
		float extraOffsetZ = 0;
		switch (step) {
		case 1:
			workPiece = program.getRawWorkPiece();
			float sh = program.getStudHeight_Workpiece();
			if (originalPosition.getZ() + workPiece.getHeight() < 0 + sh) {
				extraOffsetZ = (0 + sh) - (originalPosition.getZ() + workPiece.getHeight());
			}
			setUnloadStackerRelativeTeachedOffset(TeachedCoordinatesCalculator.calculateRelativeTeachedOffset(
					originalPosition, new Coordinates(extraOffsetX, extraOffsetY, extraOffsetZ, 0, 0, 0)));
			break;
		case 2:
			if (originalPosition.getZ() < DBHandler.getInstance().getClampBuffer().get(0).getRelativePosition().getZ()
					+ DBHandler.getInstance().getClampBuffer().get(0).getHeight()) {
				extraOffsetZ = (clampping.getRelativePosition().getZ() + clampping.getHeight())
						- originalPosition.getZ();
			}
			setLoadCNCRelativeTeachedOffset(TeachedCoordinatesCalculator.calculateRelativeTeachedOffset(
					originalPosition, new Coordinates(extraOffsetX, extraOffsetY, extraOffsetZ, 0, 0, 0)));
			break;
		case 3:	
			workPiece = program.getRawWorkPiece();
			if (originalPosition.getZ() + workPiece.getHeight() < clampping.getRelativePosition().getZ() 
					+ clampping.getHeight()) {
				extraOffsetZ = (clampping.getRelativePosition().getZ() 
						+ clampping.getHeight()) - (originalPosition.getZ() + workPiece.getHeight());
			}
			setUnloadCNCRelativeTeachedOffset(TeachedCoordinatesCalculator.calculateRelativeTeachedOffset(
					originalPosition, new Coordinates(extraOffsetX, extraOffsetY, extraOffsetZ, 0, 0, 0)));
			break;
		case 4:
			if (originalPosition.getZ() < 0
					+ program.getStudHeight_Workpiece()) {
				extraOffsetZ = (0 + program.getStudHeight_Workpiece())
						- originalPosition.getZ();
			}
			setLoadStackerRelativeTeachedOffset(TeachedCoordinatesCalculator.calculateRelativeTeachedOffset(
					originalPosition, new Coordinates(extraOffsetX, extraOffsetY, extraOffsetZ, 0, 0, 0)));
			break;
		default:
			break;
		}
	}

	protected void updateProgramOffset(Coordinates original, Coordinates offset) {
		original.setX(offset.getX());
		original.setY(offset.getY());
		original.setZ(offset.getZ());
		original.setW(offset.getW());
		original.setP(offset.getP());
		original.setR(offset.getR());
	}

	public void checkProcessExecutorStatus(FanucRobot robot,CNCMachine cncMachine) throws InterruptedException{
		robot.checkProcessExecutorStatus();
		cncMachine.checkProcessExecutorStatus();				
	}
	public Coordinates getUnloadStackerRelativeTeachedOffset() {
		return unloadStackerRelativeTeachedOffset;
	}

	public void setUnloadStackerRelativeTeachedOffset(Coordinates unloadStackerRelativeTeachedOffset) {
		this.unloadStackerRelativeTeachedOffset = unloadStackerRelativeTeachedOffset;
	}

	public Coordinates getLoadCNCRelativeTeachedOffset() {
		return loadCNCRelativeTeachedOffset;
	}

	public void setLoadCNCRelativeTeachedOffset(Coordinates loadCNCRelativeTeachedOffset) {
		this.loadCNCRelativeTeachedOffset = loadCNCRelativeTeachedOffset;
	}

	public Coordinates getUnloadCNCRelativeTeachedOffset() {
		return unloadCNCRelativeTeachedOffset;
	}

	public void setUnloadCNCRelativeTeachedOffset(Coordinates unloadCNCRelativeTeachedOffset) {
		this.unloadCNCRelativeTeachedOffset = unloadCNCRelativeTeachedOffset;
	}

	public Coordinates getLoadStackerRelativeTeachedOffset() {
		return loadStackerRelativeTeachedOffset;
	}

	public void setLoadStackerRelativeTeachedOffset(Coordinates loadStackerRelativeTeachedOffset) {
		this.loadStackerRelativeTeachedOffset = loadStackerRelativeTeachedOffset;
	}

}
