import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class rMotD extends Plugin {
	public PropertiesFile Messages;
	PluginListener listener = new rMotDListener();
	Logger log = Logger.getLogger("Minecraft");
	String defaultGroup;
	
	public rMotD () {
		Messages = new PropertiesFile("rMotD.properties");
	}
	
	public void initialize(){
		etc.getLoader().addListener(PluginLoader.Hook.LOGIN  , listener, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.COMMAND, listener, this, PluginListener.Priority.MEDIUM);
		defaultGroup = etc.getDataSource().getDefaultGroup().Name;
	}
	public void enable(){
		try {
			Messages.load();
		} catch (Exception e) {
			log.log(Level.SEVERE, "[rMotD]: Exception while loading properties file.", e);
		}
		
		/* TODO (Efficiency): Go through each command, see if any commands actually need these listeners. */
		// Regex: ^([A-Za-z0-9,]+):([A-Za-z0-9,]*:([A-Za-z0-9,]*disconnect([A-Za-z0-9,]*)
		//   
		etc.getLoader().addListener(PluginLoader.Hook.DISCONNECT , listener, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.BAN        , listener, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.LOGINCHECK , listener, this, PluginListener.Priority.MEDIUM);
		
		etc.getInstance().addCommand("/grouptell", "Tell members of a group something.");
		log.info("[rMotD] Loaded!");
	}
	public void disable(){
		/* Messages.save(); */
		etc.getInstance().removeCommand("/grouptell");
		log.info("[rMotD] Disabled!");
	} 
	
	public void sendToGroup(String sendToGroup, String message) {
		String [] arrayOfOne = new String[1];
		arrayOfOne[0] = sendToGroup;
		sendToGroups(arrayOfOne, message);
		return;
	}
	
	/* Looks through all of the messages,
	 * Sends the messages triggered by groups which 'triggerMessage' is a member of,
	 * But only if that message has the contents of 'option' as one of its options */
	public void triggerMessagesWithOption(Player triggerMessage, String option){
		String[] eventToReplace = new String[0];
		String[] eventReplaceWith = new String[0];
		triggerMessagesWithOption(triggerMessage, option, eventToReplace, eventReplaceWith);
	}
	public void triggerMessagesWithOption(Player triggerMessage, String option, String[] eventToReplace, String[] eventReplaceWith){
		String [] groupArray;
		if (triggerMessage.hasNoGroups()){
			groupArray = new String[]{defaultGroup};
		} else {
			groupArray = triggerMessage.getGroups();
		}
		for (String groupName : groupArray){
			if (Messages.keyExists(groupName)){
				String sendToGroups_Message = Messages.getString(groupName);
				String [] split =  sendToGroups_Message.split(":");
				String [] options =  split[1].split(",");
				boolean hookValid = false;
				if (split[1].isEmpty() && option.equalsIgnoreCase("onlogin")){
					hookValid = true;
				} else for (int i = 0; i <options.length && hookValid == false; i++){
					if(options[i].equalsIgnoreCase(option)) hookValid = true;
				}
				if (hookValid) {
					String message = etc.combineSplit(2, split, ":");
					String playerList = new String();
					for (Player getName : etc.getServer().getPlayerList()){
						playerList = getName.getName() + ", " + playerList;
					}
					String [] replace = {"@"	, "<<triggerer>>"          , "<<triggerer-ip>>"    , "triggerer-color"       , "<<player-list>>"};
					String [] with    = {"\n"	, triggerMessage.getName() , triggerMessage.getIP(),triggerMessage.getColor(), playerList};					
					message = parseMessage(message, replace, with);
					/* TODO: Make some special case for "all" option. */
					sendMessage(message, triggerMessage, split[0]);
				}
			}
		}
	}
	
	
	public void sendMessage(String message, Player triggerMessage, String Groups){
		/* Default: Send to player unless groups are specified.
		 * If so, send to those instead. */
		if (Groups.isEmpty()) {
			String [] replace = {"<<recipient>>"         , "<<recipient-ip>>"    , "<<recipient-color>>"};
			String [] with    = {triggerMessage.getName(), triggerMessage.getIP(), triggerMessage.getColor()};
			message = parseMessage(message, replace, with);
			for(String send : message.split("\n"))
				triggerMessage.sendMessage(send);
		}
		else {
			String [] sendToGroups = Groups.split(",");
			sendToGroups(sendToGroups, message, triggerMessage);
		}
	}
	
	public String parseMessage(String message, String [] replace, String[] with){
		String parsed = message;
		for(int i = 0; i < replace.length; i++) {
			parsed = parsed.replaceAll(replace[i], with[i]);
		}
		return parsed;
	}
	public void sendToGroups (String [] sendToGroups, String message, Player triggerer) {
		ArrayList <String> sendToGroupsFiltered = new ArrayList<String>();
		boolean everyone = false;
		for (String group : sendToGroups){
			if (group.equalsIgnoreCase("<<triggerer>>")) {
				String [] replace = {"<<recipient>>"    , "<<recipient-ip>>", "<<recipient-color>>"};
				String [] with    = {triggerer.getName(), triggerer.getIP() , triggerer.getColor()};
				message = parseMessage(message, replace, with);
				for(String send : message.split("\n"))
					triggerer.sendMessage(send);
			} else if (group.equalsIgnoreCase("<<server>>")) {
				String [] replace = {"<<recipient>>"};
				String [] with    = {"server"};
				message = "[rMotD] " + parseMessage(message, replace, with);
				for(String send : message.split("\n"))
					log.info(send);
			} else if (group.equalsIgnoreCase("<<everyone>>")){
				for (Player messageMe : etc.getServer().getPlayerList()){
					String [] replace = {"<<recipient>>"    , "<<recipient-ip>>", "<<recipient-color>>"};
					String [] with    = {messageMe.getName(), messageMe.getIP() , messageMe.getColor()};
					message = parseMessage(message, replace, with);
					for(String send : message.split("\n"))
						messageMe.sendMessage(send);
				}
				everyone = true;
			} else {
				sendToGroupsFiltered.add(group);
			}
		}
		if (!everyone) {
			sendToGroups(sendToGroupsFiltered.toArray(new String[sendToGroupsFiltered.size()]), message);
		}
	}
	/* Sends the message string to each group named in sendToGroups */
	public void sendToGroups (String [] sendToGroups, String message) {
		ArrayList <Player> sentTo = new ArrayList<Player>();
		for (Player messageMe: etc.getServer().getPlayerList()){
			boolean flag = false;
			for(String amIHere : sendToGroups) {
				if (messageMe.isInGroup(amIHere) || (messageMe.hasNoGroups() && amIHere == defaultGroup)){
					flag = true;
					break;
				}
			}
			if (flag == true) {
				String [] replace = {"<<recipient>>"    , "<<recipient-ip>>", "<<recipient-color>>"};
				String [] with    = {messageMe.getName(), messageMe.getIP() , messageMe.getColor()};
				message = parseMessage(message, replace, with);
				for(String send : message.split("\n"))
					messageMe.sendMessage(send);
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
			triggerMessagesWithOption(triggerMessage, "onlogin");
			/* Checking duplicate logins. */
			/* int numDupes = -1;
			String triggerName = triggerMessage.getName();
			List<Player> playerList = etc.getServer().getPlayerList();
			for( Player checkDupe : playerList){
				if ( checkDupe.getName() == triggerName) {
					numDupes++;
					triggerMessage = checkDupe;
				}
			}
			if (numDupes > 0){
				String [] replaceThese = {"<<numdupes>>"};
				String [] withThese = {Integer.toString(numDupes)};
				triggerMessagesWithOption(triggerMessage, "onlogin-duplicate", replaceThese, withThese);
			}*/
			return;
		}
		
		public void onDisconnect(Player triggerMessage){
			triggerMessagesWithOption(triggerMessage, "ondisconnect");
			return;
		}
		
		public void onBan(Player mod, Player triggerMessage, java.lang.String reason) {
			String [] replaceThese = {"<<ban-reason>>", "<<ban-setter>>", "<<ban-recipient>>"     };
			String [] withThese =    {reason          , mod.getName()   , triggerMessage.getName()};
			triggerMessagesWithOption(triggerMessage, "onban", replaceThese, withThese);
		}
		/*
		public String onLoginChecks (String triggerName){
			// Checking duplicate logins. 
			int numDupes = 0;
			List<Player> playerList = etc.getServer().getPlayerList();
			Player triggerMessage = null;
			for( Player checkDupe : playerList){
				if ( checkDupe.getName() == triggerName) {
					numDupes++;
					triggerMessage = checkDupe;
				}
			}
			if (numDupes > 0){
				triggerMessagesWithOption(triggerMessage, "onlogin-duplicate");
			}
			return null;
		}*/
		
		
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
