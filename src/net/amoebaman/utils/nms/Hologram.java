package net.amoebaman.utils.nms;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.WitherSkull;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.google.common.collect.ImmutableSet;

public class Hologram {
	
	private static Map<String, Set<UUID>> BY_ID = new HashMap<String, Set<UUID>>();
	private static Map<UUID, String> BY_UUID = new HashMap<UUID, String>();
	
	public static void createLines(String id, Location loc, String[] text) {
		int i = 0;
		Entity vehicle = null;
		for (String t : text) {
			createText(vehicle, id, loc, t);
			loc = loc.clone().subtract(0, 0.25, 0);
			i++;
		}
		System.out.println(i);
	}
	
	public static void createLine(String id, Location loc, String text) {
		createText(null, id, loc, text);
	}
	
	private static Entity createText(Entity vehicle, String id, Location loc, String text) {
		// Please, don't reference me further
		loc = loc.clone();
		loc = loc.add(0, 55, 0);
		Object[] entityPair = Backend.getEntityPair(loc, text, vehicle == null);
		if (entityPair[0] == null || entityPair[1] == null)
			return null;
		
		Horse horse = (Horse) Backend.getBukkitEntity(entityPair[0]);
		horse.setAgeLock(true);
		horse.setAge(-1700000);
		if (vehicle == null) {
			vehicle = (WitherSkull) Backend.getBukkitEntity(entityPair[1]);
		}
		vehicle.setPassenger(horse);
		horse.setCustomNameVisible(true);
		horse.setRemoveWhenFarAway(false);
		addUUID(id, horse.getUniqueId());
		addUUID(id, vehicle.getUniqueId());
		return vehicle;
	}
	
	private static void addUUID(String id, UUID uuid) {
		Set<UUID> uuids = BY_ID.get(id);
		if (uuids == null) {
			uuids = new HashSet<UUID>();
			BY_ID.put(id, uuids);
		}
		uuids.add(uuid);
		BY_UUID.put(uuid, id);
	}
	
	public static String getByEntity(Entity e) {
		return getByUUID(e.getUniqueId());
	}
	
	public static String getByUUID(UUID uuid) {
		return BY_UUID.containsKey(uuid) ? BY_UUID.get(uuid) : "";
	}
	
	public static boolean isHologramEntity(Entity e) {
		System.out.println(getByUUID(e.getUniqueId()));
		return !getByUUID(e.getUniqueId()).isEmpty();
	}
	
	public static Set<String> getHologramIds() {
		return ImmutableSet.<String> copyOf(BY_ID.keySet());
	}
	
	public static Set<UUID> getHologramUUIDs(String id) {
		Set<UUID> uuids = BY_ID.get(id);
		return uuids != null ? ImmutableSet.<UUID> copyOf(uuids) : ImmutableSet.<UUID> of();
	}
	
	public static void removeHologram(String id) {
		Set<UUID> uuids = BY_ID.remove(id);
		if (uuids == null)
			return;
		for (World world : Bukkit.getWorlds())
			for (Entity e : world.getEntitiesByClasses(Horse.class, WitherSkull.class))
				if (uuids.contains(e.getUniqueId()))
					e.remove();
	}
	
	public static class Chars {
		
		public static final Map<ChatColor, Color> colors = new HashMap<ChatColor, Color>();
		static {
			colors.put(ChatColor.AQUA, new Color(85, 255, 255));
			colors.put(ChatColor.BLACK, new Color(0, 0, 0));
			colors.put(ChatColor.BLUE, new Color(85, 85, 255));
			colors.put(ChatColor.DARK_AQUA, new Color(0, 170, 170));
			colors.put(ChatColor.DARK_BLUE, new Color(0, 0, 170));
			colors.put(ChatColor.DARK_GRAY, new Color(85, 85, 85));
			colors.put(ChatColor.DARK_GREEN, new Color(0, 170, 0));
			colors.put(ChatColor.DARK_PURPLE, new Color(170, 0, 170));
			colors.put(ChatColor.DARK_RED, new Color(170, 0, 0));
			colors.put(ChatColor.GOLD, new Color(255, 170, 0));
			colors.put(ChatColor.GRAY, new Color(170, 170, 170));
			colors.put(ChatColor.GREEN, new Color(85, 255, 85));
			colors.put(ChatColor.LIGHT_PURPLE, new Color(255, 85, 255));
			colors.put(ChatColor.RED, new Color(255, 85, 85));
			colors.put(ChatColor.WHITE, new Color(255, 255, 255));
			colors.put(ChatColor.YELLOW, new Color(255, 255, 85));
		}
		
		public static ChatColor bestMatch(Color color) {
			ChatColor match = ChatColor.RESET;
			double closest = Double.MAX_VALUE;
			for (ChatColor each : colors.keySet()) {
				Color c = colors.get(each);
				double dist = Math.pow(color.getRed() - c.getRed(), 2) + Math.pow(color.getGreen() - c.getGreen(), 2) + Math.pow(color.getBlue() - c.getBlue(), 2);
				if (dist < closest) {
					closest = dist;
					match = each;
				}
			}
			return match;
		}
		
