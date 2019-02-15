package cn.greatoo.easymill.db.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Collection;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cn.greatoo.easymill.entity.Coordinates;
import cn.greatoo.easymill.entity.Gripper;
import cn.greatoo.easymill.entity.GripperHead;
import cn.greatoo.easymill.entity.Program;
import cn.greatoo.easymill.entity.RobotSetting;
import cn.greatoo.easymill.entity.Smooth;
import cn.greatoo.easymill.entity.Step;
import cn.greatoo.easymill.entity.WorkPiece;

public class Programhandler {
	private final static Logger LOGGER = LogManager.getLogger(Programhandler.class.getName());
	private static Connection conn = DBHandler.getInstance().getConnection();

	public static Program getProgram() {
		Program program = null;
		try {
			PreparedStatement stmt2 = conn.prepareStatement("SELECT * FROM PROGRAM Order By PROGRAM.LASTOPENED ASC");
			ResultSet results = stmt2.executeQuery();
			while (results.next()) {
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
				program.setOrientation(results.getFloat("ORIENTATION"));
				program.setAmount(results.getInt("AMOUNT"));
				program.setLayers(results.getInt("LAYERS"));
				program.setStudHeight_Workpiece(results.getFloat("STUDHEIGHT_WORKPIECE"));
				if (program != null) {
					DBHandler.getInstance().setProgramName(program.getName());
					DBHandler.getInstance().getProgramBuffer().put(program.getName(), program);
				}
			}

			Timestamp creatTime = new Timestamp(System.currentTimeMillis());
			Timestamp lastOpenTime = new Timestamp(System.currentTimeMillis());
			Step unloadStacker = new Step(new GripperHead(), new Gripper(), new WorkPiece(), 1, new Smooth(),
					new Coordinates());
			Step loadCNC = new Step(new GripperHead(), new Gripper(), new WorkPiece(), 3, new Smooth(),
					new Coordinates());
			Step unloadCNC = new Step(new GripperHead(), new Gripper(), new WorkPiece(), 3, new Smooth(),
					new Coordinates());
			Step loadstacker = new Step(new GripperHead(), new Gripper(), new WorkPiece(), 1, new Smooth(),
					new Coordinates());
			RobotSetting robotSetting = new RobotSetting();
			program = new Program(null, unloadStacker, loadCNC, unloadCNC, loadstacker, creatTime, lastOpenTime,
					robotSetting, false, 90, 1, 1, 16);

			DBHandler.getInstance().getProgramBuffer().put(null, program);
			if (DBHandler.getInstance().getProgramBuffer().size() == 0) {
				DBHandler.getInstance().setProgramName(null);
			}

		} catch (SQLException ex) {
			LOGGER.log(Level.ERROR, "{}", ex);
		}
		return program;
	}

