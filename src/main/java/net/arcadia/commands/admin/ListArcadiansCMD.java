package net.arcadia.commands.admin;

import net.arcadia.ACommand;
import net.arcadia.Arcadian;
import net.arcadia.util.Globals;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ListArcadiansCMD extends ACommand {
	
	@Override
	public String alias() {
		return "listarcadians";
	}
	
	@Override
	public String desc() {
		return "List every player that has joined the server.";
	}
	
	@Override
	public String usage() {
		return "";
	}
	
	@Override
	public String permission() {
		return "arcadian.listarcadians";
	}
	
	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		
		List<Arcadian> arcadianList = Arcadian.getAll();
		StringBuilder builder = new StringBuilder();
		
		for (Arcadian arcadian : arcadianList) {
			
			int indexOf = arcadianList.indexOf(arcadian);
			String colorCode = indexOf % 2 == 0 ? "&b" : "&c";
			
			builder.append(colorCode).append(arcadian.getOfflinePlayer().getName());
			
			if ((indexOf + 1) != arcadianList.size()) {
				builder.append("&7, ");
			}
			
		}
		
		String list = Globals.color(builder.toString());
		
		respond(sender, "&7List of every Arcadian that has joined the server:");
		respondfnp(sender, "%s", list);
	}
}
