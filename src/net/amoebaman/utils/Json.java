package net.amoebaman.utils;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.com.google.common.collect.Lists;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.com.google.gson.stream.JsonReader;
import org.bukkit.craftbukkit.libs.com.google.gson.stream.JsonToken;
import org.bukkit.craftbukkit.libs.com.google.gson.stream.JsonWriter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Utility methods for serlializing/deserializing various things in JSON, using
 * the Google utilities JSON writer and reader embedded within Bukkit/Craftbukkit.
 * 
 * @author AmoebaMan
 */
public class Json {
	
	public static void writeItems(JsonWriter json, Iterable<ItemStack> items){
		try{
			json.beginArray();
			for(ItemStack item : items)
				writeItem(json, item);
			json.endArray();
		}
		catch(Exception e){ e.printStackTrace(); }
	}
	
	public static void writeItems(JsonWriter json, ItemStack... items){
		writeItems(json, Lists.newArrayList(items));
	}
	
	public static List<ItemStack> readItems(JsonReader json){
		List<ItemStack> items = new ArrayList<ItemStack>();
		try{
			json.beginArray();
			try{
				while(json.hasNext() && json.peek() != JsonToken.END_ARRAY)
					items.add(readItem(json));
			}
			catch(Exception e){ e.printStackTrace(); }
			json.endArray();
		}
		catch(Exception e){ e.printStackTrace(); }
		return items;
	}
	
	public static void writeItem(JsonWriter json, ItemStack stack){
		
		try{
			json.beginObject();
			
			if(stack != null && stack.getType() != Material.AIR){
				json.name("type").value(stack.getType().name());
				json.name("data").value(stack.getDurability());
				json.name("amount").value(stack.getAmount());
				
				json.name("enchants").beginObject();
				for(Enchantment enc : stack.getEnchantments().keySet())
					json.name(enc.getName()).value(stack.getEnchantmentLevel(enc));
				if(stack.hasItemMeta() && stack.getItemMeta() instanceof EnchantmentStorageMeta){
					EnchantmentStorageMeta meta = (EnchantmentStorageMeta) stack.getItemMeta();
					for(Enchantment enc : meta.getEnchants().keySet())
						json.name(enc.getName()).value(meta.getEnchantLevel(enc));
				}
				json.endObject();
				
				if(stack.hasItemMeta()){
					ItemMeta meta = stack.getItemMeta();
					json.name("meta").beginObject();
					
					if(meta.hasDisplayName())
						json.name("name").value(meta.getDisplayName());
					if(meta.hasLore()){
						json.name("lore").beginArray();
						for(String line : meta.getLore())
							json.value(line);
						json.endArray();
					}
					if(meta instanceof LeatherArmorMeta)
						json.name("color").value(((LeatherArmorMeta) meta).getColor().asRGB());
					if(meta instanceof SkullMeta)
						json.name("skull").value(((SkullMeta) meta).getOwner());
					if(meta instanceof MapMeta)
						json.name("map").value(((MapMeta) meta).isScaling());
					if(meta instanceof PotionMeta)
						writeEffectList(json.name("effects"), ((PotionMeta) meta).getCustomEffects());
					if(meta instanceof BookMeta)
						writeBook(json.name("book"), (BookMeta) meta);
					if(meta instanceof FireworkEffectMeta)
						writeBurst(json.name("burst"), ((FireworkEffectMeta) meta).getEffect());
					if(meta instanceof FireworkMeta)
						writeFirework(json.name("firework"), (FireworkMeta) meta);
					json.endObject();
				}
			}
			json.endObject();
		}
		catch(Exception e){ e.printStackTrace(); }
	}
	
