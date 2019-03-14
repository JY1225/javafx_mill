package cn.greatoo.easymill.robot;


public class RobotActionException extends Exception {

	private static final long serialVersionUID = 1L;

	private AbstractRobot robot;
	private String errorId;
	
	public RobotActionException(final AbstractRobot robot, final String errorId) {
		this.errorId = errorId;
		this.robot = robot;
	}

	@Override
	public String getMessage() {
		return "Error during the executing of an action of: " +  ", " + super.getMessage();
	}
	
	@Override
	public String getLocalizedMessage() {
		return   "执行时异常: " +  ", errorId:" + errorId;
	}
}
