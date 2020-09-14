package net.arcadia.commands.kit;

import net.arcadia.ACommand;
import net.arcadia.misc.Kit;
import net.arcadia.util.Util;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CreateKitCMD extends ACommand {
	
	@Override
	public String alias() {
		return "createkit";
	}
	
	@Override
	public String desc() {
		return "Create a new kit with the items in your inventory.";
	}
	
	@Override
	public String usage() {
		return "[name] [cooldown]";
	}
	
	@Override
	public String permission() {
		return "arcadia.create.kit";
	}
	
	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		validateArgsLength(args, 2);
		Player player = validatePlayer(sender);
		String name = args[0];
		Inventory inventory = player.getInventory();
		
		Long cooldown = Util.getFromTimeFormat(args[1]);
		if (cooldown == null) {
			respondf(sender, "&cThe value '&e%s&c' must be a time!", args[1]);
			return;
		}
		
		List<ItemStack> items = new ArrayList<>();
		ItemStack[] stacks = inventory.getContents();
		for (ItemStack stack : stacks) {
			if (stack == null) continue;
			items.add(stack);
		}
		
		Kit kit = new Kit(name, cooldown, items.toArray(new ItemStack[0]));
		kit.save();
		
		Kit.getKits().add(kit);
		
		inventory.clear();
		
		respond(sender, "&7You just created the following kit:");
		respondnp(sender, kit.toString());
		
	}
}
