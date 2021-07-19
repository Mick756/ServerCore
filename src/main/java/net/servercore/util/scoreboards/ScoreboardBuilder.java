package net.servercore.util.scoreboards;

import lombok.Getter;
import lombok.Setter;
import net.servercore.util.Globals;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class ScoreboardBuilder extends ScoreboardProvider {
	
	private static final char[] tokens = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i'};
	
	private static Plugin instance;
	private final Scoreboard scoreboard;
	private final Map<Integer, ScoreboardProvider.ScoreboardEntry> entries;
	private Objective objective;
	private @Getter
	boolean animatedTitle;
	private @Getter BukkitRunnable animatedTitleTask;
	
	/**
	 * Initialize the builder.
	 * Some things to note about a scoreboard:
	 * - Limited to 22 lines because of Minecraft's limited color codes
	 *
	 * @param instance Your plugin's main class for the purpose of BukkitRunnables
	 */
	public ScoreboardBuilder(Plugin instance) {
		ScoreboardBuilder.instance = instance;
		
		this.scoreboard = Bukkit.getServer().getScoreboardManager().getNewScoreboard();
		this.entries = new HashMap<>();
	}
	
	
	/**
	 * Place all entries created. Only used for building the scoreboard.
	 * Not for use outside of #build();
	 * @return Instance of the ScoreboardBuilder
	 */
	
	private static void setValue(Team team, String val) {
		if (team != null && val != null) {
			if (val.toCharArray().length <= 16) {
				team.setSuffix(Globals.color(val));
				team.setPrefix("");
			} else {
				String[] two = splitHalf(val);
				
				team.setPrefix(Globals.color(two[0]));
				team.setSuffix(Globals.color(two[1]));
			}
		}
	}
	
	private static void placeEntry(ScoreboardBuilder sb, boolean overwrite, int line, ScoreboardProvider.ScoreboardEntry entry) {
		if (overwrite) {
			ScoreboardProvider.ScoreboardEntry currEntry = sb.entries.get(line);
			if (currEntry != null) {
				currEntry.clear();
			}
			
			sb.entries.put(line, entry);
		} else {
			
			if (!sb.entries.containsKey(line)) {
				sb.entries.put(line, entry);
			}
		}
	}
	
	private static String[] splitHalf(String str) {
		String[] strs = new String[2];
		ChatColor cc1 = ChatColor.WHITE, cc2 = null;
		Character lastChar = null;
		
		strs[0] = "";
		for (int i = 0; i < str.length() / 2; i++) {
			char c = str.charAt(i);
			
			if (lastChar != null) {
				ChatColor cc = charsToChatColor(new char[]{lastChar, c});
				
				if (cc != null) {
					if (cc.isFormat())
						cc2 = cc;
					else {
						cc1 = cc;
						cc2 = null;
					}
				}
			}
			
			strs[0] += c;
			lastChar = c;
		}
		
		strs[1] = cc1 + "" + (cc2 != null ? cc2 : "") + str.substring(str.length() / 2);
		return strs;
	}
	
	private static ChatColor charsToChatColor(char[] chars) {
		for (ChatColor cc : ChatColor.values()) {
			char[] ccChars = cc.toString().toCharArray();
			
			int same = 0;
			for (int i = 0; i < 2; i++) {
				if (ccChars[i] == chars[i]) {
					same++;
				}
			}
			
			if (same == 2) {
				return cc;
			}
		}
		return null;
	}
	
	public static void placeEntries(ScoreboardProvider builder) {
		
		Map<Integer, ScoreboardEntry> entries;
		
		if (builder instanceof ScoreboardBuilder) {
			entries = (Map<Integer, ScoreboardEntry>) builder.getEntries();
		} else {
			entries = new HashMap<>();
			List<ScoreboardEntry> toAdd = (ArrayList<ScoreboardEntry>) builder.getEntries();
			
			for (int i = 1; i < toAdd.size(); i++) {
				entries.put(i, toAdd.get(i - 1));
			}
		}
		
		for (int line : entries.keySet()) {
			
			ScoreboardProvider.ScoreboardEntry sen = entries.get(line);
			
			if (sen.getTeam() == null) {
				
				Team team = builder.getScoreboard().registerNewTeam(builder.getObjective().getName() + "." + builder.getObjective().getCriteria() + "." + (line - 1));
				team.addEntry(ChatColor.values()[line - 1] + "");
				
				sen.setTeam(team);
			}
			
			sen.setValue(sen.getValue());
			builder.getObjective().getScore(ChatColor.values()[line - 1] + "").setScore(line - 1);
		}
	}
	
	/**
	 * Create the objective of the scoreboard for all the entries to be placed under.
	 * For the purpose of this class, it only allows for the criteria of "dummy"
	 *
	 * @param id          The id of this objective. keep it short
	 * @param displayName Set the display name of the objective. This is not final.
	 * @return Instance of the ScoreboardBuilder
	 */
	public ScoreboardBuilder createNewObjective(String id, String displayName) {
		this.objective = null;
		
		this.objective = this.scoreboard.registerNewObjective(id, "dummy", Globals.color(displayName));
		return this;
	}
	
	/**
	 * Set the title of the objective. Objective must be created in order to use this.
	 *
	 * @param title String of the title. Auto translates color.
	 * @return Instance of the ScoreboardBuilder
	 */
	public ScoreboardBuilder setTitle(String title) {
		
		this.cancelAnimatedTitleTask();
		
		if (this.objective != null) {
			this.objective.setDisplayName(Globals.color(title));
		}
		return this;
	}
	
	/**
	 * An animated title goes through each phase specified and changes the display name
	 * live.
	 *
	 * @param rateInTicks  The time between each phase of the animation
	 * @param delayInTicks The delay between each full cycle of the animation. This
	 *                     automatically compensates for the time of animation.
	 * @param phases       Each value for the cycle of the animation.
	 * @return Instance of the ScoreboardBuilder
	 */
	public ScoreboardBuilder setAnimatedTitle(int rateInTicks, int delayInTicks, String... phases) {
		
		this.cancelAnimatedTitleTask();
		if (phases == null || phases.length == 0) {
			return this;
		}
		
		this.animatedTitleTask = new BukkitRunnable() {
			@Override
			public void run() {
				new BukkitRunnable() {
					int i = 0;
					
					@Override
					public void run() {
						if (objective != null) {
							objective.setDisplayName(Globals.color(phases[i]));
						}
						
						i++;
						if (i == phases.length) {
							this.cancel();
						}
					}
				}.runTaskTimer(instance, 0, rateInTicks);
				
			}
		};
		
		this.animatedTitle = true;
		
		this.animatedTitleTask.runTaskTimer(instance, 0, delayInTicks + ((phases.length - 1) * rateInTicks));
		return this;
	}
	
	/**
	 * Set the display location of the objective.
	 *
	 * @param slot The DisplaySlot to be set.
	 * @return Instance of the ScoreboardBuilder
	 */
	public ScoreboardBuilder setDisplay(DisplaySlot slot) {
		if (this.objective != null) {
			this.objective.setDisplaySlot(slot);
		}
		return this;
	}
	
	/**
	 * This method is deprecated as it can affect ScoreboardEntries
	 *
	 * @param line  The line you want to modify
	 * @param value The value to be set on that line
	 * @return Instance of the ScoreboardBuilder
	 */
	@Deprecated
	public ScoreboardBuilder setLine(int line, String value) {
		if (this.objective != null) {
			this.objective.getScore(Globals.color(value)).setScore(line);
		}
		return this;
	}
	
	/**
	 * Remove and clear entry data of a line if it exists.
	 *
	 * @param line The line to remove the entry from
	 * @return Instance of the ScoreboardBuilder
	 */
	public ScoreboardBuilder removeEntry(int line) {
		ScoreboardProvider.ScoreboardEntry currEntry = entries.get(line);
		if (currEntry != null) {
			currEntry.clear();
		}
		
		this.entries.remove(line);
		return this;
	}
	
	/**
	 * Get every SimpleEntry from the list of created entries
	 *
	 * @return A list of the SimpleEntries in the scoreboard
	 */
	public List<SimpleEntry> getSimpleEntries() {
		List<SimpleEntry> entries = new ArrayList<>();
		
		for (ScoreboardProvider.ScoreboardEntry se : this.entries.values()) {
			if (se instanceof SimpleEntry) {
				entries.add((SimpleEntry) se);
			}
		}
		
		return entries;
	}
	
	/**
	 * Get every AnimatedEntries from the list of created entries
	 *
	 * @return A list of the AnimatedEntries in the scoreboard
	 */
	public List<AnimatedEntry> getAnimatedEntries() {
		List<AnimatedEntry> entries = new ArrayList<>();
		
		for (ScoreboardProvider.ScoreboardEntry se : this.entries.values()) {
			if (se instanceof AnimatedEntry) {
				entries.add((AnimatedEntry) se);
			}
		}
		
		return entries;
	}
	
	/**
	 * Get a SimpleEntry from a line from the entries.
	 *
	 * @return A SimpleEntry. If it is not a simple entry
	 * it returns null or if it doesn't exist.
	 */
	public SimpleEntry getSimpleEntry(int line) {
		ScoreboardProvider.ScoreboardEntry se = this.entries.get(line);
		if (se instanceof SimpleEntry) {
			return (SimpleEntry) se;
		}
		return null;
	}
	
	/**
	 * Get an AnimatedEntry from a line from the entries.
	 *
	 * @return A AnimatedEntry. If it is not a simple entry
	 * it returns null or if it doesn't exist.
	 */
	public AnimatedEntry getAnimatedEntry(int line) {
		ScoreboardProvider.ScoreboardEntry se = this.entries.get(line);
		if (se instanceof AnimatedEntry) {
			return (AnimatedEntry) se;
		}
		return null;
	}
	
	/**
	 * Create a static line on the scoreboard.
	 *
	 * @param line      The line to create
	 * @param value     The value of that line, auto-colors
	 * @param overwrite If that line has content, overwrite it.
	 * @return Instance of the ScoreboardBuilder
	 */
	public ScoreboardBuilder createSimpleEntry(int line, String value, boolean overwrite) {
		Validate.isTrue((line <= 22), "Max amount of lines is 22.");
		
		SimpleEntry entry = new SimpleEntry(this, Globals.color(value));
		
		placeEntry(this, overwrite, line, entry);
		
		return this;
	}
	
	public ScoreboardBuilder createAnimatedEntry(int line, boolean overwrite, int rateInTicks, int delayInTicks, String... phases) {
		Validate.isTrue((line <= 22), "Max amount of lines is 22.");
		
		if (phases == null || phases.length == 0) {
			return this;
		}
		
		AnimatedEntry entry = new AnimatedEntry(this, rateInTicks, delayInTicks, phases);
		
		placeEntry(this, overwrite, line, entry);
		
		return this;
	}
	
	public ScoreboardBuilder createTimer(int line, String value, int time) {
		Validate.isTrue((line <= 22), "Max amount of lines is 22.");
		SimpleEntry entry = new SimpleEntry(this, null);
		
		entry.makeTimer(time, Globals.color(value));
		entries.put(line, entry);
		
		return this;
	}
	
	@Override
	public Map<Integer, ScoreboardEntry> getEntries() {
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
	
	/**
	 * Place all the entries and build the scoreboard
	 *
	 * @return A scoreboard to send to the player.
	 */
	@Override
	public Scoreboard build() {
		placeEntries(this);
		
		return this.scoreboard;
	}
	
	/**
	 * This method is to stop a scoreboard's animations
	 * if no one is to be using it.
	 */
	@Override
	public void clear() {
		this.cancelAnimatedTitleTask();
		
		for (ScoreboardEntry ae : this.entries.values()) {
			ae.clear();
		}
		
		this.entries.clear();
	}
	
	/**
	 * Send the scoreboard to a player
	 *
	 * @return Instance of the ScoreboardBuilder
	 */
	@Override
	public void send(Player player) {
		if (player != null && player.isOnline()) {
			player.setScoreboard(this.build());
		}
	}
	
	/**
	 * Send the scoreboard to an list of players
	 *
	 * @param players The players to send the scoreboard to.
	 * @return Instance of the ScoreboardBuilder
	 */
	public ScoreboardBuilder send(List<Player> players) {
		Scoreboard sb = this.build();
		
		for (Player player : players) {
			player.setScoreboard(sb);
		}
		
		return this;
	}
	
	/**
	 * Send the scoreboard to an array of players
	 *
	 * @param players The players to send the scoreboard to.
	 * @return Instance of the ScoreboardBuilder
	 */
	public ScoreboardBuilder send(Player... players) {
		return this.send(Arrays.asList(players));
	}
	
	/**
	 * Cancel the animated title task.
	 *
	 * @return Instance of the ScoreboardBuilder
	 */
	public ScoreboardBuilder cancelAnimatedTitleTask() {
		if (this.animatedTitle && this.animatedTitleTask != null) {
			
			this.animatedTitleTask.cancel();
			this.animatedTitleTask = null;
			this.animatedTitle = false;
		}
		
		return this;
	}
	
	public static class SimpleEntry implements ScoreboardProvider.ScoreboardEntry {
		
		private final @Getter ScoreboardProvider scoreboard;
		private @Getter @Setter Team team;
		private @Getter String value;
		private @Getter String defaultValue;
		private @Getter BukkitRunnable timerTask;
		private @Getter int timeLeft;
		
		/**
		 * Constructor for a SimpleEntry.
		 * A SimpleEntry is for plain text or a simple timer.
		 *
		 * @param scoreboard The ScoreboardBuilder instance it will be placed in
		 * @param value      The value of the entry
		 */
		public SimpleEntry(ScoreboardProvider scoreboard, String value) {
			this.scoreboard = scoreboard;
			this.value = value;
		}
		
		public void setValue(String value) {
			this.value = value;
			
			ScoreboardBuilder.setValue(this.team, value);
		}
		
		public void makeTimer(int timeInSeconds, String defaultValue) {
			this.defaultValue = Globals.color(defaultValue);
			this.timeLeft = timeInSeconds;
			
			SimpleEntry entry = this;
			
			BukkitRunnable run = new BukkitRunnable() {
				@Override
				public void run() {
					if (timeLeft < 0) {
						ScoreboardTimerEndEvent event = new ScoreboardTimerEndEvent(scoreboard, entry);
						Bukkit.getPluginManager().callEvent(event);
						cancel();
					} else {
						
						int min = (timeLeft / 60);
						int sec = (timeLeft % 60);
						String minutes = min == 0 ? "00" : min <= 9 ? "0" + min : "" + min;
						String seconds = sec == 0 ? "00" : sec <= 9 ? "0" + sec : "" + sec;
						
						try {
							setValue(String.format(defaultValue, minutes, seconds));
						} catch (Exception ex) {
							setValue("%s:%s");
						}
						
						timeLeft--;
					}
				}
			};
			
			this.timerTask = run;
			run.runTaskTimer(instance, 0, 20);
		}
		
		public void stopTimer() {
			if (this.timerTask != null) {
				this.timerTask.cancel();
			}
		}
		
		@Override
		public void clear() {
			this.stopTimer();
			
			this.value = "";
		}
	}
	
	public class AnimatedEntry implements ScoreboardProvider.ScoreboardEntry {
		
		private final @Getter ScoreboardBuilder scoreboard;
		private final @Getter String[] phases;
		private final int rate;
		private final int delay;
		private final @Getter BukkitRunnable animatedTask;
		private @Getter @Setter Team team;
		private @Getter String value;
		
		/**
		 * The constructor of an animated entry to go through cycles.
		 *
		 * @param scoreboard   The ScoreboardBuilder instance it will be placed in
		 * @param rateInTicks  Time in between each phase in the cycle
		 * @param delayInTicks Time in between each cycle of the animation
		 * @param phases       The phases of the animation
		 */
		public AnimatedEntry(ScoreboardBuilder scoreboard, int rateInTicks, int delayInTicks, String... phases) {
			this.value = "";
			this.scoreboard = scoreboard;
			this.rate = rateInTicks;
			this.delay = delayInTicks;
			this.phases = phases;
			
			this.animatedTask = new BukkitRunnable() {
				@Override
				public void run() {
					new BukkitRunnable() {
						int i = 0;
						
						@Override
						public void run() {
							
							setValue(Globals.color(phases[i]));
							
							i++;
							if (i == phases.length) {
								this.cancel();
							}
						}
					}.runTaskTimer(instance, 0, rateInTicks);
					
				}
			};
			
			this.start();
		}
		
		public void start() {
			if (this.animatedTask != null) {
				this.animatedTask.runTaskTimer(instance, 0, this.delay + (this.phases.length * this.rate));
			}
		}
		
		public void stop() {
			if (this.animatedTask != null) {
				this.animatedTask.cancel();
			}
		}
		
		@Override
		public void setValue(String val) {
			this.value = val;
			
			ScoreboardBuilder.setValue(this.team, value);
		}
		
		@Override
		public void clear() {
			this.stop();
			
			this.value = "";
		}
	}
	
}
