package net.arcadia.commands.admin;

import net.arcadia.ACommand;
import net.arcadia.ArcadiaCore;
import net.arcadia.Arcadian;
import net.arcadia.chat.*;
import net.arcadia.util.Globals;
import net.arcadia.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class MuteCMD extends ACommand {
	
	private static final String[] muteHelp = Arrays.stream(new String[]{
			"&6&m----------------------------------------------------",
			" ",
			"&6&lWelcome to the mutes menu.",
			"&c   The time for a mute is measured in minutes.",
			" ",
			"&6&lAvailable commands:",
			"&e   /mute all [time] [reason]&6 -&7 Mute every player except the unmutables.",
			"&e   /mute player [player] [time] [reason]&6 -&7 Mute a specific player.",
			"&e   /mute world [world] [time] [reason]&6 -&7 Mute a specific world.",
			"&e   /mute group [group] [time] [reason]&6 -&7 Mute a specific group",
			"&e   /mute check [player]&6 -&7 Check if a player is muted.",
			"&e   /mute list&6 -&7 Get the list of all active mutes.",
			"&e   /mute delete [index]&6 -&7 End a mute based off the number in the list.",
			"&e   /mute cancel&6 -&7 Cancel a created mute.",
			"&e   /mute confirm&6 -&7 Confirm and begin a mute.",
			" ",
			"&6&lAvailable groups:",
			"&e   " + Util.toReadableList(ArcadiaCore.getUserGroups()),
			" ",
			"&6&lAvailable worlds:",
			"&e   " + Util.toReadableList(Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList())),
			" ",
			"&6&m----------------------------------------------------"
	}).map(Globals::color).toArray(String[]::new);
	private final Map<CommandSender, Mute> waitingConfirmation = new HashMap<>();
	
	private static void sendConfirmationMessage(CommandSender sender, Mute mute) {
		mute.setEndTime();
		sender.sendMessage(Globals.color("&7Below is the mute you created."));
		sender.sendMessage(Globals.color(mute.toString()));
		sender.sendMessage(Globals.color("&eType&b /mute confirm&e to start the mute."));
	}
	
	@Override
	public String alias() {
		return "mute";
	}
	
	@Override
	public String desc() {
		return "Mute a player, a world, or all the chat.";
	}
	
	@Override
	public String usage() {
		return "[type] [name]";
	}
	
	@Override
	public String permission() {
		return "arcadia.mute";
	}
	
	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		if (args.length != 0) {
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("list")) {
					respondfnp(sender, "&6&m----------------------------------------------------");
					respondfnp(sender, " ");
					if (Mute.getMutes().isEmpty()) {
						respondfnp(sender, "&cThere are currently no active mutes.");
					} else {
						respondfnp(sender, "&bAll active mutes are listed below.\n ");
						int index = 1;
						for (Mute mute : Mute.getMutes()) {
							respondfnp(sender, "&c&l" + index + ": &r" + mute.toString());
							index++;
						}
					}
					respondfnp(sender, "\n&6&m----------------------------------------------------");
					return;
				}
				if (args[0].equalsIgnoreCase("confirm")) {
					if (!waitingConfirmation.containsKey(sender)) {
						respond(sender, "&cYou do not have any mutes awaiting confirmation.");
						return;
					}
					
					Mute m = waitingConfirmation.get(sender);
					m.setEndTime();
					m.start();
					
					respondfnp(sender, m.toString());
					respond(sender, "&7Mute started. View active mutes:&b /mute list");
					
					waitingConfirmation.remove(sender);
					return;
				}
				if (args[0].equalsIgnoreCase("cancel")) {
					if (!waitingConfirmation.containsKey(sender)) {
						respond(sender, "&cYou do not have any mutes awaiting confirmation.");
						return;
					}
					
					respond(sender, "&7Your mute awaiting confirmation was&c cancelled&7.");
					
					waitingConfirmation.remove(sender);
					return;
				}
			} else if (args.length == 2) {
				if (args[0].equalsIgnoreCase("check")) {
					Player player = Bukkit.getServer().getPlayer(args[1]);
					if (player == null) {
						respondf(sender, "&cThe player '&e%s&c' was not found!", args[1]);
						return;
					}
					
					Mute mute = Mute.playerIsMuted(Arcadian.get(player));
					if (mute == null) {
						respond(sender, "&7This player is not currently muted.");
						return;
					}
					
					respond(sender, "&7This is the player's current mute:");
					respondfnp(sender, mute.toString());
					return;
				}
				if (args[0].equalsIgnoreCase("delete")) {
					Integer index = Util.isInt(args[1]);
					
					if (index == null) {
						respondf(sender, "&cThe value '&e%s&c' must be a number!", args[1]);
						return;
					}
					
					if (Mute.getMutes().size() < index || index == 0) {
						respondf(sender, "&cThe index of '&e%s&c' was not found!", args[1]);
						return;
					}
					
					Mute m = Mute.getMutes().get(index - 1);
					Mute.getMutes().remove(m);
					respond(sender, "&7You deleted the following mute: ");
					respondfnp(sender, m.toString());
					return;
				}
			} else {
				
				if (args[0].equalsIgnoreCase("all")) {
					Long time = Util.getFromTimeFormat(args[1]);
					if (time == null) {
						respondf(sender, "&cThe value '&e%s&c' must be a time!", args[1]);
						return;
					}
					AllMute allMute = new AllMute(time, Globals.descriptionFromArgs(2, args));
					
					sendConfirmationMessage(sender, allMute);
					waitingConfirmation.put(sender, allMute);
					return;
				}
				
				if (args.length >= 4) {
					Long time = Util.getFromTimeFormat(args[2]);
					if (time == null) {
						respondf(sender, "&cThe value '&e%s&c' must be a time!", args[2]);
						return;
					}
					
					if (args[0].equalsIgnoreCase("player")) {
						Player player = Bukkit.getServer().getPlayer(args[1]);
						if (player == null) {
							respondf(sender, "&cThe player '&e%s&c' was not found!", args[1]);
							return;
						}
						PlayerMute playerMute = new PlayerMute(player, time, Globals.descriptionFromArgs(3, args));
						
						sendConfirmationMessage(sender, playerMute);
						waitingConfirmation.put(sender, playerMute);
						return;
					}
					
					if (args[0].equalsIgnoreCase("world")) {
						World world = Bukkit.getServer().getWorld(args[1]);
						if (world == null) {
							respondf(sender, "&cThe world '&e%s&c' was not found!", args[1]);
							return;
						}
						WorldMute worldMute = new WorldMute(world.getName(), time, Globals.descriptionFromArgs(3, args));
						
						sendConfirmationMessage(sender, worldMute);
						waitingConfirmation.put(sender, worldMute);
						return;
					}
					
					if (args[0].equalsIgnoreCase("group")) {
						String group = args[1].toLowerCase();
						if (!ArcadiaCore.getUserGroups().contains(group)) {
							respondf(sender, "&cThe group '&e%s&c' was not found!", group);
							return;
						}
						GroupMute groupMute = new GroupMute(group, time, Globals.descriptionFromArgs(3, args));
						
						sendConfirmationMessage(sender, groupMute);
						waitingConfirmation.put(sender, groupMute);
						return;
					}
				}
			}
		}
		sender.sendMessage(muteHelp);
	}
}
