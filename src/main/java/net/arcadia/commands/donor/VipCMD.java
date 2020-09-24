package net.arcadia.commands.donor;

import com.cryptomorin.xseries.XSound;
import net.arcadia.ACommand;
import net.arcadia.ArcadiaCore;
import net.arcadia.menu.menus.rank.VipMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VipCMD extends ACommand {
	
	@Override
	public String alias() {
		return "vip";
	}
	
	@Override
	public String desc() {
		return "Access all the VIP cosmetics.";
	}
	
	@Override
	public String usage() {
		return "";
	}
	
	@Override
	public String permission() {
		return "group.vip";
	}
	
	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		Player player = validatePlayer(sender);
		
		if (player.hasPermission("group.vip")) {
			
			VipMenu menu = new VipMenu(player);
			player.openInventory(menu.inventory());
			XSound.BLOCK_CHEST_OPEN.play(player);
			respond(sender, "&7Choose your cosmetic features from the&a VIP&7 cosmetic menu!");
			
		} else {
			String website = ArcadiaCore.getInstance().getConfig().getString("shop-link");
			
			respond(sender, "&cYou do not have the&a VIP&c rank! Purchase at:");
			respondfnp(sender, "&6&l%s", website);
		}
	}
}
