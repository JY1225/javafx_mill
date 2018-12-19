package cn.greatoo.easymill.external.communication.socket;

import cn.greatoo.easymill.process.StatusChangedEvent;
import cn.greatoo.easymill.process.StatusChangedEvent.Mode;
import cn.greatoo.easymill.robot.FanucRobot;
import cn.greatoo.easymill.ui.teach.TeachMainViewController;
import javafx.application.Platform;

public class RobotStatusChangeThread implements Runnable {

	FanucRobot roboSocketConnection;
	private int previousStatus;
	private boolean alive;

	public RobotStatusChangeThread(RobotSocketCommunication roboSocketConnection) {
		this.roboSocketConnection = new FanucRobot(roboSocketConnection);
		this.alive = true;	
	}

	@Override
	public void run() {
		while (alive) {
			System.out.println("----------------statu = statu");			
			try {
				if (roboSocketConnection != null) {
					roboSocketConnection.askStatusRest();
					int statu = roboSocketConnection.getStatus();
					System.out.println("statu = "+ statu);
					if (statu != previousStatus) {						
						previousStatus = statu;						
						roboSocketConnection.statusChanged();						
					}
				}
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {					
					interrupted();
				}
			} catch (SocketDisconnectedException e) {
				System.out.println("statu = ");
				e.printStackTrace();
			} catch (SocketResponseTimedOutException e) {
				System.out.println("statu = ");
				e.printStackTrace();
			} catch (SocketWrongResponseException e) {
				System.out.println("statu = ");
				e.printStackTrace();
			} catch (InterruptedException e) {
				System.out.println("statu = ");
				e.printStackTrace();
			}
		}
	}

	public static void statusChanged(final StatusChangedEvent e) {
		Platform.runLater(new Runnable() {
			@Override public void run() {
					switch (e.getStatusId()) {
						case StatusChangedEvent.INACTIVE:
							if (e.getMode().equals(Mode.FINISHED)) {//所有工件均已加工完成。
								TeachMainViewController.teachMainContentViewController.setMessege("所有工件均已加工完成。");								
							} else {//当前程序未激活
								TeachMainViewController.teachMainContentViewController.setMessege("当前程序未激活");
							}
							break;
						case StatusChangedEvent.STARTED:
							if (e.getProcessId()==0) {//下料
								TeachMainViewController.teachMainContentViewController.setMessege("下料");
							} else if (e.getProcessId()==1) {//上料
								TeachMainViewController.teachMainContentViewController.setMessege("上料");
							}
							break;
						case StatusChangedEvent.PREPARE_DEVICE:
							if (e.getProcessId()==0) {//准备下料
								TeachMainViewController.teachMainContentViewController.setMessege("准备下料");
							} else if (e.getProcessId()==1) {//准备上料
								TeachMainViewController.teachMainContentViewController.setMessege("准备上料");
							}
							break;
						case StatusChangedEvent.EXECUTE_TEACHED:
						case StatusChangedEvent.EXECUTE_NORMAL:
							if (e.getProcessId()==0) {//下料
								TeachMainViewController.teachMainContentViewController.setMessege("下料");
							} else if (e.getProcessId()==1) {//上料
								TeachMainViewController.teachMainContentViewController.setMessege("上料");
							}
							break;
						case StatusChangedEvent.INTERVENTION_READY:
						    if(e.getProcessId()==2) {
						       //程序被干预中断
						    TeachMainViewController.teachMainContentViewController.setMessege("程序被干预中断");
						    }
							break;
						case StatusChangedEvent.PROCESSING_STARTED://加工中
							TeachMainViewController.teachMainContentViewController.setMessege("加工中");
							break;
						case StatusChangedEvent.ENDED:
							if (e.getProcessId()==0) {//成功下料
								TeachMainViewController.teachMainContentViewController.setMessege("成功下料");
							} else if (e.getProcessId()==1) {//成功上料
								TeachMainViewController.teachMainContentViewController.setMessege("成功上料");
							}
							break;
						case StatusChangedEvent.TEACHING_NEEDED://请把机器人示教至正确位置。
							TeachMainViewController.teachMainContentViewController.setMessege("请把机器人示教至正确位置。");
							break;
						case StatusChangedEvent.TEACHING_FINISHED://位置示教正确，可继续执行
							TeachMainViewController.teachMainContentViewController.setMessege("位置示教正确，可继续执行");
							break;
						case StatusChangedEvent.PREPARE:
							TeachMainViewController.teachMainContentViewController.setMessege("启动层序");//启动层序
							break;
						default:
							throw new IllegalArgumentException("Unknown status id: " + e.getStatusId());
					}				
			}
		});
	}
	
	public void interrupted() {
		alive = false;
	}

}
