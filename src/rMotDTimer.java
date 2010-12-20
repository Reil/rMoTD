import java.util.TimerTask;

public class rMotDTimer extends TimerTask{
	String [] Messages;
	rMotD rMotD;
	private int progression;
	private static final int random = 1;
	private static final int sequential = 0;
	
	public rMotDTimer(rMotD rMotD, String [] Messages, int progression){
		this.progression = progression;
		this.Messages = Messages;
		this.rMotD = rMotD;
	}
	
	public void run() {
		// TODO Auto-generated method stub		
	}

}
