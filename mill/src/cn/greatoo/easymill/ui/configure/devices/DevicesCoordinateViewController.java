package cn.greatoo.easymill.ui.configure.devices;

import javafx.fxml.FXML;

import javafx.scene.control.Button;

import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cn.greatoo.easymill.db.util.UserFrameHander;
import cn.greatoo.easymill.entity.Coordinates;
import cn.greatoo.easymill.entity.UserFrame;
import cn.greatoo.easymill.ui.main.Controller;
import cn.greatoo.easymill.util.TextInputControlListener;
import javafx.event.ActionEvent;

import javafx.scene.control.ComboBox;

import javafx.scene.layout.GridPane;

public class DevicesCoordinateViewController extends Controller {
	@FXML
	private GridPane gridPane;
	@FXML
	private GridPane contentGridPane;
	@FXML
	private Button editBt;
	@FXML
	private Button addBt;
	@FXML
	private ComboBox<String> comboBox;
	@FXML
	private TextField nameText;
	@FXML
	private TextField NrText;
	@FXML
	private TextField ZSafeText;
	@FXML
	private TextField XText;
	@FXML
	private TextField YText;
	@FXML
	private TextField ZText;
	@FXML
	private TextField WText;
	@FXML
	private TextField PText;
	@FXML
	private TextField RText;
	@FXML
	private Button saveBt;

	private boolean editMode;
	List<Button> bts;
	private UserFrame userFrame =new UserFrame("", 0, 0, new Coordinates());
	private UserFrame selectedUserFrame;
	
	public void init() {
		bts = new ArrayList<Button>();
		bts.add(editBt);
		bts.add(addBt);
		
		editMode = false;
		contentGridPane.setVisible(false);
		Set<UserFrame> set = UserFrameHander.getAllUserFrames();
		for(UserFrame uf:set) {
			comboBox.getItems().add(uf.getName());
		}
		
		if(comboBox.getValue() != null) {
			editBt.setDisable(false);
		}else {
			editBt.setDisable(true);
		}	
	}
	@FXML
	public void editBtAction(ActionEvent event) {
		if (editMode) {		
			reset();
			isDisSelect(bts, editBt);			
			contentGridPane.setVisible(false);
			addBt.setDisable(false);
			comboBox.setDisable(false);
			editMode = false;
		} else {				
			String name = comboBox.getValue().toString();
			try {
			//通过名称读取坐标系
				userFrame = UserFrameHander.getUserFrameByName(name);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			userFrameSelected(userFrame);
			nameText.setText(name);
			validate();			
			isClicked(bts, editBt);
			contentGridPane.setVisible(true);
			addBt.setDisable(true);
			comboBox.setDisable(true);
			editMode = true;
		}
	}
	
	@FXML
	public void addBtAction(ActionEvent event) {	
		userFrame =new UserFrame("", 0, 0, new Coordinates());
		if (!editMode) {	
			isClicked(bts, addBt);			
			contentGridPane.setVisible(true);
			editBt.setDisable(true);
			comboBox.setDisable(true);
			editMode = true;
		} else {
			isDisSelect(bts, addBt);
			contentGridPane.setVisible(false);
			editBt.setDisable(false);
			comboBox.setDisable(false);
			editMode = false;
		}
	}
	
	@FXML
	public void comboBoxAction(ActionEvent event) {		
		if(comboBox.getValue() != null) {
			editBt.setDisable(false);
		}else {
			editBt.setDisable(true);
		}
		
	}
	
	@FXML
	public void saveBtAction(ActionEvent event) {
		String name = nameText.getText();
		Coordinates location = new Coordinates();
		float safeDistance = Float.parseFloat(ZSafeText.getText());
		int Nr =Integer.parseInt(NrText.getText());
		location.setX(Float.parseFloat(XText.getText()));
		location.setY(Float.parseFloat(YText.getText()));
		location.setZ(Float.parseFloat(ZText.getText()));
		location.setW(Float.parseFloat(WText.getText()));
		location.setP(Float.parseFloat(PText.getText()));
		location.setR(Float.parseFloat(RText.getText()));
		location.setId(userFrame.getLocation().getId());
		validate();
		selectedUserFrame =new UserFrame(name, Nr, safeDistance, location); 
		selectedUserFrame.setId(userFrame.getId());	
			try {
				if (userFrame!=null && userFrame.getId() > 0) {
					UserFrameHander.updateuserframe(selectedUserFrame);
				}
				else {
					UserFrameHander.saveUserFrame(selectedUserFrame);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}				
	}
	@Override
	public void setMessege(String mess) {
				
	}
	
	public void reset() {
		nameText.setText("");
		NrText.setText("");
		ZSafeText.setText("");

		if (comboBox.valueProperty().get() != null) {
			editBt.setDisable(false);
		} else {
			editBt.setDisable(true);
		}
		comboBox.setDisable(false);
		addBt.setDisable(false);
		XText.setText("");
		YText.setText("");
		ZText.setText("");
		WText.setText("");
		PText.setText("");
		RText.setText("");
		validate();
	}
	public void validate() {
		if (!nameText.getText().equals("") 
				&& !NrText.getText().equals("") && (Integer.parseInt(NrText.getText()) > 0) 
				&& !ZSafeText.getText().equals("")
				&& !XText.getText().equals("")
				&& !YText.getText().equals("")
				&& !ZText.getText().equals("")
				&& !WText.getText().equals("") 
				&& !PText.getText().equals("")
				&& !RText.getText().equals("")) {
			saveBt.setDisable(false);
		} else {
			saveBt.setDisable(true);
		}
	}
	
	public void userFrameSelected(final UserFrame userFrame) {
		if (userFrame !=null) {		
		nameText.setText(userFrame.getName());
		NrText.setText("" + userFrame.getNumber());
		ZSafeText.setText("" + userFrame.getzSafeDistance());
		XText.setText("" + userFrame.getLocation().getX());
		YText.setText("" + userFrame.getLocation().getY());
		ZText.setText("" + userFrame.getLocation().getZ());
		WText.setText("" + userFrame.getLocation().getW());
		PText.setText("" + userFrame.getLocation().getP());
		RText.setText("" + userFrame.getLocation().getR());
		}
		else {
			reset();
		}
	}
	
	
	@FXML
	public void nameChanged(MouseEvent event) {
		validate();
	}
	
	@FXML
	public void NrTextChanged(MouseEvent event) {
		validate();
	}
	
	@FXML
	public void ZSafeTextChanged(MouseEvent event) {
		validate();
	}
	@FXML
	public void XTextChanged(MouseEvent event) {
		validate();
	}
	@FXML
	public void YTextChanged(MouseEvent event) {
		validate();
	}
	@FXML
	public void ZTextChanged(MouseEvent event) {
		validate();
	}
	@FXML
	public void WTextChanged(MouseEvent event) {
		validate();
	}
	@FXML
	public void PTextChanged(MouseEvent event) {
		validate();
	}
	@FXML
	public void RTextChanged(MouseEvent event) {
		validate();
	}
	@Override
	public void setTextFieldListener(TextInputControlListener listener) {
		// TODO Auto-generated method stub
		
	}
	
}