	public static ItemStack readItem(JsonReader json){
		
		ItemStack item = new ItemStack(Material.AIR);
		ItemMeta meta = Bukkit.getItemFactory().getItemMeta(item.getType());
		
		try{
			json.beginObject();
			
			while(json.hasNext() && json.peek() != JsonToken.END_OBJECT){
				String name = json.nextName();
				if(name.equals("type")){
					item.setType(Material.getMaterial(json.nextString()));
					meta = Bukkit.getItemFactory().getItemMeta(item.getType());
				}
				if(name.equals("data"))
					item.setDurability((short) json.nextInt());
				if(name.equals("amount"))
					item.setAmount(json.nextInt());
				if(name.equals("enchants")){
					json.beginObject();
					while(json.peek() != JsonToken.END_OBJECT)
						if(meta instanceof EnchantmentStorageMeta)
							((EnchantmentStorageMeta) meta).addEnchant(Enchantment.getByName(json.nextName()), json.nextInt(), true);
						else
							meta.addEnchant(Enchantment.getByName(json.nextName()), json.nextInt(), true);
					json.endObject();
				}
				
				if(name.equals("meta")){
					json.beginObject();
					while(json.peek() != JsonToken.END_OBJECT){
						name = json.nextName();
						if(name.equals("name"))
							meta.setDisplayName(json.nextString());
						if(name.equals("lore")){
							json.beginArray();
							List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<String>();
							while(json.peek() != JsonToken.END_ARRAY)
								lore.add(json.nextString());
							meta.setLore(lore);
							json.endArray();
						}
						if(name.equals("color"))
							try{ ((LeatherArmorMeta) meta).setColor(Color.fromRGB(json.nextInt())); } catch(Exception e){}
						if(name.equals("skull"))
							try{ ((SkullMeta) meta).setOwner(json.nextString()); } catch(Exception e){}
						if(name.equals("map"))
							try{ ((MapMeta) meta).setScaling(json.nextBoolean()); } catch(Exception e){}
						if(name.equals("effects"))
							for(PotionEffect effect : readEffectList(json))
								try{ ((PotionMeta) meta).addCustomEffect(effect, true); } catch(Exception e){}
						if(name.equals("book"))
							readBook(json, (BookMeta) meta);
						if(name.equals("burst"))
							((FireworkEffectMeta) meta).setEffect(readBurst(json));
						if(name.equals("firework"))
							readFirework(json, (FireworkMeta) meta);
					}
					json.endObject();
				}
			}
			json.endObject();
		}
		catch(Exception e){ e.printStackTrace(); }
		
		item.setItemMeta(meta);
		return item;
	}
	
	public static JsonWriter writeEffectList(JsonWriter json, List<PotionEffect> effects){
		try{
			json.beginArray();
			for(PotionEffect effect : effects){
				json.beginObject();
				json.name("type").value(effect.getType().getName());
				json.name("duration").value(effect.getDuration());
				json.name("amplifier").value(effect.getAmplifier());
				json.endObject();
			}
			json.endArray();
		}
		catch(Exception e){ e.printStackTrace(); }
		return json;
	}
	
	public static List<PotionEffect> readEffectList(JsonReader json){
		List<PotionEffect> effects = new ArrayList<PotionEffect>();
		try{
			json.beginArray();
			while(json.peek() != JsonToken.END_ARRAY){
				json.beginObject();
				PotionEffectType type = null;
				int duration = 0, amplifier = 0;
				while(json.peek() != JsonToken.END_OBJECT){
					String name = json.nextName();
					if(name.equals("type"))
						type = PotionEffectType.getByName(json.nextString());
					if(name.equals("duration"))
						duration = json.nextInt();
					if(name.equals("amplifier"))
						amplifier = json.nextInt();
				}
				effects.add(new PotionEffect(type, duration, amplifier));
				json.endObject();
			}
			json.endArray();
		}
		catch(Exception e){ e.printStackTrace(); }
		return effects;
	}
	
