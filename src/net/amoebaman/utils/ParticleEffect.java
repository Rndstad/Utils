package net.amoebaman.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Contains an enumeration of all available particle effects, and methods for sending them
 * to players via packets.
 * 
 * @author AmoebaMan
 */
public enum ParticleEffect {
	
	HUGE_EXPLOSION("hugeexplosion"),
	LARGE_EXPLODE("largeexplode"),
	FIREWORKS_SPARK("fireworksSpark"),
	BUBBLE("bubble"),
	SUSPEND("suspended"),
	DEPTH_SUSPEND("depthsuspend"),
	TOWN_AURA("townaura"),
	CRIT("crit"),
	MAGIC_CRIT("magicCrit"),
	SMOKE("smoke"),
	MOB_SPELL("mobSpell"),
	MOB_SPELL_AMBIENT("mobSpellAmbient"),
	SPELL("spell"),
	INSTANT_SPELL("instantSpell"),
	WITCH_MAGIC("witchMagic"),
	NOTE("note"),
	PORTAL("portal"),
	ENCHANTMENT_TABLE("enchantmenttable"),
	EXPLODE("explode"),
	FLAME("flame"),
	LAVA("lava"),
	FOOTSTEP("footstep"),
	SPLASH("splash"),
	LARGE_SMOKE("largesmoke"),
	CLOUD("cloud"),
	RED_DUST("reddust"),
	SNOWBALL_POOF("snowballpoof"),
	DRIP_WATER("dripWater"),
	DRIP_LAVA("dripLava"),
	SNOW_SHOVEL("snowshovel"),
	SLIME("slime"),
	HEART("heart"),
	ANGRY_VILLAGER("angryVillager"),
	HAPPY_VILLAGER("happyVillager"),
	BARRIER("barrier")
	;
	
	private String packetName;
	private ParticleEffect(String packetName){
		this.packetName = packetName;
	}
	
	/**
	 * Sends this particle effect to a specific player.
	 * 
	 * @param p a player
	 * @param loc a location
	 * @param offsetX the maximum random x offset
	 * @param offsetY the maximum random y offset
	 * @param offsetZ the maximum random z offset
	 * @param speed the speed of the effect
	 * @param amount the amount of particles
	 */
	public void play(Player p, Location loc, float offsetX, float offsetY, float offsetZ, float speed, int amount) {
		sendPacket(p, createNormalPacket(this, loc, offsetX, offsetY, offsetZ, speed, amount));
	}
	
	/**
	 * Sends this particle effect to all players.
	 * 
	 * @param loc a location
	 * @param offsetX the maximum random x offset
	 * @param offsetY the maximum random y offset
	 * @param offsetZ the maximum random z offset
	 * @param speed the speed of the effect
	 * @param amount the amount of particles
	 */
	public void play(Location loc, float offsetX, float offsetY, float offsetZ, float speed, int amount) {
		Object packet = createNormalPacket(this, loc, offsetX, offsetY, offsetZ, speed, amount);
		for (Player p : loc.getWorld().getPlayers()) {
			sendPacket(p, packet);
		}
	}
	
	/**
	 * Sends a tile crack particle effect to a specific player.
	 * 
	 * @param p a player
	 * @param loc a location
	 * @param offsetX the maximum random x offset
	 * @param offsetY the maximum random y offset
	 * @param offsetZ the maximum random z offset
	 * @param amount the amount of particles
	 */
	public static void playTileCrack(Player p, Location loc, int id, byte data, float offsetX, float offsetY, float offsetZ, int amount) {
		sendPacket(p, createTileCrackPacket(id, data, loc, offsetX, offsetY, offsetZ, amount));
	}

	/**
	 * Sends a tile crack particle effect to all players.
	 * 
	 * @param loc a location
	 * @param offsetX the maximum random x offset
	 * @param offsetY the maximum random y offset
	 * @param offsetZ the maximum random z offset
	 * @param amount the amount of particles
	 */
	public static void playTileCrack(Location loc, int id, byte data, float offsetX, float offsetY, float offsetZ, int amount) {
		Object packet = createTileCrackPacket(id, data, loc, offsetX, offsetY, offsetZ, amount);
		for (Player p : loc.getWorld().getPlayers()) {
			sendPacket(p, packet);
		}
	}

