package net.arcadia.listener;

import net.arcadia.command.admin.CommandSpyCMD;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandListener implements Listener {
	
	@EventHandler
	public void onCommandEvent(PlayerCommandPreprocessEvent event) {
		Player p = event.getPlayer();
		
		if (event.getMessage().startsWith("/tell ") || event.getMessage().startsWith("/whisper")) {
			event.setCancelled(true);
			return;
		}
		
		for (Player player : CommandSpyCMD.getSpies()) {
			player.sendMessage(CommandSpyCMD.getFormattedMessage(p, event.getMessage()));
		}
		
	}
}
