package es.xdec0de.langapi.cmd;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import es.xdec0de.langapi.LAPI;
import es.xdec0de.langapi.api.Lang;
import es.xdec0de.langapi.api.gui.LangGUI;
import es.xdec0de.langapi.utils.files.Config;
import es.xdec0de.langapi.utils.files.enums.LAPIMsg;
import es.xdec0de.langapi.utils.files.enums.LAPISetting;

public class LangCMD extends Config implements CommandExecutor {

	// /lang [language] <Player>
	LangGUI LangGUI = new LangGUI();

	@Override
	public boolean onCommand(CommandSender sndr, Command cmd, String label, String[] args) {
		if(args.length == 0 && LAPI.getFiles().getConfig().getBoolean(LAPISetting.LANG_USE_GUI)) {
			if(sndr instanceof Player) {
				LangGUI.open((Player)sndr);
			} else {
				LAPI.getMessages().send(sndr, LAPIMsg.NO_CONSOLE);
			}
		} else if(args.length == 1) {
			if(sndr instanceof Player) {
				Player p = (Player)sndr;
				try {
					Lang lang = Lang.valueOf(args[0].toUpperCase());
					LAPI.getAPI().setLanguage(p.getUniqueId(), lang, true);
					if(LAPI.getFiles().getConfig().getBoolean(LAPISetting.DISABLE_AUTOSELECT) && LAPI.getAPI().getAutoSelect(p.getUniqueId())) {
						LAPI.getAPI().setAutoSelect(p.getUniqueId(), false);
					}
				} catch(IllegalArgumentException ex) {
					LAPI.getMessages().send(p, LAPIMsg.LANG_NOT_FOUND);
				}
			} else {
				LAPI.getMessages().send(sndr, LAPIMsg.NO_CONSOLE);
			}
		} else if(args.length == 2) {
			try {
				Lang lang = Lang.valueOf(args[0].toUpperCase());
				if(Bukkit.getPlayer(args[1]) != null) {
					Player p = Bukkit.getPlayer(args[1]);
					LAPI.getAPI().setLanguage(p.getUniqueId(), lang, true);
					if(LAPI.getFiles().getConfig().getBoolean(LAPISetting.DISABLE_AUTOSELECT) && LAPI.getAPI().getAutoSelect(p.getUniqueId())) {
						LAPI.getAPI().setAutoSelect(p.getUniqueId(), false);
					}
					LAPI.getMessages().send(sndr, LAPIMsg.LANG_CHANGED_OTHER, new String[]{"%player%", p.getName()},new String[]{"%lang%", lang.name().toLowerCase()});
				} else {
					LAPI.getMessages().send(sndr, LAPIMsg.PLAYER_NOT_FOUND);
				}
			} catch(IllegalArgumentException ex) {
				LAPI.getMessages().send(sndr, LAPIMsg.LANG_NOT_FOUND);
			}
		} else {
			LAPI.getMessages().send(sndr, LAPIMsg.LANG_USAGE);
		}
		return true;
	}
}
