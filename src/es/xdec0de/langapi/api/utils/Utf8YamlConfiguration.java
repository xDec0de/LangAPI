package es.xdec0de.langapi.api.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import com.google.common.base.Charsets;
import com.google.common.io.Files;

/**
 * A class to manage utf-8 files.
 * 
 * @since LangAPI v1.0
 */
public class Utf8YamlConfiguration extends YamlConfiguration {

	/**
	 * Saves a file with utf-8 encoding.
	 * 
	 * @param file
	 * The file that will be saved.
	 * 
	 * @since LangAPI v1.0
	 */
    @Override
    public void save(File file) throws IOException {
        Validate.notNull(file, "File cannot be null");
        Files.createParentDirs(file);
        String data = this.saveToString();
        Writer writer = new OutputStreamWriter(new FileOutputStream(file), Charsets.UTF_8);
        try {
            writer.write(data);
        } finally {
            writer.close();
        }
    }
    
    /**
	 * Loads a file with utf-8 encoding.
	 * 
	 * @param file
	 * The file that will be loaded.
	 * 
	 * @since LangAPI v1.0
	 */
    @Override
    public void load(File file) throws FileNotFoundException, IOException, InvalidConfigurationException {
        Validate.notNull(file, "File cannot be null");
        this.load(new InputStreamReader(new FileInputStream(file), Charsets.UTF_8));
    }
}