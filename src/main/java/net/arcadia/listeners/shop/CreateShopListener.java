package net.arcadia.listeners.shop;

import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class CreateShopListener implements Listener {
	
	@EventHandler
	public void onClick(PlayerInteractEntityEvent event){
		
		Player player = event.getPlayer();
		Entity ent = event.getRightClicked();
		
		if(ent instanceof ItemFrame){
			
		}
		
	}

}
