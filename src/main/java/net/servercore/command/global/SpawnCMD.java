package net.servercore.command.global;

import net.servercore.ACommand;
import net.servercore.ServerCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class SpawnCMD extends ACommand {
	
	@Override
	public String alias() {
		return "spawn";
	}
	
	@Override
	public String desc() {
		return "Teleport to spawn";
	}
	
	@Override
	public String usage() {
		return "";
	}
	
	@Override
	public String permission() {
		return "";
	}
	
	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		Player player = validatePlayer(sender);
		
		if (args.length == 1 && args[0].equalsIgnoreCase("set")) {
			if (player.hasPermission("arcadia.setspawn")) {
				int x = player.getLocation().getBlockX();
				int y = player.getLocation().getBlockY();
				int z = player.getLocation().getBlockZ();
				
				FileConfiguration f = ServerCore.getInstance().getConfig();
				f.set("spawn.x", x);
				f.set("spawn.y", y);
				f.set("spawn.z", z);
				ServerCore.getInstance().saveConfig();
				
				World world = Bukkit.getWorld("world");
				ServerCore.setSpawn(new Location(world, x, y, z));
				
				respondf(sender, "&7You set spawn to [&eX:&b %d&7, &eY:&b %d&7, &eZ:&b %d&7].", x, y, z);
				return;
			}
		}
		
		respond(sender, "&7Teleporting you to the Arcadia spawn...");
		player.teleport(ServerCore.getSpawn());
	}
}