		public static enum Block {
			EMPTY(' ', 0), LIGHT('\u2591', 35), MEDIUM('\u2592', 75), DARK('\u2593', 150), SOLID('\u2588', 255), ;
			public char c;
			public int alpha;
			
			private Block(char c, int alpha) {
				this.c = c;
				this.alpha = alpha;
			}
			
			public static Block fromAlpha(int alpha) {
				for (Block each : values())
					if (each.alpha >= alpha)
						return each;
				return Block.SOLID;
			}
		}
		
	}
	
	public static class Image {
		
		public static char[][] getChars(BufferedImage image) {
			char[][] chars = new char[image.getWidth()][image.getHeight()];
			for (int x = 0; x < image.getWidth(); x++)
				for (int y = 0; y < image.getHeight(); y++) {
					int rgb = image.getRGB(x, y);
					int alpha = (rgb >> 24) & 0xff;
					chars[x][y] = Chars.Block.fromAlpha(alpha).c;
				}
			return chars;
		}
		
		public static ChatColor[][] getColors(BufferedImage image) {
			ChatColor[][] colors = new ChatColor[image.getWidth()][image.getHeight()];
			for (int x = 0; x < image.getWidth(); x++)
				for (int y = 0; y < image.getHeight(); y++) {
					int rgb = image.getRGB(x, y);
					Color color = new Color(rgb);
					ChatColor closest = Chars.bestMatch(color);
					colors[x][y] = closest;
				}
			return colors;
		}
		
		public static BufferedImage resizeImage(BufferedImage original, int width, int height) {
			BufferedImage resized = new BufferedImage(width, height, 6);
			Graphics2D gr = resized.createGraphics();
			gr.drawImage(original, 0, 0, width, height, null);
			gr.dispose();
			gr.setComposite(AlphaComposite.Src);
			gr.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			gr.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			gr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			return resized;
		}
		
		public static String[] toMessage(ChatColor[][] colours, char[][] chars) {
			String[] lines = new String[colours[0].length];
			for (int y = 0; y < colours[0].length; y++) {
				String line = "";
				for (int x = 0; x < colours.length; x++) {
					String before = colours[x][y].toString() + chars[x][y];
					line += before;
				}
				lines[y] = line;
			}
			return lines;
		}
		
	}
	
	public static class Holo {
		
		private boolean active = false;
		private int currentFrame = 0;
		private String id;
		private List<Frame> frames = new ArrayList<Frame>();
		private WeakReference<Horse>[] entities;
		private BukkitTask task;
		
		public Frame getCurrentFrame() {
			if (currentFrame < 0)
				currentFrame = 0;
			if (currentFrame >= this.frames.size())
				for (int i = this.frames.size(); i <= currentFrame; i++)
					frames.add(new Frame(0, new ArrayList<String>()));
			return frames.get(currentFrame);
		}
		
		public Holo addLine(String line) {
			getCurrentFrame().contents.add(line);
			return this;
		}
		
		public Holo addImage(BufferedImage image) {
			return addImage(image, image.getWidth(), image.getHeight());
		}
		
		public Holo addImage(BufferedImage image, int width, int height) {
			if (image.getWidth() != width || image.getHeight() != height)
				image = Image.resizeImage(image, width, height);
			ChatColor[][] colors = Image.getColors(image);
			char[][] chars = Image.getChars(image);
			Frame frame = getCurrentFrame();
			for (String line : Image.toMessage(colors, chars))
				frame.contents.add(line);
			return this;
		}
		
		public void create(final Location loc) {
			remove();
			/*
			 * Determine the maximum necessary width
			 */
			int size = 0;
			for (Frame frame : this.frames)
				if (frame.contents.size() > size)
					size = frame.contents.size();
			/*
			 * Enlarge the first line to match the necessary width
			 */
			List<String> f = frames.get(0).contents;
			while (f.size() < size)
				f.add("");
			createLines(id, loc, frames.get(0).getContentArray());
			if (frames.size() > 1) {
				
				this.task = new BukkitRunnable() {
					
					int thisFrame = 1;
					int tick = -1;
					
					public void run() {
						if (entities == null) {
							Set<UUID> uuids = getHologramUUIDs(id);
							List<WeakReference<Horse>> horses = new ArrayList<WeakReference<Horse>>();
							for (Horse h : loc.getWorld().getEntitiesByClass(Horse.class)) {
								if (uuids.contains(h.getUniqueId()))
									horses.add(new WeakReference<Horse>(h));
							}
							entities = horses.toArray(new WeakReference[horses.size()]);
						}
						if (active) {
							tick++;
							int ticks;
							int prevFrame = thisFrame;
							while ((ticks = getCurrentFrame().ticks) <= 0) {
								thisFrame++;
								if (thisFrame == prevFrame) {
									active = false;
									return;
								}
							}
							if (tick < ticks)
								return;
							tick = 0;
							thisFrame++;
							thisFrame %= frames.size();
							// HologramUtil.getInstance().removeHologram(id);
							// HologramUtil.getInstance().createLines(id, l,
							// frames.get(frame).toArray(new
							// String[frames.get(frame).size()]));
							Frame frame = getCurrentFrame();
							
							Horse h;
							// Guarantee no ArrayIndexOutOfBounds!
							String[] contents = frame.getContentArray();
							for (int i = 0; i < contents.length && i < entities.length; i++) {
								h = entities[i].get();
								if (h == null) {
									// Frame skip, but no casualties
									entities = null;
									return;
								}
								Backend.renameHorse(h, contents[i]);
							}
						}
						
					}
				}.runTaskTimer(Bukkit.getPluginManager().getPlugins()[0], 0, 10L);
			}
		}
		
