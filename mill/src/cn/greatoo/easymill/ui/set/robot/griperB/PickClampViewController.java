package cn.greatoo.easymill.ui.set.robot.griperB;

import java.util.ArrayList;
import java.util.List;

import cn.greatoo.easymill.db.util.DBHandler;
import cn.greatoo.easymill.entity.Gripper;
import cn.greatoo.easymill.entity.Program;
import cn.greatoo.easymill.ui.main.Controller;
import cn.greatoo.easymill.util.IconFlowSelector;
import cn.greatoo.easymill.util.TextInputControlListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

public class PickClampViewController extends Controller {
	@FXML
	private GridPane gridPane;
	@FXML
	private ComboBox<String> comboBox;
	@FXML
	private Button outBt;
	@FXML
	private Button inBt;
	List<Button> bts;
	private IconFlowSelector ifsGrippers;
	private static final double ICONFLOWSELECTOR_WIDTH = 530;
	private String programName;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void init() {
		programName = DBHandler.getInstance().getProgramName();
		bts = new ArrayList<Button>();
		bts.add(outBt);
		bts.add(inBt);
		isClicked(bts, null);
		if (comboBox.getItems().size() == 0) {
			comboBox.getItems().add("A");
			comboBox.getItems().add("B");
		}
		ifsGrippers = new IconFlowSelector(false);
		ifsGrippers.setPrefWidth(ICONFLOWSELECTOR_WIDTH);
		gridPane.add(ifsGrippers, 0, 2, 2, 1);

		comboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
			@Override
			public void changed(ObservableValue observable, Object oldValue, Object newValue) {
				DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadCNC().getGripperHead()
						.setName((String) newValue);
			}
		});

		Program program = DBHandler.getInstance().getProgramBuffer().get(programName);
		String name = program.getUnloadCNC().getGripperHead().getName();
		comboBox.getSelectionModel().select(name);
		if (DBHandler.getInstance().getProgramName() != null) {
			if (program.getUnloadCNC().getGripperHead().isGripperInner()) {
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
		DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadCNC().getGripperHead()
				.setGripperInner(false);
	}

	@FXML
	public void inBtAction(ActionEvent event) {
		isClicked(bts, inBt);
		DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadCNC().getGripperHead()
				.setGripperInner(true);
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
		ifsGrippers.setSelected(
				DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadCNC().getGripper().getName());
	}

	public void changedGripper(final Gripper gripper) {
		ifsGrippers.deselectAll();
		ifsGrippers.setSelected(gripper.getName());
		DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadCNC().getGripper()
				.setName(gripper.getName());
		System.out.println(gripper.getName());
	}

	@Override
	public void setTextFieldListener(TextInputControlListener listener) {
		// TODO Auto-generated method stub
		
	}
}
