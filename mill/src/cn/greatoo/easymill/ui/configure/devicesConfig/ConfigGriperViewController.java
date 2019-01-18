package cn.greatoo.easymill.ui.configure.devicesConfig;

import java.util.ArrayList;

import cn.greatoo.easymill.ui.main.Controller;
import cn.greatoo.easymill.util.IconFlowSelector;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;

public class ConfigGriperViewController  extends Controller{
	@FXML
	private GridPane gridPane;
	@FXML
	private ComboBox<String> combox;
	@FXML
	private Button editBt;
	@FXML
	private Button newBt;
	
	@FXML
	private ComboBox combox;

	private IconFlowSelector ifsClamping;
	private static final double ICONFLOWSELECTOR_WIDTH = 530;
	CNCClampingsView robotGripperView;
	private ArrayList<Button> bts;

	public void init() {
		bts = new ArrayList<Button>();
		bts.add(editBt);
		bts.add(newBt);
		// 夹爪图
		ifsClamping = new IconFlowSelector(false);
		ifsClamping.setPrefWidth(ICONFLOWSELECTOR_WIDTH);
		gridPane.add(ifsClamping, 0, 0, 2, 1);
		// 编辑，新增
		robotGripperView = new CNCClampingsView();
		robotGripperView.init(gridPane, editBt, newBt, ifsClamping);
		
		if(combox.getValue() != null) {
		editBt.setDisable(false);
		}else {
			editBt.setDisable(true);
		}
	}

	@FXML
	public void editBtAction(ActionEvent event) {
		isDisSelect(bts, editBt);			
		newBt.setDisable(false);
		combox.setDisable(false);
		String clampingName = combox.getValue().toString();
		robotGripperView.clickedEdit(clampingName);
	}
	
	@FXML
	public void newBtAction(ActionEvent event) {
		robotGripperView.clickedNew();
	}

	@Override
	public void setMessege(String mess) {
		// TODO Auto-generated method stub
		
	}
}
