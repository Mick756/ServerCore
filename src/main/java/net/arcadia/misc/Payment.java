package net.arcadia.misc;

import lombok.Getter;
import lombok.SneakyThrows;
import net.arcadia.ArcadiaCore;
import net.arcadia.util.Globals;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Payment {
	
	private @Getter Player sender;
	private @Getter OfflinePlayer receiver;
	private @Getter double amount;
	
	public Payment(Player sender, OfflinePlayer receiver, double amount) {
		this.sender = sender;
		this.receiver = receiver;
		this.amount = amount;
	}
	
	public boolean completePayment() {
		Economy economy = ArcadiaCore.getEconomy();
		
		if (economy.has(sender, amount)) {
			boolean success = (economy.withdrawPlayer(sender, amount).transactionSuccess() && economy.depositPlayer(receiver, amount).transactionSuccess());
			
			if (success) {
				this.log();
				return true;
			}
		}
		return false;
	}
	
	public void message() {
		this.sender.sendMessage(ArcadiaCore.getPrefix() + String.format(Globals.color("&7You successfully paid&b %s&a $%.2f&7."), this.receiver.getName(), this.amount));
		if (this.receiver.isOnline() && this.receiver.getPlayer() != null) {
			this.receiver.getPlayer().sendMessage(ArcadiaCore.getPrefix() + String.format(Globals.color("&7You received a payment from&b %s&7 of &a $%.2f&7."), this.sender.getName(), this.amount));
		}
	}
	
	@SneakyThrows
	public void log() {
		PrintWriter writer = new PrintWriter(new File(ArcadiaCore.getInstance().getDataFolder(), "pay_logs.txt"));
		writer.println(String.format("[%s] %s paid %s $%.2f", new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").format(new Date()), this.sender.getName(), this.receiver.getName(), this.amount));
	}
	
	@Override
	public String toString() {
		return Globals.color(String.format("&cPayment:&b %s&7 is paying&b %s &a$%.2f&7.", this.sender.getName(), this.receiver.getName(), this. amount));
	}
}
