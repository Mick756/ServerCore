package net.arcadia.util;

import com.cryptomorin.xseries.XMaterial;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ItemStackBuilder {
	
	private ItemStack stack;
	private ItemMeta meta;
	
	public ItemStackBuilder(Material material) {
		ItemStack stack = new ItemStack(material, 1);
		
		this.stack = stack;
		this.meta = stack.getItemMeta();
	}
	
	public ItemStackBuilder(ItemStack stack) {
		this.stack = stack;
	}
	
	public ItemStackBuilder(XMaterial material) {
		this(material.parseMaterial());
	}
	
	private ItemMeta getMeta() {
		if (this.meta == null) {
			this.meta = this.stack.getItemMeta();
		}
		return this.meta;
	}
	
	public ItemStackBuilder addEnchantment(Enchantment enchantment, int level) {
		this.stack.addEnchantment(enchantment, level);
		return this;
	}
	
	public ItemStackBuilder addUnsafeEnchantment(Enchantment enchantment, int level) {
		this.stack.addUnsafeEnchantment(enchantment, level);
		return this;
	}
	
	public ItemStackBuilder setGlassColor(DyeColor color) {
		return this.setDurability(color.getWoolData());
	}
	
	public ItemStackBuilder setDurability(short durability) {
		this.stack.setDurability(durability);
		return this;
	}
	
	public ItemStackBuilder removeEnchantment(Enchantment enchantment) {
		this.meta.removeEnchant(enchantment);
		return this;
	}
	
	public ItemStackBuilder addFlags(ItemFlag... flags) {
		this.stack.addItemFlags(flags);
		return this;
	}
	
	public ItemStackBuilder reset() {
		Material material = this.stack.getType();
		this.stack = new ItemStack(material, 1);
		this.meta = this.stack.getItemMeta();
		return this;
	}
	
	public String getDisplayName() {
		return this.getMeta().getDisplayName();
	}
	
	public ItemStackBuilder setDisplayName(String name) {
		this.getMeta().setDisplayName(name == null ? null : Globals.color(name));
		return this;
	}
	
	public ItemStackBuilder setAmount(int amount) {
		this.stack.setAmount(amount);
		return this;
	}
	
	private List<String> lore() {
		List<String> lore = this.getMeta().getLore();
		return (lore == null) ? new ArrayList<>() : lore;
	}
	
	public ItemStackBuilder setLore(String... lore) {
		List<String> newLore = Arrays.asList(lore);
		newLore.addAll(Arrays.stream(lore).map(Globals::color).collect(Collectors.toList()));
		this.getMeta().setLore(newLore);
		return this;
	}
	
	public ItemStackBuilder setLore(List<String> lore) {
		List<String> newLore = new ArrayList<>();
		lore.addAll(lore.stream().map(Globals::color).collect(Collectors.toList()));
		this.getMeta().setLore(newLore);
		return this;
	}
	
	public ItemStackBuilder addLore(String line) {
		List<String> lore = this.lore();
		lore.add(Globals.color(line));
		this.getMeta().setLore(lore);
		return this;
	}
	
	public ItemStackBuilder addLore(String... lines) {
		List<String> lore = this.lore();
		lore.addAll(Arrays.stream(lines).map(Globals::color).collect(Collectors.toList()));
		this.getMeta().setLore(lore);
		return this;
	}
	
	public ItemStackBuilder setLoreLine(int line, String text) {
		List<String> lore = this.lore();
		lore.set(line, text);
		this.getMeta().setLore(lore);
		return this;
	}
	
	public ItemStackBuilder removeLoreLine(int line) {
		List<String> lore = this.lore();
		if (lore.size() > line) {
			lore.remove(line);
		}
		this.meta.setLore(lore);
		return this;
	}
	
	public ItemStackBuilder clearLore() {
		this.meta.setLore(null);
		return this;
	}
	
	public ItemStack buildPlayerHead(Player player) {
		SkullMeta meta = (SkullMeta) this.stack.getItemMeta();
		if (this.meta.hasDisplayName()) meta.setDisplayName(this.meta.getDisplayName());
		if (this.meta.hasLore()) meta.setLore(this.lore());
		
		meta.addItemFlags(this.meta.getItemFlags().stream().toArray(ItemFlag[]::new));
		
		meta.setPlayerProfile(player.getPlayerProfile());
		
		this.stack.setItemMeta(meta);
		return this.stack;
	}
	
	public ItemStackBuilder clearItemMeta() {
		this.stack.setItemMeta(new ItemStack(this.stack.getType()).getItemMeta());
		return this;
	}
	
	public ItemStackBuilder setMeta() {
		this.stack.setItemMeta(this.meta);
		return this;
	}
	
	public ItemStack build() {
		this.setMeta();
		return this.stack;
	}
	
	public NBTItem toNBT() {
		return new NBTItem(this.build());
	}
	
}
