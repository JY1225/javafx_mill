package cn.greatoo.easymill.ui.configure.devicesConfig;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cn.greatoo.easymill.cnc.CNCMachine;
import cn.greatoo.easymill.cnc.EWayOfOperating;
import cn.greatoo.easymill.cnc.GenericMCode;
import cn.greatoo.easymill.cnc.MCodeAdapter;
import cn.greatoo.easymill.db.util.CNCHandler;
import cn.greatoo.easymill.external.communication.socket.SocketConnection;
import cn.greatoo.easymill.robot.FanucRobot;
import cn.greatoo.easymill.ui.main.Controller;
import cn.greatoo.easymill.ui.main.MainViewController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;

/**
 * 数控机床通用，M代码菜单
 *
 */
public class CNCConfigViewController extends Controller {

	@FXML
	private GridPane gridPane;
	@FXML
	private ComboBox comboBox;
	@FXML
	private Button saveBt;
	@FXML
	private Button generalBt;
	@FXML
	private Button MCodeBt;
	List<Button> bts;
	FXMLLoader fxmlLoader;
	private Parent generalParent;
	private Parent mCodeParent;
	private static CNCMachine cnc;

	@SuppressWarnings("unchecked")
	public void init() {
		bts = new ArrayList<Button>();
		bts.add(generalBt);
		bts.add(MCodeBt);
		cnc = CNCMachine.getInstance(null, null, null);
		if (cnc != null && cnc.getId() > 0) {
			comboBox.getItems().add(cnc.getSocketConnection().getName());
		} else {
			comboBox.getItems().add("");
		}
		comboBox.getSelectionModel().select(0);
	}

	@SuppressWarnings({ "static-access", "unchecked" })
	@FXML
	public void saveBtAction(ActionEvent event) {

		// askConfirmation(MainViewController.parentStackPane, "保存CNC配置信息",
		// "请注意，更改只有在重启后生效！");
		MCodeAdapter mCodeAdapter = null;
		SocketConnection socketConnection = null;
		if (mCodeConfigViewController != null) {
			List<String> robotServiceInputNames = mCodeConfigViewController.getRobotServiceInputNames();
			List<String> robotServiceOutputNames = mCodeConfigViewController.getRobotServiceOutputNames();

			List<String> mCodeNames = mCodeConfigViewController.getMCodeNames();

			List<Set<Integer>> robotServiceInputs = mCodeConfigViewController.getMCodeRobotServiceInputs();
			List<Set<Integer>> robotServiceOutputs = mCodeConfigViewController.getMCodeRobotServiceOutputs();

			List<GenericMCode> MCode = new ArrayList<>();
			for (int i = 0; i < robotServiceInputs.size(); i++) {
				GenericMCode genericMCode = new GenericMCode(i, mCodeNames.get(i), robotServiceInputs.get(i),
						robotServiceOutputs.get(i));
				MCode.add(genericMCode);
			}
			mCodeAdapter = new MCodeAdapter(MCode, robotServiceInputNames, robotServiceOutputNames);
		}
		if(generalConfigViewController != null) {
		 socketConnection = generalConfigViewController.getSocketConnection();
		}
		// 更新
		if (cnc != null && cnc.getId() > 0) {
			if(mCodeAdapter != null) {
				cnc.setMCodeAdapter(mCodeAdapter);
				CNCMachine.getInstance(null, null, null).setMCodeAdapter(mCodeAdapter);
			}
			if(socketConnection != null) {
				cnc.setSocketConnection(socketConnection);
				CNCMachine.getInstance(null, null, null).setSocketConnection(socketConnection);
			}
			cnc.setWayOfOperating(EWayOfOperating.getWayOfOperatingById(2));// 目前都是MCode模式			
			CNCMachine.getInstance(null, null, null).setWayOfOperating(EWayOfOperating.getWayOfOperatingById(2));
		} else {
			// 添加
			cnc = CNCMachine.getInstance(socketConnection, mCodeAdapter, EWayOfOperating.getWayOfOperatingById(2));
		}
		comboBox.getItems().set(0, cnc.getSocketConnection().getName());
		comboBox.getSelectionModel().select(0);
		try {
			CNCHandler.saveCNC(cnc);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void generalBtAction(ActionEvent event) {
		openGeneralview(cnc);
	}

	@FXML
	public void MCodeBtAction(ActionEvent event) {
		openMCodeView(cnc);
	}

	@FXML
	public void comboBoxAction(ActionEvent event) {
		if (comboBox.getValue() != null) {

		} else {

		}
	}

	GeneralConfigViewController generalConfigViewController;

	private void openGeneralview(CNCMachine cnc) {
		if (comboBox.getValue() == null) {
			showNotificationOverlay(MainViewController.parentStackPane, "通用配置", "注意，请选择机床！");
		} else {
			isClicked(bts, generalBt);
			if (!gridPane.getChildren().contains(generalParent)) {
				try {
					URL location = getClass()
							.getResource("/cn/greatoo/easymill/ui/configure/devicesConfig/GeneralConfigView.fxml");
					fxmlLoader = new FXMLLoader();
					fxmlLoader.setLocation(location);
					fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
					generalParent = fxmlLoader.load();
					generalConfigViewController = fxmlLoader.getController();
					generalConfigViewController.init(cnc);
					gridPane.add(generalParent, 0, 1, 2, 1);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else
				setDisVisible(1, gridPane, generalParent);
		}
	}

	MCodeConfigViewController mCodeConfigViewController;

	private void openMCodeView(CNCMachine cnc) {
		if (comboBox.getValue() == null) {
			showNotificationOverlay(MainViewController.parentStackPane, "MCode配置", "注意，请选择机床！");
		} else {
			isClicked(bts, MCodeBt);
			if (!gridPane.getChildren().contains(mCodeParent)) {
				try {
					URL location = getClass()
							.getResource("/cn/greatoo/easymill/ui/configure/devicesConfig/MCodeConfigView.fxml");
					fxmlLoader = new FXMLLoader();
					fxmlLoader.setLocation(location);
					fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
					mCodeParent = fxmlLoader.load();
					mCodeConfigViewController = fxmlLoader.getController();
					mCodeConfigViewController.init(cnc);
					gridPane.add(mCodeParent, 0, 1, 2, 1);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else
				setDisVisible(1, gridPane, mCodeParent);
		}
	}

	@Override
	public void setMessege(String mess) {
		// TODO Auto-generated method stub

	}
}
