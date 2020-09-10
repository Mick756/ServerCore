package net.arcadia.util;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import lombok.SneakyThrows;
import net.arcadia.ArcadiaCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.Vector;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class Util {
	
	public static Map<String, String[]> words = new HashMap<>();
	private static int largestWordLength = 0;
	
	public Util() {
		loadBadWords();
	}
	
	@SneakyThrows
	private static void loadBadWords() {
		BufferedReader reader = new BufferedReader(new InputStreamReader(new URL("https://docs.google.com/spreadsheets/d/1hIEi2YG3ydav1E06Bzf2mQbGZ12kh2fe4ISgLg_UBuM/export?format=csv").openConnection().getInputStream()));
		String line = "";
		int counter = 0;
		while((line = reader.readLine()) != null) {
			counter++;
			String[] content = null;
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
		return Arrays.toString(collection.toArray());
	}
	
	public static String toReadableTime(long time) {
		int seconds = (int) (time / 1000) % 60;
		int minutes = (int) (time / (1000 * 60)) % 60;
		int hours = (int) (time / (1000 * 60 * 60)) % 24;
		return String.format("%dh %dm %ds", hours, minutes, seconds);
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
					
					for(int s = 0; s < ignoreCheck.length; s++ ) {
						if(input.contains(ignoreCheck[s])) {
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
		if (badWords.size() > 0) {
			return true;
		}
		return false;
	}
	
}
