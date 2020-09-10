package net.arcadia.util.scoreboards;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ScoreboardTimerEndEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();
	
	private @Getter
	final
	ScoreboardProvider scoreboardBuilder;
	private @Getter
	final
	ScoreboardBuilder.SimpleEntry entry;
	
	public ScoreboardTimerEndEvent(ScoreboardProvider scoreboardBuilder, ScoreboardBuilder.SimpleEntry entry) {
		this.scoreboardBuilder = scoreboardBuilder;
		this.entry = entry;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
}
