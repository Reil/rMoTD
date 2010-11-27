public class rMotD extends Plugin {
	PropertiesFile Messages;
	PluginListener listener = new rMotDListener();
	public void initialize(){
		Messages = new PropertiesFile("rMotD.properties");
		etc.getLoader().addListener(PluginLoader.Hook.COMMAND, listener, this, PluginListener.Priority.MEDIUM);
	}
	
	public void enable(){
		
	}
	public void disable(){
		Messages.save();
	}
	private class rMotDListener extends PluginListener {
		
	}
}
