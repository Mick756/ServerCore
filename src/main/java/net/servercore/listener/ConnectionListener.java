package net.servercore.listener;

import net.servercore.ServerPlayer;
import net.servercore.util.Globals;
import net.servercore.util.Lang;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectionListener implements Listener {
	
	@EventHandler (priority = EventPriority.HIGH)
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		
		ServerPlayer sPlayer = new ServerPlayer(player.getUniqueId());
		ServerPlayer.getPlayers().put(player.getUniqueId(), sPlayer);
		
		
		String header = Lang.getMessage("player-list-header").replace("%player%", player.getName());
		String footer = Lang.getMessage("player-list-footer");
		//player.setPlayerListHeaderFooter(header, footer);
		
		if (!player.hasPlayedBefore()) {
			sPlayer.triggerFirstJoin();
		}
		
		String group = sPlayer.getGroup();
		if (group.equalsIgnoreCase("developer") || group.equalsIgnoreCase("owner") || group.equalsIgnoreCase("moderator")) {
			event.setJoinMessage(Globals.color(String.format("&4#&c Staff member %s joined.&4 #", player.getName())));
		} else {
			if (!player.isOp() && player.hasPermission("group.mvp")) {
				event.setJoinMessage(Globals.color(String.format("&6<&c+&6>&b %s&7 has joined the server. &6<&c+&6>", player.getDisplayName())));
			} else {
				event.setJoinMessage(Globals.color(String.format("&a+ &7%s has joined the server.", player.getName())));
			}
		}

		sPlayer.sendMessage(false, "&6Welcome %sto the&e Arcadia&6, %s!", player.hasPlayedBefore() ? "back " : "", player.getName());
		sPlayer.sendMessage(false, "&6You current have&e %d&6 active quests&6.", sPlayer.getActiveQuests().size());
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		ServerPlayer sPlayer = ServerPlayer.get(player.getUniqueId());
		
		//player.save(false);
		ServerPlayer.getPlayers().remove(player.getUniqueId());
		
		event.setQuitMessage(Globals.color(String.format("&c- &7%s has left the server.", player.getName())));
	}
	
}
