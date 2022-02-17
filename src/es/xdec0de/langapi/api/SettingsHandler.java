package es.xdec0de.langapi.api;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import es.xdec0de.langapi.LAPI;
import es.xdec0de.langapi.utils.files.enums.LAPISetting;

public class SettingsHandler implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		LangAPI.settingsCache.put(e.getPlayer().getUniqueId(), LAPI.getAPI().getSettings(e.getPlayer().getUniqueId()));
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		sendCacheToDatabase(e.getPlayer().getUniqueId());
		LangAPI.settingsCache.remove(e.getPlayer().getUniqueId());
	}

	static void sendCacheToDatabase() {
		if(LAPI.getFiles().getConfig().getBoolean(LAPISetting.MYSQL_ENABLED)) {
			try {
				Connection c = LAPI.getMySQLConnection();
				c.setAutoCommit(false);
				PreparedStatement statement = (PreparedStatement)c.prepareStatement(
						"INSERT INTO " + LAPI.getFiles().getConfig().getString(LAPISetting.MYSQL_TABLE) + " VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE Lang=?, AutoSelect=?");
				for(UUID uuid : LangAPI.settingsCache.keySet()) {
					Settings settings = LangAPI.settingsCache.get(uuid);
					statement.setString(1, uuid.toString());
					statement.setString(2, settings.getLang().name());
					statement.setBoolean(3, settings.getAutoSelect());
					statement.setString(4, settings.getLang().name());
					statement.setBoolean(5, settings.getAutoSelect());
					statement.addBatch();
				}
				statement.executeBatch();
				statement.close();
				c.commit();
				c.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			for(UUID uuid : LangAPI.settingsCache.keySet()) {
				LAPI.getFiles().getPlayers().get().set(uuid.toString()+".Lang", LAPI.getAPI().getSettings(uuid).getLang().name());
				LAPI.getFiles().getPlayers().get().set(uuid.toString()+".AutoSelect", LAPI.getAPI().getSettings(uuid).getAutoSelect());
			}
			LAPI.getFiles().getPlayers().save();
			LAPI.getFiles().getPlayers().reload();
		}
	}

	private void sendCacheToDatabase(UUID uuid) {
		Bukkit.getScheduler().runTaskAsynchronously(LAPI.getInstance(), new Runnable() {
			@Override
			public void run() {
				if(LAPI.getFiles().getConfig().getBoolean(LAPISetting.MYSQL_ENABLED)) {
					try {
						Connection c = LAPI.getMySQLConnection();
						PreparedStatement statement = (PreparedStatement)c.prepareStatement(
								"INSERT INTO " + LAPI.getFiles().getConfig().getString(LAPISetting.MYSQL_TABLE) + " VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE Lang=?, AutoSelect=?");
						Settings settings = LangAPI.settingsCache.get(uuid);
						statement.setString(1, uuid.toString());
						statement.setString(2, settings.getLang().name());
						statement.setBoolean(3, settings.getAutoSelect());
						statement.setString(4, settings.getLang().name());
						statement.setBoolean(5, settings.getAutoSelect());
						statement.execute();
						statement.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				} else {
					LAPI.getFiles().getPlayers().get().set(uuid.toString()+".Lang", LAPI.getAPI().getSettings(uuid).getLang().name());
					LAPI.getFiles().getPlayers().get().set(uuid.toString()+".AutoSelect", LAPI.getAPI().getSettings(uuid).getAutoSelect());
					LAPI.getFiles().getPlayers().save();
					LAPI.getFiles().getPlayers().reload();
				}
			}
		});
	}
}
