package net.servercore.misc;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.servercore.ServerCore;
import net.servercore.ServerPlayer;
import net.servercore.util.Globals;
import net.servercore.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Kit {
	
	private static final @Getter List<Kit> kits = new ArrayList<>();
	
	private final @Getter String name;
	private final @Getter long cooldown;
	private final @Getter ItemStack[] items;
	
	private final @Getter String permission;
	
	private final File file;
	private final @Getter YamlConfiguration config;
	
	private @Getter @Setter Map<UUID, Long> cooldowns;
	
	@SneakyThrows
	public Kit(String name, long cooldown, ItemStack[] items) {
		this.name = name;
		this.cooldown = cooldown;
		this.items = items;
		
		this.file = new File(ServerCore.getInstance().getDataFolder() + "/kits", name + ".yml");
		if (!this.file.exists()) {
			this.file.createNewFile();
		}
		
		this.config = YamlConfiguration.loadConfiguration(this.file);
		
		this.permission = "arcadia.kit." + this.name;
		
		this.cooldowns = new HashMap<>();
		
		Bukkit.getPluginManager().addPermission(new Permission(this.permission));
	}
	
	@SneakyThrows
	public void save() {
		
		this.config.set("items", Arrays.asList(this.items));
		this.config.set("cooldown", cooldown);
		
		for (UUID uuid : this.cooldowns.keySet()) {
			this.config.set("cooldowns." + uuid.toString(), this.cooldowns.get(uuid));
		}
		
		this.config.save(this.file);
	}
	
	public void give(Player player) {
		ServerPlayer sPlayer = ServerPlayer.get(player);
		Long timeLeft = this.cooldowns.get(player.getUniqueId());
		
		if (player.hasPermission("arcadia.kit."))
		
		if (timeLeft != null) {
			timeLeft = timeLeft - System.currentTimeMillis();
			
			if (timeLeft <= 0) {
				this.cooldowns.remove(player.getUniqueId());
				this.give(player);
				return;
			}

			sPlayer.sendMessage(true, "&cYou must wait&e %s&c more before using this kit again.", Util.toReadableTime(timeLeft));
			return;
		}
		
		List<Integer> emptySlots = sPlayer.getEmptySlots();
		if (emptySlots.size() < this.items.length) {
			sPlayer.sendMessage(true, "&cYou do not have enough empty slots to receive this kit.");
			return;
		}
		
		player.getInventory().addItem(this.items);
		sPlayer.sendMessage(true, "&7You have received the kit&b %s&7.", this.name);
		
		this.cooldowns.put(player.getUniqueId(), System.currentTimeMillis() + this.cooldown);
	}
	
	public void delete() {
		this.file.delete();
		kits.remove(this);
	}
	
	@Override
	public String toString() {
		return String.format(Globals.color("&6Kit:&7 Name:&b %s&7 Cooldown:&b %s&7 Item Count:&b %d"), this.name, TimeUnit.MILLISECONDS.toMinutes(this.cooldown) + " minutes", this.items.length);
	}
	
	public static void closeAndSave() {
		for (Kit kit : getKits()) {
			kit.save();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static int loadKits() {
		
		File folder = new File(ServerCore.getInstance().getDataFolder() + "/kits");
		
		if (folder.exists()) {
			File[] kitFiles = folder.listFiles();
			
			if (kitFiles != null && kitFiles.length > 0) {
				
				for (File kitFile : kitFiles) {
					String name = kitFile.getName().replace(".yml", "");
					YamlConfiguration yml = YamlConfiguration.loadConfiguration(kitFile);
					
					long cooldown = yml.getLong("cooldown");
					List<ItemStack> items = (List<ItemStack>) yml.getList("items");
					
					if (items != null) {
						Kit kit = new Kit(name, cooldown, items.toArray(new ItemStack[0]));
						Kit.getKits().add(kit);
						
						Map<UUID, Long> cooldowns = new HashMap<>();
						ConfigurationSection section = kit.getConfig().getConfigurationSection("cooldowns");
						
						if (section != null) {
							for (String key : section.getKeys(false)) {
								cooldowns.put(UUID.fromString(key), section.getLong(key));
							}
						}
						
						kit.setCooldowns(cooldowns);
						
					} else {
						ServerCore.error("&cThe kit '" + name + "' failed to load.");
					}
				}
				
			}
			
		}
		
		return Kit.getKits().size();
	}
	
	public static Kit getKit(String name) {
		
		for (Kit kit : getKits()) {
			if (!kit.getName().equalsIgnoreCase(name)) continue;
			
			return kit;
		}
		return null;
	}
	
}
