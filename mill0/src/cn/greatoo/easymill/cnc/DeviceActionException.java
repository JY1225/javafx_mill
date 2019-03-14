package cn.greatoo.easymill.cnc;


public class DeviceActionException extends Exception {

	private static final long serialVersionUID = 1L;
	
	private static final String EXCEPTION_DURING_DEVICE_ACTION = "DeviceActionException.exceptionDuringDeviceAction";
	
	private String errorId;
	
	public DeviceActionException(final String errorId) {
		this.errorId = errorId;
	}

	@Override
	public String getMessage() {
		return "Error during the executing of an action of: " + ", " + super.getMessage();
	}
	
	@Override
	public String getLocalizedMessage() {
		return "执行中的异常: " + ", errorId:" + errorId;
	}
}
