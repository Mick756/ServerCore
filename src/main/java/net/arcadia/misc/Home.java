package net.arcadia.misc;

import lombok.Getter;
import net.arcadia.Arcadian;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Home {
	
	private @Getter String name;
	private @Getter Player player;
	private @Getter Location location;
	
	public Home(String name, Player player, Location location) {
		this.name = name;
		this.player = player;
		this.location = location;
	}
	
	public void teleport() {
		Arcadian.get(this.player).sendMessage(true, "&7You are being teleported to your home&b %s&7.", this.name);
		this.player.teleport(this.location);
	}
	
	public void delete() {
		Arcadian.get(this.player).getHomes().remove(this);
	}
	
	@Override
	public String toString() {
		return String.format("&4%s\n&7Location: [X: &b%d&7, Y: &b%d&7, Z: &b%d&7]", this.name, this.location.getBlockX(), this.location.getBlockY(), this.location.getBlockZ());
	}
	
	public static List<Home> get(Arcadian arcadian) {
		List<Home> homes = new ArrayList<>();
		
		YamlConfiguration config = arcadian.getConfig();
		
		ConfigurationSection section = config.getConfigurationSection("homes");
		if (section != null) {
			for (String key : section.getKeys(false)) {
				Location loc = section.getLocation(key + ".location");
				
				homes.add(new Home(key, arcadian.getPlayer(), loc));
			}
		}
		
		return homes;
	}
}
