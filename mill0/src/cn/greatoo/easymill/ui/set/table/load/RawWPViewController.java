package cn.greatoo.easymill.ui.set.table.load;

import java.util.ArrayList;
import java.util.List;

import cn.greatoo.easymill.db.util.DBHandler;
import cn.greatoo.easymill.entity.Program;
import cn.greatoo.easymill.entity.WorkPiece;
import cn.greatoo.easymill.entity.WorkPiece.Material;
import cn.greatoo.easymill.process.WorkPiecePositions;
import cn.greatoo.easymill.ui.general.NotificationBox;
import cn.greatoo.easymill.ui.main.Controller;
import cn.greatoo.easymill.util.NumericTextField;
import cn.greatoo.easymill.util.TextInputControlListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.SVGPath;

public class RawWPViewController extends Controller {
	@FXML
	private GridPane generalGridPane;
	@FXML
	private NumericTextField fulltxtL;
	@FXML
	private Button HBt;
	@FXML
	private Button tiltedBt;
	@FXML
	private SVGPath TBt;
	@FXML
	private Button VBt;
	@FXML
	private NumericTextField fulltxtW;
	@FXML
	private NumericTextField fulltxtH;
	@FXML
	private NumericTextField fulltxtS;
	@FXML
	private Button MaxBt;
	@FXML
	private NumericTextField fulltxtC;
	@FXML
	private Button AlBt;
	@FXML
	private Button CuBt;
	@FXML
	private Button FeBt;
	@FXML
	private Button OBt;
	@FXML
	private NumericTextField fulltxtBH;
	@FXML
	private NumericTextField fulltxtWei;
	@FXML
	private Button calculateBt;
	private List<Button> bts;
	private List<Button> mBts;
	private String programName;
	private List<Button> calc;
	private Program program;

