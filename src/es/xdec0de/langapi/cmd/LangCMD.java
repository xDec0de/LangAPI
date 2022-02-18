package es.xdec0de.langapi.cmd;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import es.xdec0de.langapi.api.LAPI;
import es.xdec0de.langapi.api.Lang;
import es.xdec0de.langapi.api.LangAPI;
import es.xdec0de.langapi.api.LangPlayer;
import es.xdec0de.langapi.api.gui.LangGUI;
import es.xdec0de.langapi.utils.files.LAPIConfig;
import es.xdec0de.langapi.utils.files.LAPIMsg;
import es.xdec0de.langapi.utils.files.LAPISetting;

public class LangCMD extends LAPIConfig implements CommandExecutor {

	// /lang [language] <Player>
	LangGUI LangGUI = new LangGUI();

	@Override
	public boolean onCommand(CommandSender sndr, Command cmd, String label, String[] args) {
		if(args.length == 0 && LAPISetting.LANG_USE_GUI.asBoolean()) {
			if(sndr instanceof Player)
				LangGUI.open((Player)sndr);
			else
				LAPI.getMessages().send(sndr, LAPIMsg.NO_CONSOLE);
		} else if(args.length == 1) {
			if(sndr instanceof Player) {
				LangPlayer sender = LangAPI.getInstance().getPlayer(sndr);
				try {
					Lang lang = Lang.valueOf(args[0].toUpperCase());
					sender.setLang(lang);
					if(LAPISetting.DISABLE_AUTOSELECT.asBoolean() && sender.hasAutoSelect()) 
						sender.setAutoSelect(false);
				} catch(IllegalArgumentException ex) {
					LAPI.getMessages().send(sndr, LAPIMsg.LANG_NOT_FOUND);
				}
			} else {
				LAPI.getMessages().send(sndr, LAPIMsg.NO_CONSOLE);
			}
		} else if(args.length == 2) {
			try {
				Lang lang = Lang.valueOf(args[0].toUpperCase());
				if(Bukkit.getPlayer(args[1]) != null) {
					LangPlayer target = LangAPI.getInstance().getPlayer(args[1]);
					target.setLang(lang);
					if(LAPISetting.DISABLE_AUTOSELECT.asBoolean() && target.hasAutoSelect())
						target.setAutoSelect(false);
					LAPI.getMessages().send(sndr, LAPIMsg.LANG_CHANGED_OTHER, new String[]{"%player%", target.getName()},new String[]{"%lang%", lang.name().toLowerCase()});
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
