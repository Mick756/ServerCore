package net.servercore.quest;

import lombok.Builder;
import lombok.Getter;
import lombok.SneakyThrows;
import net.servercore.ServerCore;
import net.servercore.util.Globals;
import net.servercore.util.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Builder
public class Quest {
	
	private static final @Getter Map<QuestType, Quest> quests = new HashMap<>();
	private static final @Getter ConcurrentMap<UUID, ConcurrentHashMap<Quest, Long>> questCooldowns = new ConcurrentHashMap<>();
	
	public final UUID id;
	public final String name;
	public final long cooldown;
	public final boolean repeatable;
	public final String description;
	public final Reward reward;
	public final Mission mission;
	public final Long expiration;
	
	public ItemStack getQuestItem() {
		return new ItemStackBuilder(Material.PAPER)
				.setDisplayName(this.name)
				.addLore(this.description,
						" ",
						"")
				.build();
	}
	
	@SneakyThrows
	public void save() {
		File f = new File(ServerCore.getInstance().getDataFolder() + "/quests", this.id.toString() + ".yml");
		YamlConfiguration yml = YamlConfiguration.loadConfiguration(f);
		
		yml.set("id", this.id.toString());
		yml.set("name", this.name);
		yml.set("cooldown", this.cooldown);
		yml.set("repeatable", this.repeatable);
		yml.set("description", this.description);
		yml.set("expiration", this.expiration);
		yml.set("reward", this.reward.toFile());
		yml.set("mission", this.mission.toFile());
		
		yml.save(f);
	}
	
	@SneakyThrows
	public static int loadQuests() {
		File folder = new File(ServerCore.getInstance().getDataFolder() + "/quests");
		
		if (folder.exists()) {
			
			File[] list = folder.listFiles();
			if (list != null && list.length > 0) {
				
				for (File f : list) {
					YamlConfiguration yml = YamlConfiguration.loadConfiguration(f);
					
					Mission mission = ((Mission) Class.forName(yml.getString("mission.type")).getConstructor().newInstance()).fromFile(yml.getConfigurationSection("mission"));
					Reward reward = ((Reward) Class.forName(yml.getString("reward.type")).getConstructor().newInstance()).fromFile(yml.getConfigurationSection("reward"));
					
					Quest quest = Quest.builder()
							.id(UUID.fromString(f.getName().replace(".yml", "")))
							.name(yml.getString("name"))
							.cooldown(yml.getLong("cooldown"))
							.repeatable(yml.getBoolean("repeatable"))
							.description(yml.getString("description"))
							.expiration(yml.getLong("expiration"))
							.mission(mission)
							.reward(reward)
							.build();
					
					quests.put(quest.mission.getType(), quest);
				}
			}
			
		}
		
		return quests.size();
	}
	
	@Override
	public String toString() {
		return String.format(Globals.color(
				"&9Quest: %s\n" +
						"&7%s"
		), this.name, this.description);
	}
	
	public static boolean hasCooldown(Player player, Quest quest) {
		
		ConcurrentMap<Quest, Long> quests = questCooldowns.get(player.getUniqueId());
		if (quests != null) {
		
			for (Quest q : quests.keySet()) {
				if (!q.id.equals(quest.id)) continue;
				
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
	
	public static void startCooldown(Player player, Quest quest) {
		
		if (questCooldowns.containsKey(player.getUniqueId())) {
			questCooldowns.get(player.getUniqueId()).put(quest, quest.cooldown + System.currentTimeMillis());
		} else {
			questCooldowns.put(player.getUniqueId(), new ConcurrentHashMap<>(Collections.singletonMap(quest, quest.cooldown + System.currentTimeMillis())));
		}
		
	}
	
	public enum QuestType {
		
		OBTAIN,
		RETRIEVE,
		ACTION
		
	}
	
}
