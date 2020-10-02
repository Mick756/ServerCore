package net.arcadia.menu.menus.rank;

import com.cryptomorin.xseries.XMaterial;
import de.tr7zw.changeme.nbtapi.NBTItem;
import lombok.Getter;
import net.arcadia.menu.RankMenu;
import net.arcadia.util.Globals;
import net.arcadia.util.ItemStackBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class VipMenu extends RankMenu {
	
	public static String NAME = Globals.color("&0Select your&a VIP&0 perks!");
	
	private final @Getter Player player;
	public VipMenu(Player player) {
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
		
		NBTItem border = new ItemStackBuilder(XMaterial.LIME_STAINED_GLASS_PANE.parseMaterial()).setDisplayName(" ").toNBT();
		border.setString("action", "cancel");
		applyBorder(inventory, border.getItem(), true);
		
		NBTItem close = new ItemStackBuilder(XMaterial.BARRIER.parseMaterial()).setDisplayName("&cClose Menu").toNBT();
		close.setString("action", "close");
		inventory.setItem(50, close.getItem());
		
		return inventory;
	}
}
