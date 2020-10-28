package net.arcadia.listener.shop;

import net.arcadia.Arcadian;
import net.arcadia.command.economy.shop.CreateShopCMD;
import net.arcadia.command.economy.shop.DeleteShopCMD;
import net.arcadia.menu.menus.ShopMenu;
import net.arcadia.misc.ShopFrameItem;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public class ShopListener implements Listener {
	
	@EventHandler
	public void onClick(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		Entity ent = event.getRightClicked();
		
		if (ent instanceof ItemFrame) {
			ItemFrame frame = (ItemFrame) ent;
			
			if (DeleteShopCMD.getDeletingShop().contains(player.getUniqueId())) return;
			
			Double price = CreateShopCMD.getCreatingShop().get(player.getUniqueId());
			if (price != null) {
				event.setCancelled(true);
				
				ItemStack hand = player.getInventory().getItemInMainHand().clone();
				if (hand.getType().equals(Material.AIR)) return;
				
				ShopFrameItem shopItem = new ShopFrameItem(hand, price);
				
				frame.setInvulnerable(true);
				frame.setRotation(Rotation.NONE);
				frame.setFixed(true);
				
				frame.setItem(shopItem.getDisplayItem(), true);
				
				CreateShopCMD.getCreatingShop().remove(player.getUniqueId());
				Arcadian.get(player.getUniqueId()).sendMessage(true, "&7Successfully created a shop frame.");
				return;
			}
			
			if (!frame.getItem().getType().equals(Material.AIR)) {
				
				ShopFrameItem item = ShopFrameItem.isShopItem(frame.getItem());
				if (item != null) {
					event.setCancelled(true);
					
					ShopMenu shop = new ShopMenu(player, item, item.getPrice());
					player.openInventory(shop.inventory());
				}
			}
		}
	}
	
	@EventHandler
	public void onBreak(HangingBreakByEntityEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof ItemFrame) {
			ItemFrame frame = (ItemFrame) entity;
			if (!frame.getItem().getType().equals(Material.AIR)) {
				
				ShopFrameItem item = ShopFrameItem.isShopItem(frame.getItem());
				if (item != null) {
					
					if (event.getRemover() instanceof Player) {
						Player player = (Player) event.getRemover();
						if (DeleteShopCMD.getDeletingShop().contains(player.getUniqueId())) {
							return;
						}
					}
					
					event.setCancelled(true);
				}
			}
		}
	}

}
