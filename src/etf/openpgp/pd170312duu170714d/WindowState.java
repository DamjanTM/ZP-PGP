package etf.openpgp.pd170312duu170714d;

public abstract class WindowState {
	public enum States {
		HOME,
		RECIEVE,
		SEND_MSG,
		SEND_SGN,
		SEND_FIN
	}
	private States state;
	protected Window myWindow;
	
	public WindowState(Window win_) {
		myWindow=win_;
	}
	
	public abstract void addCenterPanel(Message msg_);
}
