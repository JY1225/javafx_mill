package cn.greatoo.easymill.ui.configure.robot;

import java.io.File;
import java.sql.SQLException;

import cn.greatoo.easymill.db.util.Gripperhandler;
import cn.greatoo.easymill.entity.Gripper;
import cn.greatoo.easymill.entity.Gripper.Type;
import cn.greatoo.easymill.robot.AbstractRobot;
import cn.greatoo.easymill.ui.main.Controller;
import cn.greatoo.easymill.util.FullTextField;
import cn.greatoo.easymill.util.IconFlowSelector;
import cn.greatoo.easymill.util.NumericTextField;
import cn.greatoo.easymill.util.TextInputControlListener;
import cn.greatoo.easymill.util.UIConstants;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

public class RobotGripperView extends Controller implements TextInputControlListener{
	private IconFlowSelector ifsGrippers;
	private AbstractRobot robot;
	private Gripper selectedGripper;
	private boolean editMode;
	private static final int HGAP = 15;
	private static final int VGAP = 15;

	protected static final String CSS_CLASS_FORM_BUTTON_ACTIVE = "form-button-active";
	protected static final String CSS_CLASS_FORM_LABEL = "form-label";
	protected static final String CSS_CLASS_FORM_FULLTEXTFIELD = "form-full-textfield";
	protected static final String CSS_CLASS_FORM_ICON = "form-icon";
	protected static final String CSS_CLASS_FORM_BUTTON_BAR_RIGHT = "form-button-bar-right";
	protected static final String CSS_CLASS_FORM_BUTTON_BAR_CENTER = "form-button-bar-center";
	protected static final String CSS_CLASS_CENTER_TEXT = "center-text";
	
	private static final String SAVE_PATH = "M 5.40625 0 L 5.40625 7.25 L 0 7.25 L 7.1875 14.40625 L 14.3125 7.25 L 9 7.25 L 9 0 L 5.40625 0 z M 7.1875 14.40625 L 0 14.40625 L 0 18 L 14.3125 18 L 14.3125 14.40625 L 7.1875 14.40625 z";
	private static final String DELETE_ICON_PATH = "M 10 0 C 4.4775 0 0 4.4775 0 10 C 0 15.5225 4.4775 20 10 20 C 15.5225 20 20 15.5225 20 10 C 20 4.4775 15.5225 0 10 0 z M 5 8.75 L 15 8.75 L 15 11.25 L 5 11.25 L 5 8.75 z";

	private static final double BTN_HEIGHT = UIConstants.BUTTON_HEIGHT;
	private static final double BTN_WIDTH = BTN_HEIGHT * 3;
	private static final double IMG_WIDTH = 90;
	private static final double IMG_HEIGHT = 90;
	
	private static final String CSS_CLASS_GRIPPER_IMAGE_EDIT = "gripper-image-edit";
	protected static final String CSS_CLASS_FORM_BUTTON_BAR_LEFT = "form-button-bar-left";
	protected static final String CSS_CLASS_FORM_BUTTON = "form-button";
	protected static final String CSS_CLASS_SAVE_BUTTON = "save-btn";
	protected static final String CSS_CLASS_DELETE_BUTTON = "delete-btn";
	
	private Button btnCreateNew;
	private Button btnEdit;
	
	private VBox vboxForm;
	private GridPane gpEditor;
	private StackPane spImage;
	private ImageView imageVw;
	private Label lblName;
	private FullTextField fulltxtName;
	private Label lblGripperType;
	private RadioButton rbGripperTypeTwoPoint;
	private RadioButton rbGripperTypeVacuum;
	private Label lblHeight;
	private NumericTextField numtxtHeight;
	private Region spacer;
	private Label lblFixedHeight;
	private CheckBox cbFixedHeight;
	private CheckBox cbA;
	private CheckBox cbB;
	private CheckBox cbC;
	private CheckBox cbD;
	
	private FileChooser fileChooser;
	private Button btnSave;
	private Button btnDelete;
	
