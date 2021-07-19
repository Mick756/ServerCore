package net.servercore.quest.mission;

import lombok.Getter;
import net.servercore.quest.Mission;
import net.servercore.quest.Quest;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

public class ObtainMaterialQuest extends Mission {
	
	public ObtainMaterialQuest() {}
	
	private @Getter Material toObtain;
	private @Getter int amount;
	private @Getter int collected;
	
	public ObtainMaterialQuest(Material toObtain, int amount, int collected) {
		this.toObtain = toObtain;
		this.amount = amount;
		this.collected = collected;
	}
	
	@Override
	public boolean isCompleted() {
		return (this.collected >= this.amount);
	}
	
	@Override
	public Quest.QuestType getType() {
		return Quest.QuestType.OBTAIN;
	}
	
	@Override
	public int getGoal() {
		return this.amount;
	}
	
	@Override
	public int getProgress() {
		return this.collected;
	}
	
	@Override
	public Map<Object, Object> toFile() {
		Map<Object, Object> map = new HashMap<>();
		
		map.put("type", this.getClass().getName());
		map.put("toObtain", this.toObtain.name());
		map.put("amount", this.amount);
		map.put("collected", this.collected);
		
		return map;
	}
	
	@Override
	public Mission fromFile(ConfigurationSection section) {
		return new ObtainMaterialQuest(Material.valueOf(section.getString("type")), section.getInt("amount"), section.getInt("collected"));
	}
}
