package net.arcadia.listeners.item;

import de.tr7zw.changeme.nbtapi.NBTItem;
import net.arcadia.survival.items.CustomItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class UseCustomItemListener implements Listener {
	
	@EventHandler
	public void onUseCustomItem(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		
		ItemStack item = player.getInventory().getItemInMainHand();
		if (!item.getType().equals(Material.AIR)) {
			
			NBTItem nbt = new NBTItem(item);
			String key = nbt.getString("ci");
			
			if (key != null) {
				CustomItem ci = CustomItem.getCustomItem(key);
				
				if (ci != null) {
					ci.action(player);
				}
				
			}
			
		}
	}
}
