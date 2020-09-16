package net.arcadia.chat;

import lombok.Getter;
import net.arcadia.Arcadian;
import net.arcadia.util.Globals;
import net.arcadia.util.Util;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class AllMute extends Mute {
	
	private @Getter
	long endAt;
	private @Getter
	final
	long duration;
	private @Getter
	final
	String description;
	
	public AllMute(long duration, String description) {
		this.duration = TimeUnit.MINUTES.toMillis(duration);
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
		return Bukkit.getOnlinePlayers().stream().map(Arcadian::get).collect(Collectors.toList());
	}
	
	@Override
	public void start() {
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
				"&6&lGlobal Mute\n&eDescription:&7 %s\n&eTime Left:&7 %s\n&eMuted Players:&7 All players",
				this.description,
				Util.toReadableTime(this.endAt - System.currentTimeMillis())));
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> to = new HashMap<>();
		to.put("type", "all");
		to.put("description", this.description);
		to.put("endAt", this.endAt);
		return to;
	}
}
