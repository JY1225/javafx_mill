package cn.greatoo.easymill.ui.configure.devicesConfig;

import java.io.File;

import cn.greatoo.easymill.ui.main.Controller;
import cn.greatoo.easymill.util.Clamping;
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
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputControl;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;

public class CNCClampingsView extends Controller implements TextInputControlListener{
	private boolean editMode;
	private Clamping selectedClamping;
	private IconFlowSelector ifsClampings;
	private Button btnNew;
	private Button btnEdit;
	
	private GridPane gpDetails;
	private Label lblName;
	private FullTextField fullTxtName;
	private Label lblHeight;
	private NumericTextField numtxtHeight;
	// relative position
	private Label lblRelativePosition;
	private Label lblX;
	private NumericTextField numtxtX;
	private Label lblY;
	private NumericTextField numtxtY;
	private Label lblZ;
	private NumericTextField numtxtZ;
	private Label lblW;
	private NumericTextField numtxtW;
	private Label lblP;
	private NumericTextField numtxtP;
	private Label lblR;
	private NumericTextField numtxtR;
	// smooth to
	private Label lblSmoothTo;
	private Label lblSmoothToX;
	private NumericTextField numtxtSmoothToX;
	private Label lblSmoothToY;
	private NumericTextField numtxtSmoothToY;
	private Label lblSmoothToZ;
	private NumericTextField numtxtSmoothToZ;
	// smooth from
	private Label lblSmoothFrom;
	private Label lblSmoothFromX;
	private NumericTextField numtxtSmoothFromX;
	private Label lblSmoothFromY;
	private NumericTextField numtxtSmoothFromY;
	private Label lblSmoothFromZ;
	private NumericTextField numtxtSmoothFromZ;		
	// image
	private StackPane spImage;
	private ImageView imageVw;
	
	private StackPane spControls;
	
	private FileChooser fileChooser;
	private String imagePath;
	
	 //WA1/WA2 checkbox
	 private CheckBox cbWa1, cbWa2;	
	
	private Button btnSave, btnCopy, btnDelete;
	
	static final String COPY = "CNCMachineClampingsView.copy";
	static final String SAVE_AS_DIALOG = "CNCMachineClampingsView.saveAsDialog";
	static final String NAME = "CNCMachineClampingsView.name";
	protected static final String CSS_CLASS_FORM_BUTTON_ACTIVE = "form-button-active";
	private static final String CSS_CLASS_GRIPPER_IMAGE_EDIT = "gripper-image-edit";

	private static final String ADD_PATH = "M 10 0 C 4.4775 0 0 4.4775 0 10 C 0 15.5225 4.4775 20 10 20 C 15.5225 20 20 15.5225 20 10 C 20 4.4775 15.5225 0 10 0 z M 8.75 5 L 11.25 5 L 11.25 8.75 L 15 8.75 L 15 11.25 L 11.25 11.25 L 11.25 15 L 8.75 15 L 8.75 11.25 L 5 11.25 L 5 8.75 L 8.75 8.75 L 8.75 5 z";
	private static final String SAVE_PATH = "M 5.40625 0 L 5.40625 7.25 L 0 7.25 L 7.1875 14.40625 L 14.3125 7.25 L 9 7.25 L 9 0 L 5.40625 0 z M 7.1875 14.40625 L 0 14.40625 L 0 18 L 14.3125 18 L 14.3125 14.40625 L 7.1875 14.40625 z";
	private static final String DELETE_ICON_PATH = "M 10 0 C 4.4775 0 0 4.4775 0 10 C 0 15.5225 4.4775 20 10 20 C 15.5225 20 20 15.5225 20 10 C 20 4.4775 15.5225 0 10 0 z M 5 8.75 L 15 8.75 L 15 11.25 L 5 11.25 L 5 8.75 z";
	
	private static final double IMG_WIDTH = 90;
	private static final double IMG_HEIGHT = 90;
	private static final double LBL_WIDTH = 25;
	private static final double BTN_HEIGHT = UIConstants.BUTTON_HEIGHT;
	private static final double BTN_WIDTH = BTN_HEIGHT * 3;
	

	private GridPane gridPane;
	public void init(GridPane gridPane, Button editBt, Button newBt,IconFlowSelector ifsClamping) {
		this.gridPane = gridPane;
		this.btnEdit = editBt;
		this.btnNew = newBt;
		this.ifsClampings = ifsClamping;
		build();
		setTextFieldListener(this);
	}

