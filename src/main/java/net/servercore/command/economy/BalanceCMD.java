package net.servercore.command.economy;

import net.servercore.ACommand;
import net.servercore.ServerCore;
import net.servercore.ServerPlayer;
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
		ServerPlayer sPlayer = ServerPlayer.get(player.getUniqueId());
		
		String balance = ServerCore.getEconomy().format(sPlayer.getBalance());
		
		respondf(sender, "&7Your current balance is&a %s&7.", balance);
	}
}
