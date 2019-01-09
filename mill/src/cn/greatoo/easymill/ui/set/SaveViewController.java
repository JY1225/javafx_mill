package cn.greatoo.easymill.ui.set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cn.greatoo.easymill.entity.Clamping;
import cn.greatoo.easymill.entity.Gripper;
import cn.greatoo.easymill.entity.Program;
import cn.greatoo.easymill.entity.RobotPutSetting;
import cn.greatoo.easymill.entity.Smooth;
import cn.greatoo.easymill.entity.Stacker;
import cn.greatoo.easymill.entity.Step;
import cn.greatoo.easymill.entity.UserFrame;
import cn.greatoo.easymill.entity.WorkPiece;
import cn.greatoo.easymill.process.DuplicateProcessFlowNameException;
import cn.greatoo.easymill.ui.configure.devicesConfig.CoordinateViewController;
import cn.greatoo.easymill.ui.general.NotificationBox;
import cn.greatoo.easymill.ui.set.cnc.CNCDeviceViewController;
import cn.greatoo.easymill.ui.set.cnc.CNCFinishedWPViewController;
import cn.greatoo.easymill.ui.set.cnc.CNCPickViewController;
import cn.greatoo.easymill.ui.set.cnc.CNCPutViewController;
import cn.greatoo.easymill.ui.set.robot.griperA.ClampViewController;
import cn.greatoo.easymill.ui.set.robot.griperB.PickClampViewController;
import cn.greatoo.easymill.ui.set.table.load.PickViewController;
import cn.greatoo.easymill.ui.set.table.load.RawWPViewController;
import cn.greatoo.easymill.ui.set.table.unload.PlaceViewController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
public class SaveViewController {
	@FXML
	private GridPane saveGridPane;
	@FXML
	private Button saveBt;
	@FXML
	private TextField fulltxtName;
	
	private static Logger logger = LogManager.getLogger(SaveViewController.class.getName());
	private static NotificationBox notificationBox;

	public void init() {		
	
	}

	@FXML
	public void save(MouseEvent event) throws DuplicateProcessFlowNameException  {
		String programName = fulltxtName.getText();
		Stacker stacker = RawWPViewController.stacker;
		WorkPiece rawWorkPiece = RawWPViewController.workPiece;
		
		Smooth unloadStackerSmooth = PickViewController.smooth;

		Gripper loadGripper = ClampViewController.gripper;
		
		Clamping clamping = CNCDeviceViewController.clamping;		
		
		Smooth loadCNCSmooth = CNCPutViewController.loadCNCSmooth;
		RobotPutSetting RobotPutSetting = CNCPutViewController.RobotPutSetting;

		
		Smooth unloadCNCSmooth = CNCPickViewController.unloadCNCSmooth;
		
		Gripper unloadGripper = PickClampViewController.gripper;
		
		WorkPiece finishWorkPiece = CNCFinishedWPViewController.workPiece;
		
		Smooth loadStackerSmooth = PlaceViewController.smooth;
		
		UserFrame satckerFrame = CoordinateViewController.userFrame;
		Step unloadStacker = new Step(loadGripper,rawWorkPiece,satckerFrame,unloadStackerSmooth,null);
		
		Program program = new Program(programName,unloadStacker,unloadStacker,unloadStacker,unloadStacker,"","");
		
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
}
