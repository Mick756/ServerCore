package net.arcadia.commands.global;

import net.arcadia.ACommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PingCMD extends ACommand {
	
	@Override
	public String alias() {
		return "ping";
	}
	
	@Override
	public String desc() {
		return "Get your current ping to the server.";
	}
	
	@Override
	public String usage() {
		return "";
	}
	
	@Override
	public String permission() {
		return "arcadia.ping";
	}
	
	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		Player player = validatePlayer(sender);
		
		respondfnp(player, "&3[Ping]&7 Your current ping is&b %d&7ms.", player.spigot().getPing());
		
	}
}
