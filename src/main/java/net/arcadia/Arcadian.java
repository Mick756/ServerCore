package net.arcadia;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.arcadia.misc.Home;
import net.arcadia.util.Globals;
import net.arcadia.util.Util;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Arcadian extends OfflineArcadian {
	
	private static final @Getter Map<UUID, Arcadian> arcadians = new HashMap<>();
	
	private final @Getter UUID uuid;
	
	private @Getter Player player = null;
	private @Getter OfflinePlayer offlinePlayer = null;
	
	private final File configFile;
	private final @Getter YamlConfiguration config;
	
	private Date firstJoin;
	
	private @Getter String customTag;
	private @Getter String tag = "";
	private @Getter String group;
	private @Getter String nick = "";
	
	private @Getter @Setter double balance;
	
	public @Getter int timesMuted;
	
	private @Getter @Setter boolean receivingMessages;
	private @Getter @Setter Arcadian lastMessaged;
	
	private @Getter List<Home> homes;
	
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
		
		boolean first;
		if (first = !this.configFile.exists()) {
			this.configFile.createNewFile();
		}
		
		this.config = YamlConfiguration.loadConfiguration(this.configFile);
		if (!first) {
			loadFromFile();
		} else {
			this.customTag = null;
			this.group = "member";
			this.receivingMessages = true;
			this.homes = new ArrayList<>();
			this.timesMuted = 0;
			this.balance = 0.0d;
			
			this.save();
		}
		
		arcadians.put(uuid, this);
	}
	
	public void triggerFirstJoin() {
		Date now = new Date();
		
		this.firstJoin = now;
		this.config.set("first-join", now);
		this.save();
		
		FileConfiguration gcon = ArcadiaCore.getInstance().getConfig();
		if (gcon.getBoolean("survival-settings.survival")) {
			this.player.teleport(ArcadiaCore.getSpawn());
		}
		
		Bukkit.broadcastMessage(Globals.color(String.format("&3&lWelcome&b %s&3&l to the server!", this.player.getName())));
	}
	
	public long getTimedPlayed() {
		return TimeUnit.SECONDS.toMillis(this.getOfflinePlayer().getStatistic(Statistic.PLAY_ONE_MINUTE) / 20);
	}
	
	public Date getFirstJoin() {
		return this.firstJoin == null ? new Date() : this.firstJoin;
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
		this.save();
	}
	
	public void changeGroup(String newGroup) {
		this.setGroup(newGroup);
		this.updateTags();
		
		Util.doLater(this::updateNameDisplay, 20L);
	}
	
	public void setCustomTag(String tag) {
		this.customTag = tag;
		
		this.save();
	}
	
	public void setNick(String nick) {
		this.nick = nick;
		
		this.save();
	}
	
	public void updateNick() {
		if (player == null) return;
		
		if (this.nick.equals("")) {
			this.player.setDisplayName(this.player.getName());
			return;
		}
		
		this.player.setDisplayName(Globals.color(nick));
	}
	
	public void updateTags() {
		if (player == null) return;
		
		this.tag = this.getCurrentlyShownTag();
	}
	
	public String getCustomDisplayName() {
		return Globals.color(String.format("%s %s", this.tag, this.getPlayer().getDisplayName()));
	}
	
	public String getCurrentlyShownTag() {
		String tag;
		
		if (customTag != null) {
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
		
		//NametagEdit.getApi().setNametag(this.getPlayer(), this.tag + " ", "");
		
		this.getPlayer().setPlayerListName(this.getCustomDisplayName());
	}
	
	public boolean deposit(double deposit) {
		Economy econ = ArcadiaCore.getEconomy();
		EconomyResponse er = econ.depositPlayer(this.getOfflinePlayer(), deposit);
		
		return er.transactionSuccess();
	}
	
	public OfflinePlayer getOfflinePlayer() {
		return this.player == null ? this.offlinePlayer : this.player;
	}
	
	@SneakyThrows
	public void loadFromFile() {
		this.customTag = this.config.getString("ctag");
		this.group = this.config.getString("group");
		this.receivingMessages = this.config.getBoolean("messages");
		this.nick = this.config.getString("nick");
		this.timesMuted = this.config.getInt("history.mutes");
		this.firstJoin = (Date) config.get("first-join");
		this.balance = config.getDouble("balance");
		this.homes = Home.get(this);
	}
	
	@SneakyThrows
	public void save() {
		this.config.set("ctag", this.customTag);
		this.config.set("group", this.group);
		this.config.set("messages", this.receivingMessages);
		this.config.set("nick", this.nick);
		this.config.set("history.mutes", this.timesMuted);
		this.config.set("balance", this.balance);
		
		this.config.set("homes", null);
		for (Home home : this.getHomes()) {
			String root = "homes." + home.getName();
			
			this.config.set(root + ".location", home.getLocation());
		}
		
		this.config.save(this.configFile);
	}
	
	public static Arcadian get(Player player) {
		return get(player.getUniqueId());
	}
	
	public static Arcadian get(UUID uuid) {
		Arcadian arcadian = arcadians.get(uuid);
		if (arcadian == null) {
			return new Arcadian(uuid);
		}
		return arcadian;
	}
	
}
