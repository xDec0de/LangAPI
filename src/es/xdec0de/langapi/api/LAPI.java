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
import es.xdec0de.langapi.utils.files.LAPIConfig;
import es.xdec0de.langapi.utils.files.LAPISetting;
import es.xdec0de.langapi.utils.files.Players;

public class LAPI extends JavaPlugin {

	private static LAPI instance;
	private static Connection connection;

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
		LAPIConfig.setup(false);
		if(LAPISetting.MYSQL_ENABLED.asBoolean()) {
			if(!connectMySQL())
				return;
		} else
			Players.setup(false);
		msgUtils = new LAPIMessageUtils();
		LangAPI.getInstance().createFiles(this);
		LangAPI.getInstance().updateFiles(this, ignoredMessages());
		getCommand("lang").setExecutor(new LangCMD());
		getServer().getPluginManager().registerEvents(DataHandler.getInstance(), this);
		getServer().getPluginManager().registerEvents(new LangGUI(), this);
		//getServer().getPluginManager().registerEvents(updater = new UpdateChecker(), this);
		int dbDelay = LAPISetting.MYSQL_CACHE_TIME.asInt() * 20;
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
		} else if(LAPISetting.AUTOSELECT_ENABLED.asBoolean())
			msgUtils.logCol("  &e- ProtocolLib &cnot detected, &6language detection&c won't work.");
		if(Bukkit.getPluginManager().isPluginEnabled("HeadDatabase"))
			msgUtils.logCol("  &e- &bHeadDatabase &7detected (&av" + Bukkit.getPluginManager().getPlugin("HeadDatabase").getDescription().getVersion() + "&7)");
		msgUtils.log(" ");
	}

	private boolean connectMySQL() {
		synchronized (this) {
			try {
				if(connection == null || connection.isClosed()) { 
					Class.forName("com.mysql.jdbc.Driver");

					connection = (Connection)DriverManager.getConnection("jdbc:mysql://" + LAPISetting.MYSQL_HOST.asString() + ":" + LAPISetting.MYSQL_PORT.asInt() + "/" +
					LAPISetting.MYSQL_DATABASE.asString() + LAPISetting.MYSQL_OPTIONS.asString(), LAPISetting.MYSQL_USERNAME.asString(), LAPISetting.MYSQL_PASSWORD.asString());

					PreparedStatement statement = (PreparedStatement)connection.prepareStatement("CREATE TABLE IF NOT EXISTS `" + LAPISetting.MYSQL_DATABASE.asString() +
							"`.`" + LAPISetting.MYSQL_TABLE.asString() + "` ( `UUID` VARCHAR(255) NOT NULL , `Lang` TEXT NOT NULL , `AutoSelect` TINYINT NOT NULL, CONSTRAINT PK_UUID PRIMARY KEY (UUID)) ENGINE = "+
							LAPISetting.MYSQL_ENGINE.asString()+";");
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
		if(LAPISetting.MYSQL_ERROR_STOP.asBoolean()) {
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
