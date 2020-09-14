package net.arcadia.commands.admin;

import net.arcadia.ACommand;
import net.arcadia.ArcadiaCore;
import net.arcadia.Arcadian;
import net.arcadia.util.Globals;
import net.arcadia.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class InfoCMD extends ACommand {
	
	@Override
	public String alias() {
		return "info";
	}
	
	@Override
	public String desc() {
		return "See info about a player.";
	}
	
	@Override
	public String usage() {
		return "[player]";
	}
	
	@Override
	public String permission() {
		return "arcadia.whois";
	}
	
	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		validateArgsLength(args, 1);
		
		UUID uuid = null;
		
		Player pl = Bukkit.getPlayer(args[0]);
		if (pl != null) {
			uuid = pl.getUniqueId();
		} else {
			
			OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
			if (offlinePlayer.hasPlayedBefore()) {
				uuid = offlinePlayer.getUniqueId();
			}
		}
		
		if (uuid == null) {
			respond(sender, "&cNo player was found with that name!");
			return;
		}
		
		Arcadian arcadian = Arcadian.get(uuid);
		OfflinePlayer offlinePlayer = arcadian.getOfflinePlayer();
		
		boolean online = arcadian.getPlayer() != null;
		List<String> infoMessage = Arrays.stream(new String[]{
				"&3&lPlayer Info:",
				"&7Some info may be hidden if the player is not online.",
				"&3Info available offline&7 -&c Info available online",
				String.format("   &3Name:&7 %s", offlinePlayer.getName()),
				String.format("   &3Current Tag:&7 %s", arcadian.getCurrentlyShownTag()),
				String.format("   &3Parent Tag:&7 %s", ArcadiaCore.getInstance().getConfig().getString("tags." + arcadian.getGroup())),
				String.format("   &3First Join:&7 %s", Util.toReadableTime(arcadian.getFirstJoin())),
				String.format("   &3Times Muted:&7 %d", arcadian.getTimesMuted()),
				String.format("   &cHealth:&7 %s", online ? Double.toString(Util.round(arcadian.getPlayer().getHealth(), 4)) : "N/A"),
				String.format("   &cHunger:&7 %s", online ? Double.toString(arcadian.getPlayer().getFoodLevel()) : "N/A"),
				String.format("   &3Time Played:&7 %s", Util.toReadableTime(arcadian.getTimedPlayed())),
				String.format("   &3Bank Balance:&7 $%.2f", ArcadiaCore.getEconomy().getBalance(offlinePlayer))
				
		}).map(Globals::color).collect(Collectors.toList());
		
		sender.sendMessage(infoMessage.stream().toArray(String[]::new));
	}
}
