package net.arcadia.commands.globals;

import net.arcadia.ACommand;
import net.arcadia.Arcadian;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReplyCMD extends ACommand {
	
	@Override
	public String alias() {
		return "reply";
	}
	
	@Override
	public String desc() {
		return "Reply to someone who sent you a message";
	}
	
	@Override
	public String usage() {
		return "[message]";
	}
	
	@Override
	public String permission() {
		return "";
	}
	
	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		Player player = validatePlayer(sender);
		Arcadian arcadian = Arcadian.get(player);
		
		if (arcadian.getLastMessaged() == null) {
			respond(sender, "&cNo one has messaged you!");
			return;
		}
		
		if (args.length == 0) {
			respond(sender, "&cYou must enter a message to reply with.");
			return;
		}
		
		List<String> newArgs = new ArrayList<>();
		newArgs.add(0, arcadian.getLastMessaged().getPlayer().getName());
		newArgs.addAll(Arrays.asList(args));
		
		new MsgCMD().execute(sender, label, newArgs.stream().toArray(String[]::new));
	}
}
