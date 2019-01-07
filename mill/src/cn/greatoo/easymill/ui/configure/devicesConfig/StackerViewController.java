package cn.greatoo.easymill.ui.configure.devicesConfig;

import cn.greatoo.easymill.entity.Stacker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class StackerViewController {
	@FXML
	private GridPane contentGridPane;
	@FXML
	private TextField nameField;
	@FXML
	private TextField hField;
	@FXML
	private TextField tXField;
	@FXML
	private TextField tYField;
	@FXML
	private TextField tZField;
	@FXML
	private TextField fXField;
	@FXML
	private TextField fYField;
	@FXML
	private TextField fZField;
	@FXML
	private Button saveBt;
	@FXML
	private ComboBox coordinateCombox;
	@FXML
	private TextField vField;
	@FXML
	private TextField holdField;
	@FXML
	private TextField studField;
	@FXML
	private TextField hDistanceField;
	@FXML
	private TextField vDistanceField;
	@FXML
	private TextField topField;
	@FXML
	private TextField safeField;
	@FXML
	private TextField r0Field;
	@FXML
	private TextField overMaxField;
	@FXML
	private TextField studHightField;
	@FXML
	private TextField paddingField;
	@FXML
	private TextField buttomField;
	@FXML
	private TextField overPersentField;
	@FXML
	private TextField r45Field;
	@FXML
	private TextField maxUnderField;
	@FXML
	private TextField overMinField;

	private static Stacker stacker;
	@SuppressWarnings("unchecked")
	public void init() {
		coordinateCombox.getItems().add("STACKER");
		stacker = new Stacker(Integer.parseInt(hField.getText()),
				Integer.parseInt(vField.getText()),
				Float.parseFloat(hDistanceField.getText()),
				Float.parseFloat(vDistanceField.getText()),
				Float.parseFloat(studField.getText()),
				Float.parseFloat(paddingField.getText()),
				Float.parseFloat(buttomField.getText()),
				Float.parseFloat(r0Field.getText()));
	}
	
	@FXML
	public void saveBtAction(ActionEvent event) {
		
	}

	public static Stacker getStacker() {
		return stacker;
	}
	
}
