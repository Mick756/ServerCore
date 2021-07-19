package net.servercore.util;

import org.bukkit.ChatColor;

public class Globals {
	
	public static String color(String color) {
		return ChatColor.translateAlternateColorCodes('&', color);
	}
	
	public static String descriptionFromArgs(int start, String[] args) {
		StringBuilder sb = new StringBuilder();
		
		int index = 1;
		for (int i = start; i < args.length; i++) {
			sb.append(args[i]);
			index++;
			if (index < args.length) {
				sb.append(' ');
			}
		}
		
		return sb.toString();
	}
	
}
