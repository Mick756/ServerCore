package net.servercore.menu.menus;

import com.cryptomorin.xseries.XMaterial;
import de.tr7zw.changeme.nbtapi.NBTItem;
import lombok.Getter;
import net.servercore.menu.ArcadiaMenu;
import net.servercore.misc.ShopFrameItem;
import net.servercore.survival.items.CustomItem;
import net.servercore.util.Globals;
import net.servercore.util.ItemStackBuilder;
import net.servercore.util.XItemStackBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ShopMenu extends ArcadiaMenu {
	
	public static String NAME = Globals.color("&0Buy some items!");
	
	private final @Getter Player player;
	private final @Getter ShopFrameItem item;
	private final @Getter double price;
	private @Getter boolean customItem = false;
	
	private final int[] amountsToBuy;
	
	public ShopMenu(Player player, ShopFrameItem item, double price) {
		this.player = player;
		this.item = item;
		this.price = price;
		
		NBTItem nbt = new NBTItem(this.item.getItem());
		
		int maxStackSize;
		
		String customItemId = nbt.getString("ci");
		if (customItemId != null) {
			customItem = true;
			
			CustomItem cItem = CustomItem.getCustomItem(customItemId);
			if (cItem != null) {
				maxStackSize = cItem.maxStackSize();
			} else {
				maxStackSize = 1;
			}
			
		} else {
			maxStackSize = this.item.getItem().getMaxStackSize();
		}
		
		switch (maxStackSize) {
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
			if (!customItem) inventory.setItem(sellSlots[i], getSellItem(amountsToBuy[i], this.price));
			inventory.setItem(buySlots[i], getBuyItem(amountsToBuy[i], this.price));
		}
		
		inventory.setItem(13, this.item.getItem());
		return inventory;
	}
	
	private static ItemStack getBuyItem(int amount, double price) {
		NBTItem item = new XItemStackBuilder(XMaterial.GOLD_INGOT)
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
		NBTItem item = new XItemStackBuilder(XMaterial.GOLD_INGOT)
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
