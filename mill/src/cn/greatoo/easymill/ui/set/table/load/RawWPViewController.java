package cn.greatoo.easymill.ui.set.table.load;

import java.util.ArrayList;
import java.util.List;

import cn.greatoo.easymill.entity.Stacker;
import cn.greatoo.easymill.entity.WorkPiece;
import cn.greatoo.easymill.ui.main.Controller;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.SVGPath;

public class RawWPViewController extends Controller {
	@FXML
	private GridPane generalGridPane;
	@FXML
	private TextField fulltxtL;
	@FXML
	private Button HBt;
	@FXML
	private Button tiltedBt;
	@FXML
	private SVGPath TBt;
	@FXML
	private Button VBt;
	@FXML
	private TextField fulltxtW;
	@FXML
	private TextField fulltxtH;
	@FXML
	private TextField fulltxtS;
	@FXML
	private Button MaxBt;
	@FXML
	private TextField fulltxtC;
	@FXML
	private Button AlBt;
	@FXML
	private Button CuBt;
	@FXML
	private Button FeBt;
	@FXML
	private Button OBt;
	@FXML
	private TextField fulltxtBH;
	@FXML
	private TextField fulltxtWei;
	@FXML
	private Button calculateBt;
	List<Button> bts;
	List<Button> mBts;
	public static WorkPiece workPiece = new WorkPiece();
	public static Stacker stacker = new Stacker();
	
	public void init() {
		bts = new ArrayList<Button>();
		bts.add(HBt);
		bts.add(tiltedBt);
		bts.add(VBt);

		mBts = new ArrayList<Button>();
		mBts.add(AlBt);
		mBts.add(CuBt);
		mBts.add(FeBt);
		mBts.add(OBt);
		
		workPiece.setType(WorkPiece.Type.RAW);
		fulltxtL.focusedProperty().addListener(new ChangeListener<Boolean>() {
	        @Override
	        public void changed(ObservableValue<? extends Boolean> arg0,Boolean arg1, Boolean arg2) {
	        	workPiece.setLength(Float.parseFloat(fulltxtL.getText()));
	        }
		});	
		
		fulltxtW.focusedProperty().addListener(new ChangeListener<Boolean>() {
	        @Override
	        public void changed(ObservableValue<? extends Boolean> arg0,Boolean arg1, Boolean arg2) {
	        	workPiece.setWidth(Float.parseFloat(fulltxtW.getText()));
	        }
		});	
		
		fulltxtH.focusedProperty().addListener(new ChangeListener<Boolean>() {
	        @Override
	        public void changed(ObservableValue<? extends Boolean> arg0,Boolean arg1, Boolean arg2) {
	        	workPiece.setHeight(Float.parseFloat(fulltxtH.getText()));
	        }
		});	
		
		fulltxtWei.focusedProperty().addListener(new ChangeListener<Boolean>() {
	        @Override
	        public void changed(ObservableValue<? extends Boolean> arg0,Boolean arg1, Boolean arg2) {
	        	workPiece.setWeight(Float.parseFloat(fulltxtWei.getText()));
	        }
		});	
		
		//螺柱高度
		fulltxtBH.focusedProperty().addListener(new ChangeListener<Boolean>() {
	        @Override
	        public void changed(ObservableValue<? extends Boolean> arg0,Boolean arg1, Boolean arg2) {
	        	stacker.setStudHeight(Float.parseFloat(fulltxtBH.getText()));
	        }
		});	
		//图层
		fulltxtC.focusedProperty().addListener(new ChangeListener<Boolean>() {
	        @Override
	        public void changed(ObservableValue<? extends Boolean> arg0,Boolean arg1, Boolean arg2) {
	        	stacker.setLayers(Integer.parseInt(fulltxtC.getText()));
	        }
		});	
		//数量
		fulltxtS.focusedProperty().addListener(new ChangeListener<Boolean>() {
	        @Override
	        public void changed(ObservableValue<? extends Boolean> arg0,Boolean arg1, Boolean arg2) {
	        	if(!"".equals(fulltxtS.getText())) {	        		
	        		stacker.setAmount(Integer.parseInt(fulltxtS.getText()));
	        	}else {
	        		stacker.setAmount(0);
	        	}
	        }
		});	
	}
	
	@FXML
	public void LChanged(MouseEvent event) {

	}
	
	@FXML
	public void HBtAction(MouseEvent event) {
		isClicked(bts, HBt);
		stacker.setOrientation(0);
	}
	
	@FXML
	public void tiltedAction(MouseEvent event) {
		isClicked(bts, tiltedBt);
		stacker.setOrientation(45);
	}
	
	@FXML
	public void VBtAction(MouseEvent event) {
		isClicked(bts, VBt);
		stacker.setOrientation(90);
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
	public void AlBtAction(MouseEvent event) {
		isClicked(mBts, AlBt);
		workPiece.setMaterial(WorkPiece.Material.AL);
	}
	
	@FXML
	public void CuBtAction(MouseEvent event) {
		isClicked(mBts, CuBt);
		workPiece.setMaterial(WorkPiece.Material.CU);
	}
	
	@FXML
	public void FeBtAction(MouseEvent event) {
		isClicked(mBts, FeBt);
		workPiece.setMaterial(WorkPiece.Material.FE);
	}
	
	@FXML
	public void OBtAction(MouseEvent event) {
		isClicked(mBts, OBt);
		workPiece.setMaterial(WorkPiece.Material.OTHER);
	}
	
	@FXML
	public void BHChanged(MouseEvent event) {
		
	}
	
	@FXML
	public void WeiChanged(MouseEvent event) {
		
	}
	
	@FXML
	public void calculateBtAction(MouseEvent event) {

	}

	@Override
	public void setMessege(String mess) {
		// TODO Auto-generated method stub
		
	}
	
}
