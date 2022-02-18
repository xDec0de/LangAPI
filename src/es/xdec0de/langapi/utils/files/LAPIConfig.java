package es.xdec0de.langapi.utils.files;
import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import es.xdec0de.langapi.api.LAPI;

public class LAPIConfig {

	static FileConfiguration cfg;
	private static File file;

	public static void setup(boolean isByReload) {
		LAPI lapi = LAPI.getPlugin(LAPI.class);
		if (!lapi.getDataFolder().exists())
			lapi.getDataFolder().mkdir(); 
		if (!(file = new File(lapi.getDataFolder(), "config.yml")).exists())
			lapi.saveResource("config.yml", false); 
		reload(lapi, true, isByReload);
	}

	private static void reload(JavaPlugin plugin, boolean update, boolean isByReload) {
		cfg = (FileConfiguration)YamlConfiguration.loadConfiguration(file);
		if(update && update(isByReload))
			reload(plugin, false, isByReload);
	}

	/**
	 * Provides full access to config.yml, {@link LAPISetting} might be a better option if 
	 * you are only trying to read config values, this method is only here to provide 
	 * access to edit the file or to access paths manually (Not recommended as they might change).
	 * 
	 * @return The {@link FileConfiguration} used by config.yml.
	 */
	public static FileConfiguration file() {
		return cfg;
	}

	private static boolean update(boolean isByReload) {
		return false;
	}
}
