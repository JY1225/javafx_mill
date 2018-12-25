package cn.greatoo.easymill.ui.set.table.load;

import java.util.ArrayList;
import java.util.List;

import cn.greatoo.easymill.ui.main.Controller;
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
		// 默认选择0度按钮，Al
		//isClicked(bts, HBt);
		//isClicked(mBts, AlBt);
		
	}
	
	@FXML
	public void LChanged(MouseEvent event) {

	}
	
	@FXML
	public void HBtAction(MouseEvent event) {
		isClicked(bts, HBt);
				
	}
	
	@FXML
	public void tiltedAction(MouseEvent event) {
		isClicked(bts, tiltedBt);
				
	}
	
	@FXML
	public void TBtAction(MouseEvent event) {
		
	}
	
	@FXML
	public void VBtAction(MouseEvent event) {
		isClicked(bts, VBt);
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
	}
	
	@FXML
	public void CuBtAction(MouseEvent event) {
		isClicked(mBts, CuBt);
	}
	
	@FXML
	public void FeBtAction(MouseEvent event) {
		isClicked(mBts, FeBt);
	}
	
	@FXML
	public void OBtAction(MouseEvent event) {
		isClicked(mBts, OBt);
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
