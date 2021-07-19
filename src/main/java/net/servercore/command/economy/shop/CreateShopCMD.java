package net.servercore.command.economy.shop;

import net.servercore.ACommand;
import net.servercore.util.Util;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreateShopCMD extends ACommand {
	
	@Override
	public String alias() {
		return "createshop";
	}
	
	@Override
	public String desc() {
		return "Create a shop by placing an item in an item frame.";
	}
	
	@Override
	public String usage() {
		return "[price|cancel]";
	}
	
	@Override
	public String permission() {
		return "arcadia.shop.create";
	}
	
	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		Player player = validatePlayer(sender);
		validateArgsLength(args, 1);
		
		if (args[0].equalsIgnoreCase("cancel")) {
			if (!getCreatingShop().containsKey(player.getUniqueId())) {
				respond(sender, "&cYou are not currently creating a shop.");
				return;
			}
			
			getCreatingShop().remove(player.getUniqueId());
			respond(sender, "&7You have cancelled the shop you were creating.");
			return;
		}
		
		Double price = Util.isDouble(args[0]);
		if (price == null) {
			respond(sender , "&cYou must enter a valid price!");
			return;
		}
		
		respond(sender, "&7Place an item into an item frame to create a shop.");
		respond(sender, "&4NOTES:");
		respond(sender, "&c - A shop will automatically detect if its a tool and only allow selling/buying one item at a time.");
		respond(sender, "&c - A shop will automatically detect the stack size of a non-tool and allow selling/buying accordingly.");
		getCreatingShop().put(player.getUniqueId(), price);
		
	}
}
