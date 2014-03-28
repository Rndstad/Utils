package net.amoebaman.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionEffect;

import net.amoebaman.utils.nms.Attributes;
import net.amoebaman.utils.nms.Attributes.Attribute;

public class YamlWriter{
	
	public static void writeItem(ItemStack item, ConfigurationSection section){
		
		if(item != null && item.getType() != Material.AIR){
			
			section.set("type", item.getType().name());
			section.set("data", item.getDurability());
			section.set("amount", item.getAmount());
			
			section.createSection("enchants");
			for(Enchantment enc : item.getEnchantments().keySet())
				section.set("enchants." + enc.getName(), item.getEnchantmentLevel(enc));
			if(item.hasItemMeta() && item.getItemMeta() instanceof EnchantmentStorageMeta){
				EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
				for(Enchantment enc : meta.getEnchants().keySet())
					section.set("enchants." + enc.getName(), meta.getEnchantLevel(enc));
			}
			
			if(item.hasItemMeta()){
				ItemMeta meta = item.getItemMeta();
				section.createSection("meta");
				
				if(meta.hasDisplayName())
					section.set("meta.name", meta.getDisplayName());
				if(meta.hasLore())
					section.set("meta.lore", meta.getLore());
				if(meta instanceof LeatherArmorMeta)
					section.set("meta.color", ((LeatherArmorMeta) meta).getColor().asRGB());
				if(meta instanceof SkullMeta)
					section.set("skull", ((SkullMeta) meta).getOwner());
				if(meta instanceof MapMeta)
					section.set("map", ((MapMeta) meta).isScaling());
				if(meta instanceof PotionMeta && ((PotionMeta) meta).hasCustomEffects()){
					section.createSection("effects");
					for(int i = 0; i < ((PotionMeta) meta).getCustomEffects().size(); i++)
						writeEffect(((PotionMeta) meta).getCustomEffects().get(i), section.createSection("effects." + i));
				}
				if(meta instanceof BookMeta)
					writeBook((BookMeta) meta, section.createSection("book"));
				if(meta instanceof FireworkEffectMeta)
					writeBurst(((FireworkEffectMeta) meta).getEffect(), section.createSection("burst"));
				if(meta instanceof FireworkMeta)
					writeFirework((FireworkMeta) meta, section.createSection("firework"));
			}
			
			List<Attribute> attrbs = new Attributes(item).getAttributes();
			if(attrbs.size() > 0){
				section.createSection("attributes");
				for(int i = 0; i < attrbs.size(); i++)
					writeAttribute(attrbs.get(i), section.createSection("attributes." + i));
			}
			
		}
		
	}
	
	public static void writeEffect(PotionEffect effect, ConfigurationSection section){
		section.set("type", effect.getType().getName());
		section.set("duration", effect.getDuration());
		section.set("amplifier", effect.getAmplifier());
	}
	
	public static void writeBook(BookMeta book, ConfigurationSection section){
		section.set("title", book.getTitle());
		section.set("author", book.getAuthor());
		section.set("pages", book.getPages());
	}
	
	public static void writeBurst(FireworkEffect burst, ConfigurationSection section){
		section.set("type", burst.getType().name());
		List<Integer> primary = new ArrayList<Integer>();
		for(Color color : burst.getColors())
			primary.add(color.asRGB());
		section.set("primary", primary);
		List<Integer> fade = new ArrayList<Integer>();
		for(Color color : burst.getFadeColors())
			fade.add(color.asRGB());
		section.set("fade", fade);
		section.set("flicker", burst.hasFlicker());
		section.set("trail", burst.hasTrail());
	}
	
	public static void writeFirework(FireworkMeta firework, ConfigurationSection section){
		section.set("fuse", firework.getPower());
		section.createSection("bursts");
		for(int i = 0; i < firework.getEffectsSize(); i++)
			writeBurst(firework.getEffects().get(i), section.createSection("bursts." + i));
	}
	
	public static void writeLoc(Location loc, boolean round, boolean rotation, ConfigurationSection section){
		section.set("world", loc.getWorld().getName());
		section.set("x", round ? loc.getBlockX() + 0.5 : loc.getX());
		section.set("y", round ? loc.getBlockY() : loc.getY());
		section.set("z", round ? loc.getBlockZ() + 0.5 : loc.getZ());
		if(rotation){
			section.set("pitch", round ? Math.round(loc.getPitch() * 22.5) / 22.5 : loc.getPitch());
			section.set("yaw", round ? Math.round(loc.getYaw() * 22.5) / 22.5 : loc.getYaw());
		}
	}
	
	public static void writeAttribute(Attribute attrb, ConfigurationSection section){
		section.set("uuid", attrb.uuid.toString());
		section.set("name", attrb.name);
		section.set("attrb", attrb.type.identifier);
		section.set("op", attrb.op.name());
		section.set("value", attrb.value);
	}
	
}
