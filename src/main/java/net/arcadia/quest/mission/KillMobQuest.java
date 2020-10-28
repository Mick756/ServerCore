package net.arcadia.quest.mission;

import lombok.Getter;
import lombok.Setter;
import net.arcadia.quest.Mission;
import net.arcadia.quest.Quest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.Map;

public class KillMobQuest extends Mission {
	
	public KillMobQuest() {}
	
	private @Getter EntityType toKill;
	private @Getter int amount;
	private @Getter @Setter int killed;
	
	public KillMobQuest(EntityType toKill, int amount, int killed) {
		this.toKill = toKill;
		this.amount = amount;
		this.killed = killed;
	}
	
	@Override
	public boolean isCompleted() {
		return (this.killed >= this.amount);
	}
	
	@Override
	public Quest.QuestType getType() {
		return Quest.QuestType.ACTION;
	}
	
	@Override
	public int getGoal() {
		return this.amount;
	}
	
	@Override
	public int getProgress() {
		return this.killed;
	}
	
	@Override
	public Map<Object, Object> toFile() {
		Map<Object, Object> map = new HashMap<>();
		
		map.put("type", this.getClass().getName());
		map.put("toKill", this.toKill.name());
		map.put("amount", this.amount);
		map.put("killed", this.killed);
		
		return map;
	}
	
	@Override
	public Mission fromFile(ConfigurationSection section) {
		return new KillMobQuest(EntityType.valueOf(section.getString("type")), section.getInt("amount"), section.getInt("killed"));
	}
}
