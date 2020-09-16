package net.arcadia.misc;

import de.tr7zw.changeme.nbtapi.NBTItem;
import lombok.Getter;
import net.arcadia.util.ItemStackBuilder;
import org.bukkit.inventory.ItemStack;

public class ShopFrameItem {
	
	private @Getter NBTItem nbtItem;
	
	public ShopFrameItem(ItemStack stack) {
		this.nbtItem = new NBTItem(stack);
	}
	
	public ShopFrameItem(ItemStack stack, double price) {
		this.nbtItem = new NBTItem(stack);
		
		this.nbtItem.setString("arcadia", "shop");
		this.nbtItem.setDouble("price", price);
		this.nbtItem.setString("original", stack.getItemMeta().getDisplayName());
		
		this.nbtItem.applyNBT(stack);
		
	}
	
	public ItemStack getDisplayItem() {
		return new ItemStackBuilder(this.getItem())
				.setDisplayName(String.format("&aClick to buy!&7 Price:&6 $%.2f", this.getPrice()))
				.build();
	}
	
	public ItemStack getItem() {
		return new ItemStackBuilder(this.nbtItem.getItem())
				.setDisplayName(this.nbtItem.getString("original"))
				.build();
	}
	
	public double getPrice() {
		return this.nbtItem.getDouble("price");
	}
	
	public static ShopFrameItem isShopItem(ItemStack stack) {
		NBTItem item = new NBTItem(stack);
		
		String arcadia = item.getString("arcadia");
		if (arcadia != null && arcadia.equalsIgnoreCase("shop")) {
			return new ShopFrameItem(stack);
		}
		
		return null;
	}
	
}
