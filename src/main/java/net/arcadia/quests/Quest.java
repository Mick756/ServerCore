package net.arcadia.quests;

import lombok.Getter;
import net.arcadia.util.Globals;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class Quest {
	
	private static final @Getter ConcurrentMap<UUID, ConcurrentHashMap<Quest, Long>> questCooldowns = new ConcurrentHashMap<>();
	
	public abstract UUID id();
	
	public abstract String name();
	
	public abstract long cooldown();
	
	public abstract boolean repeatable();
	
	public abstract String description();
	
	public abstract QuestType type();
	
	public abstract Reward reward();
	
	/**
	 * If -1, the quest is either repeatable and/or never expires.
	 * @return The time in milliseconds the quest will expire for every player.
	 */
	public abstract Long expiration();
	
	@Override
	public String toString() {
		return String.format(Globals.color(
				"&6&lQuest: %s\n" +
						""
		), this.name());
	}
	
	protected static boolean hasCooldown(Player player, Quest quest) {
		
		ConcurrentMap<Quest, Long> quests = questCooldowns.get(player.getUniqueId());
		if (quests != null) {
		
			for (Quest q : quests.keySet()) {
				if (!q.id().equals(quest.id())) continue;
				
				long toComplete = quests.get(q);
				if (toComplete - System.currentTimeMillis() <= 0) {
					questCooldowns.get(player.getUniqueId()).remove(q);
					break;
				}
				
				return true;
			}
			
		}
		
		return false;
	}
	
	@SuppressWarnings("unchecked")
	protected static void startCooldown(Player player, Quest quest) {
		
		Map<Quest, Long> quests = questCooldowns.get(player.getUniqueId());
		if (quests != null) {
			questCooldowns.get(player.getUniqueId()).put(quest, quest.cooldown() + System.currentTimeMillis());
		} else {
			questCooldowns.put(player.getUniqueId(), new ConcurrentHashMap(Collections.singletonMap(quest, quest.cooldown() + System.currentTimeMillis())));
		}
		
	}
	
	public enum QuestType {
		
		OBTAIN,
		RETRIEVE,
		ACTION
		
	}
	
}
