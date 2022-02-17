package es.xdec0de.langapi.utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import es.xdec0de.langapi.api.LAPI;
import es.xdec0de.langapi.utils.files.enums.LAPIMsg;
import net.md_5.bungee.api.ChatColor;

public class LAPIMessageUtils {

	public String get(Player player, LAPIMsg message) {
		return LAPI.getAPI().getString(message.getPath(), LAPI.getInstance(), player)
				.replaceAll("%error%", LAPI.getAPI().getString(LAPIMsg.ERROR_PREFIX.getPath(), LAPI.getInstance(), player))
				.replaceAll("%prefix%", LAPI.getAPI().getString(LAPIMsg.PREFIX.getPath(), LAPI.getInstance(), player));
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
		return LAPI.getAPI().getString(message.getPath(), LAPI.getInstance(), sender)
				.replaceAll("%error%", LAPI.getAPI().getString(LAPIMsg.ERROR_PREFIX.getPath(), LAPI.getInstance(), sender))
				.replaceAll("%prefix%", LAPI.getAPI().getString(LAPIMsg.PREFIX.getPath(), LAPI.getInstance(), sender));
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
