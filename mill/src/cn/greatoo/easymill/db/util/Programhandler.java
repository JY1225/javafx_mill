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

import cn.greatoo.easymill.entity.CNCSetting;
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
		Program oprogram = null;
		try {
			PreparedStatement stmt2 = conn.prepareStatement("SELECT * FROM PROGRAM Order By PROGRAM.LASTOPENED ASC");
			ResultSet results = stmt2.executeQuery();
			while (results.next()) {
				program = new Program();
				oprogram = new Program();
				program.setId(results.getInt("ID"));
				oprogram.setId(results.getInt("ID"));
				program.setName(results.getString("NAME"));
				oprogram.setName(results.getString("NAME"));
				program.setTimeCreate(results.getTimestamp("CREATION"));
				oprogram.setTimeCreate(results.getTimestamp("CREATION"));
				program.setTimeLastOpen(results.getTimestamp("LASTOPENED"));
				oprogram.setTimeLastOpen(results.getTimestamp("LASTOPENED"));
				Step unloadstacker = Stephandler.getProgramStepsById(results.getInt("unloadstacker"));
				program.setUnloadstacker(unloadstacker);
				oprogram.setUnloadstacker(Stephandler.getProgramStepsById(results.getInt("unloadstacker")));
				Step loadCNC = Stephandler.getProgramStepsById(results.getInt("loadCNC"));
				program.setLoadCNC(loadCNC);
				oprogram.setLoadCNC(Stephandler.getProgramStepsById(results.getInt("loadCNC")));
				Step unloadCNC = Stephandler.getProgramStepsById(results.getInt("unloadCNC"));
				program.setUnloadCNC(unloadCNC);
				oprogram.setUnloadCNC(Stephandler.getProgramStepsById(results.getInt("unloadCNC")));
				Step loadstacker = Stephandler.getProgramStepsById(results.getInt("loadstacker"));
				program.setLoadstacker(loadstacker);
				oprogram.setLoadstacker(Stephandler.getProgramStepsById(results.getInt("loadstacker")));
				RobotSetting robotSetting = RobotSettinghandler.getRobotSettingById(results.getInt("ROBOTSETTING"));
				program.setRobotSetting(robotSetting);
				oprogram.setRobotSetting(RobotSettinghandler.getRobotSettingById(results.getInt("ROBOTSETTING")));
				program.setCncSetting(CNCSettingHandler.getCNCSettingById(results.getInt("CNCSETTING")));
				oprogram.setCncSetting(CNCSettingHandler.getCNCSettingById(results.getInt("CNCSETTING")));
				program.setHasTeach(results.getBoolean("isHasTeach"));
				oprogram.setHasTeach(results.getBoolean("isHasTeach"));
				program.setOrientation(results.getFloat("ORIENTATION"));
				oprogram.setOrientation(results.getFloat("ORIENTATION"));
				program.setAmount(results.getInt("AMOUNT"));
				oprogram.setAmount(results.getInt("AMOUNT"));
				program.setLayers(results.getInt("LAYERS"));
				oprogram.setLayers(results.getInt("LAYERS"));
				program.setStudHeight_Workpiece(results.getFloat("STUDHEIGHT_WORKPIECE"));
				oprogram.setStudHeight_Workpiece(results.getFloat("STUDHEIGHT_WORKPIECE"));
				program.setRawWorkPiece(Workpiecehandler.getWorkPieceById(results.getInt("rawWorkPiece")));
				oprogram.setRawWorkPiece(Workpiecehandler.getWorkPieceById(results.getInt("rawWorkPiece")));
				program.setFinishedWorkPiece(Workpiecehandler.getWorkPieceById(results.getInt("finishedWorkPiece")));
				oprogram.setFinishedWorkPiece(Workpiecehandler.getWorkPieceById(results.getInt("finishedWorkPiece")));
				if (program != null) {
					DBHandler.getInstance().setProgramName(program.getName());
					DBHandler.getInstance().getProgramBuffer().put(program.getName(), program);					
					DBHandler.getInstance().setOProgram(oprogram);
				}
			}
			program = new Program(null, new Step(new GripperHead("A",false), new Gripper(), 1, new Smooth(5.0f,0f,5.0f),
					new Coordinates()), new Step(new GripperHead("A",false), new Gripper(), 3, new Smooth(),
					new Coordinates()), new Step(new GripperHead("B",false), new Gripper(), 3, new Smooth(),
					new Coordinates()), new Step(new GripperHead("B",false), new Gripper(), 1, new Smooth(5.0f,0f,5.0f),
					new Coordinates()), 
					new Timestamp(System.currentTimeMillis()), 
					new Timestamp(System.currentTimeMillis()),
					new RobotSetting(false),new CNCSetting(CNCSetting.ClampingType.LENGTH), false, 0, 1, 1, 16, new WorkPiece(WorkPiece.Type.RAW,0,0,0,0,WorkPiece.Material.OTHER,0), 
					new WorkPiece(WorkPiece.Type.FINISHED,0,0,0,0,WorkPiece.Material.OTHER,0));
			oprogram = new Program(null, new Step(new GripperHead("A",false), new Gripper(), 1, new Smooth(5.0f,0f,5.0f),
					new Coordinates()), new Step(new GripperHead("A",false), new Gripper(), 3, new Smooth(),
					new Coordinates()), new Step(new GripperHead("B",false), new Gripper(), 3, new Smooth(),
					new Coordinates()), new Step(new GripperHead("B",false), new Gripper(), 1, new Smooth(5.0f,0f,5.0f),
					new Coordinates()), 
					new Timestamp(System.currentTimeMillis()), 
					new Timestamp(System.currentTimeMillis()),
					new RobotSetting(false),new CNCSetting(CNCSetting.ClampingType.LENGTH), false, 0, 1, 1, 16, new WorkPiece(WorkPiece.Type.RAW,0,0,0,0,WorkPiece.Material.OTHER,0), 
					new WorkPiece(WorkPiece.Type.FINISHED,0,0,0,0,WorkPiece.Material.OTHER,0));
			DBHandler.getInstance().getProgramBuffer().put(null, program);
			if (DBHandler.getInstance().getProgramBuffer().size() == 0) {
				DBHandler.getInstance().setProgramName(null);
				DBHandler.getInstance().setOProgram(oprogram);
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
			program.getLoadstacker().getOffset().setId(0);
			CoordinatesHandler.saveCoordinates(program.getLoadstacker().getOffset());
			program.getLoadstacker().setId(0);
			Stephandler.saveProgramStep(program.getLoadstacker());

			program.getRawWorkPiece().setId(0);
			Workpiecehandler.saveWorkPiece(program.getRawWorkPiece());
			program.getFinishedWorkPiece().setId(0);
			Workpiecehandler.saveWorkPiece(program.getFinishedWorkPiece());
			program.getRobotSetting().setId(0);
			RobotSettinghandler.saveRobotSetting(program.getRobotSetting());
			program.getCncSetting().setId(0);
			CNCSettingHandler.saveCNCSetting(program.getCncSetting());
			
			PreparedStatement stmt = conn.prepareStatement(
					"INSERT INTO PROGRAM(NAME, CREATION, LASTOPENED,UNLOADSTACKER,LOADCNC,UNLOADCNC,LOADSTACKER,ROBOTSETTING,CNCSETTING"
					+ ",ISHASTEACH,ORIENTATION, LAYERS, AMOUNT, STUDHEIGHT_WORKPIECE, rawWorkPiece, finishedWorkPiece) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
					Statement.RETURN_GENERATED_KEYS);
			int index = 1;
			stmt.setString(index++, program.getName());
			stmt.setTimestamp(index++, program.getTimeCreate());
			stmt.setTimestamp(index++, program.getTimeLastOpen());
			stmt.setInt(index++, program.getUnloadstacker().getId());
			stmt.setInt(index++, program.getLoadCNC().getId());
			stmt.setInt(index++, program.getUnloadCNC().getId());
			stmt.setInt(index++, program.getLoadstacker().getId());
			stmt.setInt(index++, program.getRobotSetting().getId());
			stmt.setInt(index++, program.getCncSetting().getId());
			stmt.setBoolean(index++, program.isHasTeach());
			stmt.setFloat(index++, program.getOrientation());
			stmt.setFloat(index++, program.getLayers());
			stmt.setFloat(index++, program.getAmount());
			stmt.setFloat(index++, program.getStudHeight_Workpiece());
			stmt.setInt(index++, program.getRawWorkPiece().getId());
			stmt.setInt(index++, program.getFinishedWorkPiece().getId());
			stmt.executeUpdate();
			ResultSet keys = stmt.getGeneratedKeys();
			if ((keys != null) && (keys.next())) {
				program.setId(keys.getInt(1));
			}
			DBHandler.getInstance().getProgramBuffer().put(program.getName(), program);
		} else {
			GripperHeadHandle.saveGripperHead(program.getUnloadstacker().getGripperHead());
			Gripperhandler.getGripperIdByName(program.getUnloadstacker().getGripper().getName(),
					program.getUnloadstacker().getGripper());
			SmoothHandler.saveSmooth(program.getUnloadstacker().getSmooth());
			CoordinatesHandler.saveCoordinates(program.getUnloadstacker().getOffset());
			Stephandler.saveProgramStep(program.getUnloadstacker());

			// loadCNC
			program.getLoadCNC().setGripperHead(program.getUnloadstacker().getGripperHead());
			GripperHeadHandle.saveGripperHead(program.getLoadCNC().getGripperHead());
			program.getLoadCNC().setGripper(program.getUnloadstacker().getGripper());
			Gripperhandler.getGripperIdByName(program.getLoadCNC().getGripper().getName(),
					program.getLoadCNC().getGripper());
			SmoothHandler.saveSmooth(program.getLoadCNC().getSmooth());
			CoordinatesHandler.saveCoordinates(program.getLoadCNC().getOffset());
			Stephandler.saveProgramStep(program.getLoadCNC());

			// unloadCNC
			GripperHeadHandle.saveGripperHead(program.getUnloadCNC().getGripperHead());
			Gripperhandler.getGripperIdByName(program.getUnloadCNC().getGripper().getName(),
					program.getUnloadCNC().getGripper());
			SmoothHandler.saveSmooth(program.getUnloadCNC().getSmooth());
			CoordinatesHandler.saveCoordinates(program.getUnloadCNC().getOffset());
			Stephandler.saveProgramStep(program.getUnloadCNC());

			// loadstacker
			program.getLoadstacker().setGripperHead(program.getUnloadCNC().getGripperHead());
			GripperHeadHandle.saveGripperHead(program.getLoadstacker().getGripperHead());
			program.getLoadstacker().setGripper(program.getUnloadCNC().getGripper());
			Gripperhandler.getGripperIdByName(program.getLoadstacker().getGripper().getName(),
					program.getLoadstacker().getGripper());
			SmoothHandler.saveSmooth(program.getLoadstacker().getSmooth());
			CoordinatesHandler.saveCoordinates(program.getLoadstacker().getOffset());
			Stephandler.saveProgramStep(program.getLoadstacker());

			Workpiecehandler.saveWorkPiece(program.getRawWorkPiece());
			Workpiecehandler.saveWorkPiece(program.getFinishedWorkPiece());
			RobotSettinghandler.saveRobotSetting(program.getRobotSetting());
			CNCSettingHandler.saveCNCSetting(program.getCncSetting());
			
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

			stmt = conn.prepareStatement("DELETE FROM WORKPIECE WHERE ID IN(?,?)");
			stmt.setInt(1, p.getRawWorkPiece().getId());
			stmt.setInt(2, p.getFinishedWorkPiece().getId());
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
