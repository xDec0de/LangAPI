package es.xdec0de.langapi.api;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import es.xdec0de.langapi.utils.files.enums.LAPISetting;

class DataHandler implements Listener {

	private static DataHandler instance;

	HashMap<UUID, LangPlayer> onlineCache = new HashMap<UUID, LangPlayer>();
	HashMap<UUID, LangOfflinePlayer> offlineCache = new HashMap<UUID, LangOfflinePlayer>();

	static DataHandler getInstance() {
		return instance != null ? instance : (instance = new DataHandler());
	}

	private DataHandler() {
		Bukkit.getOnlinePlayers().forEach(on -> onlineCache.put(on.getUniqueId(), new LangPlayer(on.getUniqueId())));
	}

	@EventHandler
	public void onPlayerLoad(AsyncPlayerPreLoginEvent e) {
		addPlayer(e.getUniqueId());
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		toOfflineCache(onlineCache.get(e.getPlayer().getUniqueId()));
	}

	LangPlayer getPlayer(UUID uuid) {
		return uuid != null ? onlineCache.get(uuid) : null;
	}

	LangPlayer getPlayer(String name) {
		if(name == null)
			return null;
		Player player = Bukkit.getPlayer(name);
		return player != null ? getPlayer(player.getUniqueId()) : null;
	}

	private void addPlayer(UUID uuid) {
		if(offlineCache.containsKey(uuid)) {
			onlineCache.put(uuid, new LangPlayer(offlineCache.get(uuid)));
			offlineCache.remove(uuid);
		} else
			onlineCache.put(uuid, new LangPlayer(uuid));
	}

	List<LangPlayer> getOnlinePlayers() {
		return new ArrayList<LangPlayer>(onlineCache.values());
	}

	LangOfflinePlayer getOfflinePlayer(UUID uuid) {
		if(uuid == null)
			return null;
		if(onlineCache.containsKey(uuid))
			return (LangOfflinePlayer)onlineCache.get(uuid);
		return offlineCache.containsKey(uuid) ? offlineCache.get(uuid) : toOfflineCache(uuid);
	}

	private LangOfflinePlayer toOfflineCache(LangOfflinePlayer offline) {
		onlineCache.remove(offline.getUUID());
		offlineCache.put(offline.getUUID(), offline);
		Bukkit.getScheduler().runTaskLater(LAPI.getPlugin(LAPI.class), () -> {
			if(offlineCache.containsKey(offline.getUUID())) { // Check if player hasn't reconnected
				offline.save();
				offlineCache.remove(offline.getUUID());
			}
		}, 30 * 60 * 20); // 30 minutes delay
		return offline;
	}

	private LangOfflinePlayer toOfflineCache(UUID uuid) {
		return toOfflineCache(new LangOfflinePlayer(uuid));
	}

	void saveAll() {
		List<LangOfflinePlayer> players = new ArrayList<LangOfflinePlayer>(offlineCache.values());
		players.addAll(onlineCache.values());
		if(LAPI.getFiles().getConfig().getBoolean(LAPISetting.MYSQL_ENABLED)) {
			try {
				Connection c = LAPI.getMySQLConnection();
				c.setAutoCommit(false);
				PreparedStatement statement = (PreparedStatement)c.prepareStatement(
						"INSERT INTO " + LAPI.getFiles().getConfig().getString(LAPISetting.MYSQL_TABLE) + " VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE Lang=?, AutoSelect=?");
				for(LangOfflinePlayer player : players) {
					statement.setString(1, player.getUUID().toString());
					statement.setString(2, player.getLang().name());
					statement.setBoolean(3, player.hasAutoSelect());
					statement.setString(4, player.getLang().name());
					statement.setBoolean(5, player.hasAutoSelect());
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
			for(LangOfflinePlayer player : players) {
				String id = player.getUUID().toString();
				LAPI.getFiles().getPlayers().get().set(id+".Lang", player.getLang().name());
				LAPI.getFiles().getPlayers().get().set(id+".AutoSelect", player.hasAutoSelect());
			}
			LAPI.getFiles().getPlayers().save();
			LAPI.getFiles().getPlayers().reload();
		}
	}
}
