package net.arcadia.commands.player.gamemode;

import net.arcadia.ACommand;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GmsCMD extends ACommand {
	
	@Override
	public String alias() {
		return "gms";
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
		
		player.setGameMode(GameMode.SURVIVAL);
		respond(sender, "&7Changed your gamemode to&b survival&7.");
	}
}

