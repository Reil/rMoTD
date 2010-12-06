import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.logging.Logger;

public class rPropertiesFile {
	Hashtable<String,ArrayList<String>> Properties = new Hashtable<String,ArrayList<String>>();
	ArrayList<String> orders; 
	String fileName;
	Logger log = Logger.getLogger("Minecraft");

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
	long getLong(java.lang.String key) {
		return 0;
	}
	long getLong(java.lang.String key, long value){
		return 0;
	}
	
	String getString(java.lang.String key) {
		ArrayList<String> arrayList = Properties.get(key);
		return arrayList.get(0);
	}
	
	String getString(java.lang.String key, java.lang.String value) {
		if (Properties.containsKey(key)){
			ArrayList<String> arrayList = Properties.get(key);
			return arrayList.get(0);
		}
		else {
			setString(key, value); 
		}
		return value;
	}
	
	
	String [] getStrings(String key) {
		if (Properties.containsKey(key)) {
			ArrayList <String> rt = Properties.get(key);
			return rt.toArray(new String[rt.size()]);
		} else
		return null;
	}
	
	boolean	keyExists(java.lang.String key) {
		return Properties.containsKey(key);
	}
	void load() throws IOException {
		/* Go through, line by line. 
		 * If the line starts with # or !, then save the line in list
		 * If the line has an assignment, put the name here. */
		orders.clear();
		BufferedReader reader;
        reader = new BufferedReader(new FileReader(fileName));
        String line;
        while ((line = reader.readLine()) != null) {
        	if (line.startsWith("#"))
        		continue;
        	else {
        		/* TODO: Error checking */
        		String [] split = line.split("=");
        		String PropertySide = split[1];
        		String Value = etc.combineSplit(1, split, "=");
        		for (String Property : PropertySide.split(",")) {
	        		if (Properties.containsKey(Property)){
	        			Properties.get(Property).add(Value);
	        		}
	        		else {
	        			ArrayList<String> newList = new ArrayList<String>();
	        			newList.add(Value);
	        			Properties.put(Property, newList);
	        		}
        		}
        	}
        }
		
	}
	void save(){
		
	}
	void setBoolean(java.lang.String key, boolean value) {
		
	}
	void setInt(java.lang.String key, int value) {
		
	}
	void setLong(java.lang.String key, long value) {
		
	}
	void setString(java.lang.String key, java.lang.String value) {
		
	}
}
