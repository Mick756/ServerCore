package net.servercore.quest;

import lombok.Getter;
import lombok.Setter;
import net.servercore.util.Globals;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Map;

public abstract class Reward {
	
	public abstract RewardClaimResult redeem(Player player);

	public abstract Map<Object, Object> toFile();
	
	public abstract Reward fromFile(ConfigurationSection section);
	
	public enum RewardClaimResult {
		
		SUCCESS("&7You successfully claimed your reward!"),
		NOT_ENOUGH_SPACE("&cYou do not have enough inventory space to claim your reward!"),
		FAILURE("&cClaiming this reward failed! Contact a staff member to fix this."),
		INVALID_REWARD("&cThis reward has some kind of error! Contact a staff member to fix this.");
		
		private @Getter @Setter String message;
		
		RewardClaimResult(String message) {
			this.message = Globals.color(message);
		}
		
	}
	
}
