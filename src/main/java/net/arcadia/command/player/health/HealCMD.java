package net.arcadia.command.player.health;

import net.arcadia.ACommand;
import net.arcadia.ArcadiaCore;
import net.arcadia.util.Globals;
import net.arcadia.util.execptions.PlayerNotOnlineException;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HealCMD extends ACommand {
	
	@Override
	public String alias() {
		return "heal";
	}
	
	@Override
	public String desc() {
		return "Heal yourself or a player.";
	}
	
	@Override
	public String usage() {
		return "[player]";
	}
	
	@Override
	public String permission() {
		return "arcadia.heal";
	}
	
	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		Player player = validatePlayer(sender);
		
		if (args.length == 0) {
			player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue());
			respond(sender, "&7You have been healed.");
			return;
		}
		
		Player toChange = Bukkit.getPlayer(args[0]);
		if (toChange == null || !toChange.isOnline()) {
			throw new PlayerNotOnlineException();
		}
		
		toChange.setHealth(toChange.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue());
		toChange.sendMessage(ArcadiaCore.getPrefix() + String.format(Globals.color("&7You have been healed by&b %s&7."), player.getName()));
		respondf(sender, "&7You have been healed&b %s&7.", toChange.getName());
	}
}