	private String imagePath;
	private GridPane gridPane;
	public void init(GridPane gridPane, Button editBt, Button newBt,IconFlowSelector ifsClamping) {
		this.gridPane = gridPane;
		btnEdit = editBt;
		btnCreateNew = newBt;
		this.ifsGrippers = ifsClamping;
		build();
		setTextFieldListener(this);
	}
	private void setTextFieldListener(final TextInputControlListener listener) {
		fulltxtName.setFocusListener(listener);
		numtxtHeight.setFocusListener(listener);
	}
	protected void build() {
		gridPane.setHgap(HGAP);
		gridPane.setVgap(VGAP);
		gridPane.setAlignment(Pos.TOP_CENTER);
		gridPane.setPadding(new Insets(5, 0, 0, 0));
				
		int column = 0;
		int row = 0;
		HBox hbox = new HBox();
		
		gpEditor = new GridPane();
		gpEditor.setVgap(VGAP);
		gpEditor.setHgap(10);
		
		spImage = new StackPane();
		spImage.setStyle("-fx-background-color: #4e5055");
		spImage.setPrefSize(IMG_WIDTH, IMG_HEIGHT);
		spImage.setMaxSize(IMG_WIDTH, IMG_HEIGHT);
		spImage.getStyleClass().add(CSS_CLASS_GRIPPER_IMAGE_EDIT);
		spImage.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(final MouseEvent event) {
				fileChooser = new FileChooser();
				FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PNG (*.png)", "*.png");
				fileChooser.getExtensionFilters().add(extFilter);
				File file = fileChooser.showOpenDialog(null);
				if (file != null) {
					Image image = new Image("file:///" + file.getAbsolutePath(), IMG_WIDTH, IMG_HEIGHT, true, true);
					imageVw.setImage(image);
					imagePath = "file:///" + file.getAbsolutePath();
					validate();
				}
			}
		});
		imageVw = new ImageView();
		imageVw.setFitWidth(IMG_WIDTH);
		imageVw.setFitHeight(IMG_HEIGHT);
		spImage.getChildren().add(imageVw);
		spImage.setPadding(new Insets(10, 10, 10, 10));
		spImage.setPrefSize(IMG_HEIGHT + 20, IMG_HEIGHT + 20);
		spImage.setMinSize(IMG_HEIGHT + 20, IMG_HEIGHT + 20);
		spImage.setMaxSize(IMG_HEIGHT + 20, IMG_HEIGHT + 20);
		lblName = new Label("名称");
		fulltxtName = new FullTextField();
		fulltxtName.setPrefHeight(UIConstants.TEXT_FIELD_HEIGHT);
		fulltxtName.setOnChange(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> arg0, final String arg1, final String arg2) {
				validate();
			}
		});		
		lblGripperType = new Label("类型");
		rbGripperTypeTwoPoint = new RadioButton("夹紧");
		rbGripperTypeTwoPoint.setTextFill(Color.WHITE);
		rbGripperTypeVacuum = new RadioButton("真空");
		rbGripperTypeVacuum.setTextFill(Color.WHITE);
		ToggleGroup tgGripperType = new ToggleGroup();
		rbGripperTypeTwoPoint.setToggleGroup(tgGripperType);
		rbGripperTypeVacuum.setToggleGroup(tgGripperType);
		rbGripperTypeTwoPoint.setSelected(true);
		lblHeight = new Label("高度");
		numtxtHeight = new NumericTextField();
		numtxtHeight.setPrefHeight(UIConstants.TEXT_FIELD_HEIGHT);
		numtxtHeight.setMaxWidth(UIConstants.TEXT_FIELD_HEIGHT * 2);
		numtxtHeight.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> arg0, final Float arg1, final Float arg2) {
				validate();
			}
		});	
		spacer = new Region();
		spacer.setPrefWidth(15);
		lblFixedHeight = new Label("固定");
		cbFixedHeight = new CheckBox();
		cbA = new CheckBox("A");
		cbB = new CheckBox("B");
		cbC = new CheckBox("C");
		cbD = new CheckBox("D");
		gpEditor.setAlignment(Pos.CENTER);
		int column2 = 0;
		int row2 = 0;
		gpEditor.add(lblName, column2++, row2);
		gpEditor.add(fulltxtName, column2++, row2, 4, 1);
		column2 = 0;
		row2++;
		gpEditor.add(lblGripperType, column2++, row2);
		gpEditor.add(rbGripperTypeTwoPoint, column2++, row2);
		gpEditor.add(rbGripperTypeVacuum, column2++, row2);		
		column2 = 0;
		row2++;
		gpEditor.add(lblHeight, column2++, row2);
		gpEditor.add(numtxtHeight, column2++, row2);
		gpEditor.add(spacer, column2++, row2);
		gpEditor.add(lblFixedHeight, column2++, row2);
		gpEditor.add(cbFixedHeight, column2++, row2);
		HBox hboxHeads = new HBox();
		hboxHeads.getChildren().addAll(cbA, cbB, cbC, cbD);
		hboxHeads.setSpacing(20);
		column2 = 0;
		row2++;
		gpEditor.add(hboxHeads, column2++, row2, 5, 1);
		
		column = 0;
		row++;
		
		hbox.getChildren().addAll(spImage, gpEditor);
		hbox.setSpacing(20);
		hbox.setPrefWidth(-1);
		hbox.setAlignment(Pos.TOP_CENTER);
		
		btnSave = createButton(SAVE_PATH, CSS_CLASS_FORM_BUTTON, "保存", BTN_WIDTH, BTN_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				Gripper.Type type = Gripper.Type.TWOPOINT;
				if (rbGripperTypeTwoPoint.isSelected()) {
					type = Gripper.Type.TWOPOINT;
				} else if (rbGripperTypeVacuum.isSelected()) {
					type = Type.VACUUM;
				} else {
					throw new IllegalStateException("No type radio button selected");
				}				
				selectedGripper = new Gripper(fulltxtName.getText(), type, Float.parseFloat(numtxtHeight.getText()), imagePath);
					try {
						saveData (selectedGripper);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		});
		btnSave.getStyleClass().add("save-btn");
		
		btnDelete = createButton(DELETE_ICON_PATH, CSS_CLASS_FORM_BUTTON, "删除", BTN_WIDTH, BTN_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				//getPresenter().deleteGripper();
			}
		});
		btnDelete.getStyleClass().add("delete-btn");
		
		vboxForm = new VBox();
		vboxForm.getChildren().addAll(hbox, btnSave, btnDelete);
		vboxForm.setAlignment(Pos.CENTER);
		vboxForm.setSpacing(VGAP);
		
		column = 0;
		row++;
		gridPane.add(vboxForm, column, row,2,1);
		VBox.setMargin(vboxForm, new Insets(0, 0, 0, 0));
		vboxForm.setVisible(false);
		GridPane.setHalignment(vboxForm, HPos.CENTER);
	}



	
	public void gripperSelected(final Gripper gripper) {
		ifsGrippers.setSelected(gripper.getName());
		btnEdit.setDisable(false);
		fulltxtName.setText(gripper.getName());
		numtxtHeight.setText("" + gripper.getHeight());
		cbFixedHeight.setSelected(gripper.isFixedHeight());
		if (gripper.getType() == Type.TWOPOINT) {
			rbGripperTypeTwoPoint.setSelected(true);
		} else if (gripper.getType() == Type.VACUUM) {
			rbGripperTypeVacuum.setSelected(true);
		} else {
			throw new IllegalStateException("Unknown gripper type: " + gripper.getType());
		}
		String url = gripper.getImageUrl();
		if (url != null) {
			url = url.replace("file:///", "");
		}
		if ((url != null) && ((new File(url)).exists() || getClass().getClassLoader().getResource(url) != null)) {
			imageVw.setImage(new Image(gripper.getImageUrl(), IMG_WIDTH, IMG_HEIGHT, true, true));
		} else {
			imageVw.setImage(new Image(UIConstants.IMG_NOT_FOUND_URL, IMG_WIDTH, IMG_HEIGHT, true, true));
		}
		imagePath = gripper.getImageUrl();
		//cbA.setSelected((gripper.getSelectGripper() != null) );
		
	}
	
	public void clickedEdit() {
		if (editMode) {
			reset();
			editMode = false;
		} else {
			showFormEdit();
			editMode = true;
		}
	}
	
	public void clickedNew() {
			reset();
		if (!editMode) {
			selectedGripper = null;
			showFormNew();
			editMode = true;
		} else {
			editMode = false;
		}
	}
	
	public void reset() {
		ifsGrippers.deselectAll();
		btnCreateNew.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
		btnEdit.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
		btnCreateNew.setDisable(false);
		cbFixedHeight.setSelected(false);
		btnEdit.setDisable(true);
		fulltxtName.setText("");
		numtxtHeight.setText("");
		cbA.setSelected(true);
		cbB.setSelected(true);
		cbC.setSelected(false);
		cbD.setSelected(false);
		imageVw.setImage(null);
		imagePath = null;
		validate();
		setFormVisible(false);
	}
	
	public void showFormNew() {
		setFormVisible(true);
		btnEdit.setDisable(true);
		btnDelete.setVisible(false);
		btnCreateNew.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
		validate();
	}
	
	public void showFormEdit() {
		setFormVisible(true);
		btnCreateNew.setDisable(true);
		btnDelete.setVisible(true);
		btnEdit.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
		validate();
	}
	
	public void setFormVisible(final boolean visible) {
		vboxForm.setVisible(visible);
	}
	
	private void validate() {
		if (!fulltxtName.getText().equals("") && !numtxtHeight.getText().equals("") && (Float.parseFloat(numtxtHeight.getText()) > 0) 
				&& (imagePath != null) && !imagePath.equals("")) {
			btnSave.setDisable(false);
		} else {
			btnSave.setDisable(true);
		}
	}
	@Override
	public void closeKeyboard() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void textFieldFocussed(TextInputControl textInputControl) {
		
	}
	@Override
	public void textFieldLostFocus(TextInputControl textInputControl) {
			
	}
	@Override
	public void setMessege(String mess) {
		// TODO Auto-generated method stub
		
	}
	
	public void saveData(Gripper gripper) throws SQLException {
		if (selectedGripper.getId()>0) {
			Gripperhandler.updateGripper(gripper);
		} else {
			Gripperhandler.saveGripper(gripper);
		}
		selectedGripper = null;
		editMode = false;
		//getView().refresh();
	}
}
