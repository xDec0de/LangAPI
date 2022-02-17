package es.xdec0de.langapi.api.gui;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import es.xdec0de.langapi.api.LAPI;
import es.xdec0de.langapi.api.Lang;
import es.xdec0de.langapi.api.LangPlayer;
import es.xdec0de.langapi.utils.HeadDatabase;
import es.xdec0de.langapi.utils.files.enums.LAPIMsg;
import es.xdec0de.langapi.utils.files.enums.LAPISetting;

/**
 * Class where the LangAPI GUI is managed.
 * 
 * @author xDec0de_
 *
 * @since LangAPI v1.0
 */
public class LangGUI implements Listener {

	private static List<String> onMenu = new ArrayList<String>();

	/**
	 * Opens the language selection GUI to a player.
	 * 
	 * @param player
	 * The player that will see the GUI.
	 * 
	 * @since LangAPI v1.0
	 */
	public void open(Player player) {
		Inventory gui = Bukkit.createInventory(null, LAPI.getFiles().getConfig().getInt(LAPISetting.LANG_GUI_ROWS)*9, LAPI.getAPI().getString(LAPIMsg.LANG_GUI_TITLE.getPath(), LAPI.getInstance(), player));
		if(LAPI.getFiles().getConfig().getBoolean(LAPISetting.LANG_GUI_FILL_ENABLED)) {
			for(int row = 0 ; row < LAPI.getFiles().getConfig().getInt(LAPISetting.LANG_GUI_ROWS) ; row++) {
				ItemStack fill = getFillItem(player, row+1);
				int start = row*9;
				int end = start+8;
				for(int slot = start; slot <= end; slot++) {
					gui.setItem(slot, fill);
				}
			}
		}
		LAPI.getAPI().getString(LAPIMsg.LANG_GUI_TITLE.getPath(), LAPI.getInstance(), player);
		for(String ID : getItems()) {
			gui.setItem(LAPI.getFiles().getConfig().get().getInt("Commands.Lang.GUI.Items."+ID+".Slot"), getItem(ID, player));
		}
		onMenu.add(player.getName());
		player.openInventory(gui);
	}

	@SuppressWarnings("deprecation")
	private ItemStack getFillItem(Player player, int row) {
		Material material = Material.valueOf(LAPI.getFiles().getConfig().get().getString("Commands.Lang.GUI.Rows.Fill.Material."+row));
		ItemStack fill = new ItemStack(material, 1);
		fill.setDurability((short)12);
		ItemMeta fillmeta = fill.getItemMeta();
		fillmeta.setDisplayName(LAPI.getAPI().getString("Commands.Lang.GUI.Fill."+row, LAPI.getInstance(), player));
		fill.setItemMeta(fillmeta);
		return fill;
	}

	/**
	 * Gets the ItemStack of the specified ID.
	 * 
	 * @param ID
	 * Item ID, this is defined on config.yml, you can get all ID's with {@link #getItems()}.
	 * 
	 * @param player
	 * The player that LangAPI will use to get the language of the item.
	 * 
	 * @return
	 * The ItemStack of the selected ID.
	 * 
	 * @since LangAPI v1.0
	 */
	public ItemStack getItem(String ID, Player player) {
		ItemStack item = null;
		Material material;
		if(LAPI.getFiles().getConfig().get().getString("Commands.Lang.GUI.Items."+ID+".Material").startsWith("hdb-")) {
			HeadDatabase hdb = new HeadDatabase();
			item = hdb.getHDBItem(LAPI.getFiles().getConfig().get().getString("Commands.Lang.GUI.Items."+ID+".Material").replaceFirst("hdb-", ""));
		} else {
			material = Material.valueOf(LAPI.getFiles().getConfig().get().getString("Commands.Lang.GUI.Items."+ID+".Material"));
			item = new ItemStack(material, 1);
		}
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(LAPI.getAPI().getString("Commands.Lang.GUI.Items."+ID+".Name", LAPI.getInstance(), player));
		meta.setLore(LAPI.getAPI().getStringList("Commands.Lang.GUI.Items."+ID+".Lore", LAPI.getInstance(), player));
		item.setItemMeta(meta);
		return item;
	}

	private ConfigurationSection getItemsSection() {
		return LAPI.getFiles().getConfig().get().getConfigurationSection("Commands.Lang.GUI.Items");
	}

	/**
	 * Gets all item ID's of the GUI defined on config.yml.
	 * 
	 * @return
	 * All item ID's defined on config.yml.
	 * 
	 * @since LangAPI v1.0
	 * 
	 * @see #getItem(String, Player)
	 */
	public ArrayList<String> getItems() {
		ArrayList<String> menus = new ArrayList<String>(); 
		for (String str : getItemsSection().getKeys(false)) {  
			menus.add(str);
		}
		return menus;
	}

	@EventHandler
	private void onClick(InventoryClickEvent e) {
		if(e.getClickedInventory() != null) {
			LangPlayer p = LAPI.getAPI().getPlayer(e.getWhoClicked());
			if(onMenu.contains(p.getName())) {
				e.setCancelled(true);
				for(String ID : getItems()) {
					if(e.getSlot() == LAPI.getFiles().getConfig().get().getInt("Commands.Lang.GUI.Items."+ID+".Slot")) {
						Lang lang = Lang.valueOf(LAPI.getFiles().getConfig().get().getString("Commands.Lang.GUI.Items."+ID+".Lang"));
						p.setLang(lang);
						if(LAPI.getFiles().getConfig().getBoolean(LAPISetting.DISABLE_AUTOSELECT) && p.hasAutoSelect())
							p.setAutoSelect(false);
						p.bukkit().closeInventory();
						return;
					}
				}
			}
		}
	}

	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		if(onMenu.contains(e.getPlayer().getName())) {
			onMenu.remove(e.getPlayer().getName());
		}
	}
}
