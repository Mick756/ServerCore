package net.servercore.command.player.gamemode;

import net.servercore.ACommand;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GmCMD extends ACommand {
	
	@Override
	public String alias() {
		return "gm";
	}
	
	@Override
	public String desc() {
		return "Change your gamemode";
	}
	
	@Override
	public String usage() {
		return "[creative|survival|spectator|adventure] [player]";
	}
	
	@Override
	public String permission() {
		return "arcadia.gamemode";
	}
	
	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		Player player = validatePlayer(sender);
		
		if (args.length == 1 || args.length == 2) {
			
			GameMode gamemode = getGamemode(args[0]);
			
			if (gamemode == null) {
				respond(sender, "&cThat gamemode was not found!");
				return;
			}
			
			if (args.length == 1) {
				player.setGameMode(gamemode);
				respondf(sender, "&7You have changed your gamemode to&b %s&7.", gamemode.name().toLowerCase());
				return;
			}
			
			Player toChange = Bukkit.getPlayer(args[1]);
			if (toChange == null || !toChange.isOnline()) {
				respond(sender, "&cThat player was not found!");
				return;
			}
			
			toChange.setGameMode(gamemode);
			respondf(sender, "&7You changed&e %s&7's gamemode to&b %s&7.", toChange.getName(), gamemode.name().toLowerCase());
		}
		
	}
	
	private GameMode getGamemode(String gamemode) {
		gamemode = gamemode.toLowerCase();
		
		switch (gamemode) {
			case "creative":
			case "c":
				return GameMode.CREATIVE;
			case "survival":
			case "s":
				return GameMode.SURVIVAL;
			case "spectator":
			case "sp":
				return GameMode.SPECTATOR;
			case "adventure":
			case "a":
				return GameMode.ADVENTURE;
			default:
				return null;
		}
		
	}
}