	public void init(Button auto) {
		buildAlarmHBox(generalGridPane, 0, 0, 2, 1);
		setTextFieldListener(this);
		bts = new ArrayList<Button>();
		bts.add(HBt);
		bts.add(tiltedBt);
		bts.add(VBt);

		mBts = new ArrayList<Button>();
		mBts.add(AlBt);
		mBts.add(CuBt);
		mBts.add(FeBt);
		mBts.add(OBt);

		refresh();

		calc = new ArrayList<Button>();
		calc.add(calculateBt);

		programName = DBHandler.getInstance().getProgramName();
		program = DBHandler.getInstance().getProgramBuffer().get(programName);

		fulltxtL.setText(String.valueOf(program.getRawWorkPiece().getLength()));
		fulltxtW.setText(String.valueOf(program.getRawWorkPiece().getWidth()));
		fulltxtH.setText(String.valueOf(program.getRawWorkPiece().getHeight()));
		fulltxtWei.setText(String.valueOf(program.getRawWorkPiece().getWeight()));

		Material material = program.getRawWorkPiece().getMaterial();
		switch (material) {
		case AL:
			isClicked(mBts, AlBt);
			break;
		case CU:
			isClicked(mBts, CuBt);
			break;
		case FE:
			isClicked(mBts, FeBt);
			break;
		case OTHER:
			isClicked(mBts, OBt);
			break;
		default:
			break;
		}

		fulltxtBH.setText(String.valueOf(program.getStudHeight_Workpiece()));
		fulltxtC.setText(String.valueOf(program.getLayers()));
		fulltxtS.setText(String.valueOf(program.getAmount()));

		int orientation = (int) program.getOrientation();
		switch (orientation) {
		case 0:
			isClicked(bts, HBt);
			break;
		case 45:
			isClicked(bts, tiltedBt);
			break;
		case 90:
			isClicked(bts, VBt);
			break;
		default:
			break;
		}

		DBHandler.getInstance().getProgramBuffer().get(programName).getRawWorkPiece().setType(WorkPiece.Type.RAW);
		DBHandler.getInstance().getProgramBuffer().get(programName).getRawWorkPiece()
				.setShape(WorkPiece.WorkPieceShape.CUBIC);
		fulltxtL.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> observable, final Float oldValue,
					final Float newValue) {
				notification();
				if (newValue != program.getRawWorkPiece().getLength()) {
					if (newValue > 0 && newValue >= Float.parseFloat(fulltxtW.getText())) {
						auto.setDisable(true);
						DBHandler.getInstance().getProgramBuffer().get(programName).setHasTeach(false);
						DBHandler.getInstance().getProgramBuffer().get(programName).getRawWorkPiece()
								.setLength(newValue);
					}
				}

			}
		});

		fulltxtW.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> observable, final Float oldValue,
					final Float newValue) {
				notification();
				if (newValue != program.getRawWorkPiece().getWidth()) {
					if (newValue > 0 && newValue <= Float.parseFloat(fulltxtL.getText())) {
						auto.setDisable(true);
						DBHandler.getInstance().getProgramBuffer().get(programName).setHasTeach(false);
						DBHandler.getInstance().getProgramBuffer().get(programName).getRawWorkPiece()
								.setWidth(newValue);
					}
				}

			}
		});

		fulltxtH.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> observable, final Float oldValue,
					final Float newValue) {
				notification();
				if(newValue > 0) {
					DBHandler.getInstance().getProgramBuffer().get(programName).getRawWorkPiece().setHeight(newValue);
				}
			}
		});

		fulltxtWei.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> observable, final Float oldValue,
					final Float newValue) {
				notification();
				if(newValue > 0) {
					DBHandler.getInstance().getProgramBuffer().get(programName).getRawWorkPiece().setWeight(newValue);
				}
			}
		});

		// 螺柱高度
		fulltxtBH.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> observable, final Float oldValue,
					final Float newValue) {
				notification();
				if(newValue >= 0) {
					DBHandler.getInstance().getProgramBuffer().get(programName).setStudHeight_Workpiece(newValue);
				}
			}
		});

		// 图层
		fulltxtC.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> observable, final Float oldValue,
					final Float newValue) {
				notification();
				if(newValue > 0) {
					DBHandler.getInstance().getProgramBuffer().get(programName)
						.setLayers((int) Float.parseFloat(fulltxtC.getText()));
				}
			}
		});

		// 数量
		fulltxtS.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> observable, final Float oldValue,
					final Float newValue) {
				notification();
				if(newValue > 0) {					
				WorkPiecePositions workPiecePositions = new WorkPiecePositions(program);
				int amount = workPiecePositions.getWorkPieceAmount();
				if ((int) Float.parseFloat(fulltxtS.getText()) > amount) {
					fulltxtS.setText(amount + "");
					program.setAmount(amount);
				}
				DBHandler.getInstance().getProgramBuffer().get(programName)
						.setAmount((int) Float.parseFloat(fulltxtS.getText()));
			}
			}
		});

		notification();
	}
	
	private void notification() {
		if(Float.parseFloat(fulltxtL.getText()) <= 0) {
			showNotification("长度应该大于0", NotificationBox.Type.WARNING);
		}else if(Float.parseFloat(fulltxtW.getText()) <= 0) {
			showNotification("宽度应该大于0", NotificationBox.Type.WARNING);
		}else if(Float.parseFloat(fulltxtL.getText()) < Float.parseFloat(fulltxtW.getText())) {
			showNotification("长度应该大于宽度", NotificationBox.Type.WARNING);
		}else if(Float.parseFloat(fulltxtH.getText()) <= 0) {
			showNotification("高度应该大于0", NotificationBox.Type.WARNING);
		}else if(Float.parseFloat(fulltxtBH.getText()) < 0) {
			showNotification("螺柱高度应该大于等于0", NotificationBox.Type.WARNING);
		}else if(Float.parseFloat(fulltxtC.getText()) <= 0) {
			showNotification("图层应该大于0", NotificationBox.Type.WARNING);
		}else if(Float.parseFloat(fulltxtS.getText()) <= 0) {
			showNotification("数量应该大于0", NotificationBox.Type.WARNING);
		}else if(Float.parseFloat(fulltxtWei.getText()) <= 0) {
			showNotification("重量应该大于0", NotificationBox.Type.WARNING);
		}else {
			hideNotification();
		}
	}
	
	public void setTextFieldListener(final TextInputControlListener listener) {
		fulltxtL.setFocusListener(listener);
		fulltxtW.setFocusListener(listener);
		fulltxtH.setFocusListener(listener);
		fulltxtWei.setFocusListener(listener);
		fulltxtBH.setFocusListener(listener);
		fulltxtC.setFocusListener(listener);
		fulltxtS.setFocusListener(listener);
	}

	@FXML
	public void HBtAction(MouseEvent event) {
		isClicked(bts, HBt);
		DBHandler.getInstance().getProgramBuffer().get(programName).setOrientation(0);
		WorkPiecePositions workPiecePositions = new WorkPiecePositions(program);
		int amount = workPiecePositions.getWorkPieceAmount();
		if (program.getAmount() > amount) {
			fulltxtS.setText(amount + "");
			program.setAmount(amount);
		}
	}

	@FXML
	public void tiltedAction(MouseEvent event) {
		isClicked(bts, tiltedBt);
		DBHandler.getInstance().getProgramBuffer().get(programName).setOrientation(45);
		WorkPiecePositions workPiecePositions = new WorkPiecePositions(program);
		int amount = workPiecePositions.getWorkPieceAmount();
		if (program.getAmount() > amount) {
			fulltxtS.setText(amount + "");
			program.setAmount(amount);
		}
	}

	@FXML
	public void VBtAction(MouseEvent event) {
		isClicked(bts, VBt);
		DBHandler.getInstance().getProgramBuffer().get(programName).setOrientation(90);
		WorkPiecePositions workPiecePositions = new WorkPiecePositions(program);
		int amount = workPiecePositions.getWorkPieceAmount();
		if (program.getAmount() > amount) {
			fulltxtS.setText(amount + "");
			program.setAmount(amount);
		}
	}

	@FXML
	public void AlBtAction(MouseEvent event) {
		isClicked(mBts, AlBt);
		DBHandler.getInstance().getProgramBuffer().get(programName).getRawWorkPiece()
				.setMaterial(WorkPiece.Material.AL);
		calculateBt.setDisable(false);
		recalcWeight();
	}

	@FXML
	public void CuBtAction(MouseEvent event) {
		isClicked(mBts, CuBt);
		DBHandler.getInstance().getProgramBuffer().get(programName).getRawWorkPiece()
				.setMaterial(WorkPiece.Material.CU);
		calculateBt.setDisable(false);
		recalcWeight();
	}

	@FXML
	public void FeBtAction(MouseEvent event) {
		isClicked(mBts, FeBt);
		DBHandler.getInstance().getProgramBuffer().get(programName).getRawWorkPiece()
				.setMaterial(WorkPiece.Material.FE);
		calculateBt.setDisable(false);
		recalcWeight();
	}

	@FXML
	public void OBtAction(MouseEvent event) {
		isClicked(mBts, OBt);
		DBHandler.getInstance().getProgramBuffer().get(programName).getRawWorkPiece()
				.setMaterial(WorkPiece.Material.OTHER);
		calculateBt.setDisable(true);
	}

	private void refresh() {
		isClicked(bts, null);
		isClicked(mBts, null);
		fulltxtL.setText("");
		fulltxtW.setText("");
		fulltxtH.setText("");
		fulltxtWei.setText("");
		fulltxtBH.setText("");
		fulltxtC.setText("");
		fulltxtS.setText("");
	}

	@FXML
	public void MaxBtAction(MouseEvent event) {		
		WorkPiecePositions workPiecePositions = new WorkPiecePositions(program);
		int amount = workPiecePositions.getWorkPieceAmount();
		fulltxtS.setText(amount + "");
		program.setAmount(amount);
		notification();
	}

	@FXML
	public void calculateBtAction(MouseEvent event) {
		recalcWeight();
	}

	@Override
	public void setMessege(String mess) {
		// TODO Auto-generated method stub

	}

	public void recalcWeight() {
		DBHandler.getInstance().getProgramBuffer().get(programName).getRawWorkPiece().calculateWeight();
		fulltxtWei.setText(String
				.valueOf(DBHandler.getInstance().getProgramBuffer().get(programName).getRawWorkPiece().getWeight()));
		DBHandler.getInstance().getProgramBuffer().get(programName).getRawWorkPiece()
				.setWeight(Float.parseFloat(fulltxtWei.getText()));
		notification();
	}

}
