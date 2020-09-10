package net.arcadia.commands;

import net.arcadia.ACommand;
import net.arcadia.Arcadian;
import net.arcadia.util.Globals;
import net.arcadia.util.Util;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.regex.Pattern;

public class NickCMD extends ACommand {
	
	private static Pattern alphaPattern = Pattern.compile("[^a-zA-Z0-9&]");
	
	@Override
	public String alias() {
		return "nick";
	}
	
	@Override
	public String desc() {
		return "Set your nick name.";
	}
	
	@Override
	public String usage() {
		return "";
	}
	
	@Override
	public String permission() {
		return "arcadia.nick";
	}
	
	private static final String[] nickHelp = Arrays.stream(new String[]{
			"&6&m----------------------------------------------------",
			" ",
			"&6&lWelcome to the Nick help page.",
			"&c   Staff can see through nicks but regular players can not.",
			" ",
			"&6&lAvailable commands:",
			"&e   /nick set [nick]&6 -&7 Set your nick.",
			"&e   /nick clear&6 -&7 Clear your nick.",
			" ",
			"&6&m----------------------------------------------------"
	}).map(Globals::color).toArray(String[]::new);
	
	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		Player player = validatePlayer(sender);
		Arcadian arcadian = Arcadian.get(player);
		
		if (args.length != 0) {
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("clear")) {
					arcadian.setNick("");
					arcadian.updateNameDisplay();
					
					respond(sender, "&7You have reset your nick!");
					return;
				}
			}
			
			if (args.length == 2) {
				if (args[0].equalsIgnoreCase("set")) {
					String nick = args[1];
					
					if (Util.filterText(nick)) {
						respond(sender, "&cThis nick contains inappropriate language!");
						return;
					}
					
					if (alphaPattern.matcher(nick).find()) {
						respond(sender, "&cNicks can only contain alphanumeric characters!");
						return;
					}
					
					String coloredNick = Globals.color(nick);
					
					arcadian.setNick(coloredNick);
					arcadian.updateNameDisplay();
					
					respondf(sender, "&7You have now set your nick to %s&7.", coloredNick);
					return;
				}
			}
			
		}
		player.sendMessage(nickHelp);
	}
}