	/**
	 * Sends an icon crack particle effect to a specific player.
	 * 
	 * @param p a player
	 * @param loc a location
	 * @param offsetX the maximum random x offset
	 * @param offsetY the maximum random y offset
	 * @param offsetZ the maximum random z offset
	 * @param amount the amount of particles
	 */
	public static void playIconCrack(Player p, Location loc, int id, byte data, float offsetX, float offsetY, float offsetZ, int amount) {
		sendPacket(p, createIconCrackPacket(id, data, loc, offsetX, offsetY, offsetZ, amount));
	}

	/**
	 * Sends an icon crack particle effect to all players.
	 * 
	 * @param loc a location
	 * @param offsetX the maximum random x offset
	 * @param offsetY the maximum random y offset
	 * @param offsetZ the maximum random z offset
	 * @param amount the amount of particles
	 */
	public static void playIconCrack(Location loc, int id, byte data, float offsetX, float offsetY, float offsetZ, int amount) {
		Object packet = createIconCrackPacket(id, data, loc, offsetX, offsetY, offsetZ, amount);
		for (Player p : loc.getWorld().getPlayers()) {
			sendPacket(p, packet);
		}
	}
	
	private Object createNormalPacket(ParticleEffect effect, Location loc, float offsetX, float offsetY, float offsetZ, float speed, int amount) {
		return createPacket(effect.packetName, loc, offsetX, offsetY, offsetZ, speed, amount);
	}
	
	private static Object createTileCrackPacket(int id, byte data, Location loc, float offsetX, float offsetY, float offsetZ, int amount) {
		return createPacket("tilecrack_" + id + "_" + data, loc, offsetX, offsetY, offsetZ, 0.1F, amount);
	}
	
	private static Object createIconCrackPacket(int id, byte data, Location loc, float offsetX, float offsetY, float offsetZ, int amount) {
		return createPacket("iconcrack_" + id + "_" + data, loc, offsetX, offsetY, offsetZ, 0.1F, amount);
	}
	
	private static Object createPacket(String effectName, Location loc, float offsetX, float offsetY, float offsetZ, float speed, int amount) {
		Object packet = null;
		try {
			if (amount <= 0) {
				throw new IllegalArgumentException("Amount of particles has to be greater than 0!");
			}
	        packet = ReflectionUtil.getClass("PacketPlayOutWorldParticles");
			ReflectionUtil.setValue(packet, "a", effectName);
			ReflectionUtil.setValue(packet, "b", (float) loc.getX());
			ReflectionUtil.setValue(packet, "c", (float) loc.getY());
			ReflectionUtil.setValue(packet, "d", (float) loc.getZ());
			ReflectionUtil.setValue(packet, "e", offsetX);
			ReflectionUtil.setValue(packet, "f", offsetY);
			ReflectionUtil.setValue(packet, "g", offsetZ);
			ReflectionUtil.setValue(packet, "h", speed);
			ReflectionUtil.setValue(packet, "i", amount);
			return packet;
		} catch (Exception e) {
			Bukkit.getLogger().warning("[ParticleEffect] Failed to create a particle packet! " + packet);
			return null;
		}
	}
	
	private static void sendPacket(Player p, Object packet) {
		if (packet == null) {
			return;
		}
		try {
			Object entityPlayer = ReflectionUtil.getMethod("getHandle", p.getClass(), 0).invoke(p);
			Object playerConnection = entityPlayer.getClass().getField("playerConnection").get(entityPlayer);
			ReflectionUtil.getMethod("sendPacket", playerConnection.getClass(), 1).invoke(playerConnection, packet);
		} catch (Exception e) {
			Bukkit.getLogger().warning("[ParticleEffect] Failed to send a particle packet to " + p.getName() + "!");
		}
	}
	
	private static class ReflectionUtil {
		
		public static Object getClass(String name, Object... args) throws Exception {
			Class<?> c = Class.forName(ReflectionUtil.getPackageName() + "." + name);
			int params = 0;
			if (args != null) {
				params = args.length;
			}
			for (Constructor<?> co : c.getConstructors()) {
				if (co.getParameterTypes().length == params) {
					return co.newInstance(args);
				}
			}
			return null;
		}
		
		public static Method getMethod(String name, Class<?> c, int params) {
			for (Method m : c.getMethods()) {
				if (m.getName().equals(name) && m.getParameterTypes().length == params) {
					return m;
				}
			}
			return null;
		}
		
		public static void setValue(Object instance, String fieldName, Object value) throws Exception {
			Field field = instance.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(instance, value);
		}
		
		public static String getPackageName() {
			return "net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
		}
	}
}