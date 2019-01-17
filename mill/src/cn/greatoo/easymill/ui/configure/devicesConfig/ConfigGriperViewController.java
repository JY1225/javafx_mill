package cn.greatoo.easymill.ui.configure.devicesConfig;

import cn.greatoo.easymill.util.IconFlowSelector;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;

public class ConfigGriperViewController {
	@FXML
	private GridPane gridPane;
	@FXML
	private ComboBox<String> combox;
	@FXML
	private Button editBt;
	@FXML
	private Button newBt;

	private IconFlowSelector ifsClamping;
	private static final double ICONFLOWSELECTOR_WIDTH = 530;
	CNCClampingsView robotGripperView;

	public void init() {
		// 夹爪图
		ifsClamping = new IconFlowSelector(false);
		ifsClamping.setPrefWidth(ICONFLOWSELECTOR_WIDTH);
		gridPane.add(ifsClamping, 0, 0, 2, 1);
		// 编辑，新增
		robotGripperView = new CNCClampingsView();
		robotGripperView.init(gridPane, editBt, newBt, ifsClamping);
	}

	@FXML
	public void editBtAction(ActionEvent event) {
		robotGripperView.clickedEdit();
		
	}
	
	@FXML
	public void newBtAction(ActionEvent event) {
		robotGripperView.clickedNew();
	}
}
