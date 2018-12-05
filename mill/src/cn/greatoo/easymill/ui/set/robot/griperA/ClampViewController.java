package cn.greatoo.easymill.ui.set.robot.griperA;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.greatoo.easymill.ui.main.Controller;
import cn.greatoo.easymill.util.IconFlowSelectorItem;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class ClampViewController  extends Controller {
	@FXML
	private GridPane gridPane;
	@FXML
	private ComboBox comboBox;
	@FXML
	private Button outBt;
	@FXML
	private Button inBt;
	@FXML
	private ScrollPane scrollPane;
	@FXML
	private HBox hbox;
	List<Button> bts;
	private Map<Integer, IconFlowSelectorItem> items;
	private static final int PREF_HEIGHT = 145;
    private static final int PREF_HEIGHT_SCROLL = 175;
   // private TransportInformation transportInfo;
	@SuppressWarnings("unchecked")
	public void init() {
		bts = new ArrayList<Button>();
		bts.add(outBt);
		bts.add(inBt);
		isClicked(bts, outBt);
		
		comboBox.getItems().add("A");
		comboBox.getItems().add("B");
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
	
	public void addItem(final int index, final String name, final String iconUrl, final String extraInfo, final EventHandler<MouseEvent> handler) {
        IconFlowSelectorItem item = new IconFlowSelectorItem(index, name, iconUrl, extraInfo);
        item.setDefault(false);
        item.addEventHandler(MouseEvent.MOUSE_CLICKED, handler);
        items.put(index, item);
        if (items.size() > 4) {
        	scrollPane.setPrefHeight(PREF_HEIGHT_SCROLL);
        } else {
        	scrollPane.setPrefHeight(PREF_HEIGHT);
        }
        hbox.getChildren().add(item);
    }

    public void addItem(final int index, final String name, final String iconUrl, final EventHandler<MouseEvent> handler) {
        addItem(index, name, iconUrl, null, handler);
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