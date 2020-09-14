package net.arcadia.commands.global;

import net.arcadia.ACommand;
import net.arcadia.ArcadiaCore;
import net.arcadia.Arcadian;
import net.arcadia.misc.Home;
import net.arcadia.util.Util;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class HomeCMD extends ACommand {
	
	private static final Map<UUID, Long> waiting = new HashMap<>();
	
	private static long cooldown;
	
	public HomeCMD() {
		
		cooldown = TimeUnit.SECONDS.toMillis(ArcadiaCore.getInstance().getConfig().getLong("command-settings.tpa.cooldown"));
		
	}
	
	@Override
	public String alias() {
		return "home";
	}
	
	@Override
	public String desc() {
		return "Set and teleport to your homes";
	}
	
	@Override
	public String usage() {
		return "[create|delete|home|list]";
	}
	
	@Override
	public String permission() {
		return "";
	}
	
	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		Player player = validatePlayer(sender);
		UUID uuid = player.getUniqueId();
		Arcadian arcadian = Arcadian.get(player);
		
		if (args.length >= 1) {
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("list")) {
					respondnp(sender, "&6&m----------------------------------------------------");
					respondnp(sender, "\n&6&lList of all your homes.\n ");
					
					for (Home home : arcadian.getHomes()) {
						respondnp(sender, home.toString());
					}
					
					respondnp(sender, " ");
					respondnp(sender, "&6&m----------------------------------------------------");
					return;
				}
				
				Home home = homeExists(arcadian, args[0]);
				
				if (home == null) {
					respondf(sender, "&7A home with the name of '&b%s&7' does not exist!", args[0]);
					return;
				}
				
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
				
				home.teleport();
				waiting.put(uuid, System.currentTimeMillis() + cooldown);
				
				return;
			}
			
			if (args.length == 2) {
				if (args[0].equalsIgnoreCase("delete")) {
					Home home = homeExists(arcadian, args[1]);
					
					if (home == null) {
						respond(sender, "&cA home of that name does not exist!");
						return;
					}
					home.delete();
					
					respondf(sender, "&7Successfully deleted the home&b %s&7.", home.getName());
					return;
				}
				
				if (args[0].equalsIgnoreCase("create")) {
					if (homeExists(arcadian, args[1]) != null) {
						respond(sender, "&cYou already have a home with that name!");
						return;
					}
					
					if (args[1].equalsIgnoreCase("list")) {
						respond(sender, "&cYou are not able to create a home with that name.");
						return;
					}
					
					if (!canCreateHome(arcadian)) {
						String website = ArcadiaCore.getInstance().getConfig().getString("shop-link");
						respond(sender, "&cYou are not able to create anymore homes!");
						respond(sender, "&7Consider buying a rank to be able to create more homes!&6 " + website);
						return;
					}
					
					Home home = new Home(args[1], player, player.getLocation());
					arcadian.getHomes().add(home);
					
					respondf(sender, "&7Successfully created a home location named '&b%s&7'.", args[1]);
					return;
				}
				
			}
			
		}
		
		respondiu(sender, label);
	}
	
	private static boolean canCreateHome(Arcadian arcadian) {
		return arcadian.getHomes().size() < getMaxHomes(arcadian.getPlayer());
	}
	
	private static Home homeExists(Arcadian arcadian, String name) {
		
		for (Home home : arcadian.getHomes()) {
			if (!home.getName().equalsIgnoreCase(name)) continue;
			
			return home;
		}
		
		return null;
	}
	
	private static int getMaxHomes(Player player) {
		String[] perms = player.getEffectivePermissions().stream().map(PermissionAttachmentInfo::getPermission).toArray(String[]::new);
		
		for (String perm : perms) {
			if (!perm.startsWith("arcadia.home.")) continue;
			
			Integer num = Util.isInt(perm.replace("arcadia.home.", ""));
			
			if (num != null) {
				return num;
			}
			break;
		}
		
		return (player.isOp() ? Integer.MAX_VALUE : 1);
	}
	
}
