package net.arcadia.command.economy;

import net.arcadia.ACommand;
import net.arcadia.misc.Payment;
import net.arcadia.util.Globals;
import net.arcadia.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PayCMD extends ACommand {
	
	private static Map<UUID, Payment> confirmations = new HashMap<>();
	
	@Override
	public String alias() {
		return "pay";
	}
	
	@Override
	public String desc() {
		return "Pay someone.";
	}
	
	@Override
	public String usage() {
		return "[player|confirm] [amount]";
	}
	
	@Override
	public String permission() {
		return "";
	}
	
	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		Player player = validatePlayer(sender);
		
		if (args.length == 1 && args[0].equalsIgnoreCase("confirm")) {
			Payment payment = confirmations.get(player.getUniqueId());
			
			if (payment == null) {
				respond(sender, "&cYou do not have a payment to confirm!");
				return;
			}
			
			boolean completed = payment.completePayment();
			if (completed) {
				payment.message();
			} else {
				respond(sender, "&cYou do not have enough to pay this player this amount.");
			}
			
			confirmations.remove(player.getUniqueId());
			return;
		}
		
		validateArgsLength(args, 2);
		
		OfflinePlayer toPayPlayer = Bukkit.getOfflinePlayer(args[0]);
		if (!toPayPlayer.hasPlayedBefore()) {
			toPayPlayer = Bukkit.getPlayer(args[0]);
			if (toPayPlayer == null || !toPayPlayer.isOnline()) {
				respond(sender, "&cThat player was not found!");
				return;
			}
		}
		
		if (player.getUniqueId().equals(toPayPlayer.getUniqueId())) {
			respond(sender, "&cYou can not pay yourself!");
			return;
		}
		
		Double amount = Util.isDouble(args[1]);
		if (amount == null) {
			respond(sender , "&cYou must enter a valid amount!");
			return;
		}
		
		Payment payment = new Payment(player, toPayPlayer, amount);
		sendConfirmationMessage(sender, payment);
		confirmations.put(player.getUniqueId(), payment);
	}
	
	private static void sendConfirmationMessage(CommandSender sender, Payment payment) {
		sender.sendMessage(Globals.color("&7Below is the payment you created."));
		sender.sendMessage(Globals.color(payment.toString()));
		sender.sendMessage(Globals.color("&eType&b /pay confirm&e to complete the payment."));
	}
}
