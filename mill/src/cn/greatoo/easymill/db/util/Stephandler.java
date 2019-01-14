package cn.greatoo.easymill.db.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.greatoo.easymill.entity.Coordinates;
import cn.greatoo.easymill.entity.Gripper;
import cn.greatoo.easymill.entity.RobotSetting;
import cn.greatoo.easymill.entity.Smooth;
import cn.greatoo.easymill.entity.Step;
import cn.greatoo.easymill.entity.UserFrame;
import cn.greatoo.easymill.entity.WorkPiece;

public class Stephandler {
	
	
	private static final int STEP_TYPE_UNLOADSTACKER = 1;
	private static final int STEP_TYPE_LOADCNC = 2;
	private static final int STEP_TYPE_UNLOADCNC= 3;
	private static final int STEP_TYPE_LOADSTACKER = 4;
	Connection conn = DBHandler.getInstance().getConnection();
	Gripperhandler gripperhandler;
	UserFrameHander userFrameHander;
	CoordinatesHandler coordinatesHandler;
	Workpiecehandler workpiecehandler;
	RobotSettinghandler robotSettinghandler;
	SmoothHandler smoothHandler;
	
	
	private void saveStep(final Step step, final int index) throws SQLException {
		int type = 0;
		if (step instanceof PickAfterWaitStep) {	// note: these have to go first!
			type = STEP_TYPE_UNLOADSTACKER;
		} else if (step instanceof PutAndWaitStep) {
			type = STEP_TYPE_LOADCNC;
		} else if (step instanceof ProcessingWhileWaitingStep) {
			type = STEP_TYPE_UNLOADCNC;
		} else if (step instanceof PutStep) {
			type = STEP_TYPE_LOADSTACKER;
 else {
			throw new IllegalStateException("Unknown step type: " + step);
		}
		PreparedStatement stmt = conn.prepareStatement("INSERT INTO STEP (PROCESSFLOW, TYPE, INDEX) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		stmt.setInt(1, step.getProcessFlow().getId());
		stmt.setInt(2, type);
		stmt.setInt(3, index);
		stmt.executeUpdate();
		ResultSet keys = stmt.getGeneratedKeys();
		if ((keys != null) && (keys.next())) {
			step.setId(keys.getInt(1));
		}
		if (step instanceof DeviceStep) {
			saveDeviceActionSettings((DeviceStep) step);
		}
		if (step instanceof RobotStep) {
			saveRobotActionSettings((RobotStep) step);
		}
		if (step instanceof InterventionStep) {
			InterventionStep iStep = (InterventionStep) step;
			PreparedStatement stmt2 = conn.prepareStatement("INSERT INTO INTERVENTIONSTEP (ID, FREQUENCY) VALUES (?, ?)");
			stmt2.setInt(1, step.getId());
			stmt2.setInt(2, iStep.getFrequency());
			stmt2.executeUpdate();
		}
		if (step instanceof AbstractTransportStep) {
			AbstractTransportStep transportStep = (AbstractTransportStep) step;
			if (transportStep.getRobotSettings().getGripperHead().getGripper().isFixedHeight() && (transportStep.getRelativeTeachedOffset() != null)) {
				generalMapper.saveCoordinates(transportStep.getRelativeTeachedOffset());
				PreparedStatement stmt2 = conn.prepareStatement("INSERT INTO STEP_TEACHEDCOORDINATES (STEP, COORDINATES) VALUES (?, ?)");
				stmt2.setInt(1, step.getId());
				stmt2.setInt(2, transportStep.getRelativeTeachedOffset().getId());
				stmt2.executeUpdate();
			}
		}
	}
	
	public Step getProgramStepsById(final int programId, final int stepId) throws SQLException {
		Step step =null;
		if (programId != 0) {
            Map<Integer, Step> buffer = DBHandler.getInstance().getStepBuffer().get(programId);
            if (buffer != null) {
            	step = buffer.get(stepId);
                if (step != null) {
                    return step;
                }
            } else {
                Map<Integer, Step> newBuffer = new HashMap<Integer, Step>();
                DBHandler.getInstance().getStepBuffer().put(programId, newBuffer);
            }
        }
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM STEP WHERE ID = ?");
        stmt.setInt(1, stepId);
        ResultSet results = stmt.executeQuery();
        if (results.next()) {
        	int GripperId = results.getInt("GRIPPER");
        	Gripper gripper =gripperhandler.getGripperById(GripperId);  
        	
            int WorkpiceId = results.getInt("WOKRPICE"); 
            WorkPiece workpice =workpiecehandler.getWorkPieceById(programId, WorkpiceId);
            
            int UserFrameId = results.getInt("USERFRAME");
            UserFrame userFrame =userFrameHander.getUserFrameById(UserFrameId);
            
            int SmoothId = results.getInt("SMOOTH");         
            Smooth smooth = smoothHandler.getSmoothById(programId,SmoothId);
            
            int RobotSettingId = results.getInt("ROBOTSETTING");
            RobotSetting robotSetting = robotSettinghandler.getRobotSettingById(programId, RobotSettingId);
            
            int OffSetId = results.getInt("OFFSET");
            Coordinates offSet = coordinatesHandler.getCoordinatesById(programId,OffSetId);
            
            step = new Step(gripper, workpice, userFrame, smooth, robotSetting, offSet);
            step.setId(stepId);
        }
        stmt.close();
        if (programId != 0) {
        	DBHandler.getInstance().getStepBuffer().get(programId).put(stepId, step);
        }
        return step;          
	}
	
	
    public Coordinates getCoordinatesById(final int processFlowId, final int coordinatesId) throws SQLException {
        Coordinates coordinates = null;
        if (processFlowId != 0) {
            Map<Integer, Coordinates> buffer = DBHandler.getInstance().getCoordinatesBuffer().get(processFlowId);
            if (buffer != null) {
                coordinates = buffer.get(coordinatesId);
                if (coordinates != null) {
                    return coordinates;
                }
            } else {
                Map<Integer, Coordinates> newBuffer = new HashMap<Integer, Coordinates>();
                DBHandler.getInstance().getCoordinatesBuffer().put(processFlowId, newBuffer);
            }
        }
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM COORDINATES WHERE ID = ?");
        stmt.setInt(1, coordinatesId);
        ResultSet results = stmt.executeQuery();
        if (results.next()) {
            float x = results.getFloat("X");
            float y = results.getFloat("Y");
            float z = results.getFloat("Z");
            float w = results.getFloat("W");
            float p = results.getFloat("P");
            float r = results.getFloat("R");
            coordinates = new Coordinates(x, y, z, w, p, r);
            coordinates.setId(coordinatesId);
        }
        stmt.close();
        if (processFlowId != 0) {
        	DBHandler.getInstance().getCoordinatesBuffer().get(processFlowId).put(coordinatesId, coordinates);
        }
        return coordinates;
    }
	
	private void deleteCoordinate(Integer coordinateId) throws SQLException {
		PreparedStatement stmtDeleteCoordinates = conn.prepareStatement("delete from coordinates where id=?");
		stmtDeleteCoordinates.setInt(1, coordinateId);
		stmtDeleteCoordinates.executeUpdate();
	}	
	
    public void deleteCoordinates(final Coordinates coordinates) throws SQLException {
        if (coordinates.getId() > 0) {
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM COORDINATES WHERE ID = ?");
            stmt.setInt(1, coordinates.getId());
            stmt.executeUpdate();
            for (Map<Integer, Coordinates> buffer : DBHandler.getInstance().getCoordinatesBuffer().values()) {
                buffer.remove(coordinates.getId());
            }
            coordinates.setId(0);
        }
    }
     

}
