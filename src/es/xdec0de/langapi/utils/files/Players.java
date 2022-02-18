package es.xdec0de.langapi.utils.files;
import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import es.xdec0de.langapi.api.LAPI;
import es.xdec0de.langapi.api.LangPlayer;

public class Players {

	private static FileConfiguration cfg;
	private static File file;

	public static void setup(boolean isByReload) {
		LAPI lapi = LAPI.getPlugin(LAPI.class);
		if (!lapi.getDataFolder().exists())
			lapi.getDataFolder().mkdir(); 
		if (!(file = new File(lapi.getDataFolder(), "storage/players.yml")).exists())
			lapi.saveResource("storage/players.yml", false); 
	}

	/**
	 * Provides full access to players.yml, {@link LangPlayer} might be a better option if 
	 * you are only trying to read and edit player values, this method is only here to provide 
	 * access to edit the file or to access paths manually (Not recommended as they might change).
	 * 
	 * @return The {@link FileConfiguration} used by players.yml.
	 */
	public static FileConfiguration file() {
		return cfg;
	}

	public static void save() {
		try {
			cfg.save(file);
		} catch (IOException e) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lLangAPI&8: [&cWarning&8] &eCould not save &6players.yml&e!"));
		}
	}

	public static void reload() {
		cfg = YamlConfiguration.loadConfiguration(file);
	}
}
