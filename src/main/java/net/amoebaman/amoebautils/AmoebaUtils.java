package net.amoebaman.amoebautils;

import java.io.*;
import java.util.*;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import net.amoebaman.amoebautils.nms.ReflectionUtil;

public class AmoebaUtils extends JavaPlugin{
	
	/**
	 * Gets a YAML (.yml) configuration file from the plugin's folder. If the
	 * file does not exist and a default is embedded in the plugin's jarfile,
	 * the default will be copied to the plugin's directory and loaded instead.
	 * 
	 * @param plugin a plugin
	 * @param name the name of a configuration
	 * @return the configuration file
	 */
	public static File getConfigFile(Plugin plugin, String name){
		name.replace(".yml", "");
		try{
			File file = new File(plugin.getDataFolder().getPath() + File.separator + name + ".yml");
			file.getParentFile().mkdirs();
			if(!file.exists()){
				plugin.getLogger().info("plugins/KitMaster/" + name + ".yml was not found");
				plugin.getLogger().info("Writing new file with default contents");
				file.createNewFile();
				file.setWritable(true);
				InputStream preset = plugin.getClass().getResourceAsStream("/" + name + ".yml");
				if(preset != null){
					BufferedReader reader = new BufferedReader(new InputStreamReader(preset));
					BufferedWriter writer = new BufferedWriter(new FileWriter(file));
					while(reader.ready()){
						writer.write(reader.readLine());
						writer.newLine();
					}
					reader.close();
					writer.close();
				}
			}
			return file;
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Gets a random element out of a collection. This method is provided to
	 * allow selection of random elements from things like {@link Set sets}
	 * where there is no method to actually get an element, and also to
	 * abstract the dabbling in random numbers.
	 * 
	 * @param set
	 * @return
	 */
	public static <E> E getRandomElement(Collection<E> set){
		E element = null;
		Iterator<E> it = set.iterator();
		for(int i = 0; i < Math.random() * set.size() && it.hasNext(); i++)
			element = it.next();
		return element;
	}
	
	/**
	 * Transforms an iterable of objects into a list of strings using
	 * {@link String#valueOf(Object)}.
	 * 
	 * @param objs a bunch of objects
	 * @return a list of strings representing the objects
	 */
	public static List<String> objectsToStrings(Iterable<?> objs){
		List<String> strs = new ArrayList<String>();
		for(Object each : objs)
			strs.add(String.valueOf(each));
		return strs;
	}
	
	/**
	 * Transforms an iterable of players into a list of their names using
	 * {@link Player#getName()}.
	 * 
	 * @param players a bunch of Players
	 * @return a list of player names
	 */
	public static List<String> playersToNames(Iterable<Player> players){
		List<String> names = new ArrayList<String>();
		for(Player player : players)
			names.add(player.getName());
		return names;
	}
	
	/**
	 * Concatenates an iterable of objects into a single string, capped by a
	 * prefix and suffix and interspaced with a "glue" string. Each object is
	 * represented by {@link String#valueOf(Object)}, or alternatively the
	 * object's {@code getName()} method if it implements one.
	 * 
	 * @param elements a bunch of objects
	 * @param prefix the string to begin with
	 * @param glue the string to insert between elements
	 * @param suffix the string to end with
	 * @return the composite string
	 */
	public static String concat(Iterable<?> elements, String prefix, String glue, String suffix){
		String str = prefix;
		boolean first = true;
		for(Object element : elements){
			if(!first)
				str += glue;
			try{
				str += ReflectionUtil.getMethod(element.getClass(), "getName").invoke(element);
			}
			catch(Exception e){
				str += String.valueOf(element);
			}
			first = false;
		}
		return str + suffix;
	}

	/**
	 * See {@link #concat(Iterable, String, String, String)}.
	 * 
	 * @param elements a bunch of objects
	 * @param glue the string to insert between elements
	 * @return the composite string
	 */
	public static String concat(Iterable<?> elements, String glue){
		return concat(elements, "", glue, "");
	}
	
	/**
	 * See {@link #concat(Iterable, String, String, String)}
	 * 
	 * @param elements a bunch of objects
	 * @return the composite string
	 */
	public static String concat(Iterable<?> elements){
		return concat(elements, "");
	}
	
	/**
	 * Packages all arguments into a list, recursively unpacking iterables and
	 * arrays.
	 * <p>
	 * Specifically, this method iterates through all the arguments. If they are
	 * iterable containers or arrays, it will add all their elements to the
	 * returned list, otherwise, it will add the object itself to the returned
	 * list.
	 * <p>
	 * Generally, this method takes a whole junk load of objects, and returns a
	 * single-depth list containing all the elements present, with any arrays
	 * and containers unpacked.
	 * <p>
	 * This method guarantees that no elements returned will be arrays or
	 * containers.
	 * 
	 * @param objects a bunch of objects
	 * @return a single-depth list containing all the arguments
	 */
	public static List<Object> expand(Object... objects){
		List<Object> list = new ArrayList<Object>();
		for(Object object : objects)
			if(object instanceof Iterable)
				for(Object each : (Iterable<Object>) object)
					list.addAll(expand(each));
			else if(object instanceof Object[])
				for(Object each : (Object[]) object)
					list.addAll(expand(each));
			else
				list.add(object);
		return list;
	}
	
}
