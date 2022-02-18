package es.xdec0de.langapi;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

import es.xdec0de.langapi.api.LAPI;
import es.xdec0de.langapi.api.Lang;
import es.xdec0de.langapi.api.LangAPI;
import es.xdec0de.langapi.api.LangPlayer;
import es.xdec0de.langapi.utils.files.LAPIConfig;
import es.xdec0de.langapi.utils.files.LAPISetting;

public class ProtocolChecker extends LAPIConfig {

	public static void addLangCheck() {
		ProtocolManager manager = ProtocolLibrary.getProtocolManager();
		manager.addPacketListener(new PacketAdapter(LAPI.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Client.SETTINGS) {
			@Override
			public void onPacketReceiving(PacketEvent event) {
				LangPlayer player = LangAPI.getInstance().getPlayer(event.getPlayer());
				if(LAPISetting.AUTOSELECT_ENABLED.asBoolean() && player.hasAutoSelect())
					player.setLang(Lang.valueOf(event.getPacket().getStrings().read(0).toUpperCase()));
			}
		});
	}
}
