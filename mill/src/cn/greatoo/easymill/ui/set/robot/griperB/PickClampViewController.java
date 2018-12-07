package cn.greatoo.easymill.ui.set.robot.griperB;

import java.util.ArrayList;
import java.util.List;

import cn.greatoo.easymill.ui.main.Controller;
import cn.greatoo.easymill.util.IconFlowSelector;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;

public class PickClampViewController extends Controller {
	@FXML
	private GridPane gridPane;
	@FXML
	private ComboBox comboBox;
	@FXML
	private Button outBt;
	@FXML
	private Button inBt;
	List<Button> bts;
   // private TransportInformation transportInfo;
    private IconFlowSelector ifsClamping;
	private static final double ICONFLOWSELECTOR_WIDTH = 530;
	@SuppressWarnings("unchecked")
	public void init() {
		bts = new ArrayList<Button>();
		bts.add(outBt);
		bts.add(inBt);
		isClicked(bts, outBt);
		
		comboBox.getItems().add("A");
		comboBox.getItems().add("B");
		
		ifsClamping = new IconFlowSelector(false);
        ifsClamping.setPrefWidth(ICONFLOWSELECTOR_WIDTH);
        gridPane.add(ifsClamping, 0, 2, 2, 1);	
	}
	@FXML
	public void comboBoxAction(ActionEvent event) {

	}
	
	@FXML
	public void outBtAction(ActionEvent event) {
		isClicked(bts, outBt);
	}
	
	@FXML
	public void inBtAction(ActionEvent event) {
		isClicked(bts, inBt);
	}
	
    
    public void refresh() {
//		refreshGripperHeads();
//		refreshGrippers();
	}
	
//	public void refreshGripperHeads() {
//		GripperBody body = transportInfo.getRobotSettings().getGripperBody();
//		cbbGripperHeads.getItems().clear();
//		for (GripperHead head : body.getGripperHeads()) {
//			cbbGripperHeads.getItems().add(head.getName());
//		}
//		if (transportInfo.getPickStep().getRobotSettings() != null) {
//			if (transportInfo.getPickStep().getRobotSettings().getGripperHead() != null) {
//				cbbGripperHeads.setValue(transportInfo.getPickStep().getRobotSettings().getGripperHead().getName());
//			}
//		}
//	}
//	
//	public void refreshGrippers() {
//		ifsGrippers.clearItems();
//		GripperHead gripperHead = transportInfo.getPickStep().getRobotSettings().getGripperHead();
//		if (gripperHead != null) {
//			int itemIndex = 0;
//			for (final Gripper gripper : gripperHead.getPossibleGrippers()) {
//				ifsGrippers.addItem(itemIndex, gripper.getName(), gripper.getImageUrl(), new EventHandler<MouseEvent>() {
//					@Override
//					public void handle(final MouseEvent event) {
//						getPresenter().changedGripper(gripper);
//					}
//				});
//				itemIndex++;
//			}
//			setSelectedGripper();
//		}
//	}
//	public void setSelectedGripper() {
//		ifsGrippers.deselectAll();
//		if (transportInfo.getPickStep().getRobotSettings() != null) {
//			if (transportInfo.getPickStep().getRobotSettings().getGripperHead().getGripper() != null) {
//				Gripper gripper = transportInfo.getPickStep().getRobotSettings().getGripperHead().getGripper();
//				ifsGrippers.setSelected(gripper.getName());
//				btnInner.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
//				btnOuter.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
//				if (gripper.getType() == Type.TWOPOINT) {
//					lblInnerOuter.setDisable(false);
//					hboxButtonsInnerOuter.setDisable(false);
//					if (transportInfo.getPickStep().getRobotSettings().isGripInner()) {
//						btnInner.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
//					} else {
//						btnOuter.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
//					}
//				} else {
//					lblInnerOuter.setDisable(true);
//					hboxButtonsInnerOuter.setDisable(true);
//				}
//			} 
//		} 
//	}
}
