package net.arcadia.commands.kit;

import net.arcadia.ACommand;
import net.arcadia.misc.Kit;
import org.bukkit.command.CommandSender;

public class DeleteKitCMD extends ACommand {
	
	@Override
	public String alias() {
		return "deletekit";
	}
	
	@Override
	public String desc() {
		return "Delete a kit.";
	}
	
	@Override
	public String usage() {
		return "[kit]";
	}
	
	@Override
	public String permission() {
		return "arcadia.kit.delete";
	}
	
	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		validateArgsLength(args, 1);
		
		Kit kit = Kit.getKit(args[0]);
		if (kit == null) {
			respondf(sender, "&cA kit with the name '&e%s&c' was not found!", args[0]);
			return;
		}
		
		kit.delete();
		respondf(sender, "&7Successfully deleted the kit&b %s&7.", kit.getName());
	}
}
