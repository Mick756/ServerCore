package net.arcadia.commands.report;

import lombok.SneakyThrows;
import net.arcadia.ACommand;
import net.arcadia.ArcadiaCore;
import net.arcadia.commands.prompts.BugReportPrompt;
import net.arcadia.util.Lang;
import net.arcadia.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ReportBugCMD extends ACommand {
	
	private static final Map<UUID, Long> waiting = new HashMap<>();
	
	private static long cooldown;
	private static File reportFile;
	private static YamlConfiguration reports;
	
	public ReportBugCMD() {
		
		reportFile = new File(ArcadiaCore.getInstance().getDataFolder(), "bug_reports.yml");
		reports = YamlConfiguration.loadConfiguration(reportFile);
		
		cooldown = TimeUnit.SECONDS.toMillis(ArcadiaCore.getInstance().getConfig().getLong("command-settings.bug-report.cooldown"));
		
	}
	
	@SneakyThrows
	public static void createReport(Player reporter, String reason) {
		
		reports.set(reporter.getUniqueId().toString(), reason);
		reports.save(reportFile);
		
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.hasPermission("arcadia.bugreport.notify")) {
				respondf(player, "&bA bug report was submitted by %s!", reporter.getName());
				respondfnp(player, "&cPossible Bug:&e %s", reason);
			}
		}
	}
	
	@Override
	public String alias() {
		return "bugreport";
	}
	
	@Override
	public String desc() {
		return "Report a bug to the server administrators.";
	}
	
	@Override
	public String usage() {
		return "";
	}
	
	@Override
	public String permission() {
		return "arcadia.bugreport";
	}
	
	@SneakyThrows
	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		Player player = validatePlayer(sender);
		
		if (args.length == 0) {
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
			
			ConversationFactory conF = new ConversationFactory(ArcadiaCore.getInstance());
			conF.withFirstPrompt(new BugReportPrompt()).withLocalEcho(false).buildConversation(player).begin();
			
			waiting.put(uuid, System.currentTimeMillis() + cooldown);
			return;
		}
		
		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("list")) {
				if (player.hasPermission("arcadia.bugreport.list")) {
					respondfnp(sender, "&6&m----------------------------------------------------");
					respondnp(sender, "\n&6&lAll Bug Reports\n ");
					for (String key : reports.getKeys(false)) {
						if (key != null) {
							respondfnp(sender, "&4%s", Bukkit.getOfflinePlayer(UUID.fromString(key)).getName());
							respondfnp(sender, "&cReport:&7 %s", reports.getString(key));
							respondnp(sender, " ");
						}
					}
					respondfnp(sender, "&6&m----------------------------------------------------");
				} else {
					respond(sender, String.format(Lang.getMessage("no-permission"), permission()));
				}
				return;
			}
		}
		
		if (args.length == 2) {
			if (args[0].equalsIgnoreCase("solve")) {
				if (player.hasPermission("arcadia.bugreport.solve")) {
					OfflinePlayer reporter = Bukkit.getOfflinePlayer(args[1]);
					if (!reporter.hasPlayedBefore()) {
						respond(sender, "&cThis player has not played on this server.");
						return;
					}
					
					String reporterUUID = reporter.getUniqueId().toString();
					
					if (reports.get(reporterUUID) == null) {
						respond(sender, "&cThat player has no bug reports submitted!");
						return;
					}
					
					reports.set(reporterUUID, null);
					reports.save(reportFile);
					
					respondf(sender, "&7%s's bug reports have been marked as&7 solved&7.", reporter.getName());
				} else {
					respond(sender, String.format(Lang.getMessage("no-permission"), permission()));
				}
				return;
			}
		}
		
		respondiu(sender, label);
	}
	
}
