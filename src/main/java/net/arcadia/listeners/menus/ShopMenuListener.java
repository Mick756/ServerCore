package net.arcadia.listeners.menus;

import de.tr7zw.changeme.nbtapi.NBTItem;
import net.arcadia.Arcadian;
import net.arcadia.menu.menus.ShopMenu;
import net.arcadia.misc.ShopFrameItem;
import net.arcadia.misc.Transaction;
import net.arcadia.survival.items.CustomItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public class ShopMenuListener implements Listener {
	
	@EventHandler
	public void onMvpMenuClick(InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player)) return;
		
		String name = event.getView().getTitle();
		Player player = (Player) event.getWhoClicked();
		Arcadian arcadian = Arcadian.get(player);
		
		if (name.equalsIgnoreCase(ShopMenu.NAME)) {
			event.setCancelled(true);
			
			ItemStack current = event.getCurrentItem();
			if (current == null || current.getType().equals(Material.AIR)) return;
			
			ItemStack center = event.getInventory().getItem(13);
			ShopFrameItem item = ShopFrameItem.isShopItem(center);
			if (item == null) return;
			
			ItemStack toBuy = item.getItem();
			
			NBTItem nbt = new NBTItem(item.getItem());
			
			String customItemId = nbt.getString("ci");
			if (customItemId != null) {
				
				CustomItem cItem = CustomItem.getCustomItem(customItemId);
				if (cItem != null) {
					toBuy = cItem.getItem();
				}
			}
			
			NBTItem nbtCurrent = new NBTItem(current);
			
			String action = nbtCurrent.getString("action");
			if (action == null || action.equalsIgnoreCase("")) return;
			
			double price = nbtCurrent.getDouble("price");
			int amount = nbtCurrent.getInteger("amount");
			
			toBuy.setAmount(amount);
			
			Transaction transaction;
			if (action.equalsIgnoreCase("buy")) {
				transaction = new Transaction(player, price, true, toBuy);
			} else {
				transaction = new Transaction(player, price, false, toBuy);
			}
			
			Transaction.TransactionResponse response = transaction.execute();
			player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
			arcadian.sendMessage(true, response.getMessage());
			
		}
		
	}
}
