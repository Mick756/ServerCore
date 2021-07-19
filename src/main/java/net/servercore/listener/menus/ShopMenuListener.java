package net.servercore.listener.menus;

import de.tr7zw.changeme.nbtapi.NBTItem;
import net.servercore.ServerPlayer;
import net.servercore.menu.menus.ShopMenu;
import net.servercore.misc.ShopFrameItem;
import net.servercore.misc.Transaction;
import net.servercore.survival.items.CustomItem;
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
		ServerPlayer sPlayer = ServerPlayer.get(player);
		
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
			sPlayer.sendMessage(true, response.getMessage());
			
		}
		
	}
}