	protected void build() {
		gridPane.setVgap(10);
		gridPane.setHgap(10);
		gridPane.setAlignment(Pos.TOP_CENTER);
		gridPane.setPadding(new Insets(10, 0, 0, 0));
		gridPane.setStyle("-fx-background:transparent;");//透明
		//gridPane.getChildren().clear();
				
		
		spImage = new StackPane();
		spImage.setStyle("-fx-background-color: #4e5055");
		spImage.setPadding(new Insets(5, 5, 5, 5));
		spImage.setPrefSize(IMG_WIDTH + 10, IMG_HEIGHT + 10);
		spImage.setMinSize(IMG_WIDTH + 10, IMG_HEIGHT + 10);
		spImage.setMaxSize(IMG_WIDTH + 10, IMG_HEIGHT + 10);
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
		
		lblName = new Label("名称");
		lblName.setMinWidth(65);
		fullTxtName = new FullTextField();
		fullTxtName.setMinSize(240, UIConstants.TEXT_FIELD_HEIGHT);
		fullTxtName.setPrefSize(240, UIConstants.TEXT_FIELD_HEIGHT);
		fullTxtName.setMaxSize(240, UIConstants.TEXT_FIELD_HEIGHT);
		fullTxtName.setOnChange(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> arg0, final String arg1, final String arg2) {
				validate();
			}
		});
		lblHeight = new Label("高度");
		lblHeight.setMinWidth(65);
		numtxtHeight = new NumericTextField();
		numtxtHeight.setPrefHeight( UIConstants.TEXT_FIELD_HEIGHT);
		numtxtHeight.setMinHeight(UIConstants.TEXT_FIELD_HEIGHT);
		numtxtHeight.setMaxHeight(UIConstants.TEXT_FIELD_HEIGHT);
		numtxtHeight.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> arg0, final Float arg1, final Float arg2) {
				validate();
			}
		});
		
		cbWa1 = new CheckBox("工作区1");
		cbWa1.setMinWidth(65);
		cbWa1.setPrefWidth(65);
		cbWa1.setMaxWidth(65);
		cbWa1.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(final ObservableValue<? extends Boolean> observableValue, final Boolean oldValue, final Boolean newValue) {
				//Do the opposite for cbWa2
				if (!cbWa2.isDisabled()) {
					cbWa2.setSelected(oldValue);
				} 
			}
		});
		cbWa2 = new CheckBox("工作区2");
		cbWa2.setMinWidth(65);
		cbWa2.setPrefWidth(65);
		cbWa2.setMaxWidth(65);
		cbWa2.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(final ObservableValue<? extends Boolean> observableValue, final Boolean oldValue, final Boolean newValue) {
				//Do the opposite for cbWa1
				if (!cbWa1.isDisabled()) {
					cbWa1.setSelected(oldValue);
				} 
			}
		});
		
		lblRelativePosition = new Label("相对位置");
		lblRelativePosition.setPrefWidth(150);
		lblRelativePosition.setMinWidth(150);
		lblX = new Label("X");
		lblX.setMinWidth(LBL_WIDTH);
		lblX.setPrefWidth(LBL_WIDTH);
		numtxtX = new NumericTextField();
		numtxtX.setPrefHeight(UIConstants.TEXT_FIELD_HEIGHT);
		numtxtX.setMaxHeight( UIConstants.TEXT_FIELD_HEIGHT);
		numtxtX.setMinHeight( UIConstants.TEXT_FIELD_HEIGHT);
		numtxtX.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> arg0, final Float arg1, final Float arg2) {
				validate();
			}
		});
		lblY = new Label("Y");
		lblY.setMinWidth(LBL_WIDTH);
		lblY.setPrefWidth(LBL_WIDTH);
		numtxtY = new NumericTextField();
		numtxtY.setPrefHeight( UIConstants.TEXT_FIELD_HEIGHT);
		numtxtY.setMaxHeight(UIConstants.TEXT_FIELD_HEIGHT);
		numtxtY.setMinHeight(UIConstants.TEXT_FIELD_HEIGHT);
		numtxtY.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> arg0, final Float arg1, final Float arg2) {
				validate();
			}
		});
		lblZ = new Label("Z");
		lblZ.setMinWidth(LBL_WIDTH);
		lblZ.setPrefWidth(LBL_WIDTH);
		numtxtZ = new NumericTextField();
		numtxtZ.setPrefHeight( UIConstants.TEXT_FIELD_HEIGHT);
		numtxtZ.setMaxHeight(UIConstants.TEXT_FIELD_HEIGHT);
		numtxtZ.setMinHeight(UIConstants.TEXT_FIELD_HEIGHT);
		numtxtZ.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> arg0, final Float arg1, final Float arg2) {
				validate();
			}
		});
		lblW = new Label("W");
		lblW.setMinWidth(LBL_WIDTH);
		lblW.setPrefWidth(LBL_WIDTH);
		numtxtW = new NumericTextField();
		numtxtW.setPrefHeight(UIConstants.TEXT_FIELD_HEIGHT);
		numtxtW.setMaxHeight(UIConstants.TEXT_FIELD_HEIGHT);
		numtxtW.setMinHeight(UIConstants.TEXT_FIELD_HEIGHT);
		numtxtW.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> arg0, final Float arg1, final Float arg2) {
				validate();
			}
		});
		lblP = new Label("P");
		lblP.setMinWidth(LBL_WIDTH);
		lblP.setPrefWidth(LBL_WIDTH);
		numtxtP = new NumericTextField();
		numtxtP.setPrefHeight( UIConstants.TEXT_FIELD_HEIGHT);
		numtxtP.setMaxHeight(UIConstants.TEXT_FIELD_HEIGHT);
		numtxtP.setMinHeight(UIConstants.TEXT_FIELD_HEIGHT);
		numtxtP.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> arg0, final Float arg1, final Float arg2) {
				validate();
			}
		});
		lblR = new Label("R");
		lblR.setMinWidth(LBL_WIDTH);
		lblR.setPrefWidth(LBL_WIDTH);
		numtxtR = new NumericTextField();
		numtxtR.setPrefHeight(UIConstants.TEXT_FIELD_HEIGHT);
		numtxtR.setMaxHeight( UIConstants.TEXT_FIELD_HEIGHT);
		numtxtR.setMinHeight(UIConstants.TEXT_FIELD_HEIGHT);
		numtxtR.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> arg0, final Float arg1, final Float arg2) {
				validate();
			}
		});
		
		lblSmoothTo = new Label("smoothTo");
		lblSmoothTo.setPrefWidth(150);
		lblSmoothTo.setMinWidth(150);
		lblSmoothToX = new Label("X");
		lblSmoothToX.setPrefWidth(LBL_WIDTH);
		lblSmoothToX.setMinWidth(LBL_WIDTH);
		numtxtSmoothToX = new NumericTextField();
		numtxtSmoothToX.setPrefHeight(UIConstants.TEXT_FIELD_HEIGHT);
		numtxtSmoothToX.setMaxHeight(UIConstants.TEXT_FIELD_HEIGHT);
		numtxtSmoothToX.setMinHeight(UIConstants.TEXT_FIELD_HEIGHT);
		numtxtSmoothToX.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> arg0, final Float arg1, final Float arg2) {
				validate();
			}
		});
		lblSmoothToY = new Label("Y");
		lblSmoothToY.setPrefWidth(LBL_WIDTH);
		numtxtSmoothToY = new NumericTextField();
		numtxtSmoothToY.setPrefHeight(UIConstants.TEXT_FIELD_HEIGHT);
		numtxtSmoothToY.setMaxHeight(UIConstants.TEXT_FIELD_HEIGHT);
		numtxtSmoothToY.setMinHeight(UIConstants.TEXT_FIELD_HEIGHT);
		numtxtSmoothToY.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> arg0, final Float arg1, final Float arg2) {
				validate();
			}
		});
		lblSmoothToZ = new Label("Z");
		lblSmoothToZ.setPrefWidth(LBL_WIDTH);
		numtxtSmoothToZ = new NumericTextField();
		numtxtSmoothToZ.setPrefHeight(UIConstants.TEXT_FIELD_HEIGHT);
		numtxtSmoothToZ.setMaxHeight(UIConstants.TEXT_FIELD_HEIGHT);
		numtxtSmoothToZ.setMinHeight(UIConstants.TEXT_FIELD_HEIGHT);
		numtxtSmoothToZ.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> arg0, final Float arg1, final Float arg2) {
				validate();
			}
		});
		
		lblSmoothFrom = new Label("smoothFrom");
		lblSmoothFrom.setPrefWidth(150);
		lblSmoothFrom.setMinWidth(150);
		lblSmoothFromX = new Label("X");
		lblSmoothFromX.setPrefWidth(LBL_WIDTH);
		numtxtSmoothFromX = new NumericTextField();
		numtxtSmoothFromX.setPrefHeight(UIConstants.TEXT_FIELD_HEIGHT);
		numtxtSmoothFromX.setMaxHeight(UIConstants.TEXT_FIELD_HEIGHT);
		numtxtSmoothFromX.setMinHeight(UIConstants.TEXT_FIELD_HEIGHT);
		numtxtSmoothFromX.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> arg0, final Float arg1, final Float arg2) {
				validate();
			}
		});
		lblSmoothFromY = new Label("Y");
		lblSmoothFromY.setPrefWidth(LBL_WIDTH);
		numtxtSmoothFromY = new NumericTextField();
		numtxtSmoothFromY.setPrefHeight(UIConstants.TEXT_FIELD_HEIGHT);
		numtxtSmoothFromY.setMaxHeight(UIConstants.TEXT_FIELD_HEIGHT);
		numtxtSmoothFromY.setMinHeight(UIConstants.TEXT_FIELD_HEIGHT);
		numtxtSmoothFromY.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> arg0, final Float arg1, final Float arg2) {
				validate();
			}
		});
		lblSmoothFromZ = new Label("Z");
		lblSmoothFromZ.setPrefWidth(LBL_WIDTH);
		numtxtSmoothFromZ = new NumericTextField();
		numtxtSmoothFromZ.setPrefHeight(UIConstants.TEXT_FIELD_HEIGHT);
		numtxtSmoothFromZ.setMaxHeight(UIConstants.TEXT_FIELD_HEIGHT);
		numtxtSmoothFromZ.setMinHeight(UIConstants.TEXT_FIELD_HEIGHT);
		numtxtSmoothFromZ.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> arg0, final Float arg1, final Float arg2) {
				validate();
			}
		});
		
		btnSave = createButton(SAVE_PATH, CSS_CLASS_FORM_BUTTON, "保存", BTN_WIDTH, BTN_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				
			}
		});
		btnCopy = createButton(ADD_PATH, CSS_CLASS_FORM_BUTTON, "另存为", BTN_WIDTH + 40, BTN_HEIGHT, new EventHandler<ActionEvent>() {	
			@Override
			public void handle(final ActionEvent arg0) {
//				Clamping.Type type = null;
//				String selectedType = cbbType.getValue();
//				if (selectedType == CLAMPING_TYPE_CENTRUM) {
//					type = Type.CENTRUM;
//				} else if (selectedType == CLAMPING_TYPE_FIXED_XP) {
//					type = Type.FIXED_XP;
//				} else if (selectedType == CLAMPING_TYPE_FIXED_XM) {
//					type = Type.FIXED_XM;
//				} else if (selectedType == CLAMPING_TYPE_FIXED_YP) {
//					type = Type.FIXED_YP;
//				} else if (selectedType == CLAMPING_TYPE_FIXED_YM) {
//					type = Type.FIXED_YM;
//				}
//				int waNr = 1;
//				if (cbWa1.isSelected()) {
//					waNr = 1;
//				} else if (cbWa2.isSelected()) {
//					waNr = 2;
//				}
				//EFixtureType fixtureType = EFixtureType.getFixtureTypeFromStringValue(cbbFixtureType.getValue());
//				copyClamping(Float.parseFloat(numtxtHeight.getText()), imagePath,
//						Float.parseFloat(numtxtX.getText()), Float.parseFloat(numtxtY.getText()), 
//						Float.parseFloat(numtxtZ.getText()), Float.parseFloat(numtxtW.getText()), 
//						Float.parseFloat(numtxtP.getText()), Float.parseFloat(numtxtR.getText()),
//						Float.parseFloat(numtxtSmoothToX.getText()), Float.parseFloat(numtxtSmoothToY.getText()),
//						Float.parseFloat(numtxtSmoothToZ.getText()), Float.parseFloat(numtxtSmoothFromX.getText()), 
//						Float.parseFloat(numtxtSmoothFromY.getText()), Float.parseFloat(numtxtSmoothFromZ.getText()),
//						type),
//						waNr);	
			}
		});
		btnDelete = createButton(DELETE_ICON_PATH, CSS_CLASS_FORM_BUTTON, "删除", BTN_WIDTH, BTN_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				//deleteClamping();
			}
		});
		btnDelete.getStyleClass().add("delete-btn");
		
		spControls = new StackPane();
		spControls.getChildren().addAll(btnDelete, btnSave);
		spControls.setAlignment(Pos.CENTER);
		StackPane.setAlignment(btnDelete, Pos.CENTER_LEFT);
		StackPane.setAlignment(btnCopy, Pos.CENTER);
		StackPane.setAlignment(btnSave, Pos.CENTER_RIGHT);
		
		gpDetails = new GridPane();
		gpDetails.setAlignment(Pos.CENTER);
		gpDetails.setVgap(10);
		int column = 0;
		int row = 0;
		gpDetails.add(spImage, column, row);
		GridPane gpNameHeight = new GridPane();
		gpNameHeight.setVgap(10);
		gpNameHeight.setHgap(10);
		gpNameHeight.add(lblName, 0, 0);
		gpNameHeight.add(fullTxtName, 1, 0, 3, 1);
		gpNameHeight.add(lblHeight, 0, 1);
		gpNameHeight.add(numtxtHeight, 1, 1);
		gpNameHeight.add(cbWa1, 2, 1);
		gpNameHeight.add(cbWa2, 3, 1); 
		gpNameHeight.setAlignment(Pos.CENTER_LEFT);
		gpDetails.add(gpNameHeight, 1, row, 2, 1);
		column = 0; row++;
		GridPane gpRelativePosition = new GridPane();
		gpRelativePosition.setVgap(10);
		gpRelativePosition.setHgap(10);
		gpRelativePosition.add(lblRelativePosition, 0, 0);
		gpRelativePosition.add(lblX, 1, 0);
		gpRelativePosition.add(numtxtX, 2, 0);
		gpRelativePosition.add(lblY, 3, 0);
		gpRelativePosition.add(numtxtY, 4, 0);
		gpRelativePosition.add(lblZ, 5, 0);
		gpRelativePosition.add(numtxtZ, 6, 0);
		gpRelativePosition.add(lblW, 1, 1);
		gpRelativePosition.add(numtxtW, 2, 1);
		gpRelativePosition.add(lblP, 3, 1);
		gpRelativePosition.add(numtxtP, 4, 1);
		gpRelativePosition.add(lblR, 5, 1);
		gpRelativePosition.add(numtxtR, 6, 1);
		gpRelativePosition.setAlignment(Pos.CENTER_LEFT);
		gpDetails.add(gpRelativePosition, column++, row, 7, 1);
		column = 0; row++;
		HBox hboxSmoothTo = new HBox();
		hboxSmoothTo.setAlignment(Pos.CENTER_LEFT);
		hboxSmoothTo.setSpacing(10);
		hboxSmoothTo.getChildren().addAll(lblSmoothTo, lblSmoothToX, numtxtSmoothToX, lblSmoothToY, numtxtSmoothToY, lblSmoothToZ, numtxtSmoothToZ);
		gpDetails.add(hboxSmoothTo, column++, row, 7, 1);
		column = 0; row++;
		HBox hboxSmoothFrom = new HBox();
		hboxSmoothFrom.setAlignment(Pos.CENTER_LEFT);
		hboxSmoothFrom.setSpacing(10);
		hboxSmoothFrom.getChildren().addAll(lblSmoothFrom, lblSmoothFromX, numtxtSmoothFromX, lblSmoothFromY, numtxtSmoothFromY, lblSmoothFromZ, numtxtSmoothFromZ);
		gpDetails.add(hboxSmoothFrom, column++, row, 6, 1);

		
		//Buttons
		column = 0; row++;
		gpDetails.setVisible(false);
		spControls.setVisible(false);
		GridPane.setHalignment(spControls, HPos.CENTER);
		gpDetails.setAlignment(Pos.CENTER);
		GridPane.setHalignment(gpDetails, HPos.CENTER);
		gridPane.add(gpDetails, 0, 2,2,1);
		gridPane.add(spControls, 0,3,2,1);
		gpDetails.setPadding(new Insets(10, 0, 10, 0));
		spControls.setPadding(new Insets(0, 0, 10, 0));
		gridPane.setAlignment(Pos.CENTER);
		GridPane.setValignment(spImage, VPos.TOP);
	}
	
	public void setTextFieldListener(final TextInputControlListener listener) {
		fullTxtName.setFocusListener(listener);
		numtxtHeight.setFocusListener(listener);
		numtxtX.setFocusListener(listener);
		numtxtY.setFocusListener(listener);
		numtxtZ.setFocusListener(listener);
		numtxtW.setFocusListener(listener);
		numtxtP.setFocusListener(listener);
		numtxtR.setFocusListener(listener);
		numtxtSmoothToX.setFocusListener(listener);
		numtxtSmoothToY.setFocusListener(listener);
		numtxtSmoothToZ.setFocusListener(listener);
		numtxtSmoothFromX.setFocusListener(listener);
		numtxtSmoothFromY.setFocusListener(listener);
		numtxtSmoothFromZ.setFocusListener(listener);
	}

	public void validate() {
		//hideNotification();
		if (!fullTxtName.getText().equals("") 
				&& !numtxtHeight.getText().equals("") 
				&& isRelPosFilled()
				&& !numtxtSmoothToX.getText().equals("")
				&& !numtxtSmoothToY.getText().equals("")
				&& !numtxtSmoothToZ.getText().equals("")
				&& !numtxtSmoothFromX.getText().equals("")
				&& !numtxtSmoothFromY.getText().equals("")
				&& !numtxtSmoothFromZ.getText().equals("")
				&& (imagePath != null) && !imagePath.equals("")
				&& (cbWa1.isSelected() || cbWa2.isSelected())
				) {
			btnSave.setDisable(false);
		} else {
			btnSave.setDisable(true);
		}
	}
	
	private boolean isRelPosFilled() {
		return (!numtxtX.getText().equals("")
				&& !numtxtY.getText().equals("")
				&& !numtxtZ.getText().equals("")
				&& !numtxtW.getText().equals("")
				&& !numtxtP.getText().equals("")
				&& !numtxtR.getText().equals(""));
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
			selectedClamping = null;
			showFormNew();
			editMode = true;
		} else {
			editMode = false;
		}
	}
	public void showFormEdit() {
		gpDetails.setVisible(true);
		spControls.setVisible(true);
		btnNew.setDisable(true);
		btnDelete.setVisible(true);
		btnEdit.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
		validate();
	}
	
	public void showFormNew() {
		gpDetails.setVisible(true);
		spControls.setVisible(true);
		btnEdit.setDisable(true);
		btnDelete.setVisible(false);
		btnNew.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
		validate();
	}
	public void reset() {
		numtxtX.setDisable(false);
		numtxtY.setDisable(false);
		numtxtZ.setDisable(false);
		numtxtR.setDisable(false);
		lblRelativePosition.setDisable(false);
		lblX.setDisable(false);
		lblY.setDisable(false);
		lblZ.setDisable(false);
		lblR.setDisable(false);
		fullTxtName.setText("");
		numtxtHeight.setText("");
		btnEdit.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
		btnNew.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
		ifsClampings.deselectAll();
		btnNew.setDisable(false);
		btnEdit.setDisable(true);
		numtxtX.setText("");
		numtxtY.setText("");
		numtxtZ.setText("");
		numtxtW.setText("");
		numtxtP.setText("");
		numtxtR.setText("");
		numtxtSmoothToX.setText("");
		numtxtSmoothToY.setText("");
		numtxtSmoothToZ.setText("");
		numtxtSmoothFromX.setText("");
		numtxtSmoothFromY.setText("");
		numtxtSmoothFromZ.setText("");
		cbWa1.setSelected(true);
		cbWa2.setSelected(false);
		imagePath = null;
		imageVw.setImage(null);
		validate();
		gpDetails.setVisible(false);
		spControls.setVisible(false);
	}
	@Override
	public void closeKeyboard() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void textFieldFocussed(TextInputControl textInputControl) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void textFieldLostFocus(TextInputControl textInputControl) {
		// TODO Auto-generated method stub
		
	}
}
