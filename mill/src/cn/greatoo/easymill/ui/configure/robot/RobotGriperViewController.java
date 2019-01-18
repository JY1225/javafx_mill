package cn.greatoo.easymill.ui.configure.robot;

import java.util.List;

import cn.greatoo.easymill.db.util.Gripperhandler;
import cn.greatoo.easymill.entity.Gripper;
import cn.greatoo.easymill.util.IconFlowSelector;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;

public class RobotGriperViewController {
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
	RobotGripperView robotGripperView;
	public void init() {

		ifsClamping = new IconFlowSelector(false);
        ifsClamping.setPrefWidth(ICONFLOWSELECTOR_WIDTH);
        gridPane.add(ifsClamping, 0, 0, 2, 1);

        //编辑，新增
        robotGripperView = new RobotGripperView();
        robotGripperView.init(gridPane,editBt,newBt,ifsClamping);
        
        List<Gripper> list = Gripperhandler.getAllGripper();
		for(Gripper g:list) {
			combox.getItems().add(g.getName());
		}
	}
	@FXML
	public void editBtAction(ActionEvent event) {
		robotGripperView.clickedEdit();
		
	}
	
	@FXML
	public void newBtAction(ActionEvent event) {
		robotGripperView.clickedNew();
	}


//	public void refresh() {
//		ifsGrippers.clearItems();
//		Set<Gripper> grippers = new HashSet<Gripper>();
//		for (GripperHead head : robot.getGripperBody().getGripperHeads()) {
//			grippers.addAll(head.getPossibleGrippers());
//		}
//		int itemIndex = 0;
//		for (final Gripper gripper : grippers) {
//			ifsGrippers.addItem(itemIndex, gripper.getName(), gripper.getImageUrl(), new EventHandler<MouseEvent>() {
//				@Override
//				public void handle(final MouseEvent event) {
//					robotGripperView.gripperSelected(gripper);
//				}
//			});
//			itemIndex++;
//		}
//	}

}
