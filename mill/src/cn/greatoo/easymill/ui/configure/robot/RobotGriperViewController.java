package cn.greatoo.easymill.ui.configure.robot;


import java.util.ArrayList;
import java.util.List;

import cn.greatoo.easymill.ui.main.Controller;
import cn.greatoo.easymill.util.IconFlowSelector;
import cn.greatoo.easymill.util.TextInputControlListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class RobotGriperViewController extends Controller {
	@FXML
	private GridPane gridPane;
	@FXML
	private Button editBt;
	@FXML
	private Button newBt;
	List<Button> bts;
    private IconFlowSelector ifsGrippers;
	private static final double ICONFLOWSELECTOR_WIDTH = 530;
	RobotGripperView robotGripperView;
	public void init() {
		bts = new ArrayList<Button>();
		bts.add(editBt);
		bts.add(newBt);
		ifsGrippers = new IconFlowSelector(true);
        ifsGrippers.setPrefWidth(ICONFLOWSELECTOR_WIDTH);
        gridPane.add(ifsGrippers, 0, 0, 2, 1);

        //编辑，新增
        robotGripperView = new RobotGripperView();
        robotGripperView.init(gridPane,editBt,newBt,ifsGrippers);
		
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
