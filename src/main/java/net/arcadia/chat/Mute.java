package net.arcadia.chat;

import lombok.Getter;
import lombok.SneakyThrows;
import net.arcadia.ArcadiaCore;
import net.arcadia.Arcadian;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;

public abstract class Mute implements ConfigurationSerializable {
	
	private static @Getter final List<Mute> mutes = new ArrayList<>();
	private static @Getter final Map<UUID, PlayerMute> awaitingMutes = new HashMap();
	
	static {
		new BukkitRunnable() {
			final List<Mute> toRemove = new ArrayList<>();
			
			@Override
			public void run() {
				
				for (Mute mute : mutes) {
					
					if (mute instanceof PlayerMute) {
						PlayerMute pm = (PlayerMute) mute;
						
						if (pm.getPlayer() == null || !pm.getPlayer().isOnline()) {
							awaitingMutes.put(pm.getPlayer().getUniqueId(), pm);
							toRemove.add(mute);
						}
						
					}
					
					if (mute.endAtMilli() > System.currentTimeMillis()) continue;
					
					toRemove.add(mute);
				}
				
				mutes.removeAll(toRemove);
				toRemove.clear();
			}
		}.runTaskTimer(ArcadiaCore.getInstance(), 0, 20L);
	}
	
	public static Mute playerIsMuted(Arcadian arcadian) {
		if (arcadian.getPlayer().hasPermission("arcadia.unmutable")) return null;
		
		for (Mute mute : mutes) {
			if (!mute.getMutedPlayers().contains(arcadian)) continue;
			return mute;
		}
		
		return null;
	}
	
	@SneakyThrows
	public static void closeAndSave() {
		File file = new File(ArcadiaCore.getInstance().getDataFolder(), "mutes.yml");
		if (file.exists()) {
			file.delete();
			file.createNewFile();
		}
		YamlConfiguration mutesYml = YamlConfiguration.loadConfiguration(file);
		
		List<Mute> allMutes = new ArrayList<>();
		allMutes.addAll(mutes);
		allMutes.addAll(awaitingMutes.values());
		
		for (int i = 0; i < mutes.size(); i++) {
			Mute m = mutes.get(i);
			String path = "mutes." + i;
			
			mutesYml.set(path + ".description", m.description());
			mutesYml.set(path + ".endAt", m.endAtMilli());
			if (m instanceof PlayerMute) {
				mutesYml.set(path + ".type", "player");
				mutesYml.set(path + ".player", ((PlayerMute) m).getPlayer().getUniqueId().toString());
			} else if (m instanceof WorldMute) {
				mutesYml.set(path + ".type", "world");
				mutesYml.set(path + ".world", ((WorldMute) m).getWorld());
			} else if (m instanceof GroupMute) {
				mutesYml.set(path + ".type", "group");
				mutesYml.set(path + ".group", ((GroupMute) m).getGroup());
			} else {
				mutesYml.set(path + ".type", "all");
			}
		}
		
		mutes.clear();
		mutesYml.save(file);
	}
	
	public static void loadMutes() {
		File file = new File(ArcadiaCore.getInstance().getDataFolder(), "mutes.yml");
		YamlConfiguration mutesYml = YamlConfiguration.loadConfiguration(file);
		
		ConfigurationSection section = mutesYml.getConfigurationSection("mutes");
		if (section != null) {
			for (String mute : section.getKeys(false)) {
				
				final String type = section.getString(mute + ".type");
				final String description = section.getString(mute + ".description");
				final long endAt = section.getLong(mute + ".endAt");
				final long duration = 0;
				Mute m;
				
				if (type.equalsIgnoreCase("all")) {
					m = new AllMute(duration, description);
				} else if (type.equalsIgnoreCase("player")) {
					UUID uuid = UUID.fromString(section.getString(mute + ".player"));
					Player pl = Bukkit.getPlayer(uuid);
					
					if (pl == null || !pl.isOnline()) {
						awaitingMutes.put(uuid, new PlayerMute(pl, endAt, description));
					}
					
					m = null;
				} else if (type.equalsIgnoreCase("group")) {
					String group = section.getString(mute + ".group");
					
					m = new GroupMute(group, duration, description);
				} else {
					String world = section.getString(mute + ".world");
					
					m = new WorldMute(world, duration, description);
				}
				
				if (m != null) {
					m.setEndTime(endAt);
					m.start();
				}
			}
		}
	}
	
	public abstract long endAtMilli();
	
	public abstract String description();
	
	public abstract List<Arcadian> getMutedPlayers();
	
	public abstract void start();
	
	public abstract void setEndTime();
	
	public abstract void setEndTime(long time);
	
	public abstract String toString();
	
}
