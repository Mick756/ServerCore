package net.arcadia.listener;

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
		
		String group = arcadian.getGroup();
		if (group.equalsIgnoreCase("developer") || group.equalsIgnoreCase("owner") || group.equalsIgnoreCase("moderator")) {
			event.setJoinMessage(Globals.color(String.format("&4#&c Staff member %s joined.&4 #", player.getName())));
		} else {
			if (!player.isOp() && player.hasPermission("group.mvp")) {
				event.setJoinMessage(Globals.color(String.format("&6<&c+&6>&b %s&7 has joined the server. &6<&c+&6>", player.getDisplayName())));
			} else {
				event.setJoinMessage(Globals.color(String.format("&a+ &7%s has joined the server.", player.getName())));
			}
		}
		
		arcadian.sendMessage(false, "&6Welcome %sto the&e Arcadia&6, %s!", player.hasPlayedBefore() ? "back " : "", player.getName());
		//arcadian.sendMessage(false, "&6You current have&e %d&6 active quests&6.", arcadian.getActiveQuests().size());
		
		Arcadian.getArcadians().put(player.getUniqueId(), arcadian);
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		Arcadian arcadian = Arcadian.get(player.getUniqueId());
		
		arcadian.save(false);
		Arcadian.getArcadians().remove(player.getUniqueId());
		
		event.setQuitMessage(Globals.color(String.format("&c- &7%s has left the server.", player.getName())));
	}
}
