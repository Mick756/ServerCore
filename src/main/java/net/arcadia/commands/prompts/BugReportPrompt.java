package net.arcadia.commands.prompts;

import net.arcadia.commands.ReportBugCMD;
import net.arcadia.util.Globals;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

public class BugReportPrompt extends StringPrompt {
	
	private static void respondnp(Conversable con, String message) {
		con.sendRawMessage(Globals.color(message));
	}
	
	@Override
	public Prompt acceptInput(ConversationContext conversation, String s) {
		Conversable player = conversation.getForWhom();
		
		if (s.equalsIgnoreCase("cancel")) {
			respondnp(player, "&cYour current report was cancelled and not submitted.");
			respondnp(player, "&6&m-----------------------------------------------------");
			return END_OF_CONVERSATION;
		}
		
		if (s.length() < 30) {
			respondnp(player, "&cPlease describe your bug report in more detail like game server and when it happened.");
			respondnp(player, "&6&m-----------------------------------------------------");
			return END_OF_CONVERSATION;
		}
		
		ReportBugCMD.createReport((Player) player, s);
		respondnp(player, "\n&eYour report:&7 " + s);
		respondnp(player, "\n\n&7Successfully submitted bug report.");
		
		respondnp(player, "\n\n&6&m-----------------------------------------------------");
		return END_OF_CONVERSATION;
	}
	
	@Override
	public String getPromptText(ConversationContext conversation) {
		return Globals.color("&6&m-----------------------------------------------------" +
				"&6&l\nYou are creating a bug report." +
				"&c\nPlease explain the bug in as much detail as you can in the chat." +
				"\n&cYou can only have one bug report out at a time. Submitting a new one overwrites your previous one." +
				"&c\n\nType&e 'cancel&c' if you want to cancel the bug report.");
	}
	
}
