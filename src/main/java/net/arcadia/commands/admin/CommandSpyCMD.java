package net.arcadia.commands.admin;

import lombok.Getter;
import net.arcadia.ACommand;
import net.arcadia.util.Globals;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandSpyCMD extends ACommand {
	
	private static @Getter
	final
	List<Player> spies = new ArrayList<>();
	
	public static String getFormattedMessage(CommandSender sender, String command) {
		return Globals.color("&3[CommandSpy]&7 %p%: %c%".replace("%p%", sender.getName()).replace("%c%", command));
	}
	
	@Override
	public String alias() {
		return "commandspy";
	}
	
	@Override
	public String desc() {
		return "Be sent every command made from every player live.";
	}
	
	@Override
	public String usage() {
		return "";
	}
	
	@Override
	public String permission() {
		return "arcadia.commandspy";
	}
	
	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		Player player = validatePlayer(sender);
		
		if (spies.contains(player)) {
			spies.remove(player);
			
			respond(sender, "&7You are&c no longer&7 spying on every command from the server.");
		} else {
			spies.add(player);
			
			respond(sender, "&7You are&b now&7 spying on every command from the server.");
		}
	}
}
