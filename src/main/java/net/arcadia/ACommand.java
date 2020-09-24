package net.arcadia;

import lombok.Getter;
import net.arcadia.commands.admin.*;
import net.arcadia.commands.donor.FireworkCMD;
import net.arcadia.commands.donor.MvpCMD;
import net.arcadia.commands.donor.NickCMD;
import net.arcadia.commands.donor.VipCMD;
import net.arcadia.commands.economy.BalanceCMD;
import net.arcadia.commands.economy.BalanceEditCMD;
import net.arcadia.commands.economy.PayCMD;
import net.arcadia.commands.economy.shop.CreateShopCMD;
import net.arcadia.commands.economy.shop.DeleteShopCMD;
import net.arcadia.commands.global.*;
import net.arcadia.commands.kit.CreateKitCMD;
import net.arcadia.commands.kit.DeleteKitCMD;
import net.arcadia.commands.kit.KitCMD;
import net.arcadia.commands.permission.PermCMD;
import net.arcadia.commands.permission.PermListCMD;
import net.arcadia.commands.player.gamemode.*;
import net.arcadia.commands.player.health.FeedCMD;
import net.arcadia.commands.player.health.HealCMD;
import net.arcadia.commands.report.ReportBugCMD;
import net.arcadia.commands.report.ReportCMD;
import net.arcadia.util.Globals;
import net.arcadia.util.Lang;
import net.arcadia.util.Util;
import net.arcadia.util.execptions.IncorrectUsageException;
import net.arcadia.util.execptions.InvalidGroupException;
import net.arcadia.util.execptions.PlayerNotOnlineException;
import net.arcadia.util.execptions.PlayerRequiredException;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;

import java.util.*;

// Credit to drew6017 modified by Kidzyy
public abstract class ACommand {
	
	private static final @Getter List<ACommand> commands = new ArrayList<>();
	private static final @Getter Map<UUID, Double> creatingShop = new HashMap<>();
	private static final @Getter List<UUID> deletingShop = new ArrayList<>();
	
	public static void respondnp(CommandSender sender, String msg) {
		sender.sendMessage(Globals.color(msg));
	}
	
	public static void respondfnp(CommandSender sender, String msg, Object... objs) {
		respondnp(sender, String.format(msg, objs));
	}
	
	public static void respond(CommandSender sender, String msg) {
		msg = ArcadiaCore.getPrefix() + Globals.color(msg);
		sender.sendMessage(msg);
	}
	
	public static void respondf(CommandSender sender, String msg, Object... objects) {
		respond(sender, String.format(msg, objects));
	}
	
	public static Player validatePlayer(CommandSender sender) {
		if (sender instanceof Player) {
			return (Player) sender;
		} else throw new PlayerRequiredException();
	}
	
	public static void validateArgsLength(String[] args, int length) {
		if (args.length != length) throw new IncorrectUsageException();
	}
	
	public abstract String alias();
	
	public abstract String desc();
	
	public abstract String usage();
	
	public abstract String permission();
	
	public abstract void execute(CommandSender sender, String label, String[] args);
	
	protected boolean matchesAlias(String a) {
		return alias().equalsIgnoreCase(a);
	}
	
	public boolean hasPerm(CommandSender sender) {
		return permission().equals("") || sender.hasPermission(permission());
	}
	
	public void respondiu(CommandSender sender, String label) {
		respondf(sender, "&cIncorrect usage. Correct usage: /%s %s ", label, usage());
	}
	
	public void call(CommandSender sender, String label, String[] args) {
		if (hasPerm(sender)) {
			try {
				execute(sender, label, args);
			} catch (PlayerRequiredException e) {
				respond(sender, "&cYou must be a player to use this command.");
			} catch (IncorrectUsageException e) {
				respondiu(sender, label);
			} catch (InvalidGroupException e) {
				respondf(sender, "&cThat is not a valid group!\n &6Available groups: %s", Util.toReadableList(ArcadiaCore.getUserGroups()));
			} catch (PlayerNotOnlineException e) {
				respond(sender, "&cThat player is not currently online.");
			}
		} else {
			respond(sender, String.format(Lang.getMessage("no-permission"), permission()));
		}
	}
	
	public static int addCommands() {
		addCommand("command-spy", new CommandSpyCMD());
		commands.add(new HelpCMD());
		addCommand("home", new HomeCMD());
		addCommand("gm", new GmCMD(), new GmaCMD(), new GmcCMD(), new GmsCMD(), new GmspCMD());
		addCommand("info", new InfoCMD());
		addCommand("msg", new MsgCMD());
		addCommand("mute", new MuteCMD());
		addCommand("mvp", new MvpCMD());
		addCommand("vip", new VipCMD());
		addCommand("nick", new NickCMD());
		addCommand("ping", new PingCMD());
		addCommand("random-teleport", new RandomTeleportCMD());
		addCommand("reply", new ReplyCMD());
		addCommand("bug-report", new ReportBugCMD());
		addCommand("report", new ReportCMD());
		addCommand("tag", new TagCMD());
		addCommand("tpa", new TpaCMD());
		addCommand("spawn", new SpawnCMD());
		addCommand("heal", new HealCMD());
		addCommand("feed", new FeedCMD());
		addCommand("balance", new BalanceCMD());
		addCommand("balanceedit", new BalanceEditCMD());
		addCommand("pay", new PayCMD());
		addCommand("createkit", new CreateKitCMD());
		addCommand("deletekit", new DeleteKitCMD());
		addCommand("kit", new KitCMD());
		addCommand("permlist", new PermListCMD());
		addCommand("perm", new PermCMD());
		addCommand("createshop", new CreateShopCMD());
		addCommand("deleteshop", new DeleteShopCMD());
		addCommand("smite", new SmiteCMD());
		addCommand("tphere", new TpHereCMD());
		addCommand("serverinfo", new ServerInfoCMD());
		addCommand("firework", new FireworkCMD());
		addCommand("givecustomitem", new GiveCustomItemCMD());
		addCommand("listarcadian", new ListArcadiansCMD());
		
		PluginManager pm = Bukkit.getPluginManager();
		for (ACommand command : commands) {
			String permission = command.permission();
			if (!permission.equals("") && pm.getPermission(permission) == null) {
				pm.addPermission(new Permission(permission, command.desc()));
			}
		}
		
		return commands.size();
	}
	
	private static final FileConfiguration config = ArcadiaCore.getInstance().getConfig();
	private static void addCommand(String p, ACommand command) {
		String path = "command-settings." + p + ".enable";
		
		if (config.isSet(path)) {
			boolean enable = ArcadiaCore.getInstance().getConfig().getBoolean(path);
			if (enable) commands.add(command);
		} else {
			config.set(path, true);
			ArcadiaCore.getInstance().saveConfig();
			commands.add(command);
		}
	}
	
	private static void addCommand(String path, ACommand... commands) {
		for (ACommand command : commands) {
			addCommand(path, command);
		}
	}
}
