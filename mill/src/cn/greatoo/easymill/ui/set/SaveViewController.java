package cn.greatoo.easymill.ui.set;

import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cn.greatoo.easymill.db.util.ClampingHandler;
import cn.greatoo.easymill.db.util.DBHandler;
import cn.greatoo.easymill.db.util.Programhandler;
import cn.greatoo.easymill.db.util.Stackerhandler;
import cn.greatoo.easymill.entity.Clamping;
import cn.greatoo.easymill.entity.Coordinates;
import cn.greatoo.easymill.entity.Gripper;
import cn.greatoo.easymill.entity.GripperHead;
import cn.greatoo.easymill.entity.Program;
import cn.greatoo.easymill.entity.RobotSetting;
import cn.greatoo.easymill.entity.Smooth;
import cn.greatoo.easymill.entity.Stacker;
import cn.greatoo.easymill.entity.Step;
import cn.greatoo.easymill.entity.UserFrame;
import cn.greatoo.easymill.entity.WorkPiece;
import cn.greatoo.easymill.process.DuplicateProcessFlowNameException;
import cn.greatoo.easymill.ui.configure.devicesConfig.CoordinateViewController;
import cn.greatoo.easymill.ui.general.NotificationBox;
import cn.greatoo.easymill.ui.main.Controller;
import cn.greatoo.easymill.ui.main.MainViewController;
import cn.greatoo.easymill.ui.set.cnc.CNCDeviceViewController;
import cn.greatoo.easymill.ui.set.cnc.CNCFinishedWPViewController;
import cn.greatoo.easymill.ui.set.cnc.CNCPickViewController;
import cn.greatoo.easymill.ui.set.cnc.CNCPutViewController;
import cn.greatoo.easymill.ui.set.robot.griperA.ClampViewController;
import cn.greatoo.easymill.ui.set.robot.griperB.PickClampViewController;
import cn.greatoo.easymill.ui.set.table.load.PickViewController;
import cn.greatoo.easymill.ui.set.table.load.RawWPViewController;
import cn.greatoo.easymill.ui.set.table.unload.PlaceViewController;
import cn.greatoo.easymill.ui.teach.griperA.TeachPickViewController;
import cn.greatoo.easymill.ui.teach.griperA.TeachPutViewController;
import cn.greatoo.easymill.ui.teach.griperB.TeachGriperBPickViewController;
import cn.greatoo.easymill.ui.teach.griperB.TeachGriperBPutViewController;
import cn.greatoo.easymill.util.ThreadManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

public class SaveViewController extends Controller {
	@FXML
	private GridPane saveGridPane;
	@FXML
	private Button saveBt;
	@FXML
	private TextField fulltxtName;

	private static Logger logger = LogManager.getLogger(SaveViewController.class.getName());
	private static NotificationBox notificationBox;

	public void init() {
		fulltxtName.setText(DBHandler.getInstance().getProgramName());

	}

	@FXML
	public void save(MouseEvent event) throws DuplicateProcessFlowNameException {
		ThreadManager.submit(new Thread() {
			@Override
			public void run() {
				if (askConfirmation(MainViewController.parentStackPane, "保存", "确认保存？")) {
					saveProgram();
				}
			}
		});
	}

	private void saveProgram() {
		String programName = fulltxtName.getText();
		Timestamp creatTime = new Timestamp(System.currentTimeMillis());
		Timestamp lastOpenTime = new Timestamp(System.currentTimeMillis());

		// 设置
		Program program = RawWPViewController.program;
		WorkPiece rawWorkPiece = RawWPViewController.workPiece;

		Smooth unloadStackerSmooth = PickViewController.unloadStackerSmooth;

		Gripper loadGripper = ClampViewController.gripper;
		GripperHead loadGripperhead = ClampViewController.gripperhead;

		Clamping clamping = CNCDeviceViewController.clamping;

		Smooth loadCNCSmooth = CNCPutViewController.loadCNCSmooth;
		RobotSetting RobotSetting = CNCPutViewController.RobotPutSetting;

		Smooth unloadCNCSmooth = CNCPickViewController.unloadCNCSmooth;

		Gripper unloadGripper = PickClampViewController.gripper;
		GripperHead unloadGripperhead = PickClampViewController.gripperhead;

		WorkPiece finishWorkPiece = CNCFinishedWPViewController.workPiece;

		Smooth loadStackerSmooth = PlaceViewController.loadStackerSmooth;

		// step1
		Step unloadStacker = new Step(loadGripperhead, loadGripper, rawWorkPiece, 1, unloadStackerSmooth,
				new Coordinates());

		// step2
		Step loadCNC = new Step(loadGripperhead, loadGripper, rawWorkPiece, 3, loadCNCSmooth, new Coordinates());

		// step3
		Step unloadCNC = new Step(unloadGripperhead, unloadGripper, finishWorkPiece, 3, unloadCNCSmooth,
				new Coordinates());

		// step4
		Step loadstacker = new Step(unloadGripperhead, unloadGripper, finishWorkPiece, 1, loadStackerSmooth,
				new Coordinates());

		program.setName(programName);
		program.setUnloadstacker(unloadStacker);
		program.setLoadCNC(loadCNC);
		program.setUnloadCNC(unloadCNC);
		program.setLoadstacker(loadstacker);
		program.setTimeCreate(creatTime);
		program.setTimeLastOpen(lastOpenTime);
		program.setRobotSetting(RobotSetting);
		program.setHasTeach(false);
		if (programName.equals(DBHandler.getInstance().getProgramName())) {
			program.setId(DBHandler.getInstance().getProgramBuffer().get(programName).getId());
		}

		try {
//			if (stacker.getId() > 0) {
//				Stackerhandler.updateStacker(stacker);
//			}
			if (clamping.getId() > 0) {
				ClampingHandler.updateClamping(clamping);
			}
			Programhandler.saveProgram(program);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public static void showNotification(final String notification, NotificationBox.Type type) {
		notificationBox = SetMenuViewController.getnotificationBox();
		notificationBox.showNotification(notification, type);
		notificationBox.setVisible(true);
		notificationBox.setManaged(true);
	}

	public void hideNotification() {
		notificationBox = SetMenuViewController.getnotificationBox();
		notificationBox.setVisible(false);
		notificationBox.setManaged(false);
	}

	@Override
	public void setMessege(String mess) {
		// TODO Auto-generated method stub

	}
}
