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
		etc.getLoader().addListener(PluginLoader.Hook.COMMAND, listener, this, PluginListener.Priority.MEDIUM);
	}
	public void enable(){
		try {
			Messages.load();
		} catch (Exception e) {
			log.log(Level.SEVERE, "[rMotD]: Exception while loading properties file.", e);
		}
		
		etc.getInstance().addCommand("/grouptell", "Tell members of a group something.");
		log.info("[rMotD] Loaded!");
	}
	public void disable(){
		/* Messages.save(); */
		etc.getInstance().removeCommand("/grouptell");
		log.info("[rMotD] Disabled!");
	} 
	
	public String parseMessage(String message, String [] replace, String[] with){
		String parsed = new String();
		for(int i = 0; i < replace.length; i++)
			parsed = message.replaceAll(replace[i], with[i]);
		return parsed;
	}
	
	/* Sends the message string to each group named in sendToGroups */
	public void sendToGroups (String [] sendToGroups, String message) {
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
	
	public void sendToGroup(String sendToGroup, String message) {
		String [] arrayOfOne = new String[1];
		arrayOfOne[0] = sendToGroup;
		sendToGroups(arrayOfOne, message);
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
					String [] split =  sendToGroups_Message.split(":");
					String [] options =  split[1].split(",");
					boolean hookValid = false;
					if (split[1].isEmpty()){
						hookValid = true;
					} else for (int i = 0; i <options.length && hookValid == false; i++){
						if(options[i].equalsIgnoreCase("onlogin")) hookValid = true;
					}
					if (hookValid) {
						String message = split[2];
						/* Linebreaks! */
						String[] replace = new String[1];
						String[] with = new String[1];
						replace[0] = "@"; with[0] = ("\n");
						parseMessage(message, replace, with);
	
						sendMessage(message, triggerMessage, split[0]);
					}
				}
			}
		}
		
		public void sendMessage(String message, Player triggerMessage, String Groups){
			/* Send to player, or send to groups */
			if (Groups.isEmpty()) {
				triggerMessage.sendMessage(message);
			}
			else {
				String [] sendToGroups = Groups.split(",");
				sendToGroups(sendToGroups, message);
			}
		}
		
		public boolean onCommand(Player player, String[] split){
			if (!player.canUseCommand(split[0]))
	            return false;
	        
	        if (split[0].equalsIgnoreCase("/grouptell")){
	        	Group iShouldExist;
	        	if ((iShouldExist = etc.getDataSource().getGroup(split[1])) != null) {
		        	String tag =  "<" + player.getColor() + player.getName() + Colors.White + " to §" + iShouldExist.Prefix.charAt(0) + iShouldExist.Name + Colors.White + "> ";
		        	String message = tag + etc.combineSplit(2, split, " ");
		        	sendToGroup(split[1], message);
	        	} else {
	        		player.sendMessage(Colors.Red + "Invalid group name!");
	        	}
	        	return true;
	        }
			return false; 
		}
	}
}
