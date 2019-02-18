package cn.greatoo.easymill.ui.configure.devicesConfig;

import java.sql.SQLException;
import java.util.ArrayList;

import cn.greatoo.easymill.db.util.ClampingHandler;
import cn.greatoo.easymill.entity.Clamping;
import cn.greatoo.easymill.ui.main.Controller;
import cn.greatoo.easymill.util.IconFlowSelector;
import cn.greatoo.easymill.util.TextInputControlListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class ConfigGriperViewController  extends Controller{
	@FXML
	private GridPane gridPane;
	@FXML
	private Button editBt;
	@FXML
	private Button newBt;

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
		       
	}

	@FXML
	public void editBtAction(ActionEvent event) {
		robotGripperView.clickedEdit();
	}
		
	
	@FXML
	public void newBtAction(ActionEvent event) {
		robotGripperView.clickedNew();
	}

	@Override
	public void setMessege(String mess) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTextFieldListener(TextInputControlListener listener) {
		// TODO Auto-generated method stub
		
	}

}
