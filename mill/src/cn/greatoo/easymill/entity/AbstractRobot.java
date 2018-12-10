package cn.greatoo.easymill.entity;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractRobot {

	public GripperBody getGripperBody() {
		final Set<GripperHead> gripperHeads = new HashSet<GripperHead>();
		GripperBody activeGripperBody = new GripperBody("name", "description", gripperHeads);
		return activeGripperBody;
	}
}
