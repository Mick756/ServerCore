package net.arcadia.misc;

import lombok.Getter;
import net.arcadia.ArcadiaCore;
import net.arcadia.Arcadian;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Transaction {
	
	private final @Getter Player player;
	private final @Getter double price;
	private final @Getter boolean buying;
	private final @Getter ItemStack[] items;
	
	public Transaction(Player player, double price, boolean buying, ItemStack... items) {
		this.player = player;
		this.price = price;
		this.buying = buying;
		this.items = items;
	}
	
	public TransactionResponse execute() {
		if (items.length == 0) return new TransactionResponse(TransactionResult.ERROR, "&cThere was an error completing this transaction.");
		
		Arcadian arcadian = Arcadian.get(this.player.getUniqueId());
		Economy econ = ArcadiaCore.getEconomy();
		
		if (buying) {
			if (arcadian.getEmptySlots().size() < this.items.length) {
				return new TransactionResponse(TransactionResult.NO_INV_SPACE, "&cThere is not enough inventory space to complete the transaction.");
			}
			
			if (!econ.has(this.player, this.price)) {
				return new TransactionResponse(TransactionResult.INVALID_FUNDS, "&cYou do not have enough money to complete the transaction.");
			}
			
			EconomyResponse response = econ.withdrawPlayer(this.player, this.price);
			if (response.transactionSuccess()) {
				this.player.getInventory().addItem(this.items);
				return new TransactionResponse(TransactionResult.SUCCESS, "&7Transaction was successfully completed. You were charged for&a $%.2f&7.", this.price);
			}
		} else {
			
			boolean containsAll = false;
			for (ItemStack stack : this.items) {
				if (stack != null) {
					containsAll = arcadian.contains(stack, stack.getAmount());
				}
			}
			
			if (!containsAll) {
				return new TransactionResponse(TransactionResult.INVALID_FUNDS, "&cYou do not have enough items to complete the transaction.");
			}
			
			EconomyResponse response = econ.depositPlayer(this.player, this.price);
			if (response.transactionSuccess()) {
				
				for (ItemStack stack : this.items) {
					if (stack != null) {
						arcadian.remove(stack, stack.getAmount());
					}
				}
				return new TransactionResponse(TransactionResult.SUCCESS, "&7Transaction was successfully completed. You sold your items for&a $%.2f&7.", this.price);
			}
		}
		
		return new TransactionResponse(TransactionResult.ERROR, "&cThere was an error completing this transaction.");
	}
	
	
	public static class TransactionResponse {
		
		private final @Getter TransactionResult result;
		private final @Getter String message;
		
		public TransactionResponse(TransactionResult result, String message, Object... format) {
			this.result = result;
			this.message = String.format(message, format);
		}
		
		public boolean wasSuccessful() {
			return (result.equals(TransactionResult.SUCCESS));
		}
		
	}
	
	public enum TransactionResult {
		
		SUCCESS,
		NO_INV_SPACE,
		INVALID_FUNDS,
		DAILY_MAX_REACHED,
		DISABLED,
		ERROR,
		NOT_IMPLEMENTED
		
	}
}
