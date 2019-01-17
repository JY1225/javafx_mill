package cn.greatoo.easymill.db.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import cn.greatoo.easymill.entity.Coordinates;
import cn.greatoo.easymill.entity.Gripper;
import cn.greatoo.easymill.entity.RobotSetting;
import cn.greatoo.easymill.entity.Smooth;
import cn.greatoo.easymill.entity.Step;
import cn.greatoo.easymill.entity.WorkPiece;

public class Stephandler {
	
	
	private static final int STEP_TYPE_UNLOADSTACKER = 1;
	private static final int STEP_TYPE_LOADCNC = 2;
	private static final int STEP_TYPE_UNLOADCNC= 3;
	private static final int STEP_TYPE_LOADSTACKER = 4;
	static Connection conn = DBHandler.getInstance().getConnection();
	static Gripperhandler gripperhandler;
	static UserFrameHander userFrameHander;
	static CoordinatesHandler coordinatesHandler;
	static Workpiecehandler workpiecehandler;
	static RobotSettinghandler robotSettinghandler;
	static SmoothHandler smoothHandler;
	
	public static void saveProgramStep(Step step) throws SQLException {
		if(step.getId() <= 0) {
			PreparedStatement stmt = conn.prepareStatement(
					"INSERT INTO STEP(GRIPPER, WORKPIECE, USERFRAME,SMOOTH,ROBOTSETTING,OFFSET) VALUES (?, ?, ?,?,?,?)",
					Statement.RETURN_GENERATED_KEYS);
			stmt.setInt(1, step.getGripper().getId());
			stmt.setInt(2, step.getWorkPiece().getId());
			stmt.setInt(3, step.getUserFrame());
			stmt.setInt(4, step.getSmooth().getId());
			stmt.setInt(5, step.getRobotSetting().getId());
			stmt.setInt(6, step.getOffset().getId());
			stmt.executeUpdate();
			ResultSet keys = stmt.getGeneratedKeys();
			if ((keys != null) && (keys.next())) {
				step.setId(keys.getInt(1));
			}

		}
	}
		
	public static Step getProgramStepsById(final int programId, final int stepId) throws SQLException {
		Step step =null;
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM STEP WHERE ID = ?");
        stmt.setInt(1, stepId);
        ResultSet results = stmt.executeQuery();
        if (results.next()) {
        	int gripperHeadId = results.getInt("GRIPPERHEAD");
        	
        	int GripperId = results.getInt("GRIPPER");
        	Gripper gripper =gripperhandler.getGripperById(GripperId);  
        	
            int WorkpiceId = results.getInt("WOKRPICE"); 
            WorkPiece workpice =workpiecehandler.getWorkPieceById(programId, WorkpiceId);
            
            int UserFrameId = results.getInt("USERFRAME");
            //UserFrame userFrame =userFrameHander.getUserFrameById(UserFrameId);
            
            int SmoothId = results.getInt("SMOOTH");         
            Smooth smooth = smoothHandler.getSmoothById(programId,SmoothId);
            
            int RobotSettingId = results.getInt("ROBOTSETTING");
            RobotSetting robotSetting = robotSettinghandler.getRobotSettingById(programId, RobotSettingId);
            
            int OffSetId = results.getInt("OFFSET");
            Coordinates offSet = coordinatesHandler.getCoordinatesById(programId,OffSetId);
            
            step = new Step(gripper, workpice, UserFrameId, smooth, robotSetting, offSet);
            step.setId(stepId);
        }
        stmt.close();
        return step;          
	}
     

}
