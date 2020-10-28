package net.arcadia.chat;

import net.arcadia.ArcadiaCore;
import net.arcadia.Arcadian;
import net.arcadia.util.Globals;
import net.arcadia.util.Lang;
import net.arcadia.util.Util;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;

public class ChatListener implements Listener {
	
	private static String chatFormat;
	private final boolean filter;
	
	public ChatListener() {
		
		chatFormat = ArcadiaCore.getStringOrDefault("chat-format", "%prefix% %name%:&f %message%", true)
				.replace("%name%", "%s")
				.replace("%message%", "%s");
		
		filter = ArcadiaCore.getBooleanOrDefault("chat-filter", false, true);
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		Arcadian arcadian = Arcadian.get(player);
		
		Mute mute = Mute.playerIsMuted(arcadian);
		
		if (mute != null) {
			event.setCancelled(true);
			
			String timeLeft = Util.toReadableTime(mute.endAtMilli() - System.currentTimeMillis());
			String cancel = Lang.getMessage("chat-cancelled-because-mute")
					.replace("%description%", mute.description()).replace("%time_left%", timeLeft);
			
			arcadian.sendMessage(false, cancel);
			return;
		}
		
		if (filter && Util.filterText(event.getMessage())) {
			arcadian.sendMessage(true, "&cThis message was blocked because it contained inappropriate language!");
			event.setCancelled(true);
			return;
		}
		
		event.setFormat(Globals.color(chatFormat).replace("%prefix%", arcadian.getTag()));
	}
	
}
