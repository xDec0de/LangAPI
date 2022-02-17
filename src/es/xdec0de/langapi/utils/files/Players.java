package es.xdec0de.langapi.utils.files;
import java.io.File;
import java.io.IOException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import es.xdec0de.langapi.LAPI;

public class Players {

	private LAPI plugin = LAPI.getInstance();
	private FileConfiguration cfg;
	private File file;
	private final String path = "storage/players.yml";

	protected Players() {
		if(!plugin.getDataFolder().exists()) {
			plugin.getDataFolder().mkdir();
		}
		if(!(file = new File(plugin.getDataFolder(), path)).exists()) {
			plugin.saveResource(path, false);
		}
		cfg = YamlConfiguration.loadConfiguration(file);
		save();
	}

	public FileConfiguration get() {
		return cfg;
	}

	public void save() {
		try {
			cfg.save(file);
		} catch (IOException e) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lLangAPI&8: [&cWarning&8] &eCould not save "+path+".yml file!"));
		}
	}

	public void reload() {
		cfg = YamlConfiguration.loadConfiguration(file);
	}
}
