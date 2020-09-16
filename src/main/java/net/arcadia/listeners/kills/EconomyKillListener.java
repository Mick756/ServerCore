package net.arcadia.listeners.kills;

import net.arcadia.ArcadiaCore;
import net.arcadia.util.Globals;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class EconomyKillListener implements Listener {

	@EventHandler
	public void onKill(EntityDeathEvent event) {
		Player killer = event.getEntity().getKiller();
		if (killer != null) {
			double worth = getWorth(event.getEntity().getType());
			
			ArcadiaCore.getEconomy().depositPlayer(killer, worth);
			killer.sendMessage(String.format(Globals.color("&6+$%.2f&7 (Mob Kill)"), worth));
		}
	}

	private static double getWorth(EntityType type) {
		
		switch (type) {
			
			case CAVE_SPIDER:
			case SKELETON:
			case SPIDER:
			case ZOMBIE:
				return 5.0d;
				
			case SILVERFISH:
			case MAGMA_CUBE:
				return 6.0d;
			
			case SLIME:
			case DROWNED:
			case CREEPER:
				return 7.0d;
			
			case WITCH:
			case HUSK:
			case STRAY:
			case PILLAGER:
			case ZOMBIFIED_PIGLIN:
			case PHANTOM:
				return 50.0d;
			
			case GHAST:
			case RAVAGER:
			case BLAZE:
			case ZOMBIE_VILLAGER:
			case WITHER_SKELETON:
				return 75.0d;
				
			case SHULKER:
			case PIGLIN_BRUTE:
				return 100.0d;
				
			case ZOGLIN:
			case HOGLIN:
			case VINDICATOR:
			case GUARDIAN:
			case ENDERMAN:
			case VEX:
			case PIGLIN:
				return 150.0d;
			
			case ELDER_GUARDIAN:
				return 200.0d;
				
			case WITHER:
				return 2500.0d;
				
			case ENDER_DRAGON:
				return 5000.0d;
				
			default:
				return 1.0d;
		}
		
	}
	
}
