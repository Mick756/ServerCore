package net.arcadia.menu.menus.rank;

import com.cryptomorin.xseries.XMaterial;
import de.tr7zw.changeme.nbtapi.NBTItem;
import lombok.Getter;
import net.arcadia.menu.RankMenu;
import net.arcadia.menu.menus.cosmetic.RankColorMenu;
import net.arcadia.util.Globals;
import net.arcadia.util.ItemStackBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class MvpMenu extends RankMenu {
	
	public static String NAME = Globals.color("&0Select your&b MVP&0 perks!");
	
	private final @Getter Player player;
	public MvpMenu(Player player) {
		this.player = player;
	}
	
	@Override
	public String getRank() {
		return "MVP";
	}
	
	@Override
	public String title() {
		return NAME;
	}
	
	@Override
	public Inventory inventory() {
		Inventory inventory = this.createInventory(54);
		
		NBTItem border = new ItemStackBuilder(XMaterial.LIGHT_BLUE_STAINED_GLASS_PANE.parseMaterial()).setDisplayName(" ").toNBT();
		border.setString("action", "cancel");
		applyBorder(inventory, border.getItem(), true);
		
		NBTItem rankColor = new ItemStackBuilder(XMaterial.EMERALD.parseMaterial()).setDisplayName("&7Select your rank color!").addLore(
				" ", "&7Click here to modify the color", "&7of your rank!", " ", "&7This is special and only", "&7for&b&l MVP&7's!").toNBT();
		rankColor.setString("action", "open");
		rankColor.setString("inv", RankColorMenu.class.getName());
		inventory.setItem(10, rankColor.getItem());
		
		NBTItem close = new ItemStackBuilder(XMaterial.BARRIER.parseMaterial()).setDisplayName("&cClose Menu").toNBT();
		close.setString("action", "close");
		inventory.setItem(50, close.getItem());
		
		return inventory;
	}
}
