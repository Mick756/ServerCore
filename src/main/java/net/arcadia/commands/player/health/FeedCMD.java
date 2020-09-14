package net.arcadia.commands.player.health;

import net.arcadia.ACommand;
import net.arcadia.ArcadiaCore;
import net.arcadia.util.Globals;
import net.arcadia.util.execptions.PlayerNotOnlineException;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FeedCMD extends ACommand {
	
	@Override
	public String alias() {
		return "feed";
	}
	
	@Override
	public String desc() {
		return "Feed yourself or a player.";
	}
	
	@Override
	public String usage() {
		return "[player]";
	}
	
	@Override
	public String permission() {
		return "arcadia.feed";
	}
	
	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		Player player = validatePlayer(sender);
		
		if (args.length == 0) {
			player.setFoodLevel(20);
			respond(sender, "&7You have been fed.");
			return;
		}
		
		Player toChange = Bukkit.getPlayer(args[0]);
		if (toChange == null || !toChange.isOnline()) {
			throw new PlayerNotOnlineException();
		}
		
		toChange.setFoodLevel(20);
		toChange.sendMessage(ArcadiaCore.getPrefix() + String.format(Globals.color("&7You have been fed by&b %s&7."), player.getName()));
		respondf(sender, "&7You have been fed&b %s&7.", toChange.getName());
	}
}
