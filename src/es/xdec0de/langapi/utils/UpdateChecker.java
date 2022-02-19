package es.xdec0de.langapi.utils;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import es.xdec0de.langapi.api.LAPI;
import es.xdec0de.langapi.utils.files.LAPIMsg;
import es.xdec0de.langapi.utils.files.LAPISetting;
import net.md_5.bungee.api.ChatColor;

public class UpdateChecker implements Listener {

	private static final int resourceId = 0;

	public void getLatestVersion(final Consumer<String> consumer) {
		Bukkit.getScheduler().runTaskAsynchronously(LAPI.getPlugin(LAPI.class), () -> {
			try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + resourceId).openStream(); Scanner scanner = new Scanner(inputStream)) {
				if (scanner.hasNext())
					consumer.accept(scanner.next());
			} catch (IOException ex) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lLangAPI&8: &8[&cWarning&8] &cAn error occurred while checking for updates&8:&6 " + ex.getMessage()));
			}
		});
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		if(LAPISetting.UPDATER_ENABLED.asBoolean() && LAPISetting.UPDATER_MESSAGE_PLAYER.asBoolean()) {
			if(e.getPlayer().hasPermission(LAPISetting.UPDATER_MESSAGE_PERMISSION.asString())) {
				getLatestVersion(version -> {
					if(!LAPI.getPlugin(LAPI.class).getDescription().getVersion().equalsIgnoreCase(version))
						LAPI.getMessages().send(e.getPlayer(), LAPIMsg.UPDATE_AVAILABLE_PLAYER, new String[] {"%current%", LAPI.getPlugin(LAPI.class).getDescription().getVersion()}, new String[] {"%ver%", version});
				});
			}
		}
	}
}
