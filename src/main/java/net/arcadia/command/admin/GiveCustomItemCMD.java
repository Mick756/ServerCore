package net.arcadia.command.admin;

import net.arcadia.ACommand;
import net.arcadia.survival.items.CustomItem;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GiveCustomItemCMD extends ACommand {
	
	@Override
	public String alias() {
		return "givecustomitem";
	}
	
	@Override
	public String desc() {
		return "Receive a custom item.";
	}
	
	@Override
	public String usage() {
		return "[id]";
	}
	
	@Override
	public String permission() {
		return "arcadia.givecustomitem";
	}
	
	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		Player player = validatePlayer(sender);
		
		CustomItem item = CustomItem.getCustomItem(args[0]);
		if (item != null) {
			
			if (player.getInventory().firstEmpty() != -1) {
				
				respondf(sender, "&7You receive the&b %s&7 item!", item.name());
				
				player.getInventory().addItem(item.getItem());
			} else {
				respond(sender, "&cThere are no empty slots in your inventory!");
			}
		} else {
			respond(sender, "&cAn item with that name does not exist!");
		}
		
	}
}
