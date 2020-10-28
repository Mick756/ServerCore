package net.arcadia.command.admin;

import net.arcadia.ACommand;
import net.arcadia.ArcadiaCore;
import net.arcadia.Arcadian;
import net.arcadia.util.Globals;
import net.arcadia.util.Util;
import net.arcadia.util.execptions.InvalidGroupException;
import net.arcadia.util.execptions.PlayerNotOnlineException;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class TagCMD extends ACommand {
	
	private static final String[] tagsHelp = Arrays.stream(new String[]{
			"&6&m----------------------------------------------------",
			" ",
			"&6&lWelcome to the tags modification menu.",
			"&c   All commands except ctag modify a player's group &c&nand&r&c tag.",
			"&c   If a custom tag is set, group tags will not show up.",
			" ",
			"&6&lAvailable commands:",
			"&e   /tags tag [group] [tag]&6 -&7 Change the tag of a group.",
			"&e   /tags ctag [player] [ctag]&6 -&7 Give a player a custom tag.",
			"&e   /tags ctag [player] clear&6 -&7 Clear a custom tag of a player.",
			"&e   /tags set [player] [group]&6 -&7 Set the group of a player",
			"&e   /tags remove [player]&6 -&7 Reset a player back to Member",
			"&e   /tags reload&6 -&7 Reload all tags on the server.",
			" ",
			"&6&lAvailable groups:",
			"&e   " + Util.toReadableList(ArcadiaCore.getUserGroups()),
			" ",
			"&6&m----------------------------------------------------"
	}).map(Globals::color).toArray(String[]::new);
	
	@Override
	public String alias() {
		return "tags";
	}
	
	@Override
	public String desc() {
		return "Add/remove/modify groups from a player.";
	}
	
	@Override
	public String usage() {
		return "[action] [player] [group]";
	}
	
	@Override
	public String permission() {
		return "arcadia.tags";
	}
	
	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(tagsHelp);
		} else {
			
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("reload")) {
					reloadGroupTags();
					
					respond(sender, "&7You have reloaded all the colored tags for every user.");
					return;
				}
			}
			
			if (args.length == 2 || args.length == 3) {
				
				if (args.length == 2) {
					if (args[0].equalsIgnoreCase("remove")) {
						Player player = playerOnline(args[1]);
						Arcadian arcadian = Arcadian.get(player);
						
						arcadian.setCustomTag(null);
						arcadian.changeGroup("Member");
						
						respondf(sender, "&7You reset %s to Member.", player.getName());
					}
					
				} else {
					
					if (args[0].equalsIgnoreCase("tag")) {
						String group = args[1].toLowerCase();
						groupExists(group);
						
						String newTag = Globals.color(args[2]);
						ArcadiaCore ac = ArcadiaCore.getInstance();
						ac.getConfig().set("tags." + group.toLowerCase(), args[2]);
						ac.saveConfig();
						
						reloadGroupTags();
						respondf(sender, "&7You reset the group &e%s&7's tag to %s&7.", group, newTag);
						return;
					}
					
					if (args[0].equalsIgnoreCase("ctag")) {
						Player player = playerOnline(args[1]);
						Arcadian arcadian = Arcadian.get(player);
						
						if (args[2].equalsIgnoreCase("clear")) {
							arcadian.setCustomTag("");
							arcadian.updateNameDisplay();
							
							respondf(sender, "&7You have reset %s's custom tag.", player.getName());
							return;
						}
						
						String cTag = args[2];
						
						if (Util.filterText(cTag))  {
							arcadian.sendMessage(true, "&cThis tag was blocked because it contained inappropriate language!");
							return;
						}
						
						arcadian.setCustomTag(cTag);
						arcadian.updateNameDisplay();
						
						respondf(sender, "&7You changed %s's custom tag to %s&7.", player.getName(), Globals.color(cTag));
						return;
					}
					
					if (args[0].equalsIgnoreCase("set")) {
						String group = args[2].toLowerCase();
						groupExists(group);
						
						try {
							Player player = playerOnline(args[1]);
							Arcadian arcadian = Arcadian.get(player);
							
							arcadian.changeGroup(group);
							respondf(sender, "&7You changed %s's group to %s.", player.getName(), group);
						} catch (PlayerNotOnlineException ex) {
							OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(args[1]);
							
							if (player.hasPlayedBefore()) {
								Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.format("lp user %s parent set %s", player.getName(), group));
								
								respondf(sender, "&7You changed %s's group to %s.", player.getName(), group);
							} else {
								respond(sender, "&cThat player was not found because they have never played on the server!");
							}
						}
					} else {
						respondiu(sender, label);
						this.execute(sender, label, new String[]{});
					}
				}
			} else {
				respondiu(sender, label);
				sender.sendMessage(tagsHelp);
			}
		}
	}
	
	private void reloadGroupTags() {
		ArcadiaCore core = ArcadiaCore.getInstance();
		core.reloadConfig();
		core.updateUserGroups();
		
		for (Arcadian arcadian : Arcadian.getArcadians().values()) {
			arcadian.updateNameDisplay();
		}
	}
	
	private Player playerOnline(String name) {
		Player player = ArcadiaCore.getInstance().getServer().getPlayer(name);
		if (player == null || !player.isOnline()) {
			throw new PlayerNotOnlineException();
		}
		return player;
	}
	
	private void groupExists(String group) {
		if (!ArcadiaCore.getUserGroups().contains(group.toLowerCase()))
			throw new InvalidGroupException();
	}
}
