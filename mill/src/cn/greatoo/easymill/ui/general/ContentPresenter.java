package cn.greatoo.easymill.ui.general;

import javafx.scene.Node;

public interface ContentPresenter {
	
	void setActive(boolean active);
	Node getView();
}
