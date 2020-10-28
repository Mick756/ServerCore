package net.arcadia;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.arcadia.misc.Home;
import net.arcadia.util.Globals;
import net.arcadia.util.Util;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class Arcadian extends OfflineArcadian {
	
	private static final @Getter ConcurrentMap<UUID, Arcadian> arcadians = new ConcurrentHashMap<>();
	
	static {
		
		new BukkitRunnable() {
			@Override
			public void run() {
				
				for (UUID uuid : arcadians.keySet()) {
					
					OfflinePlayer pl = Bukkit.getOfflinePlayer(uuid);
					if (!pl.isOnline()) {
						arcadians.remove(uuid);
					}
				}
				
			}
		}.runTaskTimer(ArcadiaCore.getInstance(), 0, 200L);
		
	}
	
	private final @Getter UUID uuid;
	
	private @Getter Player player = null;
	private OfflinePlayer offlinePlayer = null;
	
	private final File configFile;
	private @Getter YamlConfiguration config;
	
	private Date firstJoin = new Date();
	
	private @Getter String customTag = "";
	private @Getter String tag = "";
	private @Getter String group = "member";
	private @Getter String nick = "";
	
	private @Getter @Setter double balance = 0.0;
	
	public @Getter int timesMuted = 0;
	
	private @Getter @Setter boolean receivingMessages = true;
	private @Getter @Setter Arcadian lastMessaged = null;
	
	private @Getter List<Home> homes = new ArrayList<>();
	//private @Getter List<Quest> activeQuests = new ArrayList<>();
	
	@SneakyThrows
	public Arcadian(UUID uuid) {
		super(uuid);
		this.uuid = uuid;
		
		Player pl = Bukkit.getPlayer(uuid);
		if (pl != null) {
			this.player = pl;
		} else {
			this.offlinePlayer = Bukkit.getOfflinePlayer(uuid);
		}
		
		this.configFile = new File(ArcadiaCore.getInstance().getDataFolder() + "/players", this.uuid.toString() + ".yml");
		
		if (this.configFile.exists()) {
			
			this.config = YamlConfiguration.loadConfiguration(this.configFile);
			loadFromFile();
		} else {
			boolean created = this.configFile.createNewFile();
			if (created) this.config = YamlConfiguration.loadConfiguration(this.configFile);
			
			this.save(true);
		}
	}
	
	public void updateFirstJoin() {
		if (this.firstJoin != null) return;
		
		this.firstJoin = new Date();
	}
	
	public void triggerFirstJoin() {
		updateFirstJoin();
		this.player.teleport(ArcadiaCore.getSpawn());
		
		Bukkit.broadcastMessage(Globals.color(String.format("&3&lWelcome&b %s&3&l to the server!", this.player.getName())));
	}
	
	public long getTimedPlayed() {
		return TimeUnit.SECONDS.toMillis(this.getOfflinePlayer().getStatistic(Statistic.PLAY_ONE_MINUTE) / 20);
	}
	
	public Date getFirstJoin() {
		updateFirstJoin();
		return this.firstJoin;
	}
	
	public void sendMessage(String message) {
		this.player.sendMessage(Globals.color(message));
	}
	
	public void sendMessage(boolean prefix, String message, Object... formats) {
		if (player == null) return;
		String msg = Globals.color(String.format("%s%s", prefix ? ArcadiaCore.getPrefix() : "", message));
		
		this.player.sendMessage(String.format(msg, formats));
	}
	
	public void setGroup(String group) {
		String command = String.format("lp user %s parent set %s", this.uuid.toString(), group);
		
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
		
		this.group = group;
		this.save(false);
	}
	
	public void changeGroup(String newGroup) {
		this.setGroup(newGroup);
		this.updateTags();
		
		Util.doLater(this::updateNameDisplay, 20L);
	}
	
	public void setCustomTag(String tag) {
		this.customTag = tag;
		
		this.save(false);
	}
	
	public void setNick(String nick) {
		this.nick = nick;
		
		this.save(false);
	}
	
	public void updateNick() {
		if (nick.equals("")) {
			this.player.setDisplayName(player.getName());
			return;
		}
		
		this.player.setDisplayName(Globals.color(nick));
	}
	
	public void updateTags() {
		this.tag = this.getCurrentlyShownTag();
	}
	
	public String getCustomDisplayName() {
		return Globals.color(String.format("%s %s", this.tag, this.getPlayer().getDisplayName()));
	}
	
	public String getCurrentlyShownTag() {
		String tag;
		
		if (!this.customTag.equals("")) {
			tag = Globals.color(this.customTag);
		} else {
			FileConfiguration config = ArcadiaCore.getInstance().getConfig();
			tag = Globals.color(config.getString("tags." + this.group));
		}
		
		return tag;
	}
	
	public void updateNameDisplay() {
		this.updateNick();
		this.updateTags();
		
		this.getPlayer().setPlayerListName(this.getCustomDisplayName());
	}
	
	public OfflinePlayer getOfflinePlayer() {
		return this.player == null ? this.offlinePlayer : this.player;
	}
	
	public List<Integer> getEmptySlots() {
		Inventory inventory = this.player.getInventory();
		List<Integer> emptySlots = new ArrayList<>();
		
		for (int slot = 0; slot < inventory.getSize(); slot++) {
			ItemStack stack = inventory.getItem(slot);
			if (stack == null || stack.getType().equals(Material.AIR)) {
				emptySlots.add(slot);
			}
		}
		
		return emptySlots;
	}
	
	public boolean inventoryContains(ItemStack stack, int amount) {
		ItemStack[] items = this.player.getInventory().getContents();
		if (items.length > 0 && amount > 0) {
			int found = 0;
			for (ItemStack item : items) {
				if (found >= amount) break;
				if (item != null && item.isSimilar(stack)) {
					found += item.getAmount();
				}
			}
			
			return (found >= amount);
		}
		return false;
	}
	
	public void inventoryRemove(ItemStack stack, int amount) {
		ItemStack[] items = this.player.getInventory().getContents();
		if (items.length > 0 && amount > 0) {
			int removed = 0;
			for (ItemStack item : items) {
				if (item != null && item.isSimilar(stack)) {
					
					item.subtract(amount);
					removed += amount;
					
				}
				if (removed >= amount) break;
			}
		}
	}
	
	@SneakyThrows
	public void loadFromFile() {
		this.customTag = this.config.getString("ctag");
		if (this.customTag == null || this.customTag.toCharArray().length == 0) {
			this.customTag = "";
		}
		
		this.group = this.config.getString("group");
		if (this.group == null || this.group.toCharArray().length == 0) {
			this.group = "member";
		}
		
		this.receivingMessages = this.config.getBoolean("messages");
		
		this.nick = this.config.getString("nick");
		if (this.nick == null || this.nick.toCharArray().length == 0) {
			this.nick = "";
		}
		
		this.timesMuted = this.config.getInt("history.mutes");
		long first = config.getLong("first-join");
		this.firstJoin = first == 0L ? new Date() : new Date(first);
		this.balance = config.getDouble("balance");
		this.homes = Home.get(this);
	}
	
	@SneakyThrows
	public void save(boolean first) {
		this.config.set("ctag", this.customTag);
		this.config.set("group", this.group);
		this.config.set("messages", this.receivingMessages);
		this.config.set("nick", this.nick);
		this.config.set("history.mutes", this.timesMuted);
		this.config.set("balance", this.balance);
		
		this.config.set("first-join", this.firstJoin.getTime());
		
		this.config.set("homes", null);
		if (!first) {
			for (Home home : this.getHomes()) {
				String root = "homes." + home.getName();
				
				this.config.set(root + ".location", home.getLocation());
			}
		}
		
		this.config.save(this.configFile);
	}
	
	public static List<Arcadian> getOnlineStaff() {
		
		List<Arcadian> staff = new ArrayList<>();
		Permission perm = ArcadiaCore.getPermission();
		
		for (Arcadian arcadian : arcadians.values()) {
			Player player = arcadian.getPlayer();
			if (perm.has(player, "group.owner") || perm.has(player, "group.developer") || perm.has(player, "group.moderator")) {
				staff.add(arcadian);
			}
		}
		
		return staff;
	}
	
	public static List<Arcadian> getAll() {
		List<Arcadian> arcadians = new ArrayList<>();
		
		File playerFolder = new File(ArcadiaCore.getInstance().getDataFolder() + "/players");
		if (!playerFolder.exists()) return arcadians;
		
		File[] playerFiles = playerFolder.listFiles();
		if (playerFiles != null && playerFiles.length > 0) {
			
			for (File f : playerFiles) {
				UUID uuid = UUID.fromString(f.getName().replace(".yml", ""));
				
				arcadians.add(get(uuid));
			}
			
		}
		
		return arcadians;
	}
	
	public static Arcadian get(Player player) {
		return get(player.getUniqueId());
	}
	
	public static Arcadian get(UUID uuid) {
		return arcadians.getOrDefault(uuid, new Arcadian(uuid));
	}
	
}
