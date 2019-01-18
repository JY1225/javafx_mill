package cn.greatoo.easymill.ui.set.robot.griperB;

import java.util.ArrayList;
import java.util.List;

import cn.greatoo.easymill.db.util.DBHandler;
import cn.greatoo.easymill.db.util.Gripperhandler;
import cn.greatoo.easymill.entity.Gripper;
import cn.greatoo.easymill.entity.GripperHead;
import cn.greatoo.easymill.entity.Program;
import cn.greatoo.easymill.ui.main.Controller;
import cn.greatoo.easymill.util.IconFlowSelector;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
	private ComboBox nameCombox;
	@FXML
	private Button outBt;
	@FXML
	private Button inBt;
	List<Button> bts;
   // private TransportInformation transportInfo;
    private IconFlowSelector ifsClamping;
	private static final double ICONFLOWSELECTOR_WIDTH = 530;
	
	public static Gripper gripper = new Gripper();
	public static GripperHead gripperhead = new GripperHead();
	@SuppressWarnings("unchecked")
	public void init() {
		bts = new ArrayList<Button>();
		bts.add(outBt);
		bts.add(inBt);
		isClicked(bts, outBt);
		
		comboBox.getItems().add("A");
		comboBox.getItems().add("B");
		List<Gripper> list = Gripperhandler.getAllGripper();
		for(Gripper g:list) {
			nameCombox.getItems().add(g.getName());
		}
		
		ifsClamping = new IconFlowSelector(false);
        ifsClamping.setPrefWidth(ICONFLOWSELECTOR_WIDTH);
        gridPane.add(ifsClamping, 0, 2, 2, 1);	
        
        gripperhead.setGripperInner(false);
        comboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
            	gripperhead.setName((String) newValue);
            }
        });
        nameCombox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
            	gripper.setName((String) newValue);
            }
        });  
        String programName = DBHandler.getInstance().getProgramName();
		if(programName != null) {
			Program program = DBHandler.getInstance().getProgramBuffer().get(programName);
			gripper = program.getUnloadCNC().getGripper();
			gripperhead = program.getUnloadCNC().getGripperHead();
			comboBox.getSelectionModel().select(gripperhead.getName());
			nameCombox.getSelectionModel().select((String)gripper.getName());
			if(gripperhead.isGripperInner()) {
				isClicked(bts, inBt);
			}else {
				isClicked(bts, outBt);
			}
		}
	}
	@FXML
	public void comboBoxAction(ActionEvent event) {

	}
	
	@FXML
	public void outBtAction(ActionEvent event) {
		isClicked(bts, outBt);
		gripperhead.setGripperInner(false);
	}
	
	@FXML
	public void inBtAction(ActionEvent event) {
		isClicked(bts, inBt);
		gripperhead.setGripperInner(true);
	}
	
    
    public void refresh() {
//		refreshGripperHeads();
//		refreshGrippers();
	}
	@Override
	public void setMessege(String mess) {
		// TODO Auto-generated method stub
		
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
