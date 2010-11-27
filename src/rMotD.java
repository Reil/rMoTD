import java.util.ArrayList;
import java.util.logging.Logger;

public class rMotD extends Plugin {
	PropertiesFile Messages;
	PluginListener listener = new rMotDListener();
	Logger log = Logger.getLogger("Minecraft");
	
	public void initialize(){
		etc.getLoader().addListener(PluginLoader.Hook.COMMAND, listener, this, PluginListener.Priority.MEDIUM);
	}
	public void enable(){
		Messages = new PropertiesFile("rMotD.properties");
		log.info("[rMotD] Loaded!");
		// etc.getInstance().addCommand("/newmessage", "Nicer formatted table of warps.");
	}
	public void disable(){
		Messages.save();
		log.info("[rMotD] Disabled!");
	}
	public Player [] sendToGroups (String [] sendToGroups, String message) {
		ArrayList <Player> sentTo = new ArrayList<Player>();
		for (Player messageMe: etc.getServer().getPlayerList()){
			boolean flag = false;
			for(String amIHere : sendToGroups) 
				if (messageMe.isInGroup(amIHere)) flag = true;
			if (flag == true) {
				messageMe.sendMessage(message);
				sentTo.add(messageMe);
			}
		}
		return (Player[]) sentTo.toArray();
	}
	
	private class rMotDListener extends PluginListener {
		public void onLogin(Player triggerMessage){
			String [] groupArray = triggerMessage.getGroups();
			for (String groupName : groupArray){
				String sendToGroups_Message = Messages.getString(groupName,":");
				String [] Split =  sendToGroups_Message.split(":");
				String [] sendToGroups = Split[0].split(",");
				String message = Split[1];
				sendToGroups(sendToGroups, message);
			}
		}
	}
}
