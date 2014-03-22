package net.amoebaman.utils;

import java.util.*;

import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.amoebaman.utils.nms.Attributes;
import net.amoebaman.utils.nms.Attributes.Attribute;
import net.amoebaman.utils.nms.Attributes.AttributeType;
import net.amoebaman.utils.nms.Attributes.Operation;


public class YamlReader{
	
	public static ItemStack readItem(ConfigurationSection section){
		
		ItemStack item = new ItemStack(Material.AIR);
		try{
			
			item.setType(Material.getMaterial(section.getString("type")));
			item.setDurability((short) section.getInt("data"));
			item.setAmount(section.getInt("amount"));
			
			if(section.isConfigurationSection("enchants")){
				ItemMeta meta = item.getItemMeta();
				for(String key : section.getConfigurationSection("enchants").getKeys(false))
					if(meta instanceof EnchantmentStorageMeta)
						((EnchantmentStorageMeta) meta).addEnchant(Enchantment.getByName(key), section.getInt("enchants." + key), true);
					else
						meta.addEnchant(Enchantment.getByName(key), section.getInt("enchants." + key), true);
				item.setItemMeta(meta);
			}
			
			if(section.isConfigurationSection("meta")){
				ItemMeta meta = item.getItemMeta();
				if(section.isString("meta.name"))
					meta.setDisplayName(section.getString("meta.name"));
				if(section.isList("meta.lore"))
					meta.setLore(section.getStringList("meta.lore"));
				if(section.isInt("meta.color"))
					((LeatherArmorMeta) meta).setColor(Color.fromRGB(section.getInt("meta.color")));
				if(section.isString("meta.skull"))
					((SkullMeta) meta).setOwner(section.getString("meta.skull"));
				if(section.isBoolean("meta.map"))
					((MapMeta) meta).setScaling(section.getBoolean("meta.map"));
				if(section.isConfigurationSection("meta.effects"))
					for(String key : section.getConfigurationSection("meta.effects").getKeys(false))
						((PotionMeta) meta).addCustomEffect(readEffect(section.getConfigurationSection("meta.effects." + key)), true);
				if(section.isConfigurationSection("meta.book"))
					readBook((BookMeta) meta, section.getConfigurationSection("meta.book"));
				if(section.isConfigurationSection("meta.burst"))
					((FireworkEffectMeta) meta).setEffect(readBurst(section.getConfigurationSection("meta.burst")));
				if(section.isConfigurationSection("meta.firework"))
					readFirework((FireworkMeta) meta, section.getConfigurationSection("meta.firework"));
				item.setItemMeta(meta);
			}
			
			if(section.isConfigurationSection("attributes")){
				Attributes attrbs = new Attributes(item);
				for(String key : section.getConfigurationSection("attributes").getKeys(false))
					attrbs.add(readAttribute(section.getConfigurationSection("attributes." + key)));
				item = attrbs.getStack();
			}
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return item;
	}
	
	public static PotionEffect readEffect(ConfigurationSection section){
		PotionEffectType type = PotionEffectType.getByName(section.getString("type"));
		int duration = section.getInt("duration");
		int amplifier = section.getInt("amplifier");
		return new PotionEffect(type, duration, amplifier);
	}
	
	public static BookMeta readBook(BookMeta book, ConfigurationSection section){
		book.setTitle(section.getString("title"));
		book.setAuthor(section.getString("author"));
		book.setPages(section.getStringList("pages"));
		return book;
	}
	
	public static BookMeta readBook(ConfigurationSection section){
		return readBook((BookMeta) Bukkit.getItemFactory().getItemMeta(Material.WRITTEN_BOOK), section);
	}
	
	public static FireworkEffect readBurst(ConfigurationSection section){
		FireworkEffect.Builder burst = FireworkEffect.builder();
		burst.with(FireworkEffect.Type.valueOf(section.getString("type")));
		List<Color> primary = new ArrayList<Color>();
		for(String key : section.getConfigurationSection("primary").getKeys(false))
			primary.add(Color.fromRGB(section.getInt("primary." + key)));
		burst.withColor(primary);
		List<Color> fade = new ArrayList<Color>();
		for(String key : section.getConfigurationSection("fade").getKeys(false))
			fade.add(Color.fromRGB(section.getInt("fade." + key)));
		burst.withFade(fade);
		if(section.getBoolean("flicker"))
			burst.withFlicker();
		if(section.getBoolean("trail"))
			burst.withTrail();
		try{
			return burst.build();
		}
		catch(Exception e){
			return null;
		}
	}
	
	public static FireworkMeta readFirework(FireworkMeta firework, ConfigurationSection section){
		firework.setPower(section.getInt("fuse"));
		for(String key : section.getConfigurationSection("bursts").getKeys(false))
			firework.addEffect(readBurst(section.getConfigurationSection("bursts." + key)));
		return firework;
	}
	
	public static FireworkMeta readFirework(ConfigurationSection section){
		return readFirework((FireworkMeta) Bukkit.getItemFactory().getItemMeta(Material.FIREWORK), section);
	}
	
	public static Location readLoc(ConfigurationSection section){
		Location loc = Bukkit.getWorlds().get(0).getSpawnLocation();
		loc.setX(section.getDouble("x"));
		loc.setY(section.getDouble("y"));
		loc.setZ(section.getDouble("z"));
		loc.setPitch((float) section.getDouble("pitch", 0.0));
		loc.setYaw((float) section.getDouble("yaw", 0.0));
		return loc;
	}
	
	public static Attribute readAttribute(ConfigurationSection section){
		Attribute attrb = new Attribute();
		attrb.uuid = UUID.fromString(section.getString("uuid"));
		attrb.name = section.getString("name");
		attrb.type = AttributeType.fromId(section.getString("attrb"));
		attrb.op = Operation.valueOf(section.getString("op"));
		attrb.value = section.getDouble("value");
		return attrb;
	}
	
}
