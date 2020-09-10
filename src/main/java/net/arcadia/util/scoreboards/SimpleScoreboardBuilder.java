package net.arcadia.util.scoreboards;

import lombok.Getter;
import net.arcadia.util.Globals;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;

public class SimpleScoreboardBuilder extends ScoreboardProvider {
	
	private final @Getter
	Plugin instance;
	private final Scoreboard scoreboard;
	private final Objective objective;
	private final List<ScoreboardEntry> entries;
	
	public SimpleScoreboardBuilder(Plugin instance, String name) {
		this.instance = instance;
		this.scoreboard = Bukkit.getServer().getScoreboardManager().getNewScoreboard();
		this.entries = new ArrayList<>();
		this.objective = this.scoreboard.registerNewObjective("ssb", "dummy", Globals.color(name));
	}
	
	public SimpleScoreboardBuilder setTitle(String value) {
		this.objective.setDisplayName(Globals.color(value));
		
		return this;
	}
	
	public SimpleScoreboardBuilder add(String value) {
		this.entries.add(new ScoreboardBuilder.SimpleEntry(this, value));
		
		return this;
	}
	
	public SimpleScoreboardBuilder add(ScoreboardProvider.ScoreboardEntry entry) {
		this.entries.add(entry);
		
		return this;
	}
	
	@Override
	public Object getEntries() {
		return this.entries;
	}
	
	@Override
	public Scoreboard getScoreboard() {
		return this.scoreboard;
	}
	
	@Override
	public Objective getObjective() {
		return this.objective;
	}
	
	@Override
	public Scoreboard build() {
		ScoreboardBuilder.placeEntries(this);
		
		return this.scoreboard;
	}
	
	@Override
	public void clear() {
		for (ScoreboardEntry ae : this.entries) {
			ae.clear();
		}
		
		this.entries.clear();
	}
	
	@Override
	public void send(Player player) {
		if (player != null && player.isOnline()) {
			player.setScoreboard(this.build());
		}
	}
}
