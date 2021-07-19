package net.servercore.misc;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.servercore.ServerPlayer;
import net.servercore.util.Globals;
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
			
			ServerPlayer sPlayer = ServerPlayer.get(UUID.fromString(in.readUTF()));

			sPlayer.setFullDisplayName(Globals.color(in.readUTF()));
			sPlayer.setPrefix(Globals.color(in.readUTF()));
			sPlayer.setNick(Globals.color(in.readUTF()));
			sPlayer.setGroup(in.readUTF());

			sPlayer.refreshName();
		}
		
	}
}
