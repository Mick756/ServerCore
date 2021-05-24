package net.arcadia.listener.menus;

import de.tr7zw.changeme.nbtapi.NBTItem;
import net.arcadia.Arcadian;
import net.arcadia.menu.menus.cosmetic.RankColorMenu;
import net.arcadia.menu.menus.rank.MvpMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class MvpMenuEvents implements Listener {
	
	@EventHandler
	public void onMvpMenuClick(InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player)) return;
		
		String name = event.getView().getTitle();
		Player player = (Player) event.getWhoClicked();
		Arcadian arcadian = Arcadian.get(player);
		
		if (name.equalsIgnoreCase(MvpMenu.NAME)) {
			
			return;
		}
		
		if (name.equalsIgnoreCase(RankColorMenu.NAME)) {
			event.setCancelled(true);
			if (event.getCurrentItem() != null) {
				
				ItemStack stack = event.getCurrentItem();
				NBTItem item = new NBTItem(stack);
				
				String action = item.getString("action");
				
				if (action != null) {
//					if (action.equalsIgnoreCase("mvp_rank")) {
//						String color = item.getString("rankcolor");
//
//						String tag = color + ChatColor.stripColor(Globals.color(ArcadiaCore.getInstance().getConfig().getString("tags.mvp")));
//						arcadian.setCustomTag(tag);
//						arcadian.updateNameDisplay();
//
//						arcadian.sendMessage(true, "&7You have successfully changed your rank color. Your new tag is: %s&7.", tag);
//						player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
//					}
				}
				
			}
		}
	}
}
