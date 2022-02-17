package es.xdec0de.langapi.api;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class LangPlayer extends LangOfflinePlayer {

	LangPlayer(UUID uuid) {
		super(uuid);
	}

	LangPlayer(LangOfflinePlayer offlinePlayer) {
		super(offlinePlayer.getUUID(), offlinePlayer.getLang(), offlinePlayer.hasAutoSelect());
	}

	@Override
	public Player bukkit() {
		return Bukkit.getPlayer(uuid);
	}

	@Override
	public String getName() {
		return bukkit().getName();
	}
}
