package net.arcadia.menu;

import net.arcadia.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class ArcadiaMenu {
	
	public abstract String title();
	
	public abstract Inventory inventory();
	
	protected Inventory createInventory(int size) {
		return Bukkit.createInventory(null, size, this.title());
	}
	
	protected static void applyBorder(Inventory inventory, ItemStack item, boolean omit) {
		
		int[] slots = Util.getInventoryBorder(inventory, omit);
		
		if (slots.length > 0) {
			for (int slot : slots) {
				inventory.setItem(slot, item);
			}
		}
	}
	
}
