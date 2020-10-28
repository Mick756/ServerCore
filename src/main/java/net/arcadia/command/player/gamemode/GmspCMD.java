package net.arcadia.command.player.gamemode;

import net.arcadia.ACommand;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GmspCMD extends ACommand {
	
	@Override
	public String alias() {
		return "gmsp";
	}
	
	@Override
	public String desc() {
		return "Change your gamemode.";
	}
	
	@Override
	public String usage() {
		return "";
	}
	
	@Override
	public String permission() {
		return "arcadia.gamemode";
	}
	
	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		Player player = validatePlayer(sender);
		
		player.setGameMode(GameMode.SPECTATOR);
		respond(sender, "&7Changed your gamemode to&b spectator&7.");
	}
}

