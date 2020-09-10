package net.arcadia.util.scoreboards;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public abstract class ScoreboardProvider {
	
	public abstract Object getEntries();
	
	public abstract Scoreboard getScoreboard();
	
	public abstract Objective getObjective();
	
	public abstract Scoreboard build();
	
	public abstract void send(Player player);
	
	public abstract void clear();
	
	interface ScoreboardEntry {
		
		Team getTeam();
		
		void setTeam(Team team);
		
		String getValue();
		
		void setValue(String val);
		
		void clear();
	}
}
