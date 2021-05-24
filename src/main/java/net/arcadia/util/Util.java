package net.arcadia.util;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import lombok.SneakyThrows;
import net.arcadia.ArcadiaCore;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Util {
	
	public static Map<String, String[]> words = new HashMap<>();
	private static int largestWordLength = 0;
	
	@SneakyThrows
	public static void loadBadWords() {
		BufferedReader reader = new BufferedReader(new InputStreamReader(new URL("https://docs.google.com/spreadsheets/d/1hIEi2YG3ydav1E06Bzf2mQbGZ12kh2fe4ISgLg_UBuM/export?format=csv").openConnection().getInputStream()));
		String line;
		while((line = reader.readLine()) != null) {
			String[] content;
			try {
				content = line.split(",");
				if(content.length == 0) {
					continue;
				}
				String word = content[0];
				String[] ignore_in_combination_with_words = new String[]{};
				if(content.length > 1) {
					ignore_in_combination_with_words = content[1].split("_");
				}
				
				if(word.length() > largestWordLength) {
					largestWordLength = word.length();
				}
				words.put(word.replaceAll(" ", ""), ignore_in_combination_with_words);
				
			} catch(Exception e) {
				e.printStackTrace();
			}
			
		}
	}
	
	public static void doAsync(Runnable runnable) {
		Bukkit.getScheduler().runTaskAsynchronously(ArcadiaCore.getInstance(), runnable);
	}
	
	public static void doSync(Runnable runnable) {
		Bukkit.getScheduler().runTask(ArcadiaCore.getInstance(), runnable);
	}
	
	public static void doLater(Runnable runnable, long time) {
		Bukkit.getScheduler().runTaskLater(ArcadiaCore.getInstance(), runnable, time);
	}
	
	public static int getInt(String in) {
		try {
			return Integer.parseInt(in);
		} catch (NumberFormatException nfe) {
			return -1;
		}
	}
	
	public static boolean withinRegion(Location check, Location pos1, Location pos2) {
		
		int maxx = Math.max(pos1.getBlockX(), pos2.getBlockX());
		int maxy = Math.max(pos1.getBlockY(), pos2.getBlockY());
		int maxz = Math.max(pos1.getBlockZ(), pos2.getBlockZ());
		
		int minx = Math.min(pos1.getBlockX(), pos2.getBlockX());
		int miny = Math.min(pos1.getBlockY(), pos2.getBlockY());
		int minz = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
		
		return (minx <= check.getBlockX() && maxx >= check.getBlockX() && miny <= check.getBlockY() && maxy >= check.getBlockY() && minz <= check.getBlockZ() && maxz >= check.getBlockZ());
	}
	
	public static String translateColor(String text) {
		return ChatColor.translateAlternateColorCodes('&', text);
	}
	
	public static void callEvent(Event event) {
		Bukkit.getServer().getPluginManager().callEvent(event);
	}
	
	public static List<String> asList(String... strings) {
		return Arrays.asList(strings);
	}
	
	public static Location getRightSide(Location location, double distance) {
		float angle = location.getYaw() / 60;
		return location.clone().subtract(new Vector(Math.cos(angle), 0, Math.sin(angle)).normalize().multiply(distance));
	}
	
	public static Sound getSound(String name) {
		Sound sound = null;
		try {
			sound = XSound.valueOf(name.toUpperCase()).parseSound();
		} catch (IllegalArgumentException ex) {
		}
		return sound;
	}
	
	public static Class<?> getNMSClass(String name) {
		String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		
		try {
			return Class.forName("net.minecraft.server." + version + "." + name);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}
	
	public static Long isLong(String s) {
		try {
			return Long.parseLong(s);
		} catch (NumberFormatException ex) {
			return null;
		}
	}
	
	public static Integer isInt(String s) {
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException ex) {
			return null;
		}
	}
	
	public static Double isDouble(String s) {
		try {
			return Double.parseDouble(s);
		} catch (NumberFormatException ex) {
			return null;
		}
	}
	
	public static int[] getInventoryBorder(Inventory inv, boolean omit) {
		int invSize = inv.getSize();
		switch (invSize) {
			case 9:
				return new int[]{0, 8};
			case 18:
				return new int[]{0, 8, 9, 17};
			case 27:
				if (!omit) {
					return new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26};
				} else return new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 19, 20, 24, 25, 26};
			case 36:
				if (!omit) {
					return new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35};
				} else return new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 28, 29, 33, 34, 35};
			case 45:
				if (!omit) {
					return new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44};
				} else return new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 37, 38, 42, 43, 44};
			case 54:
				if (!omit) {
					return new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53};
				} else return new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 46, 47, 51, 52, 53};
			default:
				return new int[]{};
		}
	}
	
	public static ItemStack getSkull(Player player) {
		ItemStack stack = new ItemStackBuilder(XMaterial.PLAYER_HEAD.parseMaterial()).build();
		SkullMeta meta = (SkullMeta) stack.getItemMeta();
		
		meta.setPlayerProfile(player.getPlayerProfile());
		
		stack.setItemMeta(meta);
		return stack;
	}
	
	public static String toReadableList(Collection<String> collection) {
		char[] array = Arrays.toString(collection.toArray()).toCharArray();
		
		array[0] = ' ';
		array[array.length - 1] = ' ';
		
		return new String(array);
	}
	
	public static String toReadableTime(long time) {
		int seconds = (int)  (time / 1000);
		String s = seconds == 0 ? "" : (seconds % 60) + "s";
		int minutes = (seconds / 60);
		String m = minutes == 0 ? "" : (minutes % 60) + "m";
		int hours = (minutes / 60);
		String h = hours == 0 ? "" : (hours % 24) + "h";
		int days = (hours / 24);
		String d = days == 0 ? "" : days + "d";
		return String.format("%s %s %s %s", d, h, m, s).trim();
	}
	
	public static String toReadableTime(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
		return format.format(date);
	}
	
	public static ArrayList<String> badWordsFound(String input) {
		if(input == null) {
			return new ArrayList<>();
		}
		
		input = input.replaceAll("1","i");
		input = input.replaceAll("!","i");
		input = input.replaceAll("3","e");
		input = input.replaceAll("4","a");
		input = input.replaceAll("@","a");
		input = input.replaceAll("5","s");
		input = input.replaceAll("7","t");
		input = input.replaceAll("0","o");
		input = input.replaceAll("9","g");
		
		ArrayList<String> badWords = new ArrayList<>();
		input = input.toLowerCase().replaceAll("[^a-zA-Z]", "");
		
		for(int start = 0; start < input.length(); start++) {
			
			for(int offset = 1; offset < (input.length()+1 - start) && offset < largestWordLength; offset++)  {
				String wordToCheck = input.substring(start, start + offset);
				
				if(words.containsKey(wordToCheck)) {
					
					String[] ignoreCheck = words.get(wordToCheck);
					boolean ignore = false;
					
					for (String value : ignoreCheck) {
						if (input.contains(value)) {
							ignore = true;
							break;
						}
					}
					
					if(!ignore) {
						badWords.add(wordToCheck);
					}
				}
			}
		}
		return badWords;
	}
	
	public static boolean filterText(String input) {
		ArrayList<String> badWords = badWordsFound(input);
		return badWords.size() > 0;
	}
	
	public static double round(double value, int places) {
		if (places < 0) throw new IllegalArgumentException();
		
		BigDecimal bd = new BigDecimal(Double.toString(value));
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}
	
	public static Long getFromTimeFormat(String input) {
		long time = Long.parseLong(input.replaceAll("[^0-9]", ""));
		String timeValue = input.replaceAll("[^A-Za-z]", "").toLowerCase();
		
		switch (timeValue) {
			case "sec":
			case "second":
			case "seconds":
				return TimeUnit.SECONDS.toMillis(time);
			case "min":
			case "minute":
			case "minutes":
				return TimeUnit.MINUTES.toMillis(time);
			case "h":
			case "hour":
			case "hours":
				return TimeUnit.HOURS.toMillis(time);
			case "d":
			case "day":
			case "days":
				return TimeUnit.DAYS.toMillis(time);
			case "w":
			case "week":
			case "weeks":
				return 7 * TimeUnit.DAYS.toMillis(time);
			case "mon":
			case "month":
			case "months":
				return 30 * TimeUnit.DAYS.toMillis(time);
			case "y":
			case "year":
			case "years":
				return 365 * TimeUnit.DAYS.toMillis(time);
			default:
				return null;
		}
	}
	
	public static void shootRandomFirework(Player player) {
		FireworkEffect.Type type;
		Firework firework = (Firework) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
		FireworkMeta fm = firework.getFireworkMeta();
		
		Random r = new Random();
		int fType = r.nextInt(5) + 1;
		switch (fType) {
			default:
				type = FireworkEffect.Type.BALL;
				break;
			case 2:
				type = FireworkEffect.Type.BALL_LARGE;
				break;
			case 3:
				type = FireworkEffect.Type.BURST;
				break;
			case 4:
				type = FireworkEffect.Type.CREEPER;
				break;
			case 5:
				type = FireworkEffect.Type.STAR;
				break;
		}
		
		int c1i = r.nextInt(16) + 1;
		int c2i = r.nextInt(16) + 1;
		Color c1 = getColor(c1i);
		Color c2 = getColor(c2i);
		
		FireworkEffect effect = FireworkEffect.builder().flicker(r.nextBoolean()).withColor(c1).withFade(c2).with(type).trail(r.nextBoolean()).build();
		int power = r.nextInt(2) + 1;
		
		fm.addEffect(effect);
		fm.setPower(power);
		firework.setFireworkMeta(fm);
	}
	
	public static Color getColor(int c) {
		switch (c) {
			default:
				return Color.AQUA;
			case 2:
				return Color.BLACK;
			case 3:
				return Color.BLUE;
			case 4:
				return Color.FUCHSIA;
			case 5:
				return Color.GRAY;
			case 6:
				return Color.GREEN;
			case 7:
				return Color.LIME;
			case 8:
				return Color.MAROON;
			case 9:
				return Color.NAVY;
			case 10:
				return Color.OLIVE;
			case 11:
				return Color.ORANGE;
			case 12:
				return Color.PURPLE;
			case 13:
				return Color.RED;
			case 14:
				return Color.SILVER;
			case 15:
				return Color.TEAL;
			case 16:
				return Color.WHITE;
			case 17:
				break;
		}
		return Color.YELLOW;
	}
	
	public static void changePlayerName(Player player, String prefix, String suffix, TeamAction action) {
		
		Scoreboard scoreboard;
		Team team;
		
		if (player == null || prefix == null || suffix == null || action == null) {
			return;
		}
		
		scoreboard = player.getScoreboard();
		
		if (scoreboard.getTeam(player.getName()) == null) {
			scoreboard.registerNewTeam(player.getName());
		}
		
		team = scoreboard.getTeam(player.getName());
		
		if (team != null) {
			team.setPrefix(Globals.color(prefix));
			team.setSuffix(Globals.color(suffix));
			team.setNameTagVisibility(NameTagVisibility.ALWAYS);
			
			switch (action) {
				case CREATE:
					team.addPlayer(player);
					break;
				case UPDATE:
					team.unregister();
					scoreboard.registerNewTeam(player.getName());
					team = scoreboard.getTeam(player.getName());
					
					if (team != null) {
						team.setPrefix(Globals.color(prefix));
						team.setSuffix(Globals.color(suffix));
						team.setNameTagVisibility(NameTagVisibility.ALWAYS);
						team.addPlayer(player);
					}
					
					break;
				case DESTROY:
					team.unregister();
					break;
			}
		}
	}
	
	public enum TeamAction {
		CREATE, DESTROY, UPDATE
	}
	
}