	public static JsonWriter writeBook(JsonWriter json, BookMeta book){
		try{
			json.beginObject();
			json.name("title").value(book.getTitle());
			json.name("author").value(book.getAuthor());
			json.name("pages").beginArray();
			for(String page : book.getPages())
				json.value(page);
			json.endArray();
			json.endObject();
		}
		catch(Exception e){ e.printStackTrace(); }
		return json;
	}
	
	public static BookMeta readBook(JsonReader json, BookMeta book){
		try{
			json.beginObject();
			while(json.peek() != JsonToken.END_OBJECT){
				String name = json.nextName();
				if(name.equals("title"))
					book.setTitle(json.nextString());
				if(name.equals("author"))
					book.setAuthor(json.nextString());
				if(name.equals("pages")){
					json.beginArray();
					while(json.peek() != JsonToken.END_ARRAY)
						book.addPage(json.nextString());
					json.endArray();
				}
			}
			json.endObject();
		}
		catch(Exception e){}
		return book;
	}
	
	public static BookMeta readBook(JsonReader json){
		return readBook(json, (BookMeta) Bukkit.getItemFactory().getItemMeta(Material.WRITTEN_BOOK));
	}

	public static JsonWriter writeBurst(JsonWriter json, FireworkEffect burst){
		try{
			json.beginObject();
			json.name("type").value(burst.getType().name());
			json.name("primary").beginArray();
			for(Color color : burst.getColors())
				json.value(color.asRGB());
			json.endArray();
			json.name("fade").beginArray();
			for(Color color : burst.getFadeColors())
				json.value(color.asRGB());
			json.endArray();
			json.name("flicker").value(burst.hasFlicker());
			json.name("trail").value(burst.hasTrail());
			json.endObject();
		}
		catch(Exception e){ e.printStackTrace(); }
		return json;
	}
	
	public static FireworkEffect readBurst(JsonReader json){
		FireworkEffect.Builder burst = FireworkEffect.builder();
		try{
			json.beginObject();
			while(json.peek() != JsonToken.END_OBJECT){
				String name = json.nextName();
				if(name.equals("type"))
					burst.with(FireworkEffect.Type.valueOf(json.nextString()));
				if(name.equals("primary")){
					json.beginArray();
					while(json.peek() != JsonToken.END_ARRAY)
						burst.withColor(Color.fromRGB(json.nextInt()));
					json.endArray();
				}
				if(name.equals("fade")){
					json.beginArray();
					while(json.peek() != JsonToken.END_ARRAY)
						burst.withFade(Color.fromRGB(json.nextInt()));
					json.endArray();
				}
				if(name.equals("flicker") && json.nextBoolean())
					burst.withFlicker();
				if(name.equals("trail") && json.nextBoolean())
					burst.withTrail();
			}
			json.endObject();
		}
		catch(Exception e){ e.printStackTrace(); }
		return burst.build();
	}
	
	public static JsonWriter writeFirework(JsonWriter json, FireworkMeta firework){
		try{
			json.beginObject();
			json.name("fuse").value(firework.getPower());
			json.name("bursts").beginArray();
			for(FireworkEffect burst : firework.getEffects())
				writeBurst(json, burst);
			json.endArray();
			json.endObject();
		}
		catch(Exception e){ e.printStackTrace(); }
		return json;
	}
	
	public static FireworkMeta readFirework(JsonReader json, FireworkMeta firework){
		try{
			json.beginObject();
			while(json.peek() != JsonToken.END_OBJECT){
				String name = json.nextName();
				if(name.equals("fuse"))
					firework.setPower(json.nextInt());
				if(name.equals("bursts")){
					json.beginArray();
					while(json.peek() != JsonToken.END_ARRAY)
						firework.addEffect(readBurst(json));
					json.endArray();
				}
			}
			json.endObject();
		}
		catch(Exception e){}
		return firework;
	}

	public static FireworkMeta readFirework(JsonReader json){
		return readFirework(json, (FireworkMeta) Bukkit.getItemFactory().getItemMeta(Material.FIREWORK));
	}
	
}
