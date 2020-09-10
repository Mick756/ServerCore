package net.arcadia.commands;

import net.arcadia.ACommand;
import net.arcadia.Arcadian;
import net.arcadia.chat.Mute;
import net.arcadia.util.Globals;
import net.arcadia.util.Lang;
import net.arcadia.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MsgCMD extends ACommand {
	
	private static String FORMAT_SEND = Globals.color("&3To %s:");
	private static String FORMAT_RECEIVE = Globals.color("&3From %s:");
	
	@Override
	public String alias() {
		return "msg";
	}
	
	@Override
	public String desc() {
		return "Send a message to this player";
	}
	
	@Override
	public String usage() {
		return "[to] [message]";
	}
	
	@Override
	public String permission() {
		return "";
	}
	
	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		Player send = validatePlayer(sender);
		Arcadian aSend = Arcadian.get(send);
		
		if (args.length == 1 && args[0].equalsIgnoreCase("toggle")) {
			boolean receiving = !aSend.isReceivingMessages();
			aSend.setReceivingMessages(receiving);
			
			respondf(sender, "&7You are %s&7 receiving messages.", receiving ? "&bnow" : "&cno longer");
			return;
		}
		
		if (!(args.length > 1)) {
			respondiu(sender, label);
			return;
		}
		
		Player receive = Bukkit.getPlayer(args[0]);
		if (receive == null || !receive.isOnline()) {
			respond(sender, "&cThat player was not found!");
			return;
		}
		
		if (send.getUniqueId().equals(receive.getUniqueId())) {
			respond(sender, "&cYou can not send a message to yourself.");
			return;
		}
		
		Mute mute = Mute.playerIsMuted(aSend);
		if (mute != null) {
			
			String timeLeft = Util.toReadableTime(mute.endAtMilli() - System.currentTimeMillis());
			String message = Lang.getMessage("chat-cancelled-because-mute")
					.replace("%description%", mute.description()).replace("%time_left%", timeLeft);
			
			respondnp(sender, message);
			return;
		}
		
		Arcadian aReceive = Arcadian.get(receive);
		if (!aReceive.isReceivingMessages()) {
			respond(sender, "&cThis player currently has messages turned off!");
			return;
		}
		
		String message = Globals.descriptionFromArgs(1, args);
		
		aSend.sendMessage(false, String.format(FORMAT_SEND, receive.getName()) + "&7 " + message);
		aReceive.sendMessage(false, String.format(FORMAT_RECEIVE, send.getName()) + "&7 " + message);
		aReceive.setLastMessaged(aSend);
	}
}
