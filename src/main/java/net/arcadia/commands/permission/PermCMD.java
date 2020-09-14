package net.arcadia.commands.permission;

import net.arcadia.ACommand;
import net.arcadia.ArcadiaCore;
import net.arcadia.util.execptions.InvalidGroupException;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class PermCMD extends ACommand {
	@Override
	public String alias() {
		return "perm";
	}
	
	@Override
	public String desc() {
		return "Add or remove perms from a group.";
	}
	
	@Override
	public String usage() {
		return "[group] [add|remove] [permission]";
	}
	
	@Override
	public String permission() {
		return "arcadia.perm";
	}
	
	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		validateArgsLength(args, 3);
		
		String group = args[0].toLowerCase();
		
		if (ArcadiaCore.getUserGroups().contains(group)) {
		
			String command = "lp group %s permission set %s %s";
			boolean toSet;
			
			if (args[1].equalsIgnoreCase("add")) {
				toSet = true;
			} else if (args[1].equalsIgnoreCase("remove")) {
				toSet = false;
			} else {
				respondiu(sender, label);
				return;
			}
			
			String permission = args[2];
			
			command = String.format(command, group, permission, toSet);
			Bukkit.dispatchCommand(sender, command);
			
			respondf(sender, "&7You have %s&7 the permission&b %s&7 %s the group&b %s&7.", toSet ? "&aadded" : "&cremoved", permission, toSet ? "to" : "from", group);
		} else {
			throw new InvalidGroupException();
		}
		
	}
}
