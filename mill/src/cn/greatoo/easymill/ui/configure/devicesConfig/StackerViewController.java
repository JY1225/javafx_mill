package cn.greatoo.easymill.ui.configure.devicesConfig;

import java.sql.SQLException;

import javax.swing.JOptionPane;

import cn.greatoo.easymill.db.util.Stackerhandler;
import cn.greatoo.easymill.entity.Coordinates;
import cn.greatoo.easymill.entity.Stacker;
import cn.greatoo.easymill.entity.WorkPiece;
import cn.greatoo.easymill.ui.main.Controller;
import cn.greatoo.easymill.ui.main.MainViewController;
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
	private TextField studHight_StakckerField;
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
		
	}
	
	@FXML
	public void saveBtAction(ActionEvent event) {
		String name = nameField.getText();
		int h = Integer.parseInt(hField.getText());
		int v = Integer.parseInt(vField.getText());
		float tX = Float.parseFloat(tXField.getText());
		float tY = Float.parseFloat(tYField.getText());
		float tZ = Float.parseFloat(tZField.getText());
		float fX = Float.parseFloat(fXField.getText());
		float fY = Float.parseFloat(fYField.getText());
		float fZ = Float.parseFloat(fZField.getText());
		float hold = Float.parseFloat(holdField.getText());
		float stud = Float.parseFloat(studField.getText());		
		float hDistance =Float.parseFloat(hDistanceField.getText());
		float vDistance =Float.parseFloat(vDistanceField.getText());
		float top = Float.parseFloat(topField.getText());
		float safe = Float.parseFloat(safeField.getText());		
		float r0 = Float.parseFloat(r0Field.getText());
		float overMax = Float.parseFloat(overMaxField.getText());
		float studHight_Stakcker =Float.parseFloat(studHight_StakckerField.getText());
		float padding = Float.parseFloat(paddingField.getText());
		float buttom = Float.parseFloat(buttomField.getText());		
		float overPersent = Float.parseFloat(overPersentField.getText());
		float r45 = Float.parseFloat(r45Field.getText());
		float maxUnder = Float.parseFloat(maxUnderField.getText());
		float overMin = Float.parseFloat(overMinField.getText());
		float orientation = stacker.getOrientation();		
		int layers = stacker.getLayers();
		int amount = stacker.getAmount();
		float studHeight_Workpiece = stacker.getStudHeight_Workpiece();		
		stacker =new Stacker(h, v, hold, stud, hDistance, vDistance, padding, top, buttom, safe, 
				overPersent, r0, r45, overMax, maxUnder, overMin, studHight_Stakcker, tX, tY, tZ,
				fX, fY, fZ, orientation, layers, amount, studHeight_Workpiece);
		validate();
		
		try {
			Stackerhandler.SaveStacker(stacker);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static Stacker getStacker() {
		return stacker;
	}

	public void validate() {
		if (!nameField.getText().equals("") 
				&& !hField.getText().equals("") && (Integer.parseInt(hField.getText()) > 0) 
				&& !tXField.getText().equals("") && (Integer.parseInt(tXField.getText()) > 0)
				&& !tYField.getText().equals("") && (Integer.parseInt(tYField.getText()) > 0)
				&& !tZField.getText().equals("") && (Integer.parseInt(tZField.getText()) > 0)
				&& !fXField.getText().equals("") && (Integer.parseInt(fXField.getText()) > 0)
				&& !fYField.getText().equals("") && (Integer.parseInt(fYField.getText()) > 0)
				&& !fZField.getText().equals("") && (Integer.parseInt(fZField.getText()) > 0)
				&& !vField.getText().equals("") && (Integer.parseInt(vField.getText()) > 0)
				&& !holdField.getText().equals("") && (Integer.parseInt(holdField.getText()) > 0)
				&& !studField.getText().equals("") && (Integer.parseInt(studField.getText()) > 0)
				&& !hDistanceField.getText().equals("") && (Integer.parseInt(hDistanceField.getText()) > 0)
				&& !vDistanceField.getText().equals("") && (Integer.parseInt(vDistanceField.getText()) > 0)
				&& !topField.getText().equals("") && (Integer.parseInt(topField.getText()) > 0)
				&& !safeField.getText().equals("") && (Integer.parseInt(safeField.getText()) > 0)
				&& !r0Field.getText().equals("") && (Integer.parseInt(r0Field.getText()) > 0)
				&& !overMaxField.getText().equals("") && (Integer.parseInt(overMaxField.getText()) > 0)
				&& !studHight_StakckerField.getText().equals("") && (Integer.parseInt(studHight_StakckerField.getText()) > 0)
				&& !paddingField.getText().equals("") && (Integer.parseInt(paddingField.getText()) > 0)
				&& !buttomField.getText().equals("") && (Integer.parseInt(buttomField.getText()) > 0)
				&& !overPersentField.getText().equals("") && (Integer.parseInt(overPersentField.getText()) > 0)
				&& !r45Field.getText().equals("") && (Integer.parseInt(r45Field.getText()) > 0)
				&& !maxUnderField.getText().equals("") && (Integer.parseInt(maxUnderField.getText()) > 0)
				&& !overMinField.getText().equals("") && (Integer.parseInt(overMinField.getText()) > 0)
				&& (stacker.getOrientation() > 0)&& (stacker.getLayers() > 0)&& (stacker.getAmount() > 0)
				&& (stacker.getStudHeight_Workpiece()> 0)) {
			saveBt.setDisable(false);
		} else {
			//saveBt.setDisable(true);
			JOptionPane.showMessageDialog(null, "数据不全，请填写完整数据后保存", "Database Error", JOptionPane.WARNING_MESSAGE);
		}
	}

	
	
}
