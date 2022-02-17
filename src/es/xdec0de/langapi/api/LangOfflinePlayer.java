package es.xdec0de.langapi.api;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import es.xdec0de.langapi.utils.files.enums.LAPISetting;

public class LangOfflinePlayer {

	final UUID uuid;
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

	public OfflinePlayer bukkit() {
		return Bukkit.getOfflinePlayer(uuid);
	}

	public String getName() {
		return bukkit().getName();
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
						statement.setString(1, uuid.toString());
						statement.setString(2, lang.name());
						statement.setBoolean(3, autoSelect);
						statement.setString(4, lang.name());
						statement.setBoolean(5, autoSelect);
						statement.execute();
						statement.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				} else {
					LAPI.getFiles().getPlayers().get().set(uuid.toString()+".Lang", lang.name());
					LAPI.getFiles().getPlayers().get().set(uuid.toString()+".AutoSelect", autoSelect);
					LAPI.getFiles().getPlayers().save();
					LAPI.getFiles().getPlayers().reload();
				}
			}
		});
	}
}
