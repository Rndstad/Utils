package net.amoebaman.utils;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.com.google.gson.stream.JsonToken;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class JsonReader extends org.bukkit.craftbukkit.libs.com.google.gson.stream.JsonReader {
	
	public JsonReader(String str) {
		super(new StringReader(str));
	}
	
	public List<ItemStack> readItemList() throws IOException {
		List<ItemStack> items = new ArrayList<ItemStack>();
		beginArray();
		while (hasNext() && peek() != JsonToken.END_ARRAY)
			items.add(readItem());
		endArray();
		return items;
	}
	
	public ItemStack readItem() throws IOException {
		
		ItemStack item = new ItemStack(Material.AIR);
		ItemMeta meta = Bukkit.getItemFactory().getItemMeta(item.getType());
		
		beginObject();
		
		while (hasNext() && peek() != JsonToken.END_OBJECT) {
			String name = nextName();
			if (name.equals("type")) {
				item.setType(Material.getMaterial(nextString()));
				meta = Bukkit.getItemFactory().getItemMeta(item.getType());
			}
			if (name.equals("data"))
				item.setDurability((short) nextInt());
			if (name.equals("amount"))
				item.setAmount(nextInt());
			if (name.equals("enchants")) {
				beginObject();
				while (peek() != JsonToken.END_OBJECT)
					if (meta instanceof EnchantmentStorageMeta)
						((EnchantmentStorageMeta) meta).addEnchant(Enchantment.getByName(nextName()), nextInt(), true);
					else
						meta.addEnchant(Enchantment.getByName(nextName()), nextInt(), true);
				endObject();
			}
			
			if (name.equals("meta")) {
				beginObject();
				while (peek() != JsonToken.END_OBJECT) {
					name = nextName();
					if (name.equals("name"))
						meta.setDisplayName(nextString());
					if (name.equals("lore")) {
						beginArray();
						List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<String>();
						while (peek() != JsonToken.END_ARRAY)
							lore.add(nextString());
						meta.setLore(lore);
						endArray();
					}
					if (name.equals("color"))
						((LeatherArmorMeta) meta).setColor(Color.fromRGB(nextInt()));
					if (name.equals("skull"))
						((SkullMeta) meta).setOwner(nextString());
					if (name.equals("map"))
						((MapMeta) meta).setScaling(nextBoolean());
					if (name.equals("effects"))
						for (PotionEffect effect : readEffectList())
							((PotionMeta) meta).addCustomEffect(effect, true);
					if (name.equals("book"))
						readBook((BookMeta) meta);
					if (name.equals("burst"))
						((FireworkEffectMeta) meta).setEffect(readBurst());
					if (name.equals("firework"))
						readFirework((FireworkMeta) meta);
				}
				endObject();
			}
		}
		endObject();
		
		item.setItemMeta(meta);
		return item;
	}
	
	public List<PotionEffect> readEffectList() throws IOException {
		List<PotionEffect> effects = new ArrayList<PotionEffect>();
		
		beginArray();
		while (peek() != JsonToken.END_ARRAY) {
			beginObject();
			PotionEffectType type = null;
			int duration = 0, amplifier = 0;
			while (peek() != JsonToken.END_OBJECT) {
				String name = nextName();
				if (name.equals("type"))
					type = PotionEffectType.getByName(nextString());
				if (name.equals("duration"))
					duration = nextInt();
				if (name.equals("amplifier"))
					amplifier = nextInt();
			}
			effects.add(new PotionEffect(type, duration, amplifier));
			endObject();
		}
		endArray();
		
		return effects;
	}
	
	public BookMeta readBook(BookMeta book) throws IOException {
		beginObject();
		while (peek() != JsonToken.END_OBJECT) {
			String name = nextName();
			if (name.equals("title"))
				book.setTitle(nextString());
			if (name.equals("author"))
				book.setAuthor(nextString());
			if (name.equals("pages")) {
				beginArray();
				while (peek() != JsonToken.END_ARRAY)
					book.addPage(nextString());
				endArray();
			}
		}
		endObject();
		return book;
	}
	
	public BookMeta readBook() throws IOException {
		return readBook((BookMeta) Bukkit.getItemFactory().getItemMeta(Material.WRITTEN_BOOK));
	}
	
	public FireworkEffect readBurst() throws IOException {
		FireworkEffect.Builder burst = FireworkEffect.builder();
		
		beginObject();
		while (peek() != JsonToken.END_OBJECT) {
			String name = nextName();
			if (name.equals("type"))
				burst.with(FireworkEffect.Type.valueOf(nextString()));
			if (name.equals("primary")) {
				beginArray();
				while (peek() != JsonToken.END_ARRAY)
					burst.withColor(Color.fromRGB(nextInt()));
				endArray();
			}
			if (name.equals("fade")) {
				beginArray();
				while (peek() != JsonToken.END_ARRAY)
					burst.withFade(Color.fromRGB(nextInt()));
				endArray();
			}
			if (name.equals("flicker") && nextBoolean())
				burst.withFlicker();
			if (name.equals("trail") && nextBoolean())
				burst.withTrail();
		}
		endObject();
		
		return burst.build();
	}
	
	public FireworkMeta readFirework(FireworkMeta firework) throws IOException {
		
		beginObject();
		while (peek() != JsonToken.END_OBJECT) {
			String name = nextName();
			if (name.equals("fuse"))
				firework.setPower(nextInt());
			if (name.equals("bursts")) {
				beginArray();
				while (peek() != JsonToken.END_ARRAY)
					firework.addEffect(readBurst());
				endArray();
			}
		}
		endObject();
		
		return firework;
	}
	
	public FireworkMeta readFirework() throws IOException {
		return readFirework((FireworkMeta) Bukkit.getItemFactory().getItemMeta(Material.FIREWORK));
	}
	
	public Map<String,String> readMap() throws IOException {
		Map<String, String> map = new HashMap<String, String>();
		beginObject();
		while(peek() != JsonToken.END_OBJECT)
			map.put(nextName(), nextString());
		endObject();
		return map;
	}
	
	public Location readLoc() throws IOException{
		Location loc = Bukkit.getWorlds().get(0).getSpawnLocation();
		beginObject();
		while(peek() != JsonToken.END_OBJECT){
			String name = nextName();
			if(name.equals("world"))
				loc.setWorld(Bukkit.getWorld(nextString()));
			if(name.equals("x"))
				loc.setX(nextDouble());
			if(name.equals("y"))
				loc.setY(nextDouble());
			if(name.equals("z"))
				loc.setZ(nextDouble());
			if(name.equals("pitch"))
				loc.setYaw((float) nextDouble());
			if(name.equals("yaw"))
				loc.setPitch((float) nextDouble());
		}
		endObject();
		return loc;
	}
	
}
