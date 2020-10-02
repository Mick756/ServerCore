package net.arcadia;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.arcadia.chat.ChatListener;
import net.arcadia.chat.Mute;
import net.arcadia.listeners.CommandListener;
import net.arcadia.listeners.ConnectionListener;
import net.arcadia.listeners.item.UseCustomItemListener;
import net.arcadia.listeners.menus.GlobalMenuListener;
import net.arcadia.listeners.menus.MvpMenuEvents;
import net.arcadia.listeners.menus.ShopMenuListener;
import net.arcadia.listeners.shop.ShopListener;
import net.arcadia.misc.ArcadianEconomy;
import net.arcadia.misc.Kit;
import net.arcadia.misc.PluginMessageManager;
import net.arcadia.survival.items.CustomItem;
import net.arcadia.survival.items.EnderPearlGun;
import net.arcadia.util.Globals;
import net.arcadia.util.Lang;
import net.arcadia.util.Util;
import net.arcadia.util.io.CustomFile;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

public class ArcadiaCore extends JavaPlugin {
	
	private static final @Getter String prefix = Globals.color("&6&l[Arcadia]&r ");
	private static @Getter ArcadiaCore instance;
	private static @Getter Collection<String> userGroups;
	
	private static @Getter LuckPerms luckPerms;
	private static @Getter Economy economy;
	private static @Getter Permission permission;
	
	private static @Getter @Setter Location spawn;
	
	private final @Getter List<ACommand> zCommands = new ArrayList<>();
	private @Getter Random random;
	
	public static void info(String message) {
		Bukkit.getConsoleSender().sendMessage(Globals.color(String.format("%s %s", prefix, message)));
	}
	
	public static void error(String message) {
		Bukkit.getConsoleSender().sendMessage(Globals.color(String.format("%s &cERROR: %s", prefix, message)));
	}
	
	@Override
	public void onEnable() {
		
		if (checkForPaper()) {
			error("Paper is required to run this version of ArcadiaCore. The plugin will now disable...");
			onDisable();
			return;
		}
		
		info("&8********************************************");
		info(String.format("Initializing ArcadiaCore version %s", this.getDescription().getVersion()));
		info("&cDo not reload the server with this plugin installed.");
		
		instance = this;
		
		luckPerms = LuckPermsProvider.get();
		info("Connected to dependencies LuckPerms and Vault.");
		
		loadFiles();
		
		String langFileName = getConfig().getString("language-file");
		File langFile;
		
		if (langFileName != null) {
			langFile = new File(getDataFolder() + "/lang", langFileName);
		} else {
			langFile = new File(getDataFolder() + "/lang", "en_us.yml");
		}
		
		if (langFile.exists()) {
			new Lang(langFile);
			info("Using the language file: " + langFileName);
		} else {
			error("The language file " + langFileName + " was not found. Defaulting to en_us.yml");
		}
		
		random = new Random();
		updateUserGroups();
		
		List<String> presentGroups = luckPerms.getGroupManager().getLoadedGroups().stream().map(Group::getName).map(String::toLowerCase).collect(Collectors.toList());
		for (String group : userGroups) {
			if (!presentGroups.contains(group)) {
				String command = String.format("lp creategroup %s", group);
				
				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
			}
		}
		info(String.format("Registered %d groups from the config", userGroups.size()));
		
		Messenger messenger = getServer().getMessenger();
		messenger.registerIncomingPluginChannel(this, "arcadiacore:in", new PluginMessageManager());
		messenger.registerOutgoingPluginChannel(this, "arcadiacore:out");
		info("Registered the incoming and outgoing BungeeCord channels");
		
		int commands = ACommand.addCommands();
		info(String.format("Registered %d commands.", commands));
		
		registerListeners(new ChatListener(), new ConnectionListener(), new CommandListener(),
				new GlobalMenuListener(), new MvpMenuEvents(), new ShopListener(),
				new ShopMenuListener(), new UseCustomItemListener());
		info("Registered all plugin listeners");
		
		Mute.loadMutes();
		info("Loaded all mutes.");
		
		Bukkit.getServicesManager().register(Economy.class, new ArcadianEconomy(), Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("Vault")), ServicePriority.Normal);
		
		setupVault();
		
		if (economy == null || permission == null) {
			error("Vault was not properly setup.");
		} else {
			info("Vault initialized.");
			
			if (!permission.hasGroupSupport()) {
				error("Group support is not enabled!");
			}
		}
		
		new Util();
		
		int kits = Kit.loadKits();
		info(String.format("Added %d kits.", kits));
		
		int customItems = addCustomItems();
		info(String.format("Added %d custom items.", customItems));
		
		World world = Bukkit.getWorld("world");
		int x = getConfig().getInt("spawn.x");
		int z = getConfig().getInt("spawn.z");
		int y = getConfig().getInt("spawn.y");
		spawn = new Location(world, x, y, z);
		
