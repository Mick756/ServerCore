package net.servercore.survival.items;

import com.cryptomorin.xseries.XMaterial;
import de.tr7zw.changeme.nbtapi.NBTItem;
import net.servercore.ServerCore;
import net.servercore.ServerPlayer;
import net.servercore.util.XItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class EnderPearlGun implements CustomItem {
	
	@Override
	public String name() {
		return "&bEnder Pearl Gun";
	}
	
	@Override
	public ItemStack getItem() {
		
		NBTItem item = new XItemStackBuilder(XMaterial.STICK).setDisplayName(this.name()).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
				.setLore("&7This gun can launch your Ender Pearls", "&7even further!").toNBT();
		
		item.setString("ci", "enderpearlgun");
		item.setLong("u", System.currentTimeMillis());
		
		return item.getItem();
	}
	
	@Override
	public void action(Player player) {
		ServerPlayer sPlayer = ServerPlayer.get(player);
		
		if (!sPlayer.inventoryContains(new ItemStack(Material.ENDER_PEARL), 1)) {
			sPlayer.sendMessage(false, "&cYou need to have an ender pearl to use this item!");
			return;
		}
		sPlayer.inventoryRemove(new ItemStack(Material.ENDER_PEARL), 1);
		
		EnderPearl pearl = player.launchProjectile(EnderPearl.class);
		pearl.setVelocity(pearl.getVelocity().multiply(1.5f));
		
		new BukkitRunnable() {
			@Override
			public void run() {
				if (!pearl.isValid() || pearl.isOnGround() || pearl.isDead()) {
					cancel();
					return;
				}
				pearl.getWorld().spawnParticle(Particle.CRIMSON_SPORE, pearl.getLocation(), 10, 0, 0, 0);
			}
		}.runTaskTimer(ServerCore.getInstance(), 0 , 1);
	}
	
	@Override
	public int maxStackSize() {
		return 1;
	}
}
