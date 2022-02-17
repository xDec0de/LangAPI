package es.xdec0de.langapi;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

import es.xdec0de.langapi.api.LAPI;
import es.xdec0de.langapi.api.Lang;
import es.xdec0de.langapi.api.LangPlayer;
import es.xdec0de.langapi.utils.files.Config;
import es.xdec0de.langapi.utils.files.enums.LAPISetting;

public class ProtocolChecker extends Config {

	public static void addLangCheck() {
		ProtocolManager manager = ProtocolLibrary.getProtocolManager();
		manager.addPacketListener(new PacketAdapter(LAPI.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Client.SETTINGS) {
			@Override
			public void onPacketReceiving(PacketEvent event) {
				LangPlayer player = LAPI.getAPI().getPlayer(event.getPlayer());
				if(LAPI.getFiles().getConfig().getBoolean(LAPISetting.AUTOSELECT_ENABLED) && player.hasAutoSelect())
					player.setLang(Lang.valueOf(event.getPacket().getStrings().read(0).toUpperCase()));
			}
		});
	}
}
