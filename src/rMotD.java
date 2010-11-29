import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class rMotD extends Plugin {
	public PropertiesFile Messages;
	PluginListener listener = new rMotDListener();
	Logger log = Logger.getLogger("Minecraft");
	
	public rMotD () {
		Messages = new PropertiesFile("rMotD.properties");
	}
	
	public void initialize(){
		etc.getLoader().addListener(PluginLoader.Hook.LOGIN, listener, this, PluginListener.Priority.MEDIUM);
	}
	public void enable(){
		try {
			Messages.load();
		} catch (Exception e) {
			log.log(Level.SEVERE, "[rMotD]: Exception while loading properties file.", e);
		}
		
		log.info("[rMotD] Loaded!");
	}
	public void disable(){
		/* Messages.save(); */
		log.info("[rMotD] Disabled!");
	} 
	
	/* Sends the message string to each group named in sendToGroups */
	public void sendToGroups (String [] sendToGroups, String message) {
		String prompt = "Sending \"" + message + "\" to: ";
		for (String Group:sendToGroups)
			prompt = prompt + " " + Group;
		
		ArrayList <Player> sentTo = new ArrayList<Player>();
		for (Player messageMe: etc.getServer().getPlayerList()){
			boolean flag = false;
			for(String amIHere : sendToGroups) {
				if (messageMe.isInGroup(amIHere)){
					flag = true;
					break;
				}
			}
			if (flag == true) {
				messageMe.sendMessage(message);
				sentTo.add(messageMe);
			}
		}
		return;
	}
	
	public class rMotDListener extends PluginListener {
		/* Checks for any messages that the player's group memberships may trigger.
		 * Parses the message line into:
		 *  - groups to send it to (or just the player)
		 *  - options
		 *  - and the message.
		 * Sends the message on its merry way using sendToGroups.*/
		public void onLogin(Player triggerMessage){
			String [] groupArray = triggerMessage.getGroups();
			for (String groupName : groupArray){
				if (Messages.keyExists(groupName)){
					String sendToGroups_Message = Messages.getString(groupName);
					String [] Split =  sendToGroups_Message.split(":");
					String message = Split[2];
					if (Split[0].isEmpty())
						triggerMessage.sendMessage(message);
					else {
						String [] sendToGroups = Split[0].split(",");
						sendToGroups(sendToGroups, message);
					}
				}
			}
		}
	}
}
