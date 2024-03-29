package net.servercore;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class OfflineServerPlayer {
	
	private final OfflinePlayer player;
	private final YamlConfiguration config;
	
	public OfflineServerPlayer(UUID uuid) {
		
		this.player = Bukkit.getOfflinePlayer(uuid);
		
		File configFile = new File(ServerCore.getInstance().getDataFolder() + "/players", this.player.getUniqueId().toString() + ".yml");
		this.config = YamlConfiguration.loadConfiguration(configFile);
		
	}
	
	public YamlConfiguration getConfig() {
		return this.config;
	}
	
	public OfflinePlayer getOfflinePlayer() {
		return this.player;
	}
	
	public long getTimedPlayed() {
		return TimeUnit.SECONDS.toMillis(this.player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20);
	}
}
