package es.xdec0de.langapi.api;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.Bukkit;

import es.xdec0de.langapi.utils.files.enums.LAPISetting;

public class LangOfflinePlayer {

	private final UUID uuid;
	private Lang lang;
	private boolean autoSelect;

	public LangOfflinePlayer(UUID uuid) {
		this.uuid = uuid;
	}

	public LangOfflinePlayer(UUID uuid, Lang lang, boolean autoSelect) {
		this.uuid = uuid;
		this.lang = lang;
		this.autoSelect = autoSelect;
	}

	public UUID getUUID() {
		return uuid;
	}

	public Lang getLang() {
		return lang;
	}

	public void setLang(Lang lang) {
		this.lang = lang;
	}

	public boolean hasAutoSelect() {
		return autoSelect;
	}

	public void setAutoSelect(boolean autoSelect) {
		this.autoSelect = autoSelect;
	}

	/*
	 * 
	 * Data related methods
	 * 
	 */

	void save() {
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