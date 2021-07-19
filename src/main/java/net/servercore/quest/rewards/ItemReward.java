package net.servercore.quest.rewards;

import lombok.Getter;
import net.servercore.ServerPlayer;
import net.servercore.quest.Reward;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ItemReward extends Reward {
	
	public ItemReward() {}
	
	private @Getter ItemStack[] items;
	
	public ItemReward(ItemStack... items) {
		this.items = items;
	}
	
	@Override
	public RewardClaimResult redeem(Player player) {
		ServerPlayer sPlayer = ServerPlayer.get(player);
		
		if (sPlayer.getEmptySlots().size() >= this.items.length) {
		
			player.getInventory().addItem(this.items);
			return RewardClaimResult.SUCCESS;
			
		} else {
			return RewardClaimResult.NOT_ENOUGH_SPACE;
		}
		
	}
	
	@Override
	public Map<Object, Object> toFile() {
		AtomicInteger index = new AtomicInteger();
		
		return new HashMap<Object, Object>(){{
			put("type", this.getClass().getName());
			Arrays.asList(items).forEach(item -> put("items" + "." + index.getAndIncrement(), item));
		}};
	}
	
	@Override
	public Reward fromFile(ConfigurationSection section) {
		List<ItemStack> items = new ArrayList<>();
		String type = section.getString("type");
		
		if (type != null &&  type.equalsIgnoreCase("itemreward")) {
			for (String key : section.getKeys(false)) {
				ItemStack stack = section.getItemStack(key);
				
				if (stack != null) {
					items.add(stack);
				}
			}
		}
		
		return new ItemReward(items.toArray(new ItemStack[0]));
	}
}
