package es.xdec0de.langapi.utils;

import org.bukkit.inventory.ItemStack;
import me.arcaniax.hdb.api.HeadDatabaseAPI;

public class HeadDatabase {
	
	HeadDatabaseAPI HeadDatabaseAPI = new HeadDatabaseAPI();
	
	public ItemStack getHDBItem(String id) {
		return HeadDatabaseAPI.getItemHead(id);
	}
}
