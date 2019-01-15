package cn.greatoo.easymill.db.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cn.greatoo.easymill.entity.Clamping;
import cn.greatoo.easymill.entity.Program;
import cn.greatoo.easymill.entity.Step;
import cn.greatoo.easymill.robot.AbstractRobot;




public class Programhandler {
	
	Connection conn = DBHandler.getInstance().getConnection();
	private static final int CLAMPING_MANNER_LENGTH = 1;
	private static final int CLAMPING_MANNER_WIDTH = 2;
	Stephandler stephandler;
	
	
	public void saveProgram(final Program program) throws SQLException {
		conn.setAutoCommit(false);
		//We will clear all the IDs given by the software and assign new IDs that match with the ID given by the Database manager
		clearProcessFlowStepsSettingsAndReferencedIds(program);
		program.setTimeCreate(new Timestamp(System.currentTimeMillis()));
		program.setTimeLastOpen(new Timestamp(System.currentTimeMillis()));
		PreparedStatement stmt = conn.prepareStatement("INSERT INTO Program (NAME, CREATION, LASTOPENED, UNLOADSTACKER, LOADCNC, UNLOADCNC, LOADSTACKER) VALUES (?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		stmt.setString(1, program.getName());
		stmt.setTimestamp(2, program.getTimeCreate());
		stmt.setTimestamp(3, program.getTimeLastOpen());
		stmt.setInt(4, program.getUnloadstacker().getId());
		stmt.setInt(4, program.getLoadCNC().getId());
		stmt.setInt(4, program.getUnloadCNC().getId());
		stmt.setInt(4, program.getLoadstacker().getId());
		try {
			stmt.executeUpdate();
			ResultSet resultSet = stmt.getGeneratedKeys();
			if (resultSet.next()) {
				clearIds(program);
				program.setId(resultSet.getInt(1));
				saveProcessFlowStepsAndSettings(program);
				conn.commit();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			conn.rollback();
		}
		conn.setAutoCommit(true);
	}
	
	private void saveProcessFlowStepsAndSettings(final Program program) throws SQLException {
		int index = 1;
		for (cn.greatoo.easymill.entity.Program.Step step : program.getProgramSteps()) {
			stephandler.saveStep(step, index);
			index++;
		}
	}
	
	public void updateProcessFlow(final Program Program) throws SQLException {
		conn.setAutoCommit(false);
		PreparedStatement stmt = conn.prepareStatement("UPDATE Program SET NAME = ?, LASTOPENED = ?, CLAMPING_MANNER = ? , SINGLE_CYCLE = ? WHERE ID = ?");
		stmt.setString(1, Program.getName());
		stmt.setTimestamp(2, Program.getLastOpened());
		int clampingMannerId = CLAMPING_MANNER_LENGTH;
		if (Program.getClampingType().getType() == ClampingManner.Type.WIDTH) {
			clampingMannerId = CLAMPING_MANNER_WIDTH;
		}
		stmt.setInt(3, clampingMannerId);
		stmt.setBoolean(4, Program.isSingleCycle());
		stmt.setInt(5, Program.getId());
		try {
			stmt.executeUpdate();
			deleteStepsAndSettings(Program);
			clearProcessFlowStepsSettingsAndReferencedIds(Program);
			saveProcessFlowStepsAndSettings(Program);
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error(e);
			conn.rollback();
		}
		conn.setAutoCommit(true);
	}
	
	public void updateLastOpened(final Program Program) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement("UPDATE Program SET LASTOPENED = ? WHERE ID = ?");
		stmt.setTimestamp(1, Program.getLastOpened());
		stmt.setInt(2, Program.getId());
		stmt.executeUpdate();
	}
	
	
	public List<Program> getAllProcessFlows() throws SQLException {
		PreparedStatement stmt = conn.prepareStatement("SELECT ID FROM Program ORDER BY LASTOPENED DESC");
		ResultSet results = stmt.executeQuery();
		List<Program> processFlows = new ArrayList<Program>();
		while (results.next()) {
			int id = results.getInt("ID");
			Program Program = getLightWeightedProcessFlowById(id);
			processFlows.add(Program);
		}
		return processFlows;
	}
	public List<Program> getLastOpenedProcessFlows(final int amount) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement("SELECT ID FROM Program ORDER BY LASTOPENED DESC");
		stmt.setMaxRows(amount);
		ResultSet results = stmt.executeQuery();
		List<Program> processFlows = new ArrayList<Program>();
		while (results.next()) {
			int id = results.getInt("ID");
			Program Program = getProcessFlowById(id);
			processFlows.add(Program);
		}
		return processFlows;
	}
	
	
	public Program getProcessFlowById(final int id) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Program WHERE ID = ?");
		stmt.setInt(1, id);
		ResultSet results = stmt.executeQuery();
		Program Program = null;
		if (results.next()) {
			generalMapper.clearBuffers(id);
			String name = results.getString("NAME");
			Timestamp creation = results.getTimestamp("CREATION");
			Timestamp lastOpened = results.getTimestamp("LASTOPENED");
			int clampingMannerId = results.getInt("CLAMPING_MANNER");
			boolean isSingleCycle = results.getBoolean("SINGLE_CYCLE");
			List<AbstractProcessStep> processSteps = getProgramSteps(id);
			// We have 1 deviceSetting per device - per Program
			Map<AbstractDevice, DeviceSettings> deviceSettings = getDeviceSettings(id);
			Map<AbstractRobot, RobotSettings> robotSettings = getRobotSettings(id);
			Program = new Program(name, processSteps, deviceSettings, robotSettings, creation, lastOpened);
			Program.setId(id);
			Program.setSingleCycle(isSingleCycle);
			if (clampingMannerId == CLAMPING_MANNER_LENGTH) {
				Program.getClampingType().setType(Clamping.Type.LENGTH);
			} else if (clampingMannerId == CLAMPING_MANNER_WIDTH) {
				Program.getClampingType().setType(ClampingManner.Type.WIDTH);
			} else {
				throw new IllegalStateException("Unknown clamping manner type: " + clampingMannerId);
			}
		}
		return Program;
	}
	
	
	private Program getLightWeightedProcessFlowById(final int id) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement("SELECT ID, NAME, LASTOPENED FROM Program WHERE ID = ?");
		stmt.setInt(1, id);
		ResultSet results = stmt.executeQuery();
		Program Program = null;
		if (results.next()) {
			generalMapper.clearBuffers(id);
			String name = results.getString("NAME");
			Timestamp lastOpened = results.getTimestamp("LASTOPENED");
			Program = new Program(name);
			Program.setId(id);
			Program.setLastOpened(lastOpened);
		}
		
		return Program;
	}
	
	public static int getProcessFlowIdForName(final String name) throws SQLException {
		int id = -1;
		PreparedStatement stmt = conn.prepareStatement("SELECT ID FROM Program WHERE NAME = ?");
		stmt.setString(1, name);
		ResultSet resultSet = stmt.executeQuery();
		if (resultSet.next()) {
			id = resultSet.getInt("ID");
		}
		return id;
	}
	
	public void deleteProcessFlow(final Program Program) throws SQLException {
		conn.setAutoCommit(false);
		generalMapper.clearBuffers(Program.getId());
		deleteStepsAndSettings(Program);
		clearProcessFlowStepsSettingsAndReferencedIds(Program);
		PreparedStatement stmt = conn.prepareStatement("DELETE FROM Program WHERE ID = ?");
		stmt.setInt(1, Program.getId());
		stmt.executeUpdate();
		conn.commit();
		conn.setAutoCommit(true);
	}
	
	private void deleteStepsAndSettings(final Program Program) throws SQLException {
		// delete all coordinates and work pieces (these are not cascaded)
		// delete all coordinates
		PreparedStatement stmtgetCoordinatesToDelete = conn.prepareStatement(""  
                + "		(select step_teachedcoordinates.coordinates from step_teachedcoordinates "
                + "			JOIN step "
                + "			ON step.id = step_teachedcoordinates.step "
                + "			where step.Program=?"
                + "		) " 	
                + " 		union "																						
                + "		(select robotactionsettings.smoothpoint from robotactionsettings "
                + "			JOIN step "
                + "			ON step.id = robotactionsettings.step "
                + "			where step.Program=?"
                + "		) "
				);	
		stmtgetCoordinatesToDelete.setInt(1, Program.getId());
		stmtgetCoordinatesToDelete.setInt(2, Program.getId());
		ResultSet resultCoordinates = stmtgetCoordinatesToDelete.executeQuery();
		while(resultCoordinates.next()) {
			deleteCoordinate(resultCoordinates.getInt(1));
		}
		// delete all work pieces (it suffices to delete the work pieces from the pick setting
		PreparedStatement stmtGetWorkPiecesToDelete = conn.prepareStatement("" 	
				+ "select distinct workpiece from robotpicksettings "
				+ "join robotactionsettings "
				+ "on robotactionsettings.id = robotpicksettings.id "
				+ "join step "
				+ "on robotactionsettings.step = step.id "
				+ "where step.Program=?"
				+ "");
		stmtGetWorkPiecesToDelete.setInt(1, Program.getId());
		ResultSet resultset =stmtGetWorkPiecesToDelete.executeQuery();
		while(resultset.next()) {
			deleteWorkPiece(resultset.getInt("workpiece"));
		}
		
		PreparedStatement stmt = conn.prepareStatement("DELETE FROM DEVICESETTINGS WHERE Program = ?");
		stmt.setInt(1, Program.getId());
		stmt.executeUpdate();	// note the cascade delete settings take care of deleting all referenced rows
		PreparedStatement stmt2 = conn.prepareStatement("DELETE FROM ROBOTSETTINGS WHERE Program = ?");
		stmt2.setInt(1, Program.getId());
		stmt2.executeUpdate();	// note the cascade delete settings take care of deleting all referenced rows
		PreparedStatement stmt3 = conn.prepareStatement("DELETE FROM STEP WHERE Program = ?");
		stmt3.setInt(1, Program.getId());
		stmt3.executeUpdate();	// note the cascade delete settings take care of deleting all referenced rows
	}

	
	private void clearIds(final Program Program) {
		Program.setId(0);
		for (DeviceSettings deviceSettings : Program.getDeviceSettings().values()) {
			deviceSettings.setId(0);
			if (deviceSettings instanceof AbstractStackPlateDeviceSettings) {
			    if(((AbstractStackPlateDeviceSettings) deviceSettings).getFinishedWorkPiece()!=null) {
			        ((AbstractStackPlateDeviceSettings) deviceSettings).getFinishedWorkPiece().setId(0);
			    }
				((AbstractStackPlateDeviceSettings) deviceSettings).getRawWorkPiece().setId(0);
			}
		}
		for (RobotSettings robotSettings : Program.getRobotSettings().values()) {
			robotSettings.setId(0);
		}
		for (AbstractProcessStep step : Program.getProgramSteps()) {
			step.setId(0);
		}
	}
	
	private void clearProcessFlowStepsSettingsAndReferencedIds(final Program program) {
		for (AbstractProcessStep step : program.getProgramSteps()) {
			if (step instanceof PickStep) {
				PickStep pStep = (PickStep) step;
				pStep.getRobotSettings().getWorkPiece().setId(0);
			}
			if (step instanceof RobotStep) {
				AbstractRobotActionSettings<?> robotActionSettings = ((RobotStep) step).getRobotSettings();
				if (robotActionSettings.getSmoothPoint() != null) {
					robotActionSettings.getSmoothPoint().setId(0);
				}
			}
			if (step instanceof AbstractTransportStep) {
				AbstractTransportStep trStep = (AbstractTransportStep) step;
				if (trStep.getRelativeTeachedOffset() != null) {
					trStep.getRelativeTeachedOffset().setId(0);
				}
			}
		}
	}
}
