package es.xdec0de.langapi.api;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import es.xdec0de.langapi.ProtocolChecker;
import es.xdec0de.langapi.api.gui.LangGUI;
import es.xdec0de.langapi.cmd.LangCMD;
import es.xdec0de.langapi.utils.LAPIMessageUtils;
import es.xdec0de.langapi.utils.files.FileUtils;
import es.xdec0de.langapi.utils.files.enums.LAPISetting;

public class LAPI extends JavaPlugin {

	private static LAPI instance;
	private static Connection connection;

	private static FileUtils files;
	private static LangAPI api;
	private static LAPIMessageUtils msgUtils;
	//private static UpdateChecker updater;

	public void onEnable() {
		executeEnable();
		msgUtils.log(" ");
		msgUtils.logCol("&8|------------------------------------------>");
		msgUtils.log(" ");
		msgUtils.logCol("             &e&lLangAPI &8- &aEnabled");
		msgUtils.log(" ");
		msgUtils.logCol("  &b- &7Author&8: &bxDec0de_");
		msgUtils.log(" ");
		msgUtils.logCol("  &b- &7Version: &b"+instance.getDescription().getVersion());
		msgUtils.log(" ");
		msgUtils.logCol("&8|------------------------------------------>");
		checkDependencies();
		//checkUpdates();
	}

	public void onDisable() {
		msgUtils.log(" ");
		msgUtils.logCol("&8|------------------------------------------>");
		msgUtils.log(" ");
		msgUtils.logCol("             &e&lLangAPI &8- &cDisabled");
		msgUtils.log(" ");
		msgUtils.logCol("  &b- &7Author&8: &bxDec0de_");
		msgUtils.log(" ");
		msgUtils.logCol("  &b- &7Version: &b"+instance.getDescription().getVersion());
		msgUtils.log(" ");
		msgUtils.logCol("&8|------------------------------------------>");
		msgUtils.log(" ");
		DataHandler.getInstance().saveAll();
	}

	private void executeEnable() {
		instance = this;
		files = new FileUtils();
		files.setupConfig();
		if(files.getConfig().getBoolean(LAPISetting.MYSQL_ENABLED) && !connectMySQL()) {
			if(connectMySQL())
				return;
		} else
			files.setupPlayers();
		msgUtils = new LAPIMessageUtils();
		LangAPI tempAPI = new LangAPI();
		api = tempAPI;
		api.createFiles(instance);
		api.updateFiles(instance, ignoredMessages());
		getCommand("lang").setExecutor(new LangCMD());
		getServer().getPluginManager().registerEvents(DataHandler.getInstance(), this);
		getServer().getPluginManager().registerEvents(new LangGUI(), this);
		//getServer().getPluginManager().registerEvents(updater = new UpdateChecker(), this);
		int dbDelay = files.getConfig().getInt(LAPISetting.MYSQL_CACHE_TIME) * 20;
		Bukkit.getScheduler().runTaskTimerAsynchronously(getPlugin(LAPI.class), () -> DataHandler.getInstance().saveAll(), dbDelay, dbDelay);
	}

	private List<String> ignoredMessages() {
		List<String> ignored = new ArrayList<String>();
		ignored.add("Commands.Lang.GUI.Fill.1");
		ignored.add("Commands.Lang.GUI.Fill.2");
		ignored.add("Commands.Lang.GUI.Fill.3");
		ignored.add("Commands.Lang.GUI.Items.Spanish");
		ignored.add("Commands.Lang.GUI.Items.Spanish.Name");
		ignored.add("Commands.Lang.GUI.Items.Spanish.Lore");
		ignored.add("Commands.Lang.GUI.Items.English");
		ignored.add("Commands.Lang.GUI.Items.English.Name");
		ignored.add("Commands.Lang.GUI.Items.English.Lore");
		return ignored;
	}

	private void checkDependencies() {
		msgUtils.log(" ");
		if(Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
			ProtocolChecker.addLangCheck();
			msgUtils.logCol("  &e- &bProtocolLib &7detected (&av" + Bukkit.getPluginManager().getPlugin("ProtocolLib").getDescription().getVersion() + "&7)");
		} else {
			if(files.getConfig().getBoolean(LAPISetting.AUTOSELECT_ENABLED)) {
				files.getConfig().get().set(LAPISetting.AUTOSELECT_ENABLED.getPath(), false);
				files.getConfig().save();
				files.getConfig().reload();
			}
			msgUtils.logCol("  &e- ProtocolLib &cnot detected, &6language detection&c won't work.");
		}
		if(Bukkit.getPluginManager().isPluginEnabled("HeadDatabase")) {
			msgUtils.logCol("  &e- &bHeadDatabase &7detected (&av" + Bukkit.getPluginManager().getPlugin("HeadDatabase").getDescription().getVersion() + "&7)");
		}
		msgUtils.log(" ");
	}

