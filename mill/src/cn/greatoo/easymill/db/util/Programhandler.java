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
import cn.greatoo.easymill.entity.RobotSetting;
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
				RobotSetting robotSetting = RobotSettinghandler.getRobotSettingById(results.getInt("ID"),
						results.getInt("ROBOTSETTING"));
				program.setRobotSetting(robotSetting);
				program.setHasTeach(results.getBoolean("isHasTeach"));
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
			GripperHeadHandle.saveGripperHead(program.getUnloadstacker().getGripperHead());
			Gripperhandler.getGripperIdByName(program.getUnloadstacker().getGripper().getName(),
					program.getUnloadstacker().getGripper());
			SmoothHandler.saveSmooth(program.getUnloadstacker().getSmooth());
			Workpiecehandler.saveWorkPiece(program.getUnloadstacker().getWorkPiece());
			CoordinatesHandler.saveCoordinates(program.getUnloadstacker().getOffset());
			Stephandler.saveProgramStep(program.getUnloadstacker());

			// loadCNC
			GripperHeadHandle.saveGripperHead(program.getLoadCNC().getGripperHead());
			Gripperhandler.getGripperIdByName(program.getLoadCNC().getGripper().getName(),
					program.getLoadCNC().getGripper());
			SmoothHandler.saveSmooth(program.getLoadCNC().getSmooth());
			Workpiecehandler.saveWorkPiece(program.getLoadCNC().getWorkPiece());
			CoordinatesHandler.saveCoordinates(program.getLoadCNC().getOffset());
			Stephandler.saveProgramStep(program.getLoadCNC());

			// unloadCNC
			GripperHeadHandle.saveGripperHead(program.getUnloadCNC().getGripperHead());
			Gripperhandler.getGripperIdByName(program.getUnloadCNC().getGripper().getName(),
					program.getUnloadCNC().getGripper());
			SmoothHandler.saveSmooth(program.getUnloadCNC().getSmooth());
			Workpiecehandler.saveWorkPiece(program.getUnloadCNC().getWorkPiece());
			CoordinatesHandler.saveCoordinates(program.getUnloadCNC().getOffset());
			Stephandler.saveProgramStep(program.getUnloadCNC());

			// loadstacker
			GripperHeadHandle.saveGripperHead(program.getLoadstacker().getGripperHead());
			Gripperhandler.getGripperIdByName(program.getLoadstacker().getGripper().getName(),
					program.getLoadstacker().getGripper());
			SmoothHandler.saveSmooth(program.getLoadstacker().getSmooth());
			Workpiecehandler.saveWorkPiece(program.getLoadstacker().getWorkPiece());
			CoordinatesHandler.saveCoordinates(program.getLoadstacker().getOffset());
			Stephandler.saveProgramStep(program.getLoadstacker());
			RobotSettinghandler.saveRobotSetting(program.getRobotSetting());
			
			PreparedStatement stmt = conn.prepareStatement(
					"INSERT INTO PROGRAM(NAME, CREATION, LASTOPENED,UNLOADSTACKER,LOADCNC,UNLOADCNC,LOADSTACKER,ROBOTSETTING,ISHASTEACH) VALUES (?,?,?,?,?,?,?,?,?)",
					Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, program.getName());
			stmt.setTimestamp(2, program.getTimeCreate());
			stmt.setTimestamp(3, program.getTimeLastOpen());
			stmt.setInt(4, program.getUnloadstacker().getId());
			stmt.setInt(5, program.getLoadCNC().getId());
			stmt.setInt(6, program.getUnloadCNC().getId());
			stmt.setInt(7, program.getLoadstacker().getId());
			stmt.setInt(8, program.getRobotSetting().getId());
			stmt.setBoolean(9, program.isHasTeach());
			stmt.executeUpdate();
			ResultSet keys = stmt.getGeneratedKeys();
			if ((keys != null) && (keys.next())) {
				program.setId(keys.getInt(1));
			}

		} else {
			if(program.getUnloadstacker().getGripperHead().getId() > 0) {
				GripperHeadHandle.saveGripperHead(program.getUnloadstacker().getGripperHead());
			}
			if(program.getUnloadstacker().getGripper().getId() > 0) {
				Gripperhandler.getGripperIdByName(program.getUnloadstacker().getGripper().getName(),
						program.getUnloadstacker().getGripper());
			}
			if(program.getUnloadstacker().getSmooth().getId() > 0) {
				SmoothHandler.saveSmooth(program.getUnloadstacker().getSmooth());
			}
			if(program.getUnloadstacker().getWorkPiece().getId() > 0) {
				Workpiecehandler.saveWorkPiece(program.getUnloadstacker().getWorkPiece());
			}
			if(program.getUnloadstacker().getOffset().getId() > 0) {
				CoordinatesHandler.saveCoordinates(program.getUnloadstacker().getOffset());
			}
			if(program.getUnloadstacker().getId() > 0) {
				Stephandler.saveProgramStep(program.getUnloadstacker());
			}
				// loadCNC
			if(program.getLoadCNC().getGripperHead().getId() > 0) {
				GripperHeadHandle.saveGripperHead(program.getLoadCNC().getGripperHead());
			}
			if(program.getLoadCNC().getGripper().getId() > 0) {
				Gripperhandler.getGripperIdByName(program.getLoadCNC().getGripper().getName(),
						program.getLoadCNC().getGripper());
			}
			if(program.getLoadCNC().getSmooth().getId() > 0) {
				SmoothHandler.saveSmooth(program.getLoadCNC().getSmooth());
			}
			if(program.getLoadCNC().getWorkPiece().getId() > 0) {
				Workpiecehandler.saveWorkPiece(program.getLoadCNC().getWorkPiece());
			}
			if(program.getLoadCNC().getOffset().getId() > 0) {
				CoordinatesHandler.saveCoordinates(program.getLoadCNC().getOffset());
			}
			if(program.getLoadCNC().getId() > 0) {
				Stephandler.saveProgramStep(program.getLoadCNC());
			}
				// unloadCNC
			if(program.getUnloadCNC().getGripperHead().getId() > 0) {
				GripperHeadHandle.saveGripperHead(program.getUnloadCNC().getGripperHead());
			}
			if(program.getUnloadCNC().getGripper().getId() > 0) {
				Gripperhandler.getGripperIdByName(program.getUnloadCNC().getGripper().getName(),
						program.getUnloadCNC().getGripper());
			}
			if(program.getUnloadCNC().getSmooth().getId() > 0) {
				SmoothHandler.saveSmooth(program.getUnloadCNC().getSmooth());
			}
			if(program.getUnloadCNC().getWorkPiece().getId() > 0) {
				Workpiecehandler.saveWorkPiece(program.getUnloadCNC().getWorkPiece());
			}
			if(program.getUnloadCNC().getOffset().getId() > 0) {
				CoordinatesHandler.saveCoordinates(program.getUnloadCNC().getOffset());
			}
			if(program.getUnloadCNC().getId() > 0) {
				Stephandler.saveProgramStep(program.getUnloadCNC());
			}
				// loadstacker
			if(program.getLoadstacker().getGripperHead().getId() > 0) {
				GripperHeadHandle.saveGripperHead(program.getLoadstacker().getGripperHead());
			}
			if(program.getLoadstacker().getGripper().getId() > 0) {
				Gripperhandler.getGripperIdByName(program.getLoadstacker().getGripper().getName(),
						program.getLoadstacker().getGripper());
			}
			if(program.getLoadstacker().getSmooth().getId() > 0) {
				SmoothHandler.saveSmooth(program.getLoadstacker().getSmooth());
			}
			if(program.getLoadstacker().getWorkPiece().getId() > 0) {
				Workpiecehandler.saveWorkPiece(program.getLoadstacker().getWorkPiece());
			}
			if(program.getLoadstacker().getOffset().getId() > 0) {
				CoordinatesHandler.saveCoordinates(program.getLoadstacker().getOffset());
			}
			if(program.getLoadstacker().getId() > 0) {
				Stephandler.saveProgramStep(program.getLoadstacker());
			}
			if(program.getRobotSetting().getId() > 0) {
				RobotSettinghandler.saveRobotSetting(program.getRobotSetting());
			}
//			PreparedStatement stmt = conn.prepareStatement(
//					"UPDATE PROGRAM SET NAME=?, CREATION=?, LASTOPENED=?,UNLOADSTACKER=?,LOADCNC=?,UNLOADCNC=?,LOADSTACKER=?,ROBOTSETTING=?,ISHASTEACH=? WHERE ID = ?");			
//			stmt.setString(1, program.getName());
//			stmt.setTimestamp(2, program.getTimeCreate());
//			stmt.setTimestamp(3, program.getTimeLastOpen());
//			stmt.setInt(4, program.getUnloadstacker().getId());
//			stmt.setInt(5, program.getLoadCNC().getId());
//			stmt.setInt(6, program.getUnloadCNC().getId());
//			stmt.setInt(7, program.getLoadstacker().getId());
//			stmt.setInt(8, program.getRobotSetting().getId());
//			stmt.setBoolean(9, program.isHasTeach());
//			stmt.setInt(10, program.getId());
//			stmt.executeUpdate();
		}
	}

	public static void updateProgramTeachStatu() {
		try {
			PreparedStatement stmt = conn.prepareStatement("UPDATE PROGRAM SET ishasteach = ? WHERE name = ?");
			stmt.setBoolean(1, true);
			stmt.setString(2, DBHandler.getInstance().getProgramName());
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
}
