package net.amoebaman.utils;

import java.io.*;
import java.util.*;

import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.Plugin;

public class GenUtil{
	
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
				InputStream preset = plugin.getClass().getResourceAsStream("/defaults/" + name + ".yml");
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
	 * Gets the {@link LivingEntity} truly responsible for the damage, if there
	 * is one. This will trace back indirect damage to its source, including
	 * arrows to their shooters, and wolves to their owners.
	 * 
	 * @param event the event in question
	 * @return the true culprit, or null if the culprit is not a living entity
	 */
	public static LivingEntity getTrueCulprit(EntityDamageByEntityEvent event){
		Entity damager = event.getDamager();
		if(damager instanceof LivingEntity)
			return (LivingEntity) event.getDamager();
		else
			if(damager instanceof Projectile && ((Projectile) damager).getShooter() instanceof LivingEntity)
				return (LivingEntity) ((Projectile) damager).getShooter();
			else
				if(damager instanceof Tameable && ((Tameable) damager).getOwner() instanceof LivingEntity)
					return (LivingEntity) ((Tameable) damager).getOwner();
				else
					return null;
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
	
	public static List<String> objectsToStrings(Iterable<Object> objs){
		List<String> strs = new ArrayList<String>();
		for(Object each : objs)
			strs.add(String.valueOf(each));
		return strs;
	}
	
	public static List<String> playersToNames(Iterable<Player> players){
		List<String> names = new ArrayList<String>();
		for(Player player : players)
			names.add(player.getName());
		return names;
	}
	
	public static String concat(Iterable<String> elements, String prefix, String glue, String suffix){
		String str = prefix;
		boolean first = true;
		for(Object element : elements){
			if(!first)
				str += glue;
			str += String.valueOf(element);
			first = false;
		}
		return str + suffix;
	}
	
}
