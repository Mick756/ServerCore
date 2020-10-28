package net.arcadia.command.economy;

import net.arcadia.ACommand;
import net.arcadia.ArcadiaCore;
import net.arcadia.Arcadian;
import net.arcadia.util.Util;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public class BalanceEditCMD extends ACommand {
	
	
	@Override
	public String alias() {
		return "balanceedit";
	}
	
	@Override
	public String desc() {
		return "Edit yours or another player's balance.";
	}
	
	@Override
	public String usage() {
		return "[add|subtract|set] [player] [amount]";
	}
	
	@Override
	public String permission() {
		return "arcadia.balanceedit";
	}
	
	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		
		if (args.length == 1) {
			
			if (args[0].equalsIgnoreCase("resetall")) {
			
				for (Arcadian arcadian : Arcadian.getAll()) {
					arcadian.setBalance(0.0);
					arcadian.save(false);
				}
				
				respond(sender, "&7You have reset everyone's balance.");
				return;
			}
		}
		
		validateArgsLength(args, 3);
		
		Economy econ = ArcadiaCore.getEconomy();
		
		OfflinePlayer toEdit = Bukkit.getOfflinePlayer(args[1]);
		if (!toEdit.hasPlayedBefore()) {
			respond(sender, "&cThat player was not found!");
			return;
		}
		
		Double amount = Util.isDouble(args[2]);
		if (amount == null) {
			respond(sender , "&cYou must enter a valid amount!");
			return;
		}
		
		if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("deposit")) {
			EconomyResponse econR = econ.depositPlayer(toEdit, amount);
			
			if (econR.transactionSuccess()) {
				respondf(sender, "&7You deposited&b $%.2f&7 into the account of&b %s&7. Their new balance is&b %s&7.", amount, toEdit.getName(), econ.format(econR.balance));
			} else {
				respond(sender, "&c" + econR.errorMessage);
			}
		} else if (args[0].equalsIgnoreCase("subtract") || args[0].equalsIgnoreCase("withdraw")) {
			EconomyResponse econR = econ.withdrawPlayer(toEdit, amount);
			
			if (econR.transactionSuccess()) {
				respondf(sender, "&7You withdrew&b $%.2f&7 out of the account of&b %s&7. Their new balance is&b %s&7.", amount, toEdit.getName(), econ.format(econR.balance));
			} else {
				respond(sender, "&c" + econR.errorMessage);
			}
		} else if (args[0].equalsIgnoreCase("set")) {
			EconomyResponse econR = econ.withdrawPlayer(toEdit, econ.getBalance(toEdit));
			EconomyResponse econRR = econ.depositPlayer(toEdit, amount);
			
			if (econR.transactionSuccess() && econRR.transactionSuccess()) {
				respondf(sender, "&7The player&b %s&7 now has a bank balance of&b $%.2f&7.", toEdit.getName(), amount);
			} else {
				respond(sender, "&c" + econR.errorMessage);
			}
		}
		
	}
}
