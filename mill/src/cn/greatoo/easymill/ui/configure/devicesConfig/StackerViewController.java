package cn.greatoo.easymill.ui.configure.devicesConfig;

import java.sql.SQLException;

import javax.swing.JOptionPane;

import cn.greatoo.easymill.db.util.DBHandler;
import cn.greatoo.easymill.db.util.Stackerhandler;
import cn.greatoo.easymill.entity.Coordinates;
import cn.greatoo.easymill.entity.Smooth;
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

	private static Stacker stacker = new Stacker();

	@SuppressWarnings("unchecked")
	public void init() {
		coordinateCombox.getItems().add("STACKER");
		if (DBHandler.getInstance().getStatckerBuffer().get(0) != null) {
			stacker = DBHandler.getInstance().getStatckerBuffer().get(0);
			hField.setText(String.valueOf(stacker.getHorizontalHoleAmount()));
			vField.setText(String.valueOf(stacker.getVerticalHoleAmount()));
			holdField.setText(String.valueOf(stacker.getHoleDiameter()));
			studField.setText(String.valueOf(stacker.getStudDiameter()));
			hDistanceField.setText(String.valueOf(stacker.getHorizontalHoleDistance()));
			vDistanceField.setText(String.valueOf(stacker.getVerticalHoleDistance()));
			paddingField.setText(String.valueOf(stacker.getHorizontalPadding()));
			topField.setText(String.valueOf(stacker.getVerticalPaddingTop()));
			buttomField.setText(String.valueOf(stacker.getVerticalPaddingBottom()));
			safeField.setText(String.valueOf(stacker.getInterferenceDistance()));
			overPersentField.setText(String.valueOf(stacker.getOverflowPercentage()));
			r0Field.setText(String.valueOf(stacker.getHorizontalR()));
			r45Field.setText(String.valueOf(stacker.getTiltedR()));
			overMaxField.setText(String.valueOf(stacker.getMaxOverflow()));
			maxUnderField.setText(String.valueOf(stacker.getMaxUnderflow()));
			studHight_StakckerField.setText(String.valueOf(stacker.getStudHeight_Stacker()));
			overMinField.setText(String.valueOf(stacker.getMinOverlap()));
			if (stacker.getSmoothto() != null) {
				tXField.setText(String.valueOf(stacker.getSmoothto().getX()));
				tYField.setText(String.valueOf(stacker.getSmoothto().getY()));
				tZField.setText(String.valueOf(stacker.getSmoothto().getZ()));
			}
			if (stacker.getSmoothfrom() != null) {
				fXField.setText(String.valueOf(stacker.getSmoothfrom().getX()));
				fYField.setText(String.valueOf(stacker.getSmoothfrom().getY()));
				fZField.setText(String.valueOf(stacker.getSmoothfrom().getZ()));
			}
		}
	}

	@FXML
	public void saveBtAction(ActionEvent event) {
		stacker.setHorizontalHoleAmount(Integer.valueOf(hField.getText()));
		stacker.setVerticalHoleAmount(Integer.valueOf(vField.getText()));
		stacker.setHoleDiameter(Float.parseFloat(holdField.getText()));
		stacker.setStudDiameter(Float.parseFloat(studField.getText()));
		stacker.setHorizontalHoleDistance(Float.parseFloat(hDistanceField.getText()));
		stacker.setVerticalHoleDistance(Float.parseFloat(vDistanceField.getText()));
		stacker.setHorizontalPadding(Float.parseFloat(paddingField.getText()));
		stacker.setVerticalPaddingTop(Float.parseFloat(topField.getText()));
		stacker.setVerticalPaddingBottom(Float.parseFloat(buttomField.getText()));
		stacker.setInterferenceDistance(Float.parseFloat(safeField.getText()));
		stacker.setOverflowPercentage(Float.parseFloat(overPersentField.getText()));
		stacker.setHorizontalR(Float.parseFloat(r0Field.getText()));
		stacker.setTiltedR(Float.parseFloat(r45Field.getText()));
		stacker.setMaxOverflow(Float.parseFloat(overMaxField.getText()));
		stacker.setMaxUnderflow(Float.parseFloat(maxUnderField.getText()));
		stacker.setStudHeight_Stacker(Float.parseFloat(studHight_StakckerField.getText()));
		stacker.setMinOverlap(Float.parseFloat(overMinField.getText()));
		
		
		
		if (stacker.getSmoothto() != null) {
			Smooth smoothto = new Smooth(Float.parseFloat(tXField.getText()), Float.parseFloat(tYField.getText()), Float.parseFloat(tZField.getText()));
			smoothto.setId(stacker.getSmoothto().getId());
			stacker.setSmoothto(smoothto);
		}
		if (stacker.getSmoothfrom() != null) {
			Smooth smoothfrom = new Smooth(Float.parseFloat(fXField.getText()), Float.parseFloat(fYField.getText()), Float.parseFloat(fZField.getText()));
			smoothfrom.setId(stacker.getSmoothfrom().getId());
			stacker.setSmoothfrom(smoothfrom);
		}
		//validate();
		DBHandler.getInstance().getStatckerBuffer().set(0, stacker);
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
		if (!nameField.getText().equals("") && !hField.getText().equals("") && (Integer.valueOf(hField.getText()) > 0)
				&& !tXField.getText().equals("") && (Float.parseFloat(tXField.getText()) > 0)
				&& !tYField.getText().equals("") && (Float.parseFloat(tYField.getText()) > 0)
				&& !tZField.getText().equals("") && (Float.parseFloat(tZField.getText()) > 0)
				&& !fXField.getText().equals("") && (Float.parseFloat(fXField.getText()) > 0)
				&& !fYField.getText().equals("") && (Float.parseFloat(fYField.getText()) > 0)
				&& !fZField.getText().equals("") && (Float.parseFloat(fZField.getText()) > 0)
				&& !vField.getText().equals("") && (Integer.parseInt(vField.getText()) > 0)
				&& !holdField.getText().equals("") && (Float.parseFloat(holdField.getText()) > 0)
				&& !studField.getText().equals("") && (Float.parseFloat(studField.getText()) > 0)
				&& !"".equals(hDistanceField.getText()) && (Float.parseFloat(hDistanceField.getText()) > 0)
				&& !vDistanceField.getText().equals("") && (Float.parseFloat(vDistanceField.getText()) > 0)
				&& !topField.getText().equals("") && (Float.parseFloat(topField.getText()) > 0)
				&& !safeField.getText().equals("") && (Float.parseFloat(safeField.getText()) > 0)
				&& !r0Field.getText().equals("") && (Float.parseFloat(r0Field.getText()) > 0)
				&& !overMaxField.getText().equals("") && (Float.parseFloat(overMaxField.getText()) > 0)
				&& !studHight_StakckerField.getText().equals("")
				&& (Float.parseFloat(studHight_StakckerField.getText()) > 0) && !paddingField.getText().equals("")
				&& (Float.parseFloat(paddingField.getText()) > 0) && !buttomField.getText().equals("")
				&& (Float.parseFloat(buttomField.getText()) > 0) && !overPersentField.getText().equals("")
				&& (Float.parseFloat(overPersentField.getText()) > 0) && !r45Field.getText().equals("")
				&& (Float.parseFloat(r45Field.getText()) > 0) && !maxUnderField.getText().equals("")
				&& (Float.parseFloat(maxUnderField.getText()) > 0) && !overMinField.getText().equals("")
				&& (Float.parseFloat(overMinField.getText()) > 0)
		// && (stacker.getOrientation() > 0)&& (stacker.getLayers() > 0)&&
		// (stacker.getAmount() > 0) && (stacker.getStudHeight_Workpiece()> 0)
		) {
			saveBt.setDisable(false);
		} else {
			// saveBt.setDisable(true);
			JOptionPane.showMessageDialog(null, "数据不全，请填写完整数据后保存", "Database Error", JOptionPane.WARNING_MESSAGE);
		}
	}

}
