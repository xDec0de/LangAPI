package es.xdec0de.langapi.utils.files;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import es.xdec0de.langapi.LAPI;
import es.xdec0de.langapi.api.utils.Utf8YamlConfiguration;
import es.xdec0de.langapi.utils.files.enums.LAPISetting;

public class Config {

	private LAPI plugin = LAPI.getInstance();
	private FileConfiguration cfg;
	private File file;

	private final String path = "config.yml";

	protected Config() {
		if(!plugin.getDataFolder().exists()) {
			plugin.getDataFolder().mkdir();
		}
		if(!(file = new File(plugin.getDataFolder(), path)).exists()) {
			plugin.saveResource(path, false);
		}
		cfg = Utf8YamlConfiguration.loadConfiguration(file);
		update(ignoredPaths());
	}

	protected void update(List<String> ignoredPaths) {
		try {
			File file = new File(plugin.getDataFolder(), "config.yml");
			FileConfiguration filecfg = Utf8YamlConfiguration.loadConfiguration(file);
			Utf8YamlConfiguration def = new Utf8YamlConfiguration();
			if(plugin.getResource("config.yml") != null) {
				def.load(LAPI.getFiles().copyInputStreamToFile(plugin.getDataFolder()+ "/config.yml", plugin.getResource("config.yml")));
			}
			for(String str : def.getKeys(true)) {
				if(!ignoredPaths.contains(str)) {
					if(!filecfg.getKeys(true).contains(str)) {
						filecfg.set(str, def.get(str));
					}
				}
			}
			filecfg.save(plugin.getDataFolder() + "/config.yml");
		} catch(InvalidConfigurationException | IOException ex) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lLangAPI&8: [&cWarning&8] &eCould not update "+path+" file!"));
		}
	}

	private List<String> ignoredPaths() {
		List<String> paths = new ArrayList<String>();
		return paths;
	}

	public FileConfiguration get() {
		return cfg;
	}

	public void save() {
		try {
			cfg.save(file);
		} catch (IOException e) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lLangAPI&8: &8[&cWarning&8] &eCould not save "+path+".yml file!"));
		}
	}

	public void reload() {
	    cfg = Utf8YamlConfiguration.loadConfiguration(file);
	}

	public String getString(LAPISetting setting) {
		return ChatColor.translateAlternateColorCodes('&', get().getString(setting.getPath()));
	}

	public String getUncoloredString(LAPISetting setting) {
		return get().getString(setting.getPath());
	}

	public List<String> getStringList(LAPISetting setting) {
		List<String> list = new ArrayList<String>();
		for(String s : get().getStringList(setting.getPath())) {
			list.add(ChatColor.translateAlternateColorCodes('&', s));
		}
		return list;
	}

	public int getInt(LAPISetting setting) {
		return get().getInt(setting.getPath());
	}

	public long getLong(LAPISetting setting) {
		return get().getLong(setting.getPath());
	}

	public double getDouble(LAPISetting setting) {
		return get().getDouble(setting.getPath());
	}

	public boolean getBoolean(LAPISetting setting) {
		return get().getBoolean(setting.getPath());
	}
}
