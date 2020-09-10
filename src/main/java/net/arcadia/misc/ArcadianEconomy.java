package net.arcadia.misc;

import net.arcadia.Arcadian;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.List;

public class ArcadianEconomy implements Economy {
	
	@Override
	public boolean isEnabled() {
		return true;
	}
	
	@Override
	public String getName() {
		return "Arcadian Economy";
	}
	
	@Override
	public boolean hasBankSupport() {
		return false;
	}
	
	@Override
	public int fractionalDigits() {
		return 2;
	}
	
	@Override
	public String format(double v) {
		return "$%d";
	}
	
	@Override
	public String currencyNamePlural() {
		return "coins";
	}
	
	@Override
	public String currencyNameSingular() {
		return "coin";
	}
	
	@Deprecated
	@Override
	public boolean hasAccount(String s) {
		return true;
	}
	
	@Override
	public boolean hasAccount(OfflinePlayer offlinePlayer) {
		return true;
	}
	
	@Deprecated
	@Override
	public boolean hasAccount(String s, String s1) {
		return false;
	}
	
	@Override
	public boolean hasAccount(OfflinePlayer offlinePlayer, String s) {
		return false;
	}
	
	@Override
	public double getBalance(String s) {
		return Arcadian.get(Bukkit.getOfflinePlayer(s).getUniqueId()).getBalance();
	}
	
	@Override
	public double getBalance(OfflinePlayer offlinePlayer) {
		return Arcadian.get(offlinePlayer.getUniqueId()).getBalance();
	}
	
	@Override
	public double getBalance(String s, String s1) {
		return 0;
	}
	
	@Override
	public double getBalance(OfflinePlayer offlinePlayer, String s) {
		return 0;
	}
	
	@Override
	public boolean has(String s, double v) {
		return this.has(Bukkit.getOfflinePlayer(s), v);
	}
	
	@Override
	public boolean has(OfflinePlayer offlinePlayer, double v) {
		return Arcadian.get(offlinePlayer.getUniqueId()).getBalance() >= v;
	}
	
	@Override
	public boolean has(String s, String s1, double v) {
		return false;
	}
	
	@Override
	public boolean has(OfflinePlayer offlinePlayer, String s, double v) {
		return false;
	}
	
	@Override
	public EconomyResponse withdrawPlayer(String s, double v) {
		return this.withdrawPlayer(Bukkit.getOfflinePlayer(s), v);
	}
	
	@Override
	public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double v) {
		
		if (this.has(offlinePlayer, v)) {
			Arcadian.get(offlinePlayer.getUniqueId()).setBalance(this.getBalance(offlinePlayer) - v);
			return new EconomyResponse(v, this.getBalance(offlinePlayer), EconomyResponse.ResponseType.SUCCESS, null);
		}
		
		return new EconomyResponse(v, this.getBalance(offlinePlayer), EconomyResponse.ResponseType.FAILURE, "Not enough coins.");
	}
	
	@Override
	public EconomyResponse withdrawPlayer(String s, String s1, double v) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented.");
	}
	
	@Override
	public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, String s, double v) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented.");
	}
	
	@Override
	public EconomyResponse depositPlayer(String s, double v) {
		return this.depositPlayer(Bukkit.getOfflinePlayer(s), v);
	}
	
	@Override
	public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double v) {
		Arcadian.get(offlinePlayer.getUniqueId()).setBalance(this.getBalance(offlinePlayer) + v);
		return new EconomyResponse(v, this.getBalance(offlinePlayer), EconomyResponse.ResponseType.SUCCESS, null);
	}
	
	@Override
	public EconomyResponse depositPlayer(String s, String s1, double v) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented.");
	}
	
	@Override
	public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, String s, double v) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented.");
	}
	
	@Override
	public EconomyResponse createBank(String s, String s1) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented.");
	}
	
	@Override
	public EconomyResponse createBank(String s, OfflinePlayer offlinePlayer) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented.");
	}
	
	@Override
	public EconomyResponse deleteBank(String s) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented.");
	}
	
	@Override
	public EconomyResponse bankBalance(String s) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented.");
	}
	
	@Override
	public EconomyResponse bankHas(String s, double v) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented.");
	}
	
	@Override
	public EconomyResponse bankWithdraw(String s, double v) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented.");
	}
	
	@Override
	public EconomyResponse bankDeposit(String s, double v) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented.");
	}
	
	@Override
	public EconomyResponse isBankOwner(String s, String s1) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented.");
	}
	
	@Override
	public EconomyResponse isBankOwner(String s, OfflinePlayer offlinePlayer) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented.");
	}
	
	@Override
	public EconomyResponse isBankMember(String s, String s1) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented.");
	}
	
	@Override
	public EconomyResponse isBankMember(String s, OfflinePlayer offlinePlayer) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented.");
	}
	
	@Override
	public List<String> getBanks() {
		return null;
	}
	
	@Override
	public boolean createPlayerAccount(String s) {
		return false;
	}
	
	@Override
	public boolean createPlayerAccount(OfflinePlayer offlinePlayer) {
		return false;
	}
	
	@Override
	public boolean createPlayerAccount(String s, String s1) {
		return false;
	}
	
	@Override
	public boolean createPlayerAccount(OfflinePlayer offlinePlayer, String s) {
		return false;
	}
}
