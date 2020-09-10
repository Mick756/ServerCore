package net.arcadia.util;

import net.arcadia.ArcadiaCore;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

public class Lang {
	
	private static YamlConfiguration lang;
	private final File langFile;
	
	public Lang(File file) {
		this.langFile = file;
		lang = YamlConfiguration.loadConfiguration(this.langFile);
	}
	
	public static String getMessage(String path) {
		if (isStringList(path)) {
			try {
				StringBuilder sb = new StringBuilder();
				int i = 0;
				List<String> lines = lang.getStringList(path);
				for (String line : lines) {
					sb.append(Globals.color(line));
					i++;
					if (i < lines.size()) {
						sb.append('\n');
					}
				}
				return sb.toString();
			} catch (Exception ex) {
				ArcadiaCore.error(String.format("The message at path '%s' contains invalid lines of text.", path));
				return String.format("%sPlease contact a server administrator about this message.", ChatColor.RED);
			}
		}
		return Globals.color(lang.getString(path));
	}
	
	public static boolean isStringList(String path) {
		return (lang.get(path) instanceof List<?>);
	}
}
