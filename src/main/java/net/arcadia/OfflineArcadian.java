package net.arcadia;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.UUID;

public class OfflineArcadian {
	
	private @Getter OfflinePlayer player;
	
	private File configFile;
	private @Getter YamlConfiguration config;
	
	public OfflineArcadian(UUID uuid) {
		
		this.player = Bukkit.getOfflinePlayer(uuid);
		
		this.configFile = new File(ArcadiaCore.getInstance().getDataFolder() + "/players", this.player.getUniqueId().toString() + ".yml");
		this.config = YamlConfiguration.loadConfiguration(this.configFile);
		
	}
}
