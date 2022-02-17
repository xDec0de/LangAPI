package es.xdec0de.langapi;

import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import es.xdec0de.langapi.api.Lang;
import es.xdec0de.langapi.utils.files.Config;
import es.xdec0de.langapi.utils.files.enums.LAPISetting;
import org.bukkit.entity.Player;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;

class ProtocolChecker extends Config {
	
	protected static void addLangCheck() {
		ProtocolManager manager = ProtocolLibrary.getProtocolManager();
		manager.addPacketListener(new PacketAdapter(LAPI.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Client.SETTINGS) {
		    @Override
		    public void onPacketReceiving(PacketEvent event) {
		        Player player = event.getPlayer();
		        PacketContainer packet = event.getPacket();
		        if(LAPI.getFiles().getConfig().getBoolean(LAPISetting.AUTOSELECT_ENABLED) && LAPI.getAPI().getAutoSelect(player.getUniqueId())) {
		        	Lang lang = Lang.valueOf(packet.getStrings().read(0).toUpperCase());
		        	LAPI.getAPI().setLanguage(player.getUniqueId(), lang, true);
		        }
		    }
		});
	}
}
