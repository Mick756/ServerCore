package net.servercore.command.kit;

import net.servercore.ACommand;
import net.servercore.misc.Kit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KitCMD extends ACommand {
	
	@Override
	public String alias() {
		return "kit";
	}
	
	@Override
	public String desc() {
		return "List or receive a kit.";
	}
	
	@Override
	public String usage() {
		return "[list|name]";
	}
	
	@Override
	public String permission() {
		return "";
	}
	
	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		validateArgsLength(args, 1);
		Player player = validatePlayer(sender);
		
		if (args[0].equalsIgnoreCase("list")) {
			respondfnp(sender, "&6&m----------------------------------------------------");
			respondfnp(sender, " ");
			if (Kit.getKits().isEmpty()) {
				respondfnp(sender, "&cThere are currently no kits to select from.");
			} else {
				respondfnp(sender, "&bAll kits are listed below.\n ");
				int index = 1;
				for (Kit kit : Kit.getKits()) {
					respondfnp(sender, "&c&l" + index + ": &r" + kit.toString());
					index++;
				}
			}
			respondfnp(sender, "\n&6&m----------------------------------------------------");
			return;
		}
		
		Kit kit = Kit.getKit(args[0]);
		if (kit == null) {
			respondf(sender, "&cA kit with the name '&e%s&c' was not found!", args[0]);
			return;
		}
		
		kit.give(player);
	}
}
