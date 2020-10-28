package net.arcadia.quest;

import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;

public abstract class Mission {
	
	public abstract Quest.QuestType getType();
	
	public abstract int getGoal();
	
	public abstract int getProgress();
	
	public abstract boolean isCompleted();
	
	public abstract Map<Object, Object> toFile();
	
	public abstract Mission fromFile(ConfigurationSection section);
	
}
