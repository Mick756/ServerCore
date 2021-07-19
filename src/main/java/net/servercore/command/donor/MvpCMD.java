package net.servercore.command.donor;

import com.cryptomorin.xseries.XSound;
import net.servercore.ACommand;
import net.servercore.ServerCore;
import net.servercore.menu.menus.rank.MvpMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MvpCMD extends ACommand {
	
	@Override
	public String alias() {
		return "mvp";
	}
	
	@Override
	public String desc() {
		return "Access all the MVP cosmetics.";
	}
	
	@Override
	public String usage() {
		return "";
	}
	
	@Override
	public String permission() {
		return "group.mvp";
	}
	
	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		Player player = validatePlayer(sender);
		
		if (player.hasPermission("group.mvp")) {
		
			MvpMenu menu = new MvpMenu(player);
			player.openInventory(menu.inventory());
			XSound.BLOCK_CHEST_OPEN.play(player);
			respond(sender, "&7Choose your cosmetic features from the&b MVP&7 cosmetic menu!");
			
		} else {
			String website = ServerCore.getInstance().getConfig().getString("shop-link");
			
			respond(sender, "&cYou do not have the&b MVP&c rank! Purchase at:");
			respondfnp(sender, "&6&l%s", website);
		}
	}
}
