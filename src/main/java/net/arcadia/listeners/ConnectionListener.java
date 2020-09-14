package net.arcadia.listeners;

import net.arcadia.Arcadian;
import net.arcadia.chat.Mute;
import net.arcadia.chat.PlayerMute;
import net.arcadia.util.Globals;
import net.arcadia.util.Lang;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class ConnectionListener implements Listener {
	
//	@EventHandler (priority = EventPriority.HIGHEST)
//	public void onPlayerLogin(PlayerLoginEvent event) {
//		String proxy = ArcadiaCore.getInstance().getConfig().getString("proxy");
//		String host = event.getAddress().getHostAddress();
//		if (!host.contains(proxy)) {
//			ArcadiaCore.error("This join would have been disallowed. Host: " + host);
//			//event.disallow(PlayerLoginEvent.Result.KICK_OTHER, Globals.color("&cYou are now able to join the server through this IP."));
//		}
//	}
	
	@EventHandler (priority = EventPriority.HIGH)
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		Arcadian arcadian = new Arcadian(player.getUniqueId());
		
		String header = Lang.getMessage("player-list-header").replace("%player%", player.getName());
		String footer = Lang.getMessage("player-list-footer");
		player.setPlayerListHeaderFooter(header, footer);
		
		if (!player.hasPlayedBefore()) {
			arcadian.triggerFirstJoin();
		}
		
		arcadian.updateNameDisplay();
		
		PlayerMute pm = null;
		for (UUID uuid : Mute.getAwaitingMutes().keySet()) {
			if (uuid.equals(player.getUniqueId())) {
				PlayerMute mute = Mute.getAwaitingMutes().get(uuid);
				mute.setPlayer(player);
				
				pm = mute;
				
				mute.setEndTime(mute.getDuration());
				mute.start();
			}
		}
		
		if (pm != null) {
			Mute.getAwaitingMutes().remove(player.getUniqueId());
		}
		
		if (!player.isOp() && player.hasPermission("group.mvp")) {
			event.setJoinMessage(Globals.color(String.format("&6<&c+&6>&b %s&7 has joined the server. &6<&c+&6>", player.getDisplayName())));
			return;
		}
		
		event.setJoinMessage(Globals.color(String.format("&7%s has joined the server.", player.getName())));
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		Arcadian arcadian = Arcadian.get(player.getUniqueId());
		
		arcadian.save(false);
		Arcadian.getArcadians().remove(player.getUniqueId());
	}
}
