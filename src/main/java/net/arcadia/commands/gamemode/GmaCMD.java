package net.arcadia.commands.gamemode;

import net.arcadia.ACommand;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GmaCMD extends ACommand {
	
	@Override
	public String alias() {
		return "gma";
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
		
		player.setGameMode(GameMode.ADVENTURE);
		respond(sender, "&7Changed your gamemode to&b adventure&7.");
	}
}

