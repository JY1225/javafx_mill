package cn.greatoo.easymill.ui.configure.devicesConfig;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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

	@SuppressWarnings("unchecked")
	public void init() {
		bts = new ArrayList<Button>();
		bts.add(generalBt);
		bts.add(MCodeBt);

		comboBox.getItems().add("CNC MACHINE1");
		comboBox.getItems().add("CNC MACHINE2");
	}

	@FXML
	public void saveBtAction(ActionEvent event) {
		showNotificationOverlay(MainViewController.parentStackPane, "保存CNC配置信息", "请注意，更改只有在重启后生效！");
	}

	@FXML
	public void generalBtAction(ActionEvent event) {
		openGeneralview();
	}

	@FXML
	public void MCodeBtAction(ActionEvent event) {
		openMCodeView();
	}

	@FXML
	public void comboBoxAction(ActionEvent event) {
		if (comboBox.getValue() != null) {

		} else {

		}
	}

	private void openGeneralview() {
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
					GeneralConfigViewController generalConfigViewController = fxmlLoader.getController();
					generalConfigViewController.init();
					gridPane.add(generalParent, 0, 1, 2, 1);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else
				setDisVisible(1, gridPane, generalParent);
		}
	}

	private void openMCodeView() {
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
					MCodeConfigViewController mCodeConfigViewController = fxmlLoader.getController();
					mCodeConfigViewController.init();
					gridPane.add(mCodeParent, 0, 1, 2, 1);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else
				setDisVisible(1, gridPane, mCodeParent);
		}
	}
}
