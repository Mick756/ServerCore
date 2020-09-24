package net.arcadia.commands.global;

import net.arcadia.ACommand;
import net.arcadia.ArcadiaCore;
import net.arcadia.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class TpaCMD extends ACommand {
	
	private static final Map<UUID, Long> waiting = new HashMap<>();
	private static final Map<UUID, UUID> tpas = new HashMap<>();
	
	private static long cooldown;
	
	public TpaCMD() {
		
		cooldown = TimeUnit.SECONDS.toMillis(ArcadiaCore.getInstance().getConfig().getLong("command-settings.tpa.cooldown"));
		
	}
	
	@Override
	public String alias() {
		return "tpa";
	}
	
	@Override
	public String desc() {
		return "Send a teleport request to a player.";
	}
	
	@Override
	public String usage() {
		return "[player|accept|decline]";
	}
	
	@Override
	public String permission() {
		return "";
	}
	
	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		validateArgsLength(args, 1);
		Player player = validatePlayer(sender);
		
		UUID uuid = player.getUniqueId();
		if (args[0].equalsIgnoreCase("accept")) {
			if (!tpas.containsKey(uuid)) {
				respond(sender, "&cYou do not have a tpa request to accept!");
				return;
			}
			
			Player tpa = Bukkit.getPlayer(tpas.get(uuid));
			if (tpa != null && tpa.isOnline()) {
				respondf(sender, "&7Teleporting&b %s&7 to your location...", tpa.getName());
				
				Location pLoc = player.getLocation().clone();
				if (player.isFlying()) {
					double y = pLoc.getWorld().getHighestBlockYAt(pLoc.getBlockX(), pLoc.getBlockZ());
					pLoc.setY(y);
				}
				
				respond(tpa, "&7Teleporting you now...");
				tpa.teleport(pLoc);
			} else {
				respond(sender, "&cThat player is no longer online, request cancelled.");
			}
		} else if (args[0].equalsIgnoreCase("decline")) {
			if (!tpas.containsKey(uuid)) {
				respond(sender, "&cYou do not have a tpa request to decline!");
				return;
			}
			
			Player tpaer = Bukkit.getPlayer(tpas.get(player.getUniqueId()));
			tpas.remove(player.getUniqueId());
			respond(sender, "&cYou declined the tpa request!");
			if (tpaer != null && tpaer.isOnline()) respondf(sender, "&e%s&c declined your tpa request!", player.getName());
		} else {
			
			if (waiting.containsKey(uuid)) {
				long timeLeft = waiting.get(uuid) - System.currentTimeMillis();
				
				if (timeLeft < 0) {
					waiting.remove(uuid);
					this.execute(sender, label, args);
					return;
				}
				
				respondf(sender, "&cYou still must wait&e %s&c to use this command.", Util.toReadableTime(timeLeft));
				return;
			}
			
			Player tpa = Bukkit.getPlayer(args[0]);
			if (tpa == null || !tpa.isOnline()) {
				respond(sender, "&cThat player was not found!");
				return;
			}
			
			if (tpa.getUniqueId().equals(player.getUniqueId())) {
				respond(sender, "&cYou can not tpa to yourself.");
				return;
			}
			
			tpas.put(tpa.getUniqueId(), player.getUniqueId());
			respondf(sender, "&7You sent a tpa request to&b %s&7.", tpa.getName());
			respond(sender, "&7This tpa request will expire in&c 10 seconds&7.");
			respondf(tpa, "&b%s&7 would like to teleport to you.", player.getName());
			respond(tpa, "&7You can either&b /tpa accept&7 or&b /tpa decline&7.");
			respond(tpa, "&7This tpa request will expire in&c 10 seconds&7.");
			
			new BukkitRunnable() {
				@Override
				public void run() {
					if (tpas.containsKey(tpa.getUniqueId())) {
						respondf(sender, "&cThe tpa request to&e %s&c expired!", tpa.getName());
					}
					
					tpas.remove(tpa.getUniqueId());
				}
			}.runTaskLater(ArcadiaCore.getInstance(), 200);
			
			waiting.put(uuid, System.currentTimeMillis() + cooldown);
		}
	}
}
