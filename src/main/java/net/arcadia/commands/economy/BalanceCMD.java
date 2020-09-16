package net.arcadia.commands.economy;

import net.arcadia.ACommand;
import net.arcadia.ArcadiaCore;
import net.arcadia.Arcadian;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BalanceCMD extends ACommand {
	@Override
	public String alias() {
		return "balance";
	}
	
	@Override
	public String desc() {
		return "View your wallet balance.";
	}
	
	@Override
	public String usage() {
		return "";
	}
	
	@Override
	public String permission() {
		return "";
	}
	
	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		Player player = validatePlayer(sender);
		Arcadian arcadian = Arcadian.get(player.getUniqueId());
		
		String balance = ArcadiaCore.getEconomy().format(arcadian.getBalance());
		
		respondf(sender, "&7Your current balance is&a %s&7.", balance);
	}
}
