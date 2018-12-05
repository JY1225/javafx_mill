package cn.greatoo.easymill.ui.configure.process.alarms;
	 
	import javafx.scene.control.ContextMenu;
	import javafx.scene.control.MenuItem;
	 

	public class AlarmView extends ContextMenu
	{
	
		private MenuItem settingMenuItem;
		private MenuItem updateMenuItem;
		
	    private static AlarmView INSTANCE = null;
	 
	    /**
	     * 私有构�?�函�?
	     */
	    private AlarmView()
	    {
	        settingMenuItem = new MenuItem("连接异常  CNC1");
	        updateMenuItem = new MenuItem("连接异常  CNC2");	 	 
	        getItems().add(settingMenuItem);
	        getItems().add(updateMenuItem);

	    }
	 
	    /**
	     * 获取实例
	     * @return AlarmView
	     */
	    public static AlarmView getInstance()
	    {
	        if (INSTANCE == null)
	        {
	            INSTANCE = new AlarmView();
	        }
	 
	        return INSTANCE;
	    }
	

}
