package cn.greatoo.easymill.process;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cn.greatoo.easymill.db.util.DBHandler;
import cn.greatoo.easymill.robot.RobotPickSettings;
import cn.greatoo.easymill.util.Coordinates;
import cn.greatoo.easymill.workpiece.RectangularDimensions;
import cn.greatoo.easymill.workpiece.WorkPiece;
import cn.greatoo.easymill.workpiece.WorkPiece.Material;
import cn.greatoo.easymill.workpiece.WorkPiece.Type;


public class ProcessFlowManager {
	private ProcessFlow activeProcessFlow;
	private static Logger logger = LogManager.getLogger(ProcessFlowManager.class.getName());
	
	public void setActiveProcessFlow(final ProcessFlow processFlow) {
		this.activeProcessFlow = processFlow;
	}
	
	public ProcessFlow getLastProcessFlow() {
		List<ProcessFlow> processFlows;
		try {
			processFlows = DBHandler.getInstance().getLastOpenedProcessFlows(1);
			if (processFlows.size() > 0) {
				return processFlows.get(0);
			}
		} catch (SQLException e) {
			logger.error(e);
			e.printStackTrace();
		}
		return null;
	}
	
	public ProcessFlow getProcessFlowForId(final int processFlowId) {
		try {
			return DBHandler.getInstance().getProcessFlowById(processFlowId);
		} catch (SQLException e) {
			logger.error(e);
			e.printStackTrace();
		}
		return null;
	}
	
	//TODO add buffer and look at delete!
	public List<ProcessFlow> getProcessFlows() {
		try {
			return DBHandler.getInstance().getAllProcessFlows();
		} catch (SQLException e) {
			logger.error(e);
			e.printStackTrace();
		}
		return null;
	}
	
	public ProcessFlow createNewProcessFlow() {
		WorkPiece rawWorkPiece = new WorkPiece(Type.RAW, new RectangularDimensions(), Material.OTHER, 0.0f);
		WorkPiece finishedWorkPiece = new WorkPiece(Type.FINISHED, new RectangularDimensions(), Material.OTHER, 0.0f);
//		RobotPickSettings.setWorkPiece(rawWorkPiece);
//		RobotPutSettings.setWorkPiece(finishedWorkPiece);
//
//	
//				GripperHead headA = robotStep.getRobot().getGripperBody().getGripperHeadByName("A");
//				GripperHead headB = robotStep.getRobot().getGripperBody().getGripperHeadByName("B");
//				robotStep.getRobotSettings().setGripperHead(headA);
//				robotStep.getRobotSettings().setGripperHead(headB);
//				robotStep.getRobotSettings().setGripperHead(headB);

		ProcessFlow processFlow = new ProcessFlow("", new Timestamp(System.currentTimeMillis()), null);	
		return processFlow;
	}

	public void updateProcessFlow(final ProcessFlow processFlow) throws DuplicateProcessFlowNameException, IllegalArgumentException {
		try {
			int idForName = DBHandler.getInstance().getProcessFlowIdForName(processFlow.getName());
			if (idForName == -1) {
				saveProcessFlow(processFlow);
			} else if (idForName == processFlow.getId()) {
				if (processFlow.getId() > 0) {
					// update
					logger.info("Updating processflow with id: [" + processFlow.getId() + "] and name: [" + processFlow.getName() + "].");
					updateProcessFlow(processFlow);
				} else {
					//FIXME - check op naam ipv id
					throw new IllegalArgumentException("ProcessFlow should have a valid id for save");
				}
			} else {
				throw new DuplicateProcessFlowNameException();
			}
		} catch (SQLException e) {
			logger.error(e);
			e.printStackTrace();
		}
	}
	
	public void updateLastOpened(final ProcessFlow processFlow) throws SQLException {
		try {
			DBHandler.getInstance().updateLastOpened(processFlow);
		} catch (SQLException e) {
			logger.error(e);
			e.printStackTrace();
		}
	}
	
	public void saveProcessFlow(final ProcessFlow processFlow) throws DuplicateProcessFlowNameException {
		try {
			int idForName = DBHandler.getInstance().getProcessFlowIdForName(processFlow.getName());
			if (idForName == -1) {
				logger.info("Saving processflow with name: [" + processFlow.getName() + "].");
				DBHandler.getInstance().saveProcessFlow(processFlow);
			} else {
				throw new DuplicateProcessFlowNameException();
			}
		} catch (SQLException e) {
			logger.error(e);
			e.printStackTrace();
		}
	}
	
	public ProcessFlow getActiveProcessFlow() {
		return this.activeProcessFlow;
	}
}
