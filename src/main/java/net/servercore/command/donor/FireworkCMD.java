package net.servercore.command.donor;

import net.servercore.ACommand;
import net.servercore.util.Util;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FireworkCMD extends ACommand {
	
	@Override
	public String alias() {
		return "firework";
	}
	
	@Override
	public String desc() {
		return "Launch a random firework.";
	}
	
	@Override
	public String usage() {
		return "";
	}
	
	@Override
	public String permission() {
		return "group.mvp";
	}
	
	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		Player player = validatePlayer(sender);
		
		Util.shootRandomFirework(player);
		respond(sender, "&7BOOM!");
		
	}
}
