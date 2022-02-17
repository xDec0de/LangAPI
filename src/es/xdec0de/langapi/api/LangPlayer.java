package es.xdec0de.langapi.api;

import java.util.UUID;

public class LangPlayer extends LangOfflinePlayer {

	LangPlayer(UUID uuid) {
		super(uuid);
	}

	LangPlayer(LangOfflinePlayer offlinePlayer) {
		super(offlinePlayer.getUUID(), offlinePlayer.getLang(), offlinePlayer.hasAutoSelect());
	}
}
