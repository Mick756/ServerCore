package net.servercore;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.servercore.misc.Home;
import net.servercore.quest.Quest;
import net.servercore.util.Globals;
import net.servercore.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ServerPlayer extends OfflineServerPlayer {
	
	private static final @Getter ConcurrentMap<UUID, ServerPlayer> players = new ConcurrentHashMap<>();
	
	static {
		
		new BukkitRunnable() {
			@Override
			public void run() {
				
				for (UUID uuid : players.keySet()) {
					
					OfflinePlayer pl = Bukkit.getOfflinePlayer(uuid);
					if (!pl.isOnline()) {
						players.remove(uuid);
					}
				}
				
			}
		}.runTaskTimer(ServerCore.getInstance(), 0, 200L);
		
	}
	
	private final @Getter UUID uuid;
	
	private final @Getter Player player;
	
	private Date firstJoin = new Date();
	
	private @Getter @Setter String fullDisplayName = "";
	private @Getter @Setter String prefix = "";
	private @Getter @Setter String nick = "";
	private @Getter String group = "member";
	
	private @Getter @Setter String modeRank = "";
	
	private @Getter @Setter double balance = 0.0;
	
	public @Getter int timesMuted = 0;
	
	private @Getter @Setter boolean receivingMessages = true;
	private @Getter @Setter
	ServerPlayer lastMessaged = null;
	
	private final @Getter List<Home> homes = new ArrayList<>();
	private final @Getter List<Quest> activeQuests = new ArrayList<>();
	
	@SneakyThrows
	public ServerPlayer(UUID uuid) {
		super(uuid);
		this.uuid = uuid;
		
		this.player = Bukkit.getPlayer(uuid);
	}
	
	public void updateFirstJoin() {
		if (this.firstJoin != null) return;
		
		this.firstJoin = new Date();
	}
	
	public void triggerFirstJoin() {
		updateFirstJoin();
		this.player.teleport(ServerCore.getSpawn());
		
		Bukkit.broadcastMessage(Globals.color(String.format("&3&lWelcome&b %s&3&l to the server!", this.player.getName())));
	}
	
	public Date getFirstJoin() {
		updateFirstJoin();
		return this.firstJoin;
	}
	
	public void sendMessage(@NotNull String message) {
		this.player.sendMessage(Globals.color(message));
	}
	
	public void sendMessage(boolean prefix, @NotNull String message, Object... formats) {
		String msg = Globals.color(String.format("%s%s", prefix ? ServerCore.getPrefix() : "", message));
		
		this.player.sendMessage(String.format(msg, formats));
	}
	
	public void setGroup(@NotNull String group) {
		String command = String.format("lp user %s parent set %s", this.uuid.toString(), group);
		
		Objects.requireNonNull(ServerCore.getLuckPerms().getUserManager().getUser(this.uuid)).setPrimaryGroup(group);
		
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
		
		this.group = group;
	}
	
	public void refreshName() {
		if (this.player == null) return;
		
		Util.changePlayerName(this.player, this.prefix + " ", "", Util.TeamAction.CREATE);
		
		String nick = this.getNick().equals("") ? this.player.getName() : this.getNick();
		String name = Globals.color(String.format("%s %s", this.getPrefix(), nick));
		
		this.setFullDisplayName(name);
		
		this.player.setDisplayName(this.fullDisplayName);
		this.player.setPlayerListName(this.fullDisplayName);
		
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
	
	public boolean inventoryContains(@NotNull ItemStack stack, int amount) {
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
	
	public void inventoryRemove(@NotNull ItemStack stack, int amount) {
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

	public int getMaxHomes() {

		if (this.getPlayer().isOp()) return Integer.MAX_VALUE;
		for (int i = 40; i > 0; i--) {
			if (ServerCore.getPermission().has(this.getPlayer(), "arcadia.home." + i)) {
				return i;
			}
		}

		return 1;
	}

	public static @NotNull List<ServerPlayer> getAll() {
		List<ServerPlayer> players = new ArrayList<>();
		
		File playerFolder = new File(ServerCore.getInstance().getDataFolder() + "/players");
		if (!playerFolder.exists()) return players;
		
		File[] playerFiles = playerFolder.listFiles();
		if (playerFiles != null && playerFiles.length > 0) {
			
			for (File f : playerFiles) {
				UUID uuid = UUID.fromString(f.getName().replace(".yml", ""));
				
				players.add(get(uuid));
			}
			
		}
		
		return players;
	}
	
	public static @NotNull ServerPlayer get(@NotNull Player player) {
		return get(player.getUniqueId());
	}
	
	public static @NotNull ServerPlayer get(UUID uuid) {
		return players.getOrDefault(uuid, new ServerPlayer(uuid));
	}
	
}
