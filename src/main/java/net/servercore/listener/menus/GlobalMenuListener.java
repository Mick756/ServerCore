package net.servercore.listener.menus;

import com.cryptomorin.xseries.XMaterial;
import de.tr7zw.changeme.nbtapi.NBTItem;
import lombok.SneakyThrows;
import net.servercore.menu.ArcadiaMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;

import java.lang.reflect.Constructor;

public class GlobalMenuListener implements Listener {
	
	@SneakyThrows
	@EventHandler
	public void onClick(InventoryClickEvent event) {
		if (!event.getInventory().getType().equals(InventoryType.CREATIVE)
				&& event.getCurrentItem() != null && !event.getCurrentItem().getType().equals(XMaterial.AIR.parseMaterial())) {
			
			Player player = (Player) event.getWhoClicked();
			
			NBTItem item = new NBTItem(event.getCurrentItem());
			String action = item.getString("action");
			
			if (action != null) {
				
				if (action.equalsIgnoreCase("cancel")) {
					event.setCancelled(true);
					return;
				}
				
				if (action.equalsIgnoreCase("close")) {
					event.setCancelled(true);
					player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
					return;
				}
				
				if (action.equalsIgnoreCase("open")) {
					event.setCancelled(true);
					
					String inv = item.getString("inv");
					
					Class clazz = Class.forName(inv);
					Constructor con = clazz.getConstructor(Player.class);
					ArcadiaMenu menu = (ArcadiaMenu) con.newInstance(player);
					
					player.openInventory(menu.inventory());
				}
 			}
			
		}
	}
}
