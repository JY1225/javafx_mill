package cn.greatoo.easymill.util;

import cn.greatoo.easymill.ui.controls.keyboard.KeyboardParentPresenter;
import javafx.scene.control.TextInputControl;

public interface TextInputControlListener extends KeyboardParentPresenter {

	void textFieldFocussed(TextInputControl textInputControl);
	void textFieldLostFocus(TextInputControl textInputControl);
		
}
