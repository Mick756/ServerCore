package net.servercore.command.economy.shop;

import net.servercore.ACommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class DeleteShopCMD extends ACommand {
	@Override
	public String alias() {
		return "deleteshop";
	}
	
	@Override
	public String desc() {
		return "Delete shop(s).";
	}
	
	@Override
	public String usage() {
		return "";
	}
	
	@Override
	public String permission() {
		return "arcadia.shop.delete";
	}
	
	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		Player player = validatePlayer(sender);
		validateArgsLength(args, 0);
		
		UUID uuid = player.getUniqueId();
		
		if (getDeletingShop().contains(uuid)) {
			respond(sender, "&7You are no longer removing shops.");
			getDeletingShop().remove(uuid);
		} else {
			respond(sender, "&7You are now removing shops.");
			getDeletingShop().add(uuid);
		}
	}
}
