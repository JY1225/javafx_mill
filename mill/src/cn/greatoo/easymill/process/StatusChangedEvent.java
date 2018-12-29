package cn.greatoo.easymill.process;
/**
 * 0启动程序 1从table下料  2给机床上料   3机床加工   4从机床下料   5给table上料
 * 6成功上料 8请把机器人示教至正确位置  9位置示教正确，可继续执行
 * 10加工完成
 */
public class StatusChangedEvent {
	
	public static final int STARTED = 0;
	public static final int PICK_FROM_TABLE = 1;
	public static final int PUT_TO_CNC = 2;
	public static final int CNC_PROCESSING = 3;
	public static final int PICK_FROM_CNC = 4;
	public static final int PUT_TO_TABLE = 5;
	public static final int ENDED = 6;	
	public static final int EXECUTE_TEACHED = 7;	
	public static final int TEACHING_NEEDED = 8;
	public static final int TEACHING_FINISHED = 9;
	public static final int FINISHED = 10;	
	private int statusId;
	
	public StatusChangedEvent(final int statusId) {
		this.statusId = statusId;
	}
	
	public int getStatusId() {
		return statusId;
	}
}
