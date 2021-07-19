package net.servercore.command.admin;

import net.servercore.ACommand;
import net.servercore.ServerPlayer;
import net.servercore.util.Globals;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ListPlayersCMD extends ACommand {
	
	@Override
	public String alias() {
		return "listplayers";
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
		return "player.listplayers";
	}
	
	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		
		List<ServerPlayer> playerList = ServerPlayer.getAll();
		StringBuilder builder = new StringBuilder();
		
		for (ServerPlayer player : playerList) {
			
			int indexOf = playerList.indexOf(player);
			String colorCode = indexOf % 2 == 0 ? "&b" : "&c";
			
			builder.append(colorCode).append(player.getOfflinePlayer().getName());
			
			if ((indexOf + 1) != playerList.size()) {
				builder.append("&7, ");
			}
			
		}
		
		String list = Globals.color(builder.toString());
		
		respond(sender, "&7List of every Player that has joined the server:");
		respondfnp(sender, "%s", list);
	}
}
