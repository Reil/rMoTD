import java.util.ArrayList;
import java.util.Hashtable;

public class rPropertiesFile {
	Hashtable<String,ArrayList<String>> Properties = new Hashtable<String,ArrayList<String>>();
	
	/**
     * Creates or opens a properties file using specified filename
     * 
     * @param fileName
     */
    public rPropertiesFile(String fileName) {
        this.fileName = fileName;
        File file = new File(fileName);

        if (file.exists()) {
            try {
                load();
            } catch (IOException ex) {
                log.severe("[PropertiesFile] Unable to load " + fileName + "!");
            }
        } else {
            save();
        }
    }
	
	boolean getBoolean(java.lang.String key) {
		return true; 
	}
	boolean	getBoolean(java.lang.String key, boolean value) {
		return true;
	}
	int	getInt(java.lang.String key){
		return 0;
	}
	int	getInt(java.lang.String key, int value){
		return 0;
	}
	long	getLong(java.lang.String key) {
		return 0;
	}
	long	getLong(java.lang.String key, long value){
		return 0;
	}
	java.lang.String	getString(java.lang.String key) {
		return "Hey0!";
	}
	java.lang.String	getString(java.lang.String key, java.lang.String value) {
		return "Hey0!";
	}
	boolean	keyExists(java.lang.String key) {
		return true;
	}
	void	load(){
		
	}
	void	save(){
		
	}
	void	setBoolean(java.lang.String key, boolean value) {
		
	}
	void	setInt(java.lang.String key, int value) {
		
	}
	void	setLong(java.lang.String key, long value) {
		
	}
	void	setString(java.lang.String key, java.lang.String value) {
		
	}
}
