package net.servercore.command.admin;

import net.servercore.ACommand;
import net.servercore.util.execptions.PlayerNotOnlineException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpHereCMD extends ACommand {
	
	@Override
	public String alias() {
		return "tphere";
	}
	
	@Override
	public String desc() {
		return "Teleport a player to yourself.";
	}
	
	@Override
	public String usage() {
		return "[player]";
	}
	
	@Override
	public String permission() {
		return "arcadia.tphere";
	}
	
	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		Player player = validatePlayer(sender);
		validateArgsLength(args, 1);
		
		Player toTeleport = Bukkit.getPlayer(args[0]);
		if (toTeleport == null || !toTeleport.isOnline()) {
			throw new PlayerNotOnlineException();
		}
		
		if (player.getUniqueId().equals(toTeleport.getUniqueId())) {
			respond(sender, "&cYou can not teleport to yourself.");
			return;
		}
		
		Location loc = player.getLocation().clone();
		
		if (!player.isOnGround()) {
			int y = player.getWorld().getHighestBlockYAt(loc.getBlockX(), loc.getBlockZ());
			loc.setY(y + 1);
			
			toTeleport.teleport(loc);
		} else
			toTeleport.teleport(loc);
		
		respondf(sender, "&7You have teleported&b %s&7 to yourself.", toTeleport.getName());
	}
}
