package net.amoebaman.amoebautils;

import java.util.Collection;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

/**
 * Represents a snapshot of a {@link Player}'s most important information,
 * completely immutable, which can restore itself to its player.
 * 
 * @author AmoebaMan
 */
public class PlayerSnapshot{
	
	private UUID id;
	
	private boolean healthScaled;
	private double health, maxHealth, healthScale;
	private int food;
	private float saturation, exhaustion;
	private int air, maxAir;
	private float exp;
	private int level;
	private GameMode mode;
	private Location loc;
	private ItemStack[] inv, armor, chest;
	private Collection<PotionEffect> effects;
	
	/**
	 * Constructs a snapshot of a {@link Player} in time.
	 * 
	 * @param player a player
	 */
	public PlayerSnapshot(Player player){
		id = player.getUniqueId();
		
		health = player.getHealth();
		maxHealth = player.getMaxHealth();
		healthScale = player.getHealthScale();
		healthScaled = player.isHealthScaled();
		
		food = player.getFoodLevel();
		saturation = player.getSaturation();
		exhaustion = player.getExhaustion();
		
		air = player.getRemainingAir();
		maxAir = player.getMaximumAir();
		
		exp = player.getExp();
		level = player.getLevel();
		
		inv = player.getInventory().getContents();
		armor = player.getInventory().getArmorContents();
		chest = player.getEnderChest().getContents();
		
		effects = player.getActivePotionEffects();
		
		mode = player.getGameMode();
		
		loc = player.getLocation();
	}
	
	/**
	 * Gets the player that this snapshot represents.
	 * 
	 * @return the player
	 */
	public Player getPlayer(){
		return Bukkit.getPlayer(id);
	}
	
	/**
	 * Attempts to restore the player's status to match this snapshot.
	 * 
	 * @return true if the restoration was successful, false otherwise
	 */
	@SuppressWarnings("deprecation")
    public boolean restore(){
		Player player = Bukkit.getPlayer(id);
		if(player == null)
			return false;
		
		player.setHealth(health);
		player.setMaxHealth(maxHealth);
		player.setHealthScale(healthScale);
		player.setHealthScaled(healthScaled);
		
		player.setFoodLevel((int) food);
		player.setSaturation(saturation);
		player.setExhaustion(exhaustion);
		
		player.setRemainingAir(air);
		player.setMaximumAir(maxAir);
		
		player.setExp(exp);
		player.setLevel(level);
		
		player.getInventory().setContents(inv);
		player.getInventory().setArmorContents(armor);
		player.getEnderChest().setContents(chest);
		player.updateInventory();
		
		for(PotionEffect active : player.getActivePotionEffects())
			player.removePotionEffect(active.getType());
		for(PotionEffect effect : effects)
			player.addPotionEffect(effect);
		
		player.setGameMode(mode);
		
		player.teleport(loc);
		
		return true;
	}
	
}
