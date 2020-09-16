package net.arcadia.chat;

import lombok.Getter;
import net.arcadia.Arcadian;
import net.arcadia.util.Globals;
import net.arcadia.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GroupMute extends Mute {
	
	private @Getter final String group;
	private @Getter long endAt;
	private @Getter final long duration;
	private @Getter final String description;
	
	public GroupMute(String group, long duration, String description) {
		this.group = group;
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
		
		for (Player player : Bukkit.getOnlinePlayers()) {
			Arcadian arc = Arcadian.get(player);
			if (!arc.getGroup().equalsIgnoreCase(this.group)) continue;
			arcadians.add(arc);
		}
		
		return arcadians;
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
				"&6&lMuted Group\n&eDescription:&7 %s\n&eTime Left:&7 %s\n&eMuted Players:&7 %s",
				this.description,
				Util.toReadableTime(this.endAt - System.currentTimeMillis()),
				Util.toReadableList(this.getMutedPlayers().stream().map(Arcadian::getPlayer).map(Player::getName).collect(Collectors.toList()))));
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> to = new HashMap<>();
		to.put("type", "group");
		to.put("group", this.group);
		to.put("description", this.description);
		to.put("endAt", this.endAt);
		return to;
	}
}
