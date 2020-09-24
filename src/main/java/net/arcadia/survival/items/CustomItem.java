package net.arcadia.survival.items;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public interface CustomItem {
	
	Map<String[], CustomItem> customItems = new HashMap<>();
	
	String name();
	
	ItemStack getItem();
	
	void action(Player player);
	
	int maxStackSize();
	
	static CustomItem getCustomItem(String id) {
		
		for (String[] names : customItems.keySet()) {
			
			for (String name : names) {
				if (!id.equalsIgnoreCase(name)) continue;
				
				return customItems.get(names);
			}
			
		}
		
		return null;
	}
	
}
