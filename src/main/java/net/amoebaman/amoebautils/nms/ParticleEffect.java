package net.amoebaman.amoebautils.nms;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Contains an enumeration of all available particle effects, and methods for sending them
 * to players via packets.
 * 
 * @author AmoebaMan
 */
public enum ParticleEffect {
	
	//TODO -> basic javadoc for each of these effects
	
	/** Large explosion particles */
	HUGE_EXPLOSION("hugeexplosion"),
	
	/** Small explosion particles */
	LARGE_EXPLODE("largeexplode"),
	
	/** Particles given off as a firework ascends */
	FIREWORKS_SPARK("fireworksSpark"),
	
	/** Bubbles caused by entities interacting with water */
	BUBBLE("bubble"),
	
	/** Point particles suspended underwater */
	SUSPEND("suspended"),
	
	/** Point particles found near bedrock */
	DEPTH_SUSPEND("depthsuspend"),
	
	/** Point particles given off by mycelium */
	TOWN_AURA("townaura"),
	
	/** Critical hit particles */
	CRIT("crit"),
	
	/** Enchanted weapon hit particles */
	MAGIC_CRIT("magicCrit"),
	
	/** Small smoke particles from torches */
	SMOKE("smoke"),
	
	/** Opaque potion effect swirls */
	MOB_SPELL("mobSpell"),
	
	/** Nearly transparent potion effect swirls */
	MOB_SPELL_AMBIENT("mobSpellAmbient"),
	
	/** Potion effect swirls from breaking splash potions */
	SPELL("spell"),
	
	/** Particles from instant healt/damage potions */
	INSTANT_SPELL("instantSpell"),
	
	/** That weird crap over witches' heads */
	WITCH_MAGIC("witchMagic"),
	
	/** Notes from note blocks */
	NOTE("note"),
	
	/** Particles given off by endermen and nether portals */
	PORTAL("portal"),
	
	/** Runes sucked in by enchanting tables from bookshelves */
	ENCHANTMENT_TABLE("enchantmenttable"),
	
	/** The old explosion particles */
	EXPLODE("explode"),
	
	/** Flames from funances, torches, and mobspawners */
	FLAME("flame"),
	
	/** Embers with smoke trails ejected from lava */
	LAVA("lava"),
	
	/** Weird square things that are supposed to be footsteps */
	FOOTSTEP("footstep"),
	
	/** Water particles from rain hitting the ground */
	SPLASH("splash"),
	
	/** Big smoke particles given off by fires */
	LARGE_SMOKE("largesmoke"),
	
	/** Poofs given off by dying mobs */
	CLOUD("cloud"),
	
	/** Red puffs given off by activated restone */
	RED_DUST("reddust"),
	
	/** Particles from when thrown snowballs break */
	SNOWBALL_POOF("snowballpoof"),
	
	/** Water dripping through a block */
	DRIP_WATER("dripWater"),
	
	/** Lava dripping through a block */
	DRIP_LAVA("dripLava"),
	
	/** Dunno... */
	SNOW_SHOVEL("snowshovel"),
	
	/** Bits of slime from when slimes jump */
	SLIME("slime"),
	
	/** Hearts given off from breeding animals */
	HEART("heart"),
	
	/** Particles indicating villager happiness */
	ANGRY_VILLAGER("angryVillager"),
	
	/** Particles indidcating villager irritation */
	HAPPY_VILLAGER("happyVillager"),
	
	/** Shows players in creative mode where barriers are */
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
	public static void playBlockCrack(Player p, Location loc, int id, byte data, float offsetX, float offsetY, float offsetZ, int amount) {
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
	public static void playBlockCrack(Location loc, int id, byte data, float offsetX, float offsetY, float offsetZ, int amount) {
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
		return createPacket("blockcrack_" + id + "_" + data, loc, offsetX, offsetY, offsetZ, 0.1F, amount);
	}
	
	private static Object createIconCrackPacket(int id, byte data, Location loc, float offsetX, float offsetY, float offsetZ, int amount) {
		return createPacket("iconcrack_" + id + "_" + data, loc, offsetX, offsetY, offsetZ, 0.1F, amount);
	}
	
	private static Object createPacket(String effectName, Location loc, float offsetX, float offsetY, float offsetZ, float speed, int amount) {
		Class<?> packetClass = null;
		Object packet = null;
		try {
			if (amount <= 0) {
				throw new IllegalArgumentException("Amount of particles has to be greater than 0!");
			}
			packetClass = ReflectionUtil.getNMSClass("PacketPlayOutWorldParticles");
	        packet = packetClass.getConstructor().newInstance();
	        
	        ReflectionUtil.getField(packetClass, "a").set(packet, effectName);
	        ReflectionUtil.getField(packetClass, "b").set(packet, (float) loc.getX());
	        ReflectionUtil.getField(packetClass, "c").set(packet, (float) loc.getY());
	        ReflectionUtil.getField(packetClass, "d").set(packet, (float) loc.getZ());
	        ReflectionUtil.getField(packetClass, "e").set(packet, offsetX);
	        ReflectionUtil.getField(packetClass, "f").set(packet, offsetY);
	        ReflectionUtil.getField(packetClass, "g").set(packet, offsetZ);
	        ReflectionUtil.getField(packetClass, "h").set(packet, speed);
	        ReflectionUtil.getField(packetClass, "i").set(packet, amount);
			return packet;
		}
		catch (Exception e) { e.printStackTrace(); }
		return null;
	}
	
	private static void sendPacket(Player p, Object packet) {
		if (packet == null) {
			return;
		}
		try {
			Object entityPlayer = ReflectionUtil.getMethod(p.getClass(), "getHandle").invoke(p);
			Object playerConnection = entityPlayer.getClass().getField("playerConnection").get(entityPlayer);
			ReflectionUtil.getMethod(playerConnection.getClass(), "sendPacket").invoke(playerConnection, packet);
		}
		catch (Exception e) { e.printStackTrace(); }
	}
}