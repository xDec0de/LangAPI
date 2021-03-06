package es.xdec0de.langapi.utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import es.xdec0de.langapi.api.LAPI;
import es.xdec0de.langapi.api.LangAPI;
import es.xdec0de.langapi.utils.files.LAPIMsg;
import net.md_5.bungee.api.ChatColor;

public class LAPIMessageUtils {

	public String get(Player player, LAPIMsg message) {
		LangAPI api = LangAPI.getInstance();
		LAPI lapi = LAPI.getPlugin(LAPI.class);
		return api.getString(message.getPath(), lapi, player)
				.replaceAll("%error%", api.getString(LAPIMsg.ERROR_PREFIX.getPath(), lapi, player))
				.replaceAll("%prefix%", api.getString(LAPIMsg.PREFIX.getPath(), lapi, player));
	}

	public String get(Player player, LAPIMsg message, String[]... replace) {
		String msg = get(player, message);
		for(String[] rep : replace) {
			if(rep.length == 2) {
				msg = msg.replaceAll(rep[0], rep[1]);
			}
		}
		return msg;
	}

	public String get(CommandSender sender, LAPIMsg message) {
		LangAPI api = LangAPI.getInstance();
		LAPI lapi = LAPI.getPlugin(LAPI.class);
		return api.getString(message.getPath(), lapi, sender)
				.replaceAll("%error%", api.getString(LAPIMsg.ERROR_PREFIX.getPath(), lapi, sender))
				.replaceAll("%prefix%", api.getString(LAPIMsg.PREFIX.getPath(), lapi, sender));
	}

	public String get(CommandSender sender, LAPIMsg message, String[]... replace) {
		String msg = get(sender, message);
		for(String[] rep : replace) {
			if(rep.length == 2) {
				msg = msg.replaceAll(rep[0], rep[1]);
			}
		}
		return msg;
	}

	public void send(Player player, LAPIMsg message) {
		player.sendMessage(get(player, message));
	}

	public void send(Player player, LAPIMsg message, String[]... replace) {
		player.sendMessage(get(player, message, replace));
	}

	public void send(CommandSender sender, LAPIMsg message) {
		sender.sendMessage(get(sender, message));
	}

	public void send(CommandSender sender, LAPIMsg message, String[]... replace) {
		sender.sendMessage(get(sender, message, replace));
	}

	public void log(String message) {
		Bukkit.getConsoleSender().sendMessage(message);
	}

	public void logCol(String message) {
		Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', message));
	}
}
