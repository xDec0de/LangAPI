package es.xdec0de.langapi.utils.files;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {
	
	private Config cfg;
	private Players players;
	
	public File copyInputStreamToFile(String path, InputStream inputStream) {
		File file = new File(path);
	    try (FileOutputStream outputStream = new FileOutputStream(file)) {
	    	int read;
	        byte[] bytes = new byte[1024];
	        while ((read = inputStream.read(bytes)) != -1) {
	        	outputStream.write(bytes, 0, read);
	        }
	        return file;
	    } catch (IOException e) {
	    	e.printStackTrace();
			return null;
		}
	}
	
	public void setupConfig() {
		cfg = new Config();
	}
	
	public Config getConfig() {
		return cfg;
	}
	
	public void setupPlayers() {
		players = new Players();
	}
	
	public Players getPlayers() {
		return players;
	}
}