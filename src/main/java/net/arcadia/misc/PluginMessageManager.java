package net.arcadia.misc;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.arcadia.Arcadian;
import net.arcadia.util.Globals;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PluginMessageManager implements PluginMessageListener {
	
	@Override
	public void onPluginMessageReceived(String channel, @NotNull Player player, byte[] bytes) {
		if (!channel.equalsIgnoreCase("arcadiacore:main")) {
			return;
		}
		
		ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
		String subChannel = in.readUTF();
		
		if (subChannel.equalsIgnoreCase("updatedisplayname")) {
			
			Arcadian arcadian = Arcadian.get(UUID.fromString(in.readUTF()));
			
			arcadian.setFullDisplayName(Globals.color(in.readUTF()));
			arcadian.setPrefix(Globals.color(in.readUTF()));
			arcadian.setNick(Globals.color(in.readUTF()));
			arcadian.setGroup(in.readUTF());
			
			arcadian.refreshName();
		}
		
	}
}
