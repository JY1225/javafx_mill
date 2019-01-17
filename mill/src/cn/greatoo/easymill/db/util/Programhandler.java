package cn.greatoo.easymill.db.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cn.greatoo.easymill.entity.Program;
import cn.greatoo.easymill.entity.Step;

public class Programhandler {
	private final static Logger LOGGER = LogManager.getLogger(Programhandler.class.getName());
	private static Connection conn = DBHandler.getInstance().getConnection();

	public static Program getProgram() {
		Program program = null;
		Timestamp lastOpened = null;
		try {
			PreparedStatement stmt = conn.prepareStatement("SELECT MAX(LASTOPENED) FROM PROGRAM");
			ResultSet result = stmt.executeQuery();
			if (result.next()) {
				lastOpened = result.getTimestamp("1");
			}
			PreparedStatement stmt2 = conn.prepareStatement("SELECT * FROM PROGRAM WHERE LASTOPENED = ?");
			stmt2.setTimestamp(1, lastOpened);
			ResultSet results = stmt2.executeQuery();
			if (results.next()) {
				program = new Program();
				program.setId(results.getInt("ID"));
				program.setName(results.getString("NAME"));
				program.setTimeCreate(results.getTimestamp("CREATION"));
				program.setTimeLastOpen(results.getTimestamp("LASTOPENED"));
				Step unloadstacker = Stephandler.getProgramStepsById(results.getInt("ID"),
						results.getInt("unloadstacker"));
				program.setUnloadstacker(unloadstacker);
				Step loadCNC = Stephandler.getProgramStepsById(results.getInt("ID"), results.getInt("loadCNC"));
				program.setLoadCNC(loadCNC);
				Step unloadCNC = Stephandler.getProgramStepsById(results.getInt("ID"), results.getInt("unloadCNC"));
				program.setUnloadCNC(unloadCNC);
				Step loadstacker = Stephandler.getProgramStepsById(results.getInt("ID"), results.getInt("loadstacker"));
				program.setLoadstacker(loadstacker);

			}
			if (program != null) {
				DBHandler.getInstance().setProgramName(program.getName());
				DBHandler.getInstance().getProgramBuffer().put(program.getName(), program);				
			}
		} catch (SQLException ex) {
			LOGGER.log(Level.ERROR, "{}", ex);
		}
		return program;
	}

	public static Program getProgramByName(String name) {
		
		Program program = DBHandler.getInstance().getProgramBuffer().get(name);
		if (program != null) {
			DBHandler.getInstance().setProgramName(program.getName());
			return program;
		}
		try {
			PreparedStatement stmt = conn.prepareStatement("select * from program where name = ?");
			stmt.setString(1, name);
			ResultSet results = stmt.executeQuery();
			if (results.next()) {
				program = new Program();
				program.setId(results.getInt("ID"));
				program.setName(results.getString("NAME"));
				program.setTimeCreate(results.getTimestamp("CREATION"));
				program.setTimeLastOpen(results.getTimestamp("LASTOPENED"));
				Step unloadstacker = Stephandler.getProgramStepsById(results.getInt("ID"),
						results.getInt("unloadstacker"));
				program.setUnloadstacker(unloadstacker);
				Step loadCNC = Stephandler.getProgramStepsById(results.getInt("ID"), results.getInt("loadCNC"));
				program.setLoadCNC(loadCNC);
				Step unloadCNC = Stephandler.getProgramStepsById(results.getInt("ID"), results.getInt("unloadCNC"));
				program.setUnloadCNC(unloadCNC);
				Step loadstacker = Stephandler.getProgramStepsById(results.getInt("ID"), results.getInt("loadstacker"));
				program.setLoadstacker(loadstacker);
				DBHandler.getInstance().setProgramName(program.getName());
				DBHandler.getInstance().getProgramBuffer().put(name, program);
			}

		} catch (SQLException ex) {
			LOGGER.log(Level.ERROR, "{}", ex);
		}
		return program;
	}

	public static void saveProgram(Program program) throws SQLException {
		if (program.getId() <= 0) {// insert
			// unloadstacker
			Gripperhandler.saveGripper(program.getUnloadstacker().getGripper());
			SmoothHandler.saveSmooth(program.getUnloadstacker().getSmooth());
			Workpiecehandler.saveWorkPiece(program.getUnloadstacker().getWorkPiece());
			CoordinatesHandler.saveCoordinates(program.getUnloadstacker().getOffset());
			Stephandler.saveProgramStep(program.getUnloadstacker());
			//loadCNC
			Gripperhandler.saveGripper(program.getLoadCNC().getGripper());
			RobotSettinghandler.saveRobotSetting(program.getLoadCNC().getRobotSetting());
			SmoothHandler.saveSmooth(program.getLoadCNC().getSmooth());
			Workpiecehandler.saveWorkPiece(program.getLoadCNC().getWorkPiece());
			CoordinatesHandler.saveCoordinates(program.getLoadCNC().getOffset());
			Stephandler.saveProgramStep(program.getLoadCNC());
			//unloadCNC
			Gripperhandler.saveGripper(program.getUnloadCNC().getGripper());
			SmoothHandler.saveSmooth(program.getUnloadCNC().getSmooth());
			Workpiecehandler.saveWorkPiece(program.getUnloadCNC().getWorkPiece());
			CoordinatesHandler.saveCoordinates(program.getUnloadCNC().getOffset());
			Stephandler.saveProgramStep(program.getUnloadCNC());
			//loadstacker
			Gripperhandler.saveGripper(program.getLoadstacker().getGripper());
			SmoothHandler.saveSmooth(program.getLoadstacker().getSmooth());
			Workpiecehandler.saveWorkPiece(program.getLoadstacker().getWorkPiece());
			CoordinatesHandler.saveCoordinates(program.getLoadstacker().getOffset());
			Stephandler.saveProgramStep(program.getLoadstacker());
			PreparedStatement stmt = conn.prepareStatement(
					"INSERT INTO PROGRAM(NAME, CREATION, LASTOPENED,UNLOADSTACKER,LOADCNC,UNLOADCNC,LOADSTACKER) VALUES (?, ?, ?,?,?,?,?)",
					Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, program.getName());
			stmt.setTimestamp(2, program.getTimeCreate());
			stmt.setTimestamp(3, program.getTimeLastOpen());
			stmt.setInt(4, program.getUnloadstacker().getId());
			stmt.setInt(5, program.getLoadCNC().getId());
			stmt.setInt(6, program.getUnloadCNC().getId());
			stmt.setInt(7, program.getLoadstacker().getId());
			stmt.executeUpdate();
			ResultSet keys = stmt.getGeneratedKeys();
			if ((keys != null) && (keys.next())) {
				program.setId(keys.getInt(1));
			}

		} else {// update

		}
	}

}
