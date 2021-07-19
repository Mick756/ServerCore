package net.servercore;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.servercore.listener.CommandListener;
import net.servercore.listener.ConnectionListener;
import net.servercore.listener.item.UseCustomItemListener;
import net.servercore.listener.menus.GlobalMenuListener;
import net.servercore.listener.menus.MvpMenuEvents;
import net.servercore.listener.menus.ShopMenuListener;
import net.servercore.listener.shop.ShopListener;
import net.servercore.misc.GenericEconomy;
import net.servercore.misc.Kit;
import net.servercore.misc.PluginMessageManager;
import net.servercore.survival.items.CustomItem;
import net.servercore.survival.items.EnderPearlGun;
import net.servercore.util.Globals;
import net.servercore.util.Lang;
import net.servercore.util.io.CustomFile;
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
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

public class ServerCore extends JavaPlugin {
	
	private static final @Getter String prefix = Globals.color("&6&l[Arcadia]&r ");
	private static @Getter
    ServerCore instance;
	private static @Getter Collection<String> userGroups;
	
	private static @Getter LuckPerms luckPerms;
	private static @Getter Economy economy;
	private static @Getter Permission permission;
	
	private static @Getter @Setter Location spawn;
	
	private final @Getter List<ACommand> zCommands = new ArrayList<>();
	private @Getter Random random;
	
	@Override
	public void onEnable() {
		
		if (this.checkForPaper()) {
			error("Paper is required to run this version of ArcadiaCore. The plugin will now disable...");
			this.onDisable();
			return;
		}
		
		info("&8********************************************");
		info(String.format("Initializing ArcadiaCore version %s", this.getDescription().getVersion()));
		info("&cDo not reload the server with this plugin installed.");
		
		instance = this;
		
		luckPerms = LuckPermsProvider.get();
		info("Connected to dependencies LuckPerms.");
		
		this.loadFiles();
		
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
		this.updateUserGroups();
		
		List<String> presentGroups = luckPerms.getGroupManager().getLoadedGroups().stream().map(Group::getName).map(String::toLowerCase).collect(Collectors.toList());
		for (String group : userGroups) {
			if (!presentGroups.contains(group)) {
				String command = String.format("lp creategroup %s", group);
				
				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
			}
		}
		info(String.format("Registered %d groups from the config", userGroups.size()));
		
		Messenger messenger = getServer().getMessenger();
		messenger.registerIncomingPluginChannel(this, "arcadiacore:main", new PluginMessageManager());
		info("Registered the incoming BungeeCord channels");
		
		int commands = ACommand.addCommands();
		info(String.format("Registered %d commands.", commands));
		
		registerListeners(new ConnectionListener(), new CommandListener(),
				new GlobalMenuListener(), new MvpMenuEvents(), new ShopListener(),
				new ShopMenuListener(), new UseCustomItemListener());
		info("Registered all plugin listeners");
		
		Bukkit.getServicesManager().register(Economy.class, new GenericEconomy(), Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("Vault")), ServicePriority.Normal);
		
		this.setupVault();
		
		if (economy == null || permission == null) {
			error("Vault was not properly setup.");
		} else {
			info("Vault initialized.");
			
			if (!permission.hasGroupSupport()) {
				error("Group support is not enabled!");
			}
		}
		
//		Util.loadBadWords();
		
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
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
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
		
		info("Saving all kits.");
		Kit.closeAndSave();
		
		info("Completed. Good-bye!");
		info("&8********************************************");
	}
	
	public static void info(String message) {
		Bukkit.getConsoleSender().sendMessage(Globals.color(String.format("%s %s", prefix, message)));
	}
	
	public static void error(String message) {
		Bukkit.getConsoleSender().sendMessage(Globals.color(String.format("%s &cERROR: %s", prefix, message)));
	}
	
	
	public void registerListeners(Listener... listeners) {
		for (Listener listener : listeners) {
			Bukkit.getPluginManager().registerEvents(listener, this);
		}
	}
	
	public void updateUserGroups() {
		userGroups = Objects.requireNonNull(getConfig().getConfigurationSection("tags")).getKeys(false).stream().map(String::toLowerCase).collect(Collectors.toList());
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
		
		CustomFile[] files = new CustomFile[] {
				new CustomFile(getDataFolder(), null, true),
				new CustomFile(new File(getDataFolder() + "/lang"), null, true),
				new CustomFile(new File(getDataFolder() + "/players"), null, true),
				new CustomFile(new File(getDataFolder() + "/kits"), null, true),
				new CustomFile(new File(getDataFolder() + "/quests"), null, true),
				new CustomFile(new File(this.getDataFolder(), "config.yml"), "config.yml", false),
				new CustomFile(new File(this.getDataFolder() + "/lang", "en_us.yml"), "en_us.yml", false),
				new CustomFile(new File(this.getDataFolder(), "mutes.yml"), null, false),
				new CustomFile(new File(this.getDataFolder(), "reports.yml"), null, false),
				new CustomFile(new File(this.getDataFolder(), "bug_reports.yml"), null, false),
				new CustomFile(new File(this.getDataFolder(), "pay_logs.txt"), null, false)
		};
		
		for (CustomFile cf : files) {
			if (!cf.getFile().exists()) {
				
				if (cf.isDir()) {
					cf.getFile().mkdir();
				} else {
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
							while (true) {
								assert in != null;
								if (!((ln = in.read(buffer)) > 0)) break;
								out.write(buffer, 0, ln);
							}
							out.close();
							in.close();
						}
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
