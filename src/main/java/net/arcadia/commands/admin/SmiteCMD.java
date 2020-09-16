package net.arcadia.commands.admin;

import net.arcadia.ACommand;
import net.arcadia.util.execptions.PlayerNotOnlineException;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SmiteCMD extends ACommand {
	
	@Override
	public String alias() {
		return "smite";
	}
	
	@Override
	public String desc() {
		return "Smite a player.";
	}
	
	@Override
	public String usage() {
		return "[player]";
	}
	
	@Override
	public String permission() {
		return "arcadia.smite";
	}
	
	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		Player player = validatePlayer(sender);
		validateArgsLength(args, 1);
		
		Player toSmite = Bukkit.getPlayer(args[0]);
		if (toSmite == null || !toSmite.isOnline()) {
			throw new PlayerNotOnlineException();
		}
		
		if (player.getUniqueId().equals(toSmite.getUniqueId())) {
			respond(sender, "&cYou can not smite yourself.");
			return;
		}
		
		toSmite.getWorld().strikeLightning(toSmite.getLocation());
		respondf(sender, "&7You smote&b %s&7.", toSmite.getName());
	}
}