		info("Initialization complete.");
		info("&8********************************************");
		
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		for (ACommand acmds : ACommand.getCommands()) {
			if (acmds.matchesAlias(label) || acmds.matchesAlias(command.getName())) {
				acmds.call(sender, label, args);
			}
		}
		
		return true;
	}
	
	@Override
	public void onDisable() {
		info("&8********************************************");
		info(String.format("Disabling ArcadiaCore version %s", this.getDescription().getVersion()));
		
		info("Saving all mutes.");
		Mute.closeAndSave();
		
		for (Arcadian arcadian : Arcadian.getArcadians().values()) {
			arcadian.save(false);
		}
		
		info("Saving all kits.");
		Kit.closeAndSave();
		
		info("Completed. Good-bye!");
		info("&8********************************************");
	}
	
	public void registerListeners(Listener... listeners) {
		for (Listener listener : listeners) {
			Bukkit.getPluginManager().registerEvents(listener, this);
		}
	}
	
	public void updateUserGroups() {
		userGroups = getConfig().getConfigurationSection("tags").getKeys(false).stream().map(String::toLowerCase).collect(Collectors.toList());
	}
	
	public static String getStringOrDefault(String path, String def, boolean setIfNot) {
		String found = def;
		
		FileConfiguration config = instance.getConfig();
		if (config.isSet(path)) {
			found = config.getString(path);
		} else {
			if (setIfNot) {
				config.set(path, def);
				instance.saveConfig();
			}
		}
		
		return found;
	}
	
	public static long getLongOrDefault(String path, long def, boolean setIfNot) {
		long found = def;
		
		FileConfiguration config = instance.getConfig();
		if (config.isSet(path)) {
			found = config.getLong(path);
		} else {
			if (setIfNot) {
				config.set(path, def);
				instance.saveConfig();
			}
		}
		
		return found;
	}
	
	public static boolean getBooleanOrDefault(String path, boolean def, boolean setIfNot) {
		boolean found = def;
		
		FileConfiguration config = instance.getConfig();
		if (config.isSet(path)) {
			found = config.getBoolean(path);
		} else {
			if (setIfNot) {
				config.set(path, def);
				instance.saveConfig();
			}
		}
		
		return found;
	}
	
	public static int addCustomItems() {
		
		CustomItem.customItems.put(new String[]{"Ender Pearl Gun", "endergun", "enderpearlgun", "epg"}, new EnderPearlGun());
		
		return CustomItem.customItems.size();
	}
	
	@SneakyThrows
	public void loadFiles() {
		if (!getDataFolder().exists()) {
			getDataFolder().mkdir();
		}
		
		File langFolder = new File(getDataFolder() + "/lang");
		if (!langFolder.exists()) {
			langFolder.mkdir();
		}
		
		File playerFolder = new File(getDataFolder() + "/players");
		if (!playerFolder.exists()) {
			playerFolder.mkdir();
		}
		
		File kitFolder = new File(getDataFolder() + "/kits");
		if (!kitFolder.exists()) {
			kitFolder.mkdir();
		}
		
		CustomFile[] files = new CustomFile[]{
				new CustomFile(new File(this.getDataFolder(), "config.yml"), "config.yml"),
				new CustomFile(new File(this.getDataFolder() + "/lang", "en_us.yml"), "en_us.yml"),
				new CustomFile(new File(this.getDataFolder(), "mutes.yml"), null),
				new CustomFile(new File(this.getDataFolder(), "reports.yml"), null),
				new CustomFile(new File(this.getDataFolder(), "bug_reports.yml"), null),
				new CustomFile(new File(this.getDataFolder(), "pay_logs.txt"), null)
		};
		
		for (CustomFile cf : files) {
			if (!cf.getFile().exists()) {
				if (cf.getFile().getParentFile() == this.getDataFolder()) {
					this.saveResource(cf.getResource(), true);
				} else {
					cf.getFile().createNewFile();
					if (cf.getResource() != null) {
						File outFile = cf.getFile();
						InputStream in = getResource(cf.getResource());
						OutputStream out = new FileOutputStream(outFile);
						byte[] buffer = new byte[1024];
						int ln;
						while ((ln = in.read(buffer)) > 0) {
							out.write(buffer, 0, ln);
						}
						out.close();
						in.close();
					}
				}
				info("Created file: " + cf.getFile().getName());
			} else {
				info("Found file: " + cf.getFile().getName());
			}
		}
	}
	
	private void setupVault() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return;
		}
		
		RegisteredServiceProvider<Economy> econ = getServer().getServicesManager().getRegistration(Economy.class);
		if (econ != null) {
			economy = econ.getProvider();
		}
		
		RegisteredServiceProvider<Permission> perm = getServer().getServicesManager().getRegistration(Permission.class);
		if (perm != null) {
			permission = perm.getProvider();
		}
	}
	
	private boolean checkForPaper() {
		String ver = getServer().getVersion();
		return ver.contains("paper");
	}
}
