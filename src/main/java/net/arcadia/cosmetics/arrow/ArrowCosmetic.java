package net.arcadia.cosmetics.arrow;

import net.arcadia.cosmetics.Cosmetic;
import org.bukkit.Particle;

public interface ArrowCosmetic extends Cosmetic {
	
	Particle particle();
	
	String permission();
	
}