	public static void saveProgram(Program program) throws SQLException {
		if (program.getId() <= 0) {// insert
			// unloadstacker
			program.getUnloadstacker().getGripperHead().setId(0);
			GripperHeadHandle.saveGripperHead(program.getUnloadstacker().getGripperHead());
			program.getUnloadstacker().getGripper().setId(0);
			Gripperhandler.getGripperIdByName(program.getUnloadstacker().getGripper().getName(),
					program.getUnloadstacker().getGripper());
			program.getUnloadstacker().getSmooth().setId(0);
			SmoothHandler.saveSmooth(program.getUnloadstacker().getSmooth());
			program.getUnloadstacker().getWorkPiece().setId(0);
			Workpiecehandler.saveWorkPiece(program.getUnloadstacker().getWorkPiece());
			program.getUnloadstacker().getOffset().setId(0);
			CoordinatesHandler.saveCoordinates(program.getUnloadstacker().getOffset());
			program.getUnloadstacker().setId(0);
			Stephandler.saveProgramStep(program.getUnloadstacker());

			// loadCNC
			program.getLoadCNC().setGripperHead(program.getUnloadstacker().getGripperHead());
			GripperHeadHandle.saveGripperHead(program.getLoadCNC().getGripperHead());
			program.getLoadCNC().setGripper(program.getUnloadstacker().getGripper());
			Gripperhandler.getGripperIdByName(program.getLoadCNC().getGripper().getName(),
					program.getLoadCNC().getGripper());
			program.getLoadCNC().getSmooth().setId(0);
			SmoothHandler.saveSmooth(program.getLoadCNC().getSmooth());
			program.getLoadCNC().setWorkPiece(program.getUnloadstacker().getWorkPiece());
			Workpiecehandler.saveWorkPiece(program.getLoadCNC().getWorkPiece());
			program.getLoadCNC().getOffset().setId(0);
			CoordinatesHandler.saveCoordinates(program.getLoadCNC().getOffset());
			program.getLoadCNC().setId(0);
			Stephandler.saveProgramStep(program.getLoadCNC());

			// unloadCNC
			program.getUnloadCNC().getGripperHead().setId(0);
			GripperHeadHandle.saveGripperHead(program.getUnloadCNC().getGripperHead());
			program.getUnloadCNC().getGripper().setId(0);
			Gripperhandler.getGripperIdByName(program.getUnloadCNC().getGripper().getName(),
					program.getUnloadCNC().getGripper());
			program.getUnloadCNC().getSmooth().setId(0);
			SmoothHandler.saveSmooth(program.getUnloadCNC().getSmooth());
			program.getUnloadCNC().getWorkPiece().setId(0);
			Workpiecehandler.saveWorkPiece(program.getUnloadCNC().getWorkPiece());
			program.getUnloadCNC().getOffset().setId(0);
			CoordinatesHandler.saveCoordinates(program.getUnloadCNC().getOffset());
			program.getUnloadCNC().setId(0);
			Stephandler.saveProgramStep(program.getUnloadCNC());

			// loadstacker
			program.getLoadstacker().setGripperHead(program.getUnloadCNC().getGripperHead());
			GripperHeadHandle.saveGripperHead(program.getLoadstacker().getGripperHead());
			program.getLoadstacker().setGripper(program.getUnloadCNC().getGripper());
			Gripperhandler.getGripperIdByName(program.getLoadstacker().getGripper().getName(),
					program.getLoadstacker().getGripper());
			program.getLoadstacker().getSmooth().setId(0);
			SmoothHandler.saveSmooth(program.getLoadstacker().getSmooth());
			program.getLoadstacker().setWorkPiece(program.getUnloadCNC().getWorkPiece());
			Workpiecehandler.saveWorkPiece(program.getLoadstacker().getWorkPiece());
			program.getLoadstacker().getOffset().setId(0);
			CoordinatesHandler.saveCoordinates(program.getLoadstacker().getOffset());
			program.getLoadstacker().setId(0);
			Stephandler.saveProgramStep(program.getLoadstacker());

			program.getRobotSetting().setId(0);
			RobotSettinghandler.saveRobotSetting(program.getRobotSetting());

			PreparedStatement stmt = conn.prepareStatement(
					"INSERT INTO PROGRAM(NAME, CREATION, LASTOPENED,UNLOADSTACKER,LOADCNC,UNLOADCNC,LOADSTACKER,ROBOTSETTING,ISHASTEACH,ORIENTATION, LAYERS, AMOUNT, STUDHEIGHT_WORKPIECE) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)",
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
			stmt.setFloat(10, program.getOrientation());
			stmt.setFloat(11, program.getLayers());
			stmt.setFloat(12, program.getAmount());
			stmt.setFloat(13, program.getStudHeight_Workpiece());
			stmt.executeUpdate();
			ResultSet keys = stmt.getGeneratedKeys();
			if ((keys != null) && (keys.next())) {
				program.setId(keys.getInt(1));
			}
			// DBHandler.getInstance().setProgramName(program.getName());
			DBHandler.getInstance().getProgramBuffer().put(program.getName(), program);
		} else {
			GripperHeadHandle.saveGripperHead(program.getUnloadstacker().getGripperHead());
			Gripperhandler.getGripperIdByName(program.getUnloadstacker().getGripper().getName(),
					program.getUnloadstacker().getGripper());
			SmoothHandler.saveSmooth(program.getUnloadstacker().getSmooth());
			Workpiecehandler.saveWorkPiece(program.getUnloadstacker().getWorkPiece());
			CoordinatesHandler.saveCoordinates(program.getUnloadstacker().getOffset());
			Stephandler.saveProgramStep(program.getUnloadstacker());

			// loadCNC
			program.getLoadCNC().setGripperHead(program.getUnloadstacker().getGripperHead());
			GripperHeadHandle.saveGripperHead(program.getLoadCNC().getGripperHead());

			program.getLoadCNC().setGripper(program.getUnloadstacker().getGripper());
			Gripperhandler.getGripperIdByName(program.getLoadCNC().getGripper().getName(),
					program.getLoadCNC().getGripper());
			SmoothHandler.saveSmooth(program.getLoadCNC().getSmooth());
			program.getLoadCNC().setWorkPiece(program.getUnloadstacker().getWorkPiece());
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
			program.getLoadstacker().setGripperHead(program.getUnloadCNC().getGripperHead());
			GripperHeadHandle.saveGripperHead(program.getLoadstacker().getGripperHead());
			program.getLoadstacker().setGripper(program.getUnloadCNC().getGripper());
			Gripperhandler.getGripperIdByName(program.getLoadstacker().getGripper().getName(),
					program.getLoadstacker().getGripper());
			SmoothHandler.saveSmooth(program.getLoadstacker().getSmooth());
			program.getLoadstacker().setWorkPiece(program.getUnloadCNC().getWorkPiece());
			Workpiecehandler.saveWorkPiece(program.getLoadstacker().getWorkPiece());
			CoordinatesHandler.saveCoordinates(program.getLoadstacker().getOffset());
			Stephandler.saveProgramStep(program.getLoadstacker());

			RobotSettinghandler.saveRobotSetting(program.getRobotSetting());

			PreparedStatement stmt = conn.prepareStatement(
					"UPDATE PROGRAM SET ORIENTATION = ?, LAYERS = ?, AMOUNT = ?, STUDHEIGHT_WORKPIECE = ? WHERE ID = ?");
			stmt.setFloat(1, program.getOrientation());
			stmt.setFloat(2, program.getLayers());
			stmt.setFloat(3, program.getAmount());
			stmt.setFloat(4, program.getStudHeight_Workpiece());
			stmt.setInt(5, program.getId());
			stmt.executeUpdate();
		}
	}

	public static void deleProgram(Program p) {
		try {
			PreparedStatement stmt = conn.prepareStatement("DELETE FROM PROGRAM WHERE ID = ?");
			stmt.setInt(1, p.getId());
			stmt.executeUpdate();
			stmt = conn.prepareStatement("DELETE FROM ROBOTSETTING WHERE ID = ?");
			stmt.setInt(1, p.getRobotSetting().getId());
			stmt.executeUpdate();

			stmt = conn.prepareStatement("DELETE FROM STEP WHERE ID IN(?,?,?,?)");
			stmt.setInt(1, p.getUnloadstacker().getId());
			stmt.setInt(2, p.getLoadCNC().getId());
			stmt.setInt(3, p.getUnloadCNC().getId());
			stmt.setInt(4, p.getLoadstacker().getId());
			stmt.executeUpdate();

			stmt = conn.prepareStatement("DELETE FROM GRIPPERHEAD WHERE ID IN(?,?,?,?)");
			stmt.setInt(1, p.getUnloadstacker().getGripperHead().getId());
			stmt.setInt(2, p.getLoadCNC().getGripperHead().getId());
			stmt.setInt(3, p.getUnloadCNC().getGripperHead().getId());
			stmt.setInt(4, p.getLoadstacker().getGripperHead().getId());
			stmt.executeUpdate();

			stmt = conn.prepareStatement("DELETE FROM WORKPIECE WHERE ID IN(?,?,?,?)");
			stmt.setInt(1, p.getUnloadstacker().getWorkPiece().getId());
			stmt.setInt(2, p.getLoadCNC().getWorkPiece().getId());
			stmt.setInt(3, p.getUnloadCNC().getWorkPiece().getId());
			stmt.setInt(4, p.getLoadstacker().getWorkPiece().getId());
			stmt.executeUpdate();

			stmt = conn.prepareStatement("DELETE FROM COORDINATES WHERE ID IN(?,?,?,?)");
			stmt.setInt(1, p.getUnloadstacker().getOffset().getId());
			stmt.setInt(2, p.getLoadCNC().getOffset().getId());
			stmt.setInt(3, p.getUnloadCNC().getOffset().getId());
			stmt.setInt(4, p.getLoadstacker().getOffset().getId());
			stmt.executeUpdate();

			stmt = conn.prepareStatement("DELETE FROM SMOOTH WHERE ID IN(?,?,?,?)");
			stmt.setInt(1, p.getUnloadstacker().getSmooth().getId());
			stmt.setInt(2, p.getLoadCNC().getSmooth().getId());
			stmt.setInt(3, p.getUnloadCNC().getSmooth().getId());
			stmt.setInt(4, p.getLoadstacker().getSmooth().getId());
			stmt.executeUpdate();

			if (DBHandler.getInstance().getProgramName().equals(p.getName())) {
				DBHandler.getInstance().setProgramName(null);
			}
			DBHandler.getInstance().getProgramBuffer().remove(p.getName(), p);

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public static void updateProgramTeachStatu(boolean statu) {
		try {
			PreparedStatement stmt = conn.prepareStatement("UPDATE PROGRAM SET ishasteach = ? WHERE name = ?");
			stmt.setBoolean(1, statu);
			stmt.setString(2, DBHandler.getInstance().getProgramName());
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public static void updateLastOpenProgram(Program program) {
		try {
			PreparedStatement stmt = conn.prepareStatement("UPDATE PROGRAM SET LASTOPENED = ? WHERE name = ?");
			stmt.setTimestamp(1, program.getTimeLastOpen());
			stmt.setString(2, program.getName());
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
}