	private boolean connectMySQL() {
		synchronized (this) {
			try {
				if(connection == null || connection.isClosed()) { 
					Class.forName("com.mysql.jdbc.Driver");
					connection = (Connection)DriverManager.getConnection("jdbc:mysql://" + files.getConfig().getString(LAPISetting.MYSQL_HOST) + ":" + files.getConfig().getInt(LAPISetting.MYSQL_PORT) + "/" + files.getConfig().getString(LAPISetting.MYSQL_DATABASE) + files.getConfig().getString(LAPISetting.MYSQL_OPTIONS), files.getConfig().getString(LAPISetting.MYSQL_USERNAME),files.getConfig().getString(LAPISetting.MYSQL_PASSWORD)); 	
					PreparedStatement statement = (PreparedStatement)connection.prepareStatement("CREATE TABLE IF NOT EXISTS `" + files.getConfig().getString(LAPISetting.MYSQL_DATABASE) + "`.`" + files.getConfig().getString(LAPISetting.MYSQL_TABLE) + "` ( `UUID` VARCHAR(255) NOT NULL , `Lang` TEXT NOT NULL , `AutoSelect` TINYINT NOT NULL, CONSTRAINT PK_UUID PRIMARY KEY (UUID)) ENGINE = "+files.getConfig().getString(LAPISetting.MYSQL_ENGINE)+";");
					statement.execute();
					statement.close();
				}
				return true;
			} catch (SQLException | ClassNotFoundException e) {
				MySQLError(e);
				return false;
			}
		}
	}

	private void MySQLError(Exception e) {
		if(files.getConfig().getBoolean(LAPISetting.MYSQL_ERROR_STOP)) {
			msgUtils.logCol("&8|------------------------------------------>");
			msgUtils.log(" ");
			msgUtils.logCol("&e      LangAPI &7MySQL connection error");
			msgUtils.log(" ");
			msgUtils.logCol("&4- &7Stopping server as defined on configuration.");
			msgUtils.log(" ");
			msgUtils.logCol("&8|------------------------------------------>");
			Bukkit.getServer().shutdown();
		} else {
			msgUtils.logCol("&8|------------------------------------------>");
			msgUtils.log(" ");
			msgUtils.logCol("&e      LangAPI &7MySQL connection error");
			msgUtils.log(" ");
			msgUtils.logCol("&4- &7Disabling plugin, stop is recommended.");
			msgUtils.log(" ");
			msgUtils.logCol("&8|------------------------------------------>");
			Bukkit.getPluginManager().disablePlugin(instance);
		}
		msgUtils.log(" ");
		msgUtils.logCol("&4- &7Check your config, error&8: &e"+e.getCause());
		msgUtils.log(" ");
	}

	public static Connection getMySQLConnection() {
		return connection;
	}

	public static FileUtils getFiles() {
		return files;
	}

	/**
	 * The method to access LangAPI's API.
	 * 
	 * @return An instance of the LangAPI class.
	 * 
	 * @see #getInstance()
	 * 
	 * @since LangAPI v1.0
	 */
	public static LangAPI getAPI() {
		return api;
	}

	/**
	 * Returns the instance of the LAPI class, better known as the <strong>plugin instance</strong>.
	 * 
	 * @return The instance of the plugin.
	 * 
	 * @see #getAPI()
	 * 
	 * @since LangAPI v1.0
	 */
	public static LAPI getInstance() {
		return instance;
	}

	public static LAPIMessageUtils getMessages() {
		return msgUtils;
	}

/*	private void checkUpdates() {
		if(files.getConfig().getBoolean(LAPISetting.UPDATER_ENABLED) && files.getConfig().getBoolean(LAPISetting.UPDATER_MESSAGE_CONSOLE)) {
			updater.getLatestVersion(version -> {
				msgUtils.log(" ");
				if(getDescription().getVersion().equals(version)) {
					msgUtils.logCol("&8|------------------------------------------>");
					msgUtils.log(" ");
					msgUtils.logCol("&e         LangAPI &7update checker");
					msgUtils.log(" ");
					msgUtils.logCol("&b- &7You are running the latest version.");
					msgUtils.log(" ");
					msgUtils.logCol("&8|------------------------------------------>");
				} else {
					msgUtils.logCol("&8|------------------------------------------>");
					msgUtils.log(" ");
					msgUtils.logCol("&e         LangAPI &7update checker");
					msgUtils.log(" ");
					msgUtils.logCol("&b- &7A new version is available&8: &6v"+ version);
					msgUtils.log(" ");
					msgUtils.logCol("&b- &7Currently using&8: &cv"+instance.getDescription().getVersion());
					msgUtils.log(" ");
					msgUtils.logCol("&8|------------------------------------------>");
				}
				msgUtils.log(" ");
			});
		}
	}*/
}
