package cn.greatoo.easymill.ui.set.robot.griperA;

import java.util.ArrayList;
import java.util.List;

import cn.greatoo.easymill.db.util.DBHandler;
import cn.greatoo.easymill.entity.Gripper;
import cn.greatoo.easymill.entity.GripperHead;
import cn.greatoo.easymill.entity.Program;
import cn.greatoo.easymill.ui.main.Controller;
import cn.greatoo.easymill.util.IconFlowSelector;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

public class ClampViewController extends Controller {
	@FXML
	private GridPane gridPane;
	@FXML
	private ComboBox comboBox;
//	@FXML
//	private ComboBox nameCombox;
	@FXML
	private Button outBt;
	@FXML
	private Button inBt;

	List<Button> bts;
	// private TransportInformation transportInfo;
	private IconFlowSelector ifsGrippers;
	private static final double ICONFLOWSELECTOR_WIDTH = 530;

	public static Gripper gripper = new Gripper();
	public static GripperHead gripperhead = new GripperHead();

	@SuppressWarnings({ "unchecked", "static-access", "rawtypes" })
	public void init() {
		bts = new ArrayList<Button>();
		bts.add(outBt);
		bts.add(inBt);
		isClicked(bts, outBt);

		comboBox.getItems().add("A");
		comboBox.getItems().add("B");

		ifsGrippers = new IconFlowSelector(false);
		ifsGrippers.setPrefWidth(ICONFLOWSELECTOR_WIDTH);
		gridPane.add(ifsGrippers, 0, 2, 2, 1);

		gripperhead.setGripperInner(false);
		comboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
			@Override
			public void changed(ObservableValue observable, Object oldValue, Object newValue) {
				gripperhead.setName((String) newValue);
			}
		});


		String programName = DBHandler.getInstance().getProgramName();
		if (programName != null) {
			Program program = DBHandler.getInstance().getProgramBuffer().get(programName);
			gripper = program.getUnloadstacker().getGripper();
			gripperhead = program.getUnloadstacker().getGripperHead();
			comboBox.getSelectionModel().select(gripperhead.getName());
			if (gripperhead.isGripperInner()) {
				isClicked(bts, inBt);
			} else {
				isClicked(bts, outBt);
			}
		}
		refresh();
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
		refreshGrippers();
	}

	@Override
	public void setMessege(String mess) {
		// TODO Auto-generated method stub

	}

	public void refreshGrippers() {
		ifsGrippers.clearItems();
		ifsGrippers.clearItems();
		List<Gripper> grippers = DBHandler.getInstance().getGriperBuffer();
		int itemIndex = 0;
		for (final Gripper gripper : grippers) {
			ifsGrippers.addItem(itemIndex, gripper.getName(), gripper.getImageUrl(), new EventHandler<MouseEvent>() {
				@Override
				public void handle(final MouseEvent event) {
					changedGripper(gripper);
				}
			});
			itemIndex++;
		}
		setSelectedGripper();

	}

	public void setSelectedGripper() {
		ifsGrippers.deselectAll();
		ifsGrippers.setSelected(gripper.getName());		
	}
	
	public void changedGripper(final Gripper gripper) {
		ifsGrippers.deselectAll();
		ifsGrippers.setSelected(gripper.getName());		
		this.gripper.setName(gripper.getName());
		System.out.println(gripper.getName());
	}
}
