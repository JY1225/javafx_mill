package cn.greatoo.easymill.ui.configure.robot;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cn.greatoo.easymill.db.util.Gripperhandler;
import cn.greatoo.easymill.entity.Gripper;
import cn.greatoo.easymill.ui.main.Controller;
import cn.greatoo.easymill.util.IconFlowSelector;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;

public class RobotGriperViewController extends Controller {
	@FXML
	private GridPane gridPane;
	@FXML
	private ComboBox<String> combox;
	@FXML
	private Button editBt;
	@FXML
	private Button newBt;
	List<Button> bts;
    private IconFlowSelector ifsClamping;
	private static final double ICONFLOWSELECTOR_WIDTH = 530;
	RobotGripperView robotGripperView;
	public void init() {
		bts = new ArrayList<Button>();
		bts.add(editBt);
		bts.add(newBt);
		ifsClamping = new IconFlowSelector(false);
        ifsClamping.setPrefWidth(ICONFLOWSELECTOR_WIDTH);
        gridPane.add(ifsClamping, 0, 0, 2, 1);

        //编辑，新增
        robotGripperView = new RobotGripperView();
        robotGripperView.init(gridPane,editBt,newBt,ifsClamping);
		
        combox.getItems().add("Gripper1");
        combox.getItems().add("Gripper2");
        
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
		String gripperName = combox.getValue().toString();
		robotGripperView.clickedEdit(gripperName);

		
	}
	
	@FXML
	public void newBtAction(ActionEvent event) {
		robotGripperView.clickedNew();
	}

	
	
	@FXML
	public void comboBoxAction(ActionEvent event) {		
		if(combox.getValue() != null) {
			editBt.setDisable(false);
		}else {
			editBt.setDisable(true);
		}
		
	}
	@Override
	public void setMessege(String mess) {
		// TODO Auto-generated method stub
		
	}
	
	public static void saveData(Gripper gripper) throws SQLException {
		if (gripper.getId()>0) {
			Gripperhandler.updateGripper(gripper);
		} else {
			Gripperhandler.saveGripper(gripper);
		}
		//getView().refresh();
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
