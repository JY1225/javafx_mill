package cn.greatoo.easymill.ui.set.table.load;

import java.util.ArrayList;
import java.util.List;

import cn.greatoo.easymill.db.util.DBHandler;
import cn.greatoo.easymill.entity.Program;
import cn.greatoo.easymill.entity.WorkPiece;
import cn.greatoo.easymill.entity.WorkPiece.Material;
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

public class RawWPViewController extends Controller{
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
	List<Button> bts;
	List<Button> mBts;
	private String programName;
	List<Button> calc;
	public void init() {
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
		Program program = DBHandler.getInstance().getProgramBuffer().get(programName);
		if (programName != null) {
			fulltxtL.setText(String.valueOf(program.getUnloadstacker().getWorkPiece().getLength()));
			fulltxtW.setText(String.valueOf(program.getUnloadstacker().getWorkPiece().getWidth()));
			fulltxtH.setText(String.valueOf(program.getUnloadstacker().getWorkPiece().getHeight()));
			fulltxtWei.setText(String.valueOf(program.getUnloadstacker().getWorkPiece().getWeight()));
			Material material = program.getUnloadstacker().getWorkPiece().getMaterial();
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
		}
		DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadstacker().getWorkPiece()
				.setType(WorkPiece.Type.RAW);
		DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadstacker().getWorkPiece()
				.setShape(WorkPiece.WorkPieceShape.CUBIC);
		fulltxtL.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
				DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadstacker().getWorkPiece()
					.setLength(Float.parseFloat(fulltxtL.getText()));								
			}
		});

		fulltxtW.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
					DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadstacker().getWorkPiece()
							.setWidth(Float.parseFloat(fulltxtW.getText()));
			}
		});

		fulltxtH.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
				DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadstacker().getWorkPiece()
							.setHeight(Float.parseFloat(fulltxtH.getText()));
			}
		});

		fulltxtWei.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
				DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadstacker().getWorkPiece()
							.setWeight(Float.parseFloat(fulltxtWei.getText()));
			}
		});

		// 螺柱高度
		fulltxtBH.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
					DBHandler.getInstance().getProgramBuffer().get(programName)
							.setStudHeight_Workpiece(Float.parseFloat(fulltxtBH.getText()));				
			}
		});
		// 图层
		fulltxtC.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
					DBHandler.getInstance().getProgramBuffer().get(programName)
							.setLayers((int) Float.parseFloat(fulltxtC.getText()));				
			}
		});
		// 数量
		fulltxtS.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {				
				DBHandler.getInstance().getProgramBuffer().get(programName)
							.setAmount((int) Float.parseFloat(fulltxtS.getText()));
				
			}
		});
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

	}

	@FXML
	public void tiltedAction(MouseEvent event) {
		isClicked(bts, tiltedBt);
		DBHandler.getInstance().getProgramBuffer().get(programName).setOrientation(45);
	}

	@FXML
	public void VBtAction(MouseEvent event) {
		isClicked(bts, VBt);
		DBHandler.getInstance().getProgramBuffer().get(programName).setOrientation(90);

	}

	@FXML
	public void AlBtAction(MouseEvent event) {
		isClicked(mBts, AlBt);
		DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadstacker().getWorkPiece()
				.setMaterial(WorkPiece.Material.AL);
		calculateBt.setDisable(false);	
		recalcWeight();
	}

	@FXML
	public void CuBtAction(MouseEvent event) {
		isClicked(mBts, CuBt);
		DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadstacker().getWorkPiece()
				.setMaterial(WorkPiece.Material.CU);
		calculateBt.setDisable(false);	
		recalcWeight();
	}

	@FXML
	public void FeBtAction(MouseEvent event) {
		isClicked(mBts, FeBt);
		DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadstacker().getWorkPiece()
				.setMaterial(WorkPiece.Material.FE);
		calculateBt.setDisable(false);	
		recalcWeight();
	}

	@FXML
	public void OBtAction(MouseEvent event) {
		isClicked(mBts, OBt);
		DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadstacker().getWorkPiece()
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
	public void LChanged(MouseEvent event) {

	}

	@FXML
	public void TBtAction(MouseEvent event) {

	}

	@FXML
	public void WChanged(MouseEvent event) {

	}

	@FXML
	public void HChanged(MouseEvent event) {

	}

	@FXML
	public void SChanged(MouseEvent event) {

	}

	@FXML
	public void MaxBtAction(MouseEvent event) {

	}

	@FXML
	public void CChanged(MouseEvent event) {

	}

	@FXML
	public void BHChanged(MouseEvent event) {

	}

	@FXML
	public void WeiChanged(MouseEvent event) {

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
		DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadstacker().getWorkPiece().calculateWeight();
    	fulltxtWei.setText(String.valueOf(DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadstacker().getWorkPiece().getWeight()));
    	DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadstacker().getWorkPiece().setWeight(Float.parseFloat(fulltxtWei.getText()));
    }
	
}
