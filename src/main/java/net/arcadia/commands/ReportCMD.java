package net.arcadia.commands;

import lombok.SneakyThrows;
import net.arcadia.ACommand;
import net.arcadia.ArcadiaCore;
import net.arcadia.util.Globals;
import net.arcadia.util.Lang;
import net.arcadia.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ReportCMD extends ACommand {
	
	private static final Map<UUID, Long> waiting = new HashMap<>();
	
	private static long cooldown;
	private static File reportFile;
	private static YamlConfiguration reports;
	
	public ReportCMD() {
		
		reportFile = new File(ArcadiaCore.getInstance().getDataFolder(), "reports.yml");
		reports = YamlConfiguration.loadConfiguration(reportFile);
		
		cooldown = TimeUnit.SECONDS.toMillis(ArcadiaCore.getInstance().getConfig().getLong("command-settings.report.cooldown"));
		
	}
	
	@SneakyThrows
	private static void createReport(Player reporter, OfflinePlayer reported, String reason) {
		String reportedUUID = reported.getUniqueId().toString();
		List<String> reportList;
		
		reportList = reports.getStringList(reportedUUID);
		reportList.add(String.format("%s: %s", reporter.getName(), reason));
		
		reports.set(reportedUUID, reportList);
		reports.save(reportFile);
		
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.hasPermission("arcadia.report.notify")) {
				respondf(player, "&bA report was submitted by %s!", reporter.getName());
				respondfnp(player, "&cReported:&e %s", reported.getName());
				respondfnp(player, "&cReason:&e %s", reason);
			}
		}
	}
	
	@Override
	public String alias() {
		return "report";
	}
	
	@Override
	public String desc() {
		return "Report a player for cheating or bad behavior.";
	}
	
	@Override
	public String usage() {
		return "[player] [reason]";
	}
	
	@Override
	public String permission() {
		return "arcadia.report";
	}
	
	@SneakyThrows
	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		Player player = validatePlayer(sender);
		
		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("list")) {
				if (player.hasPermission("arcadia.report.list")) {
					respondnp(sender, "&6&m----------------------------------------------------");
					respondnp(sender, "\n&6&lAll Reports\n ");
					for (String key : reports.getKeys(false)) {
						if (key != null) {
							List<String> reps = reports.getStringList(key);
							
							respondfnp(sender, "&4%s", Bukkit.getOfflinePlayer(UUID.fromString(key)).getName());
							if (reps.size() > 0) {
								int index = 1;
								for (String report : reps) {
									respondfnp(player, "&c%d&7 %s", index, report);
									index++;
								}
							}
							respondnp(sender, " ");
						}
					}
					respondnp(sender, "&6&m----------------------------------------------------");
				} else {
					respond(sender, String.format(Lang.getMessage("no-permission"), permission()));
				}
				return;
			}
		}
		
		if (args.length == 2) {
			if (args[0].equalsIgnoreCase("solve")) {
				if (player.hasPermission("arcadia.report.solve")) {
					OfflinePlayer reported = Bukkit.getOfflinePlayer(args[1]);
					if (!reported.hasPlayedBefore()) {
						respond(sender, "&cThis player has not played on this server.");
						return;
					}
					
					String reportedUUID = reported.getUniqueId().toString();
					
					if (reports.get(reportedUUID) == null) {
						respond(sender, "&cThat player has no reports against them!");
						return;
					}
					
					reports.set(reportedUUID, null);
					reports.save(reportFile);
					
					respondf(sender, "&7%s's reports have been marked as&7 solved&7.", reported.getName());
				} else {
					respond(sender, String.format(Lang.getMessage("no-permission"), permission()));
				}
				return;
			}
		}
		
		if (args.length > 1) {
			UUID uuid = player.getUniqueId();
			
			if (waiting.containsKey(uuid)) {
				long timeLeft = waiting.get(uuid) - System.currentTimeMillis();
				
				if (timeLeft < 0) {
					waiting.remove(uuid);
					this.execute(sender, label, args);
					return;
				}
				
				respondf(sender, "&cYou still must wait&e %s&c to use this command.", Util.toReadableTime(timeLeft));
				return;
			}
			
			OfflinePlayer reported = Bukkit.getOfflinePlayer(args[0]);
			if (!reported.hasPlayedBefore()) {
				respond(sender, "&cThis player has not played on this server.");
				return;
			}
			
			String reason = Globals.descriptionFromArgs(1, args);
			
			createReport(player, reported, reason);
			respond(sender, "&7Your report was successfully created. Staff will take action accordingly.");
			
			waiting.put(uuid, System.currentTimeMillis() + cooldown);
			
			return;
		}
		respondiu(sender, label);
	}
}
