package net.arcadia.quest.rewards;

import lombok.Getter;
import net.arcadia.Arcadian;
import net.arcadia.quest.Reward;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class MoneyReward extends Reward {
	
	public MoneyReward() {}
	
	private @Getter double amount;
	
	public MoneyReward(double amount) {
		this.amount = amount;
	}
	
	@Override
	public RewardClaimResult redeem(Player player) {
		Arcadian arcadian = Arcadian.get(player);
		
		arcadian.setBalance(arcadian.getBalance() + this.amount);
		
		return RewardClaimResult.SUCCESS;
	}
	
	@Override
	public Map<Object, Object> toFile() {
		Map<Object, Object> map = new HashMap<>();
		
		map.put("type", this.getClass().getName());
		map.put("value", this.amount);
		
		return map;
	}
	
	@Override
	public Reward fromFile(ConfigurationSection section) {
		return new MoneyReward(section.getDouble("value"));
	}
}
