package net.arcadia.menu.menus;

import com.cryptomorin.xseries.XMaterial;
import de.tr7zw.changeme.nbtapi.NBTItem;
import lombok.Getter;
import net.arcadia.menu.ArcadiaMenu;
import net.arcadia.misc.ShopFrameItem;
import net.arcadia.util.Globals;
import net.arcadia.util.ItemStackBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ShopMenu extends ArcadiaMenu {
	
	public static String NAME = Globals.color("&0Buy some items!");
	
	private @Getter Player player;
	private @Getter ShopFrameItem item;
	private @Getter double price;
	
	private int[] amountsToBuy;
	
	public ShopMenu(Player player, ShopFrameItem item, double price) {
		this.player = player;
		this.item = item;
		this.price = price;
		
		switch (this.item.getItem().getMaxStackSize()) {
			case 64:
				amountsToBuy = new int[]{1, 32, 64};
				break;
			case 32:
				amountsToBuy = new int[]{1, 16, 32};
				break;
			case 16:
				amountsToBuy = new int[]{1, 8, 16};
				break;
			case 8:
				amountsToBuy = new int[]{1, 4, 8};
				break;
			default:
				amountsToBuy = new int[]{1, 1, 1};
				break;
		}
		
	}
	
	@Override
	public String title() {
		return NAME;
	}
	
	@Override
	public Inventory inventory() {
		Inventory inventory = createInventory(27);
		
		int[] sellSlots = new int[]{12, 11, 10};
		int[] buySlots = new int[]{14, 15, 16};
		
		for (int i = 2; i >= 0; i--) {
			inventory.setItem(sellSlots[i], getSellItem(amountsToBuy[i], this.price));
			inventory.setItem(buySlots[i], getBuyItem(amountsToBuy[i], this.price));
		}
		
		inventory.setItem(13, new ItemStackBuilder(this.item.getDisplayItem()).setDisplayName("&c<-- Sell&7 |&6 Buy -->").build());
		return inventory;
	}
	
	@Override
	public String getPermission() {
		return "";
	}
	
	private static ItemStack getBuyItem(int amount, double price) {
		NBTItem item = new ItemStackBuilder(XMaterial.GOLD_INGOT)
				.setAmount(amount)
				.setDisplayName("&aBuy&6 " + amount + "&a of this item")
				.addLore(" ", "&7Price:&6 $" + (price * amount))
				.toNBT();
		item.setString("action", "buy");
		item.setInteger("amount", amount);
		item.setDouble("price", (price * amount));
		return item.getItem();
	}
	
	private static ItemStack getSellItem(int amount, double price) {
		NBTItem item = new ItemStackBuilder(XMaterial.GOLD_INGOT)
				.setAmount(amount)
				.setDisplayName("&cSell&6 " + amount + "&c of this item")
				.addLore(" ", "&7Worth:&6 $" + ((price * amount) / 2.00))
				.toNBT();
		item.setString("action", "sell");
		item.setInteger("amount", amount);
		item.setDouble("price", ((price * amount) / 2.00));
		return item.getItem();
	}
}
