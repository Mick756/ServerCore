package net.arcadia.chat;

import lombok.Getter;
import lombok.Setter;
import net.arcadia.Arcadian;
import net.arcadia.util.Globals;
import net.arcadia.util.Util;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PlayerMute extends Mute {
	
	private @Getter @Setter Player player;
	private @Getter long endAt;
	private @Getter final long duration;
	private @Getter final String description;
	
	public PlayerMute(Player player, long duration, String description) {
		this.player = player;
		this.duration = duration;
		this.description = description;
	}
	
	@Override
	public long endAtMilli() {
		return this.endAt;
	}
	
	@Override
	public String description() {
		return this.description;
	}
	
	@Override
	public List<Arcadian> getMutedPlayers() {
		List<Arcadian> arcadians = new ArrayList<>();
		arcadians.add(Arcadian.get(this.player));
		return arcadians;
	}
	
	@Override
	public void start() {
		Arcadian.get(player).timesMuted++;
		
		Mute.getMutes().add(this);
	}
	
	@Override
	public void setEndTime() {
		this.endAt = System.currentTimeMillis() + this.duration;
	}
	
	@Override
	public void setEndTime(long time) {
		this.endAt = time;
	}
	
	@Override
	public String toString() {
		return Globals.color(String.format(
				"&6&lMuted Player\n&eDescription:&7 %s\n&eTime Left:&7 %s\n&eMuted Players:&7 %s",
				this.description,
				Util.toReadableTime(this.endAt - System.currentTimeMillis()),
				Util.toReadableList(this.getMutedPlayers().stream().map(Arcadian::getPlayer).map(Player::getName).collect(Collectors.toList()))));
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> to = new HashMap<>();
		to.put("type", "player");
		to.put("player", this.player.getUniqueId().toString());
		to.put("description", this.description);
		to.put("endAt", this.endAt);
		return to;
	}
}
