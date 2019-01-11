package cn.greatoo.easymill.ui.configure.devicesConfig;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cn.greatoo.easymill.cnc.CNCMachine;
import cn.greatoo.easymill.cnc.EWayOfOperating;
import cn.greatoo.easymill.cnc.GenericMCode;
import cn.greatoo.easymill.cnc.MCodeAdapter;
import cn.greatoo.easymill.external.communication.socket.SocketConnection;
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
		comboBox.getItems().add("CNC MACHINE");
		comboBox.getSelectionModel().select(0);
		cnc = CNCMachine.getInstance(null, null, null);
		if(cnc.getSocketConnection() == null) {
			cnc.setName((String)comboBox.getValue());
		}else {		
			cnc.getSocketConnection().setName("CNC MACHINE");
		}
	}

	@SuppressWarnings({"static-access" })
	@FXML
	public void saveBtAction(ActionEvent event) {
		//askConfirmation(MainViewController.parentStackPane, "保存CNC配置信息", "请注意，更改只有在重启后生效！");
		List<String> robotServiceInputNames = mCodeConfigViewController.getRobotServiceInputNames();
		List<String> robotServiceOutputNames = mCodeConfigViewController.getRobotServiceOutputNames();
		
		List<String> mCodeNames = mCodeConfigViewController.getMCodeNames();
		
		List<Set<Integer>> robotServiceInputs = mCodeConfigViewController.getMCodeRobotServiceInputs();
		List<Set<Integer>> robotServiceOutputs = mCodeConfigViewController.getMCodeRobotServiceOutputs();
		
		SocketConnection socketConnection = generalConfigViewController.getSocketConnection();
				
		List<GenericMCode> genericMCodes = new ArrayList<>();
		for(int i = 0;i < robotServiceInputs.size();i++) {		
			GenericMCode genericMCode = new GenericMCode(i, mCodeNames.get(i), robotServiceInputs.get(i),
					robotServiceOutputs.get(i));
			genericMCodes.add(genericMCode);
		}
		
		MCodeAdapter mCodeAdapter = new MCodeAdapter(genericMCodes,robotServiceInputNames,robotServiceOutputNames);
		cnc.setMCodeAdapter(mCodeAdapter);
		cnc.setSocketConnection(socketConnection);
		cnc.setWayOfOperating(EWayOfOperating.getWayOfOperatingById(2));
		
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
