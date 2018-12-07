package cn.greatoo.easymill.ui.configure.robot;

import cn.greatoo.easymill.util.IconFlowSelector;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class RobotGriperViewController {
	@FXML
	private GridPane gridPane;
	@FXML
	private Button editBt;
	@FXML
	private Button newBt;

    private IconFlowSelector ifsClamping;
	private static final double ICONFLOWSELECTOR_WIDTH = 530;
	public void init() {
		
		ifsClamping = new IconFlowSelector(false);
        ifsClamping.setPrefWidth(ICONFLOWSELECTOR_WIDTH);
        gridPane.add(ifsClamping, 0, 0, 2, 1);	
	}
	@FXML
	public void editBtAction(ActionEvent event) {
		
	}
	
	@FXML
	public void newBtAction(ActionEvent event) {
		
	}
	
}