		public void remove() {
			this.entities = null;
			removeHologram(this.id);
			if (this.task != null)
				this.task.cancel();
		}
		
		public static class Frame {
			public int ticks;
			public List<String> contents;
			
			private Frame(int ticks, List<String> contents) {
				this.ticks = ticks;
				this.contents = contents;
			}
			
			public String[] getContentArray() {
				return this.contents.toArray(new String[this.contents.size()]);
			}
			
		}
		
	}
	
	public static class Backend{
		
		private static Class<?> nmsEntity = ReflectionUtil.getNMSClass("Entity");
		private static Class<?> nmsEntityInsentient = ReflectionUtil.getNMSClass("EntityInsentient");
		private static Class<?> nmsHorse = ReflectionUtil.getNMSClass("EntityHorse");
		private static Class<?> nmsWorld = ReflectionUtil.getNMSClass("World");
		private static Class<?> nmsWitherSkeleton = ReflectionUtil.getNMSClass("EntityWitherSkull");
		private static Class<?> nmsFireball = ReflectionUtil.getNMSClass("EntityFireball");
		
		private static Constructor<?> nmsHorseCons;
		private static Constructor<?> nmsWitherSkeletonCons;
		static{
			try {
				nmsHorseCons = nmsHorse.getConstructor(nmsWorld);
				nmsWitherSkeletonCons = nmsWitherSkeleton.getConstructor(nmsWorld);
			}
			catch (Exception e) { e.printStackTrace(); }
		}
		
		public static Entity getBukkitEntity(Object entity) {
			try {
				return (Entity) ReflectionUtil.getMethod(nmsEntity, "getBukkitEntity").invoke(entity, new Class[0], new Object[0]);
			}
			catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
		public static Object[] getEntityPair(Location loc, String text, boolean spawn) {
			Object world = ReflectionUtil.getHandle(loc.getWorld());
			Object horse = null;
			Object skull = null;
			try {
				horse = nmsHorseCons.newInstance(world);
				skull = nmsWitherSkeletonCons.newInstance(world);
			}
			catch (Exception e) {}
			
			if (horse != null && skull != null) {
				
				try{
					ReflectionUtil.getField(nmsEntity, "locX").set(horse, loc.getX());
					ReflectionUtil.getField(nmsEntity, "locY").set(horse, loc.getY());
					ReflectionUtil.getField(nmsEntity, "locZ").set(horse, loc.getZ());
					ReflectionUtil.getField(nmsEntity, "invulnerable").set(horse, true);
					ReflectionUtil.getMethod(nmsEntityInsentient, "setCustomName").invoke(horse, text);
					
					ReflectionUtil.getField(nmsEntity, "locX").set(skull, loc.getX());
					ReflectionUtil.getField(nmsEntity, "locY").set(skull, loc.getY());
					ReflectionUtil.getField(nmsEntity, "locZ").set(skull, loc.getZ());
					ReflectionUtil.getField(nmsEntity, "motX").set(skull, 0);
					ReflectionUtil.getField(nmsEntity, "motY").set(skull, 0);
					ReflectionUtil.getField(nmsEntity, "motZ").set(skull, 0);
					ReflectionUtil.getField(nmsFireball, "dirX").set(skull, 0);
					ReflectionUtil.getField(nmsFireball, "dirY").set(skull, 0);
					ReflectionUtil.getField(nmsFireball, "dirZ").set(skull, 0);
					
					if (spawn) {
						ReflectionUtil.getMethod(nmsWorld, "addEntity").invoke(world, horse);
						ReflectionUtil.getMethod(nmsWorld, "addEntity").invoke(world, skull);
					}
				}
				catch(Exception e){}
			}
			
			return new Object[] { horse, skull };
		}
		
		public static void renameHorse(Horse horse, String name) {
			Object datHorse = ReflectionUtil.getHandle(horse);
			try {
				ReflectionUtil.getMethod(nmsEntityInsentient, "setCustomName").invoke(datHorse, name);
			}
			catch (Exception e) { e.printStackTrace(); }
		}
	}
}
