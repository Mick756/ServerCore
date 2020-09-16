package net.arcadia.commands.admin;

import net.arcadia.ACommand;
import net.arcadia.ArcadiaCore;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class ServerInfoCMD extends ACommand {
	
	@Override
	public String alias() {
		return "serverinfo";
	}
	
	@Override
	public String desc() {
		return "See the information of a server.";
	}
	
	@Override
	public String usage() {
		return "";
	}
	
	@Override
	public String permission() {
		return "arcadia.serverinfo";
	}
	
	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		Runtime r = Runtime.getRuntime();
		
		long used = (r.totalMemory() - r.freeMemory()) / 1024L / 1024L;
		long max = r.maxMemory() / 1024L / 1024L;
		int coreCount = Runtime.getRuntime().availableProcessors();
		
		String osname = System.getProperty("os.name");
		String osversion = System.getProperty("os.version");
		String java_version = System.getProperty("java.version");
		
		respondnp(sender, "&3&lArcadiaCore Information");
		respondfnp(sender, "&7   Players:&b %d&7/&b%d", Bukkit.getOnlinePlayers().size(), Bukkit.getMaxPlayers());
		respondfnp(sender, "&7   Version:&b %s", ArcadiaCore.getInstance().getDescription().getVersion());
		respondnp(sender, "&c&lSystem/Server Information");
		respondfnp(sender, "&7   Version:&b %s", Bukkit.getVersion());
		respondfnp(sender, "&7   Bukkit Version:&b %s", Bukkit.getBukkitVersion());
		respondfnp(sender, "&7   System:&b %s %s", osname, osversion);
		respondfnp(sender, "&7   Version:&b %s", java_version);
		respondfnp(sender, "&7   Usage:&b %d&7/&b%d", used, max);
		respondfnp(sender, "&7   Core Count:&b %d", coreCount);
	}
}
