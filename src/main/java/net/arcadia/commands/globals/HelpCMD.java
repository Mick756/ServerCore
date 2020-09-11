package net.arcadia.commands.globals;

import net.arcadia.ACommand;
import org.bukkit.command.CommandSender;

public class HelpCMD extends ACommand {
	
	@Override
	public String alias() {
		return "help";
	}
	
	@Override
	public String desc() {
		return "Get a list of useful hints for navigating the server.";
	}
	
	@Override
	public String usage() {
		return "[page]";
	}
	
	@Override
	public String permission() {
		return "";
	}
	
	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		
		respond(sender, "&7Here are all the server commands you are able to use:");
		for (ACommand cmd : ACommand.getCommands()) {
			if (cmd.hasPerm(sender)) {
				respondfnp(sender, "&6/%s %s\n   &7%s", cmd.alias(), cmd.usage(), cmd.desc());
			}
		}
	}
}
