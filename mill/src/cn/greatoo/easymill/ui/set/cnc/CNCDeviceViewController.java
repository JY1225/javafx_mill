package cn.greatoo.easymill.ui.set.cnc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.greatoo.easymill.db.util.DBHandler;
import cn.greatoo.easymill.entity.Clamping;
import cn.greatoo.easymill.entity.Gripper;
import cn.greatoo.easymill.entity.Program;
import cn.greatoo.easymill.ui.main.Controller;
import cn.greatoo.easymill.util.IconFlowSelector;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

public class CNCDeviceViewController  extends Controller {
	@FXML
	private GridPane gridPane;
	@FXML
	private Button LBt;
	@FXML
	private Button wBt;
	private IconFlowSelector ifsClampings;
	private static final double ICONFLOWSELECTOR_WIDTH = 530;
	List<Button> bts;
	public static Clamping clamping = new Clamping();
	
	public void init() {
		bts = new ArrayList<Button>();
		bts.add(LBt);
		bts.add(wBt);
		isClicked(bts, LBt);
		ifsClampings = new IconFlowSelector(false);
        ifsClampings.setPrefWidth(ICONFLOWSELECTOR_WIDTH);
        gridPane.add(ifsClampings, 0, 2, 2, 1);
              
        clamping.setClampingType(Clamping.ClampingType.LENGTH);
        List<Clamping> list = DBHandler.getInstance().getClampBuffer();        
        for(Clamping c:list) {
        	clamping = c;
        	if(c.getClampingType().equals(Clamping.ClampingType.LENGTH)) {
        		isClicked(bts, LBt);
			}else {
				isClicked(bts, wBt);
			}
		}  
        refresh();
	}
	@FXML
	public void LBtAction(ActionEvent event) {
		isClicked(bts, LBt);
		clamping.setClampingType(Clamping.ClampingType.LENGTH);
	}
	
	@FXML
	public void wBtAction(ActionEvent event) {
		isClicked(bts, wBt);
		clamping.setClampingType(Clamping.ClampingType.WIDTH);
	}
	@Override
	public void setMessege(String mess) {
		
	}
	public void refresh() {
		ifsClampings.clearItems();
		List<Clamping> clampings = DBHandler.getInstance().getClampBuffer();
		int itemIndex = 0;
		for (final Clamping clamping : clampings) {
			ifsClampings.addItem(itemIndex, clamping.getName(), clamping.getImageUrl(), new EventHandler<MouseEvent>() {
				@Override
				public void handle(final MouseEvent event) {
					changedGripper(clamping);					
				}
			});
			itemIndex++;
		}
		setSelectedGripper();
	}
	public void setSelectedGripper() {
		ifsClampings.deselectAll();
		ifsClampings.setSelected(clamping.getName());		
	}
	
	public void changedGripper(final Clamping clamping) {
		ifsClampings.deselectAll();
		ifsClampings.setSelected(clamping.getName());		
		this.clamping.setName(clamping.getName());
		System.out.println(clamping.getName());
	}
}
