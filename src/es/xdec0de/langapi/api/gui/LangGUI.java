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
import es.xdec0de.langapi.api.LangAPI;
import es.xdec0de.langapi.api.LangPlayer;
import es.xdec0de.langapi.utils.HeadDatabase;
import es.xdec0de.langapi.utils.files.LAPIConfig;
import es.xdec0de.langapi.utils.files.LAPIMsg;
import es.xdec0de.langapi.utils.files.LAPISetting;

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
		Inventory gui = Bukkit.createInventory(null, LAPISetting.LANG_GUI_ROWS.asInt()*9, LangAPI.getInstance().getString(LAPIMsg.LANG_GUI_TITLE.getPath(), LAPI.getPlugin(LAPI.class), player));
		if(LAPISetting.LANG_GUI_FILL_ENABLED.asBoolean()) {
			for(int row = 0 ; row < LAPISetting.LANG_GUI_ROWS.asInt() ; row++) {
				ItemStack fill = getFillItem(player, row+1);
				int start = row*9;
				int end = start+8;
				for(int slot = start; slot <= end; slot++) {
					gui.setItem(slot, fill);
				}
			}
		}
		LangAPI.getInstance().getString(LAPIMsg.LANG_GUI_TITLE.getPath(), LAPI.getPlugin(LAPI.class), player);
		for(String ID : getItems()) {
			gui.setItem(LAPIConfig.file().getInt("Commands.Lang.GUI.Items."+ID+".Slot"), getItem(ID, player));
		}
		onMenu.add(player.getName());
		player.openInventory(gui);
	}

	@SuppressWarnings("deprecation")
	private ItemStack getFillItem(Player player, int row) {
		Material material = Material.valueOf(LAPIConfig.file().getString("Commands.Lang.GUI.Rows.Fill.Material."+row));
		ItemStack fill = new ItemStack(material, 1);
		fill.setDurability((short)12);
		ItemMeta fillmeta = fill.getItemMeta();
		fillmeta.setDisplayName(LangAPI.getInstance().getString("Commands.Lang.GUI.Fill."+row, LAPI.getPlugin(LAPI.class), player));
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
		if(LAPIConfig.file().getString("Commands.Lang.GUI.Items."+ID+".Material").startsWith("hdb-")) {
			HeadDatabase hdb = new HeadDatabase();
			item = hdb.getHDBItem(LAPIConfig.file().getString("Commands.Lang.GUI.Items."+ID+".Material").replaceFirst("hdb-", ""));
		} else {
			material = Material.valueOf(LAPIConfig.file().getString("Commands.Lang.GUI.Items."+ID+".Material"));
			item = new ItemStack(material, 1);
		}
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(LangAPI.getInstance().getString("Commands.Lang.GUI.Items."+ID+".Name", LAPI.getPlugin(LAPI.class), player));
		meta.setLore(LangAPI.getInstance().getStringList("Commands.Lang.GUI.Items."+ID+".Lore", LAPI.getPlugin(LAPI.class), player));
		item.setItemMeta(meta);
		return item;
	}

	private ConfigurationSection getItemsSection() {
		return LAPIConfig.file().getConfigurationSection("Commands.Lang.GUI.Items");
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
			LangPlayer p = LangAPI.getInstance().getPlayer(e.getWhoClicked());
			if(onMenu.contains(p.getName())) {
				e.setCancelled(true);
				for(String ID : getItems()) {
					if(e.getSlot() == LAPIConfig.file().getInt("Commands.Lang.GUI.Items."+ID+".Slot")) {
						Lang lang = Lang.valueOf(LAPIConfig.file().getString("Commands.Lang.GUI.Items."+ID+".Lang"));
						p.setLang(lang);
						if(LAPISetting.DISABLE_AUTOSELECT.asBoolean() && p.hasAutoSelect())
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
