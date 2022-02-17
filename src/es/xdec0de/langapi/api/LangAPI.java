package es.xdec0de.langapi.api;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import es.xdec0de.langapi.api.utils.Utf8YamlConfiguration;
import es.xdec0de.langapi.utils.files.FileUtils;
import es.xdec0de.langapi.utils.files.enums.LAPISetting;


/**
 * Class where all files, messages and language changes are managed.
 * 
 * @author xDec0de_
 * 
 * @see #createFiles(Plugin) 
 *
 * @since LangAPI v1.0
 */
public class LangAPI extends FileUtils {

	public LangAPI() throws SecurityException {
		// TODO remove
		if(LAPI.getAPI() != null) {
			Bukkit.getServer().getPluginManager().disablePlugin(LAPI.getInstance());
			throw new SecurityException("A LangAPI object has been manually created when LAPI.getAPI() is not null.");
		}
	}
	
	private String applyColor(String message) {
		char COLOR_CHAR = '\u00A7';
		final Pattern hexPattern = Pattern.compile("#([A-Fa-f0-9]{6})");
		Matcher matcher = hexPattern.matcher(message);
		StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);
		while (matcher.find()) {
			String group = matcher.group(1);
			matcher.appendReplacement(buffer, COLOR_CHAR + "x"
					+ COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1)
					+ COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3)
					+ COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5)
					);
		}
		return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
	}

	/*
	 * 
	 * File creation
	 * 
	 */
	
	/**
	 * Generates language files depending on server settings, its important that the plugin has a lang/default.yml file, otherwise, the files won't be created. 
	 * By "generating language files" I mean, for example, creating a copy of <strong>lang/default.yml</strong> and renaming it to lang/EN_US.yml. You can also set 
	 * predefined translated files on the plugin just by creating the file itself on your lang folder. Every language file should be a exact copy of "lang/default.yml" 
	 * with the <strong>only difference</strong> being the translated messages, in other words, lang/EN_US.yml must have the <strong>same paths that lang/default.yml</strong> if you 
	 * don't want exceptions being throwed when you try to access a path that exists on lang/default.yml but doesn't exist on lang/EN_US.yml or vice versa.
	 * 
	 * @param plugin
	 * Instance of the plugin that will have the files created.
	 * 
	 * @see #updateFiles(Plugin)
	 * @see #contains(String, Plugin, Player)
	 * 
	 * @since LangAPI v1.0
	 */
	public void createFiles(Plugin plugin) {
		if(!new File(plugin.getDataFolder()+"").exists())
			new File(plugin.getDataFolder()+"").mkdir();
		if(!new File(plugin.getDataFolder(), "lang").exists())
			new File(plugin.getDataFolder(), "lang").mkdir();
		if(plugin.getResource("lang/default.yml") == null) {
			LAPI.getMessages().logCol("&6&lLangAPI&8: [&cWarning&8] &eCould not update lang files, "+plugin.getName()+""
					+ " does not contain a &clang/default.yml&e file! This is not a LangAPI nor server error"
					+ ", contact "+plugin.getDescription().getAuthors().get(0) +" &8(&e"+plugin.getName()+"'s author&8)");
			return;
		}
		for(Lang lang : getLanguagesAllowed()) {
			try {
				File file = new File(plugin.getDataFolder(), "lang/"+lang.name()+".yml");
				if(!file.exists()) {
					if(plugin.getResource("lang/"+lang.name()+".yml") != null)
						plugin.saveResource("lang/"+lang.name()+".yml", false);
					else {
						file.createNewFile();
						Utf8YamlConfiguration def = new Utf8YamlConfiguration();
						FileConfiguration filecfg = Utf8YamlConfiguration.loadConfiguration(file);
						def.load(LAPI.getFiles().copyInputStreamToFile(plugin.getDataFolder()+ "/lang/"+lang.name()+".yml", plugin.getResource("lang/default.yml")));
						for(String str : def.getKeys(true)) {
							filecfg.set(str, def.get(str));
						}
						filecfg.save(plugin.getDataFolder() + "/lang/"+lang.name()+".yml");
					}
				}
			} catch(InvalidConfigurationException | IOException ex) {
				ex.printStackTrace();
				LAPI.getMessages().logCol("&6&lLangAPI&8: [&cWarning&8] &eCould not create &6"+lang.name()+".yml"+"&e file!");
			}
		}
	}

	/*
	 * 
	 * File updaters
	 * 
	 */

	/**
	 * This method will add all missing values to every language file selected on the server and the default language file, its important that the plugin has 
	 * a <strong>"lang/default.yml" file</strong>, otherwise, files won't be updated, also, files should be created first with <strong>{@link #createFiles(Plugin)}</strong>
	 * 
	 * @param plugin
	 * Instance of the plugin that will have the files updated.
	 * 
	 * @see #createFiles(Plugin)
	 * @see #updateFiles(Plugin, List)
	 * 
	 * @since LangAPI v1.0
	 */
	public void updateFiles(Plugin plugin) {
		if(plugin.getResource("lang/default.yml") == null) {
			LAPI.getMessages().logCol("&6&lLangAPI&8: [&cWarning&8] &eCould not update lang files, "+plugin.getName()+""
					+ " does not contain a &clang/default.yml&e file! This is not a LangAPI nor server error"
					+ ", contact "+plugin.getDescription().getAuthors().get(0) +" &8(&e"+plugin.getName()+"'s author&8)");
			return;
		}
		for(Lang lang : getLanguagesAllowed()) {
			try {
				File file = new File(plugin.getDataFolder(), "lang/"+lang.name()+".yml");
				FileConfiguration filecfg = Utf8YamlConfiguration.loadConfiguration(file);
				Utf8YamlConfiguration def = new Utf8YamlConfiguration();
				if(plugin.getResource("lang/"+lang.name()+".yml") != null)
					def.load(LAPI.getFiles().copyInputStreamToFile(plugin.getDataFolder()+ "/lang/"+lang.name()+".yml", plugin.getResource("lang/"+lang.name()+".yml")));
				else
					def.load(LAPI.getFiles().copyInputStreamToFile(plugin.getDataFolder()+ "/lang/"+lang.name()+".yml", plugin.getResource("lang/default.yml")));
				for(String str : def.getKeys(true))
					if(!filecfg.getKeys(true).contains(str))
						filecfg.set(str, def.get(str));
				filecfg.save(plugin.getDataFolder() + "/lang/"+lang.name()+".yml");
			} catch(InvalidConfigurationException | IOException ex) {
				LAPI.getMessages().logCol("&6&lLangAPI: &8[&cWarning&8] &eCould not update &6"+lang.name()+".yml"+"&e file!");
			}
		}
	}

	/**
	 * This method will add all missing values to every language file selected on the server ignoring the paths specified, its important that the plugin has 
	 * a <strong>"lang/default.yml" file</strong>, otherwise, files won't be updated, also, files must be created first with <strong>{@link #createFiles(Plugin)}</strong>
	 * 
	 * @param plugin
	 * Instance of the plugin that will have the files updated.
	 * 
	 * @param ignoredPaths
	 * The paths that the updater will skip, useful if you have default values that can be removed by the user, this values will be generated anyways on {@link #createFiles(Plugin)}
	 * if the file doesn't exist.
	 * 
	 * @see #createFiles(Plugin)
	 * @see #updateFiles(Plugin)
	 * 
	 * @since LangAPI v1.0
	 */
	public void updateFiles(Plugin plugin, List<String> ignoredPaths) {
		if(plugin.getResource("lang/default.yml") == null) {
			LAPI.getMessages().logCol("&6&lLangAPI&8: [&cWarning&8] &eCould not update lang files, "+plugin.getName()+""
					+ " does not contain a &clang/default.yml&e file! This is not a LangAPI nor server error"
					+ ", contact "+plugin.getDescription().getAuthors().get(0) +" &8(&e"+plugin.getName()+"'s author&8)");
			return;
		}
		for(Lang lang : getLanguagesAllowed()) {
			try {
				File file = new File(plugin.getDataFolder(), "lang/"+lang.name()+".yml");
				FileConfiguration filecfg = Utf8YamlConfiguration.loadConfiguration(file);
				Utf8YamlConfiguration def = new Utf8YamlConfiguration();
				if(plugin.getResource("lang/"+lang.name()+".yml") != null)
					def.load(LAPI.getFiles().copyInputStreamToFile(plugin.getDataFolder()+ "/lang/"+lang.name()+".yml", plugin.getResource("lang/"+lang.name()+".yml")));
				else
					def.load(LAPI.getFiles().copyInputStreamToFile(plugin.getDataFolder()+ "/lang/"+lang.name()+".yml", plugin.getResource("lang/default.yml")));
				for(String str : def.getKeys(true))
					if(!ignoredPaths.contains(str) && !filecfg.getKeys(true).contains(str))
						filecfg.set(str, def.get(str));
				filecfg.save(plugin.getDataFolder() + "/lang/"+lang.name()+".yml");
			} catch(InvalidConfigurationException | IOException ex) {
				LAPI.getMessages().logCol("&6&lLangAPI: &8[&cWarning&8] &eCould not update &6"+lang.name()+".yml"+"&e file!");
			}
		}
	}

	/*
	 * 
	 * File reloader
	 * 
	 */

	/**
	 * This method will reload all the language files selected on the server, files must exist for this to work ({@link #createFiles(Plugin)}).
	 * 
	 * @param plugin
	 * Instance of the plugin that will have the files reloaded.
	 * 
	 * @see #createFiles(Plugin)
	 * @see #updateFiles(Plugin)
	 * 
	 * @since LangAPI v1.0
	 */
	public void reloadFiles(Plugin plugin) {
		for(Lang lang : getLanguagesAllowed()) {
			try {
				File file = new File(plugin.getDataFolder(), "lang/"+lang.name()+".yml");
				FileConfiguration filecfg = Utf8YamlConfiguration.loadConfiguration(file);
				filecfg.load(file);
			} catch(InvalidConfigurationException | IOException ex) {
				LAPI.getMessages().logCol("&6&lLangAPI: &8[&cWarning&8] &eCould not reload &6"+lang.name()+".yml"+"&e file!");
			}
		}
	}

	/*
	 * 
	 * File getters
	 * 
	 */
	
	/**
	 * Returns all the language files selected on the server as a {@link File} list.
	 * 
	 * @param plugin
	 * Instance of the plugin that LangAPI will get the files from.
	 * 
	 * @return
	 * A list of files of every server selected language as a {@link File} list.
	 * 
	 * @see #createFiles(Plugin)
	 * 
	 * @since LangAPI v1.0
	 */
	public List<File> getFiles(Plugin plugin) {
		List<File> files = new ArrayList<File>();
		getLanguagesAllowed().stream().forEach(lang -> files.add(new File(plugin.getDataFolder(), "lang/"+lang.name()+".yml")));
		return files;
	}

	/**
	 * Returns all the language files selected on the server as a {@link FileConfiguration} list.
	 * 
	 * @param plugin
	 * Instance of the plugin that LangAPI will get the files from.
	 * 
	 * @return
	 * A list of files of every server selected language as a {@link FileConfiguration} list.
	 * 
	 * @see #createFiles(Plugin)
	 * 
	 * @since LangAPI v1.0
	 */
	public List<FileConfiguration> getConfFiles(Plugin plugin) {
		List<FileConfiguration> files = new ArrayList<FileConfiguration>();
		getFiles(plugin).stream().forEach(file -> files.add(Utf8YamlConfiguration.loadConfiguration(file)));
		return files;
	}

	/**
	 * Returns the language file of the selected {@link Lang} if if {@link #isAllowed(Lang)} returns true.
	 * 
	 * @param plugin
	 * Instance of the plugin that LangAPI will get the file from.
	 * 
	 * @param lang
	 * The {@link Lang} LangAPI will get the file from, {@link #getReplacement(Lang)} will be called on this argument.
	 * 
	 * @return
	 * The {@link File} that corresponds to the selected {@link Lang} if {@link #isAllowed(Lang)} returns true, otherwise, null will be returned.
	 * 
	 * @since LangAPI v1.0
	 */
	public File getFile(Plugin plugin, Lang lang) {
		return isAllowed(lang) ? new File(plugin.getDataFolder(), "lang/"+getReplacement(lang).name()+".yml") : null;
	}

	/**
	 * Returns the {@link FileConfiguration} of the selected {@link Lang} if {@link #isAllowed(Lang)} returns true.
	 * 
	 * @param plugin
	 * Instance of the plugin that LangAPI will get the file from.
	 * 
	 * @param lang
	 * The {@link Lang} LangAPI will get the file from, {@link #getReplacement(Lang)} will be called on this argument.
	 * 
	 * @return
	 * The {@link FileConfiguration} that corresponds to the specified {@link Lang} if {@link #isAllowed(Lang)} returns true, otherwise, null will be returned.
	 * 
	 * @since LangAPI v1.0
	 */
	public FileConfiguration getConfFile(Plugin plugin, Lang lang) {
		return isAllowed(lang) ? Utf8YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "lang/"+getReplacement(lang).name()+".yml")) : null;
	}

	/**
	 * Returns the language file of the selected {@link Player}.
	 * 
	 * @param plugin
	 * Instance of the plugin that LangAPI will get the file from.
	 * 
	 * @param player
	 * The {@link Player} LangAPI will get the file from.
	 * 
	 * @return
	 * The {@link File} that corresponds to the selected {@link Player}.
	 * 
	 * @since LangAPI v1.0
	 */
	public File getFile(Plugin plugin, LangPlayer player) {
		return new File(plugin.getDataFolder(), "lang/"+player.getLang().name()+".yml");
	}

	/**
	 * Returns a {@link FileConfiguration} loaded by {@link Utf8YamlConfiguration} that corresponds to the specified {@link Player}.
	 * 
	 * @param plugin
	 * Instance of the plugin that LangAPI will get the file from.
	 * 
	 * @param player
	 * The {@link Player} LangAPI will get the file from.
	 * 
	 * @return
	 * The {@link FileConfiguration} loaded by {@link Utf8YamlConfiguration} that corresponds to the specified {@link Player}.
	 * 
	 * @since LangAPI v1.0
	 */
	public FileConfiguration getConfFile(Plugin plugin, Player player) {
		return Utf8YamlConfiguration.loadConfiguration(getFile(plugin, player));
	}

	/**
	 * Returns the language file of the selected {@link CommandSender}, server's default language file will be returned if the {@link CommandSender} is not a {@link Player}.
	 * 
	 * @param plugin
	 * Instance of the plugin that LangAPI will get the file from.
	 * 
	 * @param player
	 * The {@link Player} LangAPI will get the file from.
	 * 
	 * @return
	 * The {@link File} that corresponds to the selected {@link Player}.
	 * 
	 * @since LangAPI v1.0
	 */
	public File getFile(Plugin plugin, CommandSender sender) {
		return sender instanceof Player ? getFile(plugin, (Player)sender) : getDefaultFile(plugin);
	}

	/**
	 * Returns a {@link FileConfiguration} loaded by {@link Utf8YamlConfiguration} of {@link CommandSender}'s language, server's default language file will be returned if the {@link CommandSender} is not a {@link Player}.
	 * 
	 * @param plugin
	 * Instance of the plugin that LangAPI will get the file from.
	 * 
	 * @param player
	 * The {@link Player} LangAPI will get the file from.
	 * 
	 * @return
	 * The {@link File} that corresponds to the selected {@link Player}.
	 * 
	 * @since LangAPI v1.0
	 */
	public FileConfiguration getConfFile(Plugin plugin, CommandSender sender) {
		return Utf8YamlConfiguration.loadConfiguration(getFile(plugin, sender));
	}

	/**
	 * Returns the default language file selected on the server.
	 * 
	 * @param plugin
	 * Instance of the plugin that LangAPI will get the file from.
	 * 
	 * @return
	 * The {@link File} that corresponds to the default language file selected on the server.
	 * 
	 * @since LangAPI v1.0
	 */
	public File getDefaultFile(Plugin plugin) {
		return new File(plugin.getDataFolder(), "lang/"+getDefaultLanguage().name()+".yml");
	}

	/**
	 * Returns the default language file selected on the server as a {@link FileConfiguration} loaded by {@link Utf8YamlConfiguration}.
	 * 
	 * @param plugin
	 * Instance of the plugin that LangAPI will get the file from.
	 * 
	 * @return
	 * The {@link FileConfiguration} that corresponds to the default language file selected on the server.
	 * 
	 * @since LangAPI v1.0
	 */
	public FileConfiguration getDefaultConfFile(Plugin plugin) {
		return Utf8YamlConfiguration.loadConfiguration(getDefaultFile(plugin));
	}

	/*
	 * 
	 * String getters
	 * 
	 */

	/**
	 * Gets the <strong>uncolored {@link String}</strong> on the language the player has selected
	 * 
	 * @param path
	 * The path of the string, you can use the paths of lang/default.yml, as mentioned on {@link #createFiles(Plugin)}, predefined files must have the same paths that
	 * the default file.
	 * 
	 * @param plugin
	 * Instance of the plugin that will have the files updated.
	 * 
	 * @param player
	 * The player that LangAPI will get the language from.
	 * 
	 * @return
	 * Uncolored {@link String} depending on player's language.
	 * 
	 * @see #getString(String, Plugin, Player)
	 * 
	 * @since LangAPI v1.0
	 */
	public String getUncoloredString(String path, Plugin plugin, Player player) {
		return getConfFile(plugin, player).getString(path);
	}

	/**
	 * Gets the <strong>colored string</strong> on the language the player has selected.
	 * 
	 * @param path
	 * The path of the string, you can use the paths of lang/default.yml, as mentioned on {@link #createFiles(Plugin)}, predefined files must have the same paths that
	 * the default file.
	 * 
	 * @param plugin
	 * Instance of the plugin that will have the files updated.
	 * 
	 * @param player
	 * The player that LangAPI will get the language from.
	 * 
	 * @return
	 * Colored string depending on player's language.
	 * 
	 * @see #getUncoloredString(String, Plugin, Player)
	 * 
	 * @since LangAPI v1.0
	 */
	public String getString(String path, Plugin plugin, Player player) {
		return applyColor(getUncoloredString(path, plugin, player));
	}

	/**
	 * Gets the uncolored string on the language the {@link CommandSender} has selected, server's default language file will be used if the {@link CommandSender} is not a {@link Player}.
	 * 
	 * @param path
	 * The path of the string, you can use the paths of lang/default.yml, as mentioned on {@link #createFiles(Plugin)}, predefined files must have the same paths that
	 * the default file.
	 * 
	 * @param plugin
	 * Instance of the plugin that will have the files updated.
	 * 
	 * @param sender
	 * The sender that LangAPI will get the language from, if the sender is not a player, the default language will be used.
	 * 
	 * @return
	 * Uncolored string depending on sender's language.
	 * 
	 * @see #getString(String, Plugin, CommandSender)
	 * 
	 * @since LangAPI v1.0
	 */
	public String getUncoloredString(String path, Plugin plugin, CommandSender sender) {
		return getConfFile(plugin, sender).getString(path);
	}

	/**
	 * Gets the colored string on the language the {@link CommandSender} has selected, server's default language file will be used if the {@link CommandSender} is not a {@link Player}.
	 * 
	 * @param path
	 * The path of the string, you can use the paths of lang/default.yml, as mentioned on {@link #createFiles(Plugin)}, predefined files must have the same paths that
	 * the default file.
	 * 
	 * @param plugin
	 * Instance of the plugin that will have the files updated.
	 * 
	 * @param sender
	 * The sender that LangAPI will get the language from, if the sender is not a player, the default language will be used.
	 * 
	 * @return
	 * Colored string depending on sender's language.
	 * 
	 * @see #getUncoloredString(String, Plugin, CommandSender)
	 * 
	 * @since LangAPI v1.0
	 */
	public String getString(String path, Plugin plugin, CommandSender sender) {
		return applyColor(getUncoloredString(path, plugin, sender));
	}

	/**
	 * Gets the uncolored string list on the language the {@link Player} has selected.
	 * 
	 * @param path
	 * The path of the string, you can use the paths of lang/default.yml, as mentioned on {@link #createFiles(Plugin)}, predefined files must have the same paths that
	 * the default file.
	 * 
	 * @param plugin
	 * Instance of the plugin that will have the files updated.
	 * 
	 * @param player
	 * The sender that LangAPI will get the language from.
	 * 
	 * @return
	 * An uncolored List<String> depending on player's language.
	 * 
	 * @see #getStringList(String, Plugin, Player)
	 * 
	 * @since LangAPI v1.0
	 */
	public List<String> getUncoloredStringList(String path, Plugin plugin, Player player) {
		return getConfFile(plugin, player).getStringList(path);
	}

	/**
	 * Gets the colored string list on the language the {@link Player} has selected.
	 * 
	 * @param path
	 * The path of the string, you can use the paths of lang/default.yml, as mentioned on {@link #createFiles(Plugin)}, predefined files must have the same paths that
	 * the default file.
	 * 
	 * @param plugin
	 * Instance of the plugin that will have the files updated.
	 * 
	 * @param player
	 * The sender that LangAPI will get the language from.
	 * 
	 * @return
	 * A colored List<String> depending on player's language.
	 * 
	 * @see #getStringList(String, Plugin, Player)
	 * 
	 * @since LangAPI v1.0
	 */
	public List<String> getStringList(String path, Plugin plugin, Player player) {
		List<String> list = new ArrayList<String>();
		for(String str : getStringList(path, plugin, player)) {
			list.add(applyColor(str));
		}
		return list;
	}

	/**
	 * Gets the uncolored string list on the language the {@link CommandSender} has selected, server's default language file will be used if the {@link CommandSender} is not a {@link Player}.
	 * 
	 * @param path
	 * The path of the string, you can use the paths of lang/default.yml, as mentioned on {@link #createFiles(Plugin)}, predefined files must have the same paths that
	 * the default file.
	 * 
	 * @param plugin
	 * Instance of the plugin that will have the files updated.
	 * 
	 * @param sender
	 * The sender that LangAPI will get the language from, if the {@link CommandSender} is not a {@link Player}, the default language will be used.
	 * 
	 * @return
	 * An uncolored List<String> depending on sender's language.
	 * 
	 * @see #getStringList(String, Plugin, CommandSender)
	 * 
	 * @since LangAPI v1.0
	 */
	public List<String> getUncoloredStringList(String path, Plugin plugin, CommandSender sender) {
		return getConfFile(plugin, sender).getStringList(path);
	}

	/**
	 * Gets the colored string list on the language the {@link CommandSender} has selected, server's default language file will be used if the {@link CommandSender} is not a {@link Player}.
	 * 
	 * @param path
	 * The path of the string, you can use the paths of lang/default.yml, as mentioned on {@link #createFiles(Plugin)}, predefined files must have the same paths that
	 * the default file.
	 * 
	 * @param plugin
	 * Instance of the plugin that will have the files updated.
	 * 
	 * @param sender
	 * The sender that LangAPI will get the language from, if the {@link CommandSender} is not a {@link Player}, the default language will be used.
	 * 
	 * @return
	 * A colored List<String> depending on sender's language.
	 * 
	 * @see #getUncoloredStringList(String, Plugin, CommandSender)
	 * 
	 * @since LangAPI v1.0
	 */
	public List<String> getStringList(String path, Plugin plugin, CommandSender sender) {
		List<String> list = new ArrayList<String>();
		for(String str : getStringList(path, plugin, sender)) {
			list.add(applyColor(str));
		}
		return list;
	}

	/**
	 * Gets the <strong>boolean</strong> from the specified <strong>path</strong> on the language the {@link Player} has selected.
	 * 
	 * @param path
	 * The path of the boolean, you can use the paths of lang/default.yml, as mentioned on {@link #createFiles(Plugin)}, predefined files must have the same paths that
	 * the default file.
	 * 
	 * @param plugin
	 * Instance of the plugin that will have the files updated.
	 * 
	 * @param player
	 * The player that LangAPI will get the language from.
	 * 
	 * @return
	 * boolean from the file depending on {@link Player}'s language.
	 * 
	 * @see #getFile(Plugin, Player)
	 * 
	 * @since LangAPI v1.0
	 */
	public boolean getBoolean(String path, Plugin plugin, Player player) {
		return getConfFile(plugin, player).getBoolean(path);
	}

	/**
	 * Gets the <strong>boolean</strong> from the specified <strong>path</strong> on the language the {@link CommandSender} has selected, server's default language file will be used if the {@link CommandSender} is not a {@link Player}.
	 * 
	 * @param path
	 * The path of the boolean, you can use the paths of lang/default.yml, as mentioned on {@link #createFiles(Plugin)}, predefined files must have the same paths that
	 * the default file.
	 * 
	 * @param plugin
	 * Instance of the plugin that will have the files updated.
	 * 
	 * @param sender
	 * The player that LangAPI will get the language from.
	 * 
	 * @return
	 * boolean from the file depending on {@link CommandSender}'s language, if the {@link CommandSender} is not a {@link Player}, the default language will be used.
	 * 
	 * @see #getFile(Plugin, Player)
	 * 
	 * @since LangAPI v1.0
	 */
	public boolean getBoolean(String path, Plugin plugin, CommandSender sender) {
		return sender instanceof Player ? getBoolean(path, plugin, (Player)sender) : getDefaultConfFile(plugin).getBoolean(path);
	}

	/**
	 * Gets the <strong>int</strong> from the specified <strong>path</strong> on the language the {@link Player} has selected.
	 * 
	 * @param path
	 * The path of the int, you can use the paths of lang/default.yml, as mentioned on {@link #createFiles(Plugin)}, predefined files must have the same paths that
	 * the default file.
	 * 
	 * @param plugin
	 * Instance of the plugin that will have the files updated.
	 * 
	 * @param player
	 * The player that LangAPI will get the language from.
	 * 
	 * @return
	 * int from the file depending on {@link Player}'s language.
	 * 
	 * @see #getFile(Plugin, Player)
	 * 
	 * @since LangAPI v1.0
	 */
	public int getInt(String path, Plugin plugin, Player player) {
		return getConfFile(plugin, player).getInt(path);
	}

	/**
	 * Gets the <strong>int</strong> from the specified <strong>path</strong> on the language the {@link CommandSender} has selected, server's default language file will be used if the {@link CommandSender} is not a {@link Player}.
	 * 
	 * @param path
	 * The path of the boolean, you can use the paths of lang/default.yml, as mentioned on {@link #createFiles(Plugin)}, predefined files must have the same paths that
	 * the default file.
	 * 
	 * @param plugin
	 * Instance of the plugin that will have the files updated.
	 * 
	 * @param player
	 * The player that LangAPI will get the language from.
	 * 
	 * @return
	 * int from the file depending on {@link CommandSender}'s language.
	 * 
	 * @see #getFile(Plugin, Player)
	 * 
	 * @since LangAPI v1.0
	 */
	public int getInt(String path, Plugin plugin, CommandSender sender) {
		return sender instanceof Player ? getInt(path, plugin, (Player)sender) : getDefaultConfFile(plugin).getInt(path);
	}

	/**
	 * Checks if a path exists on the language file the {@link CommandSender} has selected, server's default language file will be used if the {@link CommandSender} is not a {@link Player}.
	 * 
	 * @param path
	 * The path that will be checked, you can use the paths of lang/default.yml, as mentioned on {@link #createFiles(Plugin)}, predefined files must have the same paths that
	 * the default file.
	 * 
	 * @param plugin
	 * Instance of the plugin that LangAPI will get the file from.
	 * 
	 * @param player
	 * The player that LangAPI will get the language from.
	 * 
	 * @return
	 * A colored List<String> depending on sender's language.
	 * 
	 * @see #getUncoloredStringList(String, Plugin, CommandSender)
	 * 
	 * @since LangAPI v1.0
	 */
	public boolean contains(String path, Plugin plugin, LangPlayer player) {
		Configuration file = Utf8YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "lang/"+player.getLang().name()+".yml"));
		return file.contains(path);
	}

	/**
	 * Gets server's default language, this is defined on LangAPI's config.yml.
	 * 
	 * @since LangAPI v1.0
	 */
	public Lang getDefaultLanguage() {
		return Lang.valueOf(LAPI.getFiles().getConfig().getString(LAPISetting.DEFAULT_LANG));
	}

	/**
	 * Gets the replacement of a language, this is defined on LangAPI's config.yml.
	 * 
	 * @param language
	 * The language that will be checked.
	 * 
	 * @return
	 * The default server language.
	 * 
	 * @since LangAPI v1.0
	 */
	public Lang getReplacement(Lang language) {
		if(LAPI.getFiles().getConfig().getBoolean(LAPISetting.REPLACE_ENABLED))
			if(!LAPI.getFiles().getConfig().get().getString("Replace.Langs."+language.name()).equals(""))
				return Lang.valueOf(LAPI.getFiles().getConfig().get().getString("Replace.Langs."+language.name()));
		return language;
	}

	/**
	 * Gets whether a language is allowed or not on the server, this is defined on LangAPI's config.yml.
	 * 
	 * @param language
	 * The language that will be checked.
	 * 
	 * @return
	 * true if allowed, false otherwise.
	 * 
	 * @since LangAPI v1.0
	 */
	public boolean isAllowed(Lang language) {
		return LAPI.getFiles().getConfig().getBoolean(LAPISetting.ALL_LANGUAGES_ENABLED) ? true : getLanguagesAllowed().contains(language);
	}

	/**
	 * Gets the languages allowed, this is defined on LangAPI's config.yml.
	 * 
	 * @return
	 * A list of server allowed languages.
	 * 
	 * @since LangAPI v1.0
	 */
	public List<Lang> getLanguagesAllowed() {
		if(LAPI.getFiles().getConfig().getBoolean(LAPISetting.ALL_LANGUAGES_ENABLED))
			return Arrays.asList(Lang.values());
		List<Lang> allowed = new ArrayList<Lang>();
		LAPI.getFiles().getConfig().getStringList(LAPISetting.LANGS_USING).stream().forEach(lang -> allowed.add(Lang.valueOf(lang)));
		return allowed;
	}

	public List<LangPlayer> getOnlinePlayers() {
		return DataHandler.getInstance().getOnlinePlayers();
	}

	public LangPlayer getPlayer(UUID uuid) {
		return DataHandler.getInstance().getPlayer(uuid);
	}

	public LangPlayer getPlayer(Player player) {
		return DataHandler.getInstance().getPlayer(player.getUniqueId());
	}

	public LangPlayer getPlayer(CommandSender sender) {
		return sender instanceof Player ? DataHandler.getInstance().getPlayer(((Player)sender).getUniqueId()) : null;
	}

	public LangPlayer getPlayer(String name) {
		return DataHandler.getInstance().getPlayer(name);
	}


	public LangOfflinePlayer getOfflinePlayer(UUID uuid) {
		return DataHandler.getInstance().getOfflinePlayer(uuid);
	}
}
