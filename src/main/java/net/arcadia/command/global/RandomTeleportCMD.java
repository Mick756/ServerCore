package net.arcadia.command.global;

import net.arcadia.ACommand;
import net.arcadia.ArcadiaCore;
import net.arcadia.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class RandomTeleportCMD extends ACommand {
	
	private static final Map<UUID, Long> cooldowns = new HashMap<>();
	
	private static long cooldown;
	private static int minX;
	private static int maxX;
	private static int minZ;
	private static int maxZ;
	
	public RandomTeleportCMD() {
		FileConfiguration config = ArcadiaCore.getInstance().getConfig();
		
		cooldown = TimeUnit.SECONDS.toMillis(config.getLong("command-settings.random-teleport.cooldown"));
		
		String rootPath = "command-settings.random-teleport.bounds";
		
		minX = config.getInt(rootPath + ".x.min");
		maxX = config.getInt(rootPath + ".x.max");
		minZ = config.getInt(rootPath + ".z.min");
		maxZ = config.getInt(rootPath + ".z.max");
		
	}
	
	@Override
	public String alias() {
		return "random-teleport";
	}
	
	@Override
	public String desc() {
		return "Teleport to a random location in your world.";
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
		
		if (!player.getWorld().getName().equals("world")) {
			respond(sender, "&cYou must be in the overworld to use this command.");
			return;
		}
		
		if (!cooldowns.containsKey(player.getUniqueId())) {
			
			Location rLoc = this.getRandomLocation();
			player.teleport(rLoc);
			
			if (cooldown != -1) {
				long endAt = System.currentTimeMillis() + cooldown;
				cooldowns.put(player.getUniqueId(), endAt);
			}
			
			respondf(sender, "&7You have been teleported to [&eX:&b %d&7, &eY:&b %d&7, &eZ:&b %d&7].", rLoc.getBlockX(), rLoc.getBlockY(), rLoc.getBlockZ());
		} else {
			Long endAt = cooldowns.get(player.getUniqueId());
			if (endAt <= System.currentTimeMillis()) {
				cooldowns.remove(player.getUniqueId());
				
				this.execute(sender, label, args);
			} else {
				
				respondf(sender, "&cPlease wait to use this command. Time remaining:&e %s", Util.toReadableTime(endAt - System.currentTimeMillis()));
			}
		}
	}
	
	private Location getRandomLocation() {
		World world = Bukkit.getWorld("world");
		double x = minX + (Math.random() * (maxX - minX + 1));
		double z = minZ + (Math.random() * (maxZ - minZ + 1));
		
		return new Location(world, x + 0.5, world.getHighestBlockYAt((int) x, (int) z) + 1, z + 0.5);
	}
}
