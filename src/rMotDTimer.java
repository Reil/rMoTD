import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class rMotDTimer extends TimerTask{
	String [] Messages;
	rMotD rMotD;
	Timer timer;
	private int progression;
	Random generator;
	private static final int random = 1;
	private static final int sequential = 0;
	int nextMessage = 0;
	int delay;
	// Listname, delay, progression
	public rMotDTimer(rMotD rMotD, Timer timer, String [] Messages){
		this.Messages = Messages;
		this.rMotD = rMotD;
		if (progression == random){
			this.generator = new Random();
			this.nextMessage =  generator.nextInt(Messages.length);
		} else if (progression != sequential){
			// Uh, throw an error here or something
		}
		String [] split =  Messages[0].split(":");
		String [] options = split[1].split(",");
		this.progression = new Integer(options[2]);
		this.delay = new Integer(options[1]);
	}
	
	public void run() {
		// parse into groups, next time, 'progression'
		String toParse = Messages[nextMessage];
		String [] split =  toParse.split(":");
		String [] options =  split[1].split(",");
		String Groups = split[0];
		try{
			delay = new Integer(options[1]);
		} catch (NumberFormatException blargh){
			rMotD.log.info("[rMotD] Invalid timer interval!");
			return;
		}
		String message = etc.combineSplit(2, split, ":");

		// Send message
		String [] sendToGroups = Groups.split(",");
		rMotD.sendToGroups(sendToGroups, message, null);
		
		// Find next sequence
		if (progression == random)
			nextMessage = generator.nextInt(Messages.length);
		else if (progression == sequential)
			nextMessage = ( nextMessage + 1 ) % Messages.length;
		else {
			// TODO: I am error!
		}
		
		// Shit bricks
		// Schedule next run
		timer.schedule(this, delay);
	}

}
