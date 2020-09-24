package net.arcadia.menu.menus.cosmetic;

import com.cryptomorin.xseries.XMaterial;
import de.tr7zw.changeme.nbtapi.NBTItem;
import lombok.Getter;
import net.arcadia.menu.ArcadiaMenu;
import net.arcadia.menu.menus.rank.MvpMenu;
import net.arcadia.util.Globals;
import net.arcadia.util.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class RankColorMenu extends ArcadiaMenu {
	
	public static String NAME = Globals.color("&0Select your&b MVP&0 rank color!");
	
	private final @Getter Player player;
	public RankColorMenu(Player player) {
		this.player = player;
	}
	
	@Override
	public String title() {
		return NAME;
	}
	
	@Override
	public Inventory inventory() {
		Inventory inventory = this.createInventory(54);
		
		NBTItem border = new ItemStackBuilder(XMaterial.PURPLE_STAINED_GLASS_PANE.parseMaterial()).setDisplayName(" ").toNBT();
		border.setString("action", "cancel");
		applyBorder(inventory, border.getItem(), true);
		
		ItemColorCombo[] dyes = new ItemColorCombo[] {
				new ItemColorCombo(new ItemStackBuilder(XMaterial.BLACK_DYE).setDisplayName("&7Choose&0 Black"), ChatColor.BLACK),
				new ItemColorCombo(new ItemStackBuilder(XMaterial.BLUE_DYE).setDisplayName("&7Choose&1 Blue"), ChatColor.DARK_BLUE),
				new ItemColorCombo(new ItemStackBuilder(XMaterial.CYAN_DYE).setDisplayName("&3Choose&b Cyan"), ChatColor.AQUA),
				new ItemColorCombo(new ItemStackBuilder(XMaterial.GREEN_DYE).setDisplayName("&7Choose&2 Green"), ChatColor.DARK_GREEN),
				new ItemColorCombo(new ItemStackBuilder(XMaterial.GRAY_DYE).setDisplayName("&7Choose&8 Gray"), ChatColor.DARK_GRAY),
				new ItemColorCombo(new ItemStackBuilder(XMaterial.LIGHT_BLUE_DYE).setDisplayName("&7Choose&9 Light Blue"), ChatColor.BLUE),
				new ItemColorCombo(new ItemStackBuilder(XMaterial.LIGHT_GRAY_DYE).setDisplayName("&7Choose&7 Light Gray"), ChatColor.GRAY),
				new ItemColorCombo(new ItemStackBuilder(XMaterial.YELLOW_DYE).setDisplayName("&7Choose&e Yellow"), ChatColor.YELLOW),
				new ItemColorCombo(new ItemStackBuilder(XMaterial.MAGENTA_DYE).setDisplayName("&7Choose&5 Dark Purple"), ChatColor.DARK_PURPLE),
				new ItemColorCombo(new ItemStackBuilder(XMaterial.PINK_DYE).setDisplayName("&7Choose&d Purple"), ChatColor.LIGHT_PURPLE),
				new ItemColorCombo(new ItemStackBuilder(XMaterial.LIME_DYE).setDisplayName("&7Choose&7 Lime"), ChatColor.GREEN),
				new ItemColorCombo(new ItemStackBuilder(XMaterial.ORANGE_DYE).setDisplayName("&7Choose&6 Gold"), ChatColor.GOLD),
				new ItemColorCombo(new ItemStackBuilder(XMaterial.RED_DYE).setDisplayName("&7Choose&c Red"), ChatColor.RED),
				new ItemColorCombo(new ItemStackBuilder(XMaterial.WHITE_DYE).setDisplayName("&7Choose&f White"), ChatColor.WHITE)
		};
		
		int index = 19;
		for (ItemColorCombo dye : dyes) {
			dye.getBuilder().addLore("&7Click to change your rank color!");
			NBTItem dyeItem = dye.getBuilder().toNBT();
			dyeItem.setString("action", "mvp_rank");
			dyeItem.setString("rankcolor", dye.color.toString());
			
			while (inventory.getItem(index) != null) {
				index++;
			}
			
			inventory.setItem(index, dyeItem.getItem());
			index++;
		}
		
		NBTItem back = new ItemStackBuilder(XMaterial.ARROW.parseMaterial()).setDisplayName("&bGo Back").toNBT();
		back.setString("action", "open");
		back.setString("inv", MvpMenu.class.getName());
		inventory.setItem(48, back.getItem());
		
		NBTItem close = new ItemStackBuilder(XMaterial.BARRIER.parseMaterial()).setDisplayName("&cClose Menu").toNBT();
		close.setString("action", "close");
		inventory.setItem(50, close.getItem());
		
		return inventory;
	}
	
	private static class ItemColorCombo {
		
		private final @Getter ItemStackBuilder builder;
		private final @Getter ChatColor color;
		
		public ItemColorCombo(ItemStackBuilder builder, ChatColor color) {
			this.builder = builder;
			this.color = color;
		}
		
	}
}
