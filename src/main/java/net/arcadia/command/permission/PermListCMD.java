package net.arcadia.command.permission;

import net.arcadia.ACommand;
import net.arcadia.ArcadiaCore;
import net.arcadia.util.Util;
import net.arcadia.util.execptions.InvalidGroupException;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

import java.util.ArrayList;
import java.util.List;

public class PermListCMD extends ACommand {
	
	@Override
	public String alias() {
		return "permlist";
	}
	
	@Override
	public String desc() {
		return "List the permissions of a group.";
	}
	
	@Override
	public String usage() {
		return "[group]";
	}
	
	@Override
	public String permission() {
		return "arcadia.permlist";
	}
	
	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		validateArgsLength(args, 1);
		
		String group = args[0].toLowerCase();
		
		if (ArcadiaCore.getUserGroups().contains(group)) {
			
			net.milkbowl.vault.permission.Permission vaultPerm = ArcadiaCore.getPermission();
			List<String> perms = new ArrayList<>();
			
			if (vaultPerm.groupHas((String) null, group, "*")) {
				respond(sender, "&7Permissions for the group " + group + "\n&cAll permissions");
				return;
			}
			
			for (String perm : Bukkit.getPluginManager().getPermissions().stream().map(Permission::getName).toArray(String[]::new)) {
				if (vaultPerm.groupHas((String) null, group, perm)) {
					perms.add(perm);
				}
			}
			
			respond(sender, "&7Permissions for the group " + group + "\n&c" + Util.toReadableList(perms));
			
		} else {
			throw new InvalidGroupException();
		}
		
	}
}
