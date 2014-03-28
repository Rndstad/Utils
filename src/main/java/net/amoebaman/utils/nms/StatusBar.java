package net.amoebaman.utils.nms;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.amoebaman.utils.maps.PlayerMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Assorted methods for manipulating packets to spawn fake Ender Dragons and
 * show players a status bar at the top of the screen using their health bar.
 * This class uses reflection, so even though it accesses NSM methods it should
 * be version-safe (assuming the names of classes don't change). This is a
 * clean-up/fix-up/refactoring of SoThatsIt's code, which originally did nearly
 * the same thing, but was less readable, had less features, and was broken in
 * some places. It also uses a trimmed down version of my own {@link PlayerMap}
 * class to store the fake dragons.
 * 
 * @author AmoebaMan
 */
public class StatusBar {
	
	private static PlayerMap<FakeDragon> DRAGONS = new PlayerMap<FakeDragon>();
	private static PlayerMap<Integer> EXPIRY_TASKS = new PlayerMap<Integer>(-1);
	
	/**
	 * Checks to see if the player is currently being displayed a status bar via
	 * fake Ender Dragon. <br>
	 * <br>
	 * This may sometimes return a false positive. Specifically, if a player is
	 * sent a fake dragon, and subsequently logs off and back on and the bar is
	 * not restored, the record of the dragon will remain here even though the
	 * client no longer has the entity. To avoid this, be sure to remove the bar
	 * manually using {@link #removeStatusBar(Player)} when the player leaves
	 * the server ({@link org.bukkit.event.player.PlayerQuitEvent} and
	 * {@link org.bukkit.event.player.PlayerKickEvent})
	 * 
	 * @param player a player
	 * @return true if this API has a record of the player being sent a bar
	 */
	public static boolean hasStatusBar(Player player) {
		return DRAGONS.containsKey(player) && DRAGONS.get(player) != null;
	}
	
	/**
	 * Removes a player's status bar by destroying their fake dragon (if they
	 * have one).
	 * 
	 * @param player a player
	 */
	public static void removeStatusBar(Player player) {
		if(hasStatusBar(player)) {
			sendPacket(player, DRAGONS.get(player).getDestroyPacket());
			DRAGONS.remove(player);
		}
	}
	
	/**
	 * Sets a player's status bar to display a specific message and fill amount.
	 * The fill amount is in decimal percent (i.e. 1 = 100%, 0 = 0%, 0.5 = 50%,
	 * 0.775 = 77.5%, etc.).
	 * 
	 * @param player a player
	 * @param text some text
	 * @param percent a decimal percent in the range (0,1]
	 * @param duration the duration in seconds of this status bar (0 is infinity)
	 */
	public static void setStatusBar(final Player player, String text, float percent, float duration) {
		if(player == null || !player.isValid() || player.isDead())
			return;
		FakeDragon dragon = DRAGONS.containsKey(player) ? DRAGONS.get(player) : null;
		
		if(percent > 1.0f)
			percent = 1.0f;
		if(percent < 0.005f)
			percent = 0.005f;
		
		if(text.isEmpty() && dragon != null)
			removeStatusBar(player);
		if(dragon == null) {
			dragon = new FakeDragon(player.getLocation().add(0, -200, 0), text, percent);
			sendPacket(player, dragon.getSpawnPacket());
			DRAGONS.put(player, dragon);
		}
		else {
			dragon.setName(text);
			dragon.setHealth(percent);
			sendPacket(player, dragon.getMetaPacket(dragon.getWatcher()));
			sendPacket(player, dragon.getTeleportPacket(player.getLocation().add(0, -200, 0)));
		}
		Bukkit.getScheduler().cancelTask(EXPIRY_TASKS.get(player));
		if(duration > 0)
			EXPIRY_TASKS.put(player, Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugins()[0], new Runnable() {
				
				public void run() {
					removeStatusBar(player);
				}
			}, (long) (20 * duration)));
		
	}
	
	/**
	 * Removes the status bar for all players on the server. See
	 * {@link #removeStatusBar(Player)}.
	 */
	public static void removeAllStatusBars() {
		for(Player each : Bukkit.getOnlinePlayers())
			removeStatusBar(each);
	}
	
	/**
	 * Sets the status bar for all players on the server. See
	 * {@link #setStatusBar(Player, String, float)}.
	 * 
	 * @param text some text with 64 characters or less
	 * @param percent a decimal percent in the range (0,1]
	 * @param duration the duration in seconds of this status bar (0 is infinity)
	 */
	public static void setAllStatusBars(String text, float percent, float duration) {
		for(Player each : Bukkit.getOnlinePlayers())
			setStatusBar(each, text, percent, duration);
	}
	
	private static void sendPacket(Player player, Object packet) {
		try {
			Object nmsPlayer = ReflectionUtil.getHandle(player);
			Field connectionField = nmsPlayer.getClass().getField("playerConnection");
			Object connection = connectionField.get(nmsPlayer);
			Method sendPacket = ReflectionUtil.getMethod(connection.getClass(), "sendPacket");
			sendPacket.invoke(connection, packet);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private static class FakeDragon {
		
		private static final int MAX_HEALTH = 200;
		private int id;
		private int x;
		private int y;
		private int z;
		private int pitch = 0;
		private int yaw = 0;
		private byte xvel = 0;
		private byte yvel = 0;
		private byte zvel = 0;
		private float health;
		private boolean visible = false;
		private String name;
		private Object world;
		
		private Object dragon;
		
		public FakeDragon(Location loc, String name, float percent) {
			this.name = name;
			this.x = loc.getBlockX();
			this.y = loc.getBlockY();
			this.z = loc.getBlockZ();
			this.health = percent * MAX_HEALTH;
			this.world = ReflectionUtil.getHandle(loc.getWorld());
		}
		
		public void setHealth(float percent) {
			this.health = percent * MAX_HEALTH;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public Object getSpawnPacket() {
			Class<?> Entity = ReflectionUtil.getNMSClass("Entity");
			Class<?> EntityLiving = ReflectionUtil.getNMSClass("EntityLiving");
			Class<?> EntityEnderDragon = ReflectionUtil.getNMSClass("EntityEnderDragon");
			
			try {
				dragon = EntityEnderDragon.getConstructor(ReflectionUtil.getNMSClass("World")).newInstance(world);
				
				ReflectionUtil.getMethod(EntityEnderDragon, "setLocation", double.class, double.class, double.class, float.class, float.class).invoke(dragon, x, y, z, pitch, yaw);
				ReflectionUtil.getMethod(EntityEnderDragon, "setInvisible", boolean.class).invoke(dragon, visible);
				ReflectionUtil.getMethod(EntityEnderDragon, "setCustomName", String.class).invoke(dragon, name);
				ReflectionUtil.getMethod(EntityEnderDragon, "setHealth", float.class).invoke(dragon, health);
				
				ReflectionUtil.getField(Entity, "motX").set(dragon, xvel);
				ReflectionUtil.getField(Entity, "motY").set(dragon, yvel);
				ReflectionUtil.getField(Entity, "motZ").set(dragon, zvel);
				
				this.id = (Integer) ReflectionUtil.getMethod(EntityEnderDragon, "getId").invoke(dragon);
				
				Class<?> packetClass = ReflectionUtil.getNMSClass("PacketPlayOutSpawnEntityLiving");
				return packetClass.getConstructor(new Class<?>[] { EntityLiving}).newInstance(dragon);
			}
			catch(Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
		public Object getDestroyPacket() {
			try {
				Class<?> packetClass = ReflectionUtil.getNMSClass("PacketPlayOutEntityDestroy");
				return packetClass.getConstructor(new Class<?>[] { int[].class}).newInstance(new int[] { id});
			}
			catch(Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
		public Object getMetaPacket(Object watcher) {
			try {
				Class<?> watcherClass = ReflectionUtil.getNMSClass("DataWatcher");
				Class<?> packetClass = ReflectionUtil.getNMSClass("PacketPlayOutEntityMetadata");
				return packetClass.getConstructor(new Class<?>[] { int.class, watcherClass, boolean.class}).newInstance(id, watcher, true);
			}
			catch(Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
		public Object getTeleportPacket(Location loc) {
			try {
				Class<?> packetClass = ReflectionUtil.getNMSClass("PacketPlayOutEntityTeleport");
				return packetClass.getConstructor(new Class<?>[] { int.class, int.class, int.class, int.class, byte.class, byte.class}).newInstance(this.id, loc.getBlockX() * 32, loc.getBlockY() * 32, loc.getBlockZ() * 32, (byte) ((int) loc.getYaw() * 256 / 360), (byte) ((int) loc.getPitch() * 256 / 360));
			}
			catch(Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
		public Object getWatcher() {
			Class<?> Entity = ReflectionUtil.getNMSClass("Entity");
			Class<?> DataWatcher = ReflectionUtil.getNMSClass("DataWatcher");
			
			try {
				Object watcher = DataWatcher.getConstructor(new Class<?>[] { Entity}).newInstance(dragon);
				Method a = ReflectionUtil.getMethod(DataWatcher, "a", new Class<?>[] { int.class, Object.class});
				
				a.invoke(watcher, 0, visible ? (byte) 0 : (byte) 0x20);
				a.invoke(watcher, 6, (Float) health);
				a.invoke(watcher, 7, (Integer) 0);
				a.invoke(watcher, 8, (Byte) (byte) 0);
				a.invoke(watcher, 10, name);
				a.invoke(watcher, 11, (Byte) (byte) 1);
				return watcher;
			}
			catch(Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
	}
	
}
