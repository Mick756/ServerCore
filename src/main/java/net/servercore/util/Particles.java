package net.servercore.util;

import lombok.Getter;
import net.servercore.ServerCore;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static net.servercore.util.Util.asList;

public enum Particles {
	
	HEART(Particle.HEART, asList("hearts", "heart"), ParticleAttribute.SPAM, ParticleAttribute.LARGE),
	CRIT(Particle.CRIT, asList("crit", "crits", "crits_normal", "crit_normal")),
	NOTES(Particle.NOTE, asList("note", "notes"), ParticleAttribute.COLORABLE, ParticleAttribute.SPAM),
	REDSTONE(Particle.REDSTONE, asList("redstone", "dust"), ParticleAttribute.COLORABLE);
	
	private @Getter
	final
	Particle particle;
	private @Getter
	final
	List<String> names;
	private @Getter
	final
	List<ParticleAttribute> attributes;
	
	Particles(Particle particle, List<String> names, ParticleAttribute... attributes) {
		this.particle = particle;
		this.names = names;
		this.attributes = attributes.length == 0 ? new ArrayList<>() : Arrays.asList(attributes);
	}
	
	public static Particles getParticle(String name) {
		Particles particle = null;
		
		for (Particles pt : values()) {
			if (!pt.getNames().contains(name.toLowerCase())) continue;
			particle = pt;
			break;
		}
		return particle;
	}
	
	public void display(Location location, int amount, boolean color) {
		World w = location.getWorld();
		Object data = null;
		if (this.particle == Particle.REDSTONE && color) {
			data = generateRandom(3);
		}
		if (this.isSpammable() && amount < 10) {
			amount /= 2;
		}
		w.spawnParticle(this.particle, location, amount, 0, 0, 0, 0, data);
	}
	
	public boolean isColorable() {
		return this.attributes.contains(ParticleAttribute.COLORABLE);
	}
	
	public boolean isSpammable() {
		return this.attributes.contains(ParticleAttribute.SPAM);
	}
	
	private Particle.DustOptions generateRandom(int size) {
		Random r = ServerCore.getInstance().getRandom();
		return new Particle.DustOptions(Color.fromRGB(r.nextInt(255), r.nextInt(255), r.nextInt(255)), size);
	}
	
	private enum ParticleAttribute {
		
		COLORABLE,
		SPAM,
		LARGE
	}
	
}
