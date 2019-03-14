package cn.greatoo.easymill.external.communication.socket;

public abstract class AbstractCommunicationException extends Exception {

	protected static final long serialVersionUID = 1L;

	@Override
	public abstract String getMessage();
	
	@Override
	public abstract String getLocalizedMessage();
}
