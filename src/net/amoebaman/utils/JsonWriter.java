package net.amoebaman.utils;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
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

/**
 * Extension of the Google libs JsonWriter included in CraftBukkit. It contains
 * several convenience methods for serializing common things into JSON form, and
 * also eliminates the need to explicitly reference the StringWriter behind the
 * scenes (assuming you're using a StringWriter).
 * 
 * @author AmoebaMan
 */
public class JsonWriter extends org.bukkit.craftbukkit.libs.com.google.gson.stream.JsonWriter {
	
	private StringWriter out;
	
	public JsonWriter() { this(new StringWriter()); }
	public JsonWriter(StringWriter out) { super(out); this.out = out; }
	
	public StringWriter getWriter() { return out; }
	public String toString() { return out == null ? null : out.toString(); }
	
	public JsonWriter beginObject() { try{ return (JsonWriter) super.beginObject(); } catch(Exception e){ e.printStackTrace(); return this; } }
	public JsonWriter beginArray() { try{ return (JsonWriter) super.beginArray(); } catch(Exception e){ e.printStackTrace(); return this; } }
	public JsonWriter endObject() { try{ return (JsonWriter) super.endObject(); } catch(Exception e){ e.printStackTrace(); return this; } }
	public JsonWriter endArray() { try{ return (JsonWriter) super.endArray(); } catch(Exception e){ e.printStackTrace(); return this; } }
	public JsonWriter name(String name) { try{ return (JsonWriter) super.name(name); } catch(Exception e){ e.printStackTrace(); return this; } }
	public JsonWriter value(boolean value) { try{ return (JsonWriter) super.value(value); } catch(Exception e){ e.printStackTrace(); return this; } }
	public JsonWriter value(double value) { try{ return (JsonWriter) super.value(value); } catch(Exception e){ e.printStackTrace(); return this; } }
	public JsonWriter value(long value) { try{ return (JsonWriter) super.value(value); } catch(Exception e){ e.printStackTrace(); return this; } }
	public JsonWriter value(Number value) { try{ return (JsonWriter) super.value(value); } catch(Exception e){ e.printStackTrace(); return this; } }
	public JsonWriter value(String value) { try{ return (JsonWriter) super.value(value); } catch(Exception e){ e.printStackTrace(); return this; } }
	
	public JsonWriter writeItemList(Iterable<ItemStack> items) {
		beginArray();
		for (ItemStack item : items)
			writeItem(item);
		endArray();
		return this;
	}
	
	public JsonWriter writeItemList(ItemStack... items) {
		beginArray();
		for (ItemStack item : items)
			writeItem(item);
		endArray();
		return this;
	}
	
	public JsonWriter writeItem(ItemStack item) {
		beginObject();
		
		if (item != null && item.getType() != Material.AIR) {
			name("type").value(item.getType().name());
			name("data").value(item.getDurability());
			name("amount").value(item.getAmount());
			
			name("enchants").beginObject();
			for (Enchantment enc : item.getEnchantments().keySet())
				name(enc.getName()).value(item.getEnchantmentLevel(enc));
			if (item.hasItemMeta() && item.getItemMeta() instanceof EnchantmentStorageMeta) {
				EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
				for (Enchantment enc : meta.getEnchants().keySet())
					name(enc.getName()).value(meta.getEnchantLevel(enc));
			}
			endObject();
			
			if (item.hasItemMeta()) {
				ItemMeta meta = item.getItemMeta();
				name("meta").beginObject();
				
				if (meta.hasDisplayName())
					name("name").value(meta.getDisplayName());
				if (meta.hasLore()) {
					name("lore").beginArray();
					for (String line : meta.getLore())
						value(line);
					endArray();
				}
				if (meta instanceof LeatherArmorMeta)
					name("color").value(((LeatherArmorMeta) meta).getColor().asRGB());
				if (meta instanceof SkullMeta)
					name("skull").value(((SkullMeta) meta).getOwner());
				if (meta instanceof MapMeta)
					name("map").value(((MapMeta) meta).isScaling());
				if (meta instanceof PotionMeta)
					name("effects").writeEffectList(((PotionMeta) meta).getCustomEffects());
				if (meta instanceof BookMeta)
					name("book").writeBook((BookMeta) meta);
				if (meta instanceof FireworkEffectMeta)
					name("burst").writeBurst(((FireworkEffectMeta) meta).getEffect());
				if (meta instanceof FireworkMeta)
					name("firework").writeFirework((FireworkMeta) meta);
				endObject();
			}
		}
		endObject();
		return this;
	}
	
	public JsonWriter writeEffectList(List<PotionEffect> effects) {
		beginArray();
		for (PotionEffect effect : effects) {
			beginObject();
			name("type").value(effect.getType().getName());
			name("duration").value(effect.getDuration());
			name("amplifier").value(effect.getAmplifier());
			endObject();
		}
		endArray();
		return this;
	}
	
	public JsonWriter writeBook(BookMeta book) {
		beginObject();
		name("title").value(book.getTitle());
		name("author").value(book.getAuthor());
		name("pages").beginArray();
		for (String page : book.getPages())
			value(page);
		endArray();
		endObject();
		return this;
	}
	
	public JsonWriter writeBurst(FireworkEffect burst) {
		beginObject();
		name("type").value(burst.getType().name());
		name("primary").beginArray();
		for (Color color : burst.getColors())
			value(color.asRGB());
		endArray();
		name("fade").beginArray();
		for (Color color : burst.getFadeColors())
			value(color.asRGB());
		endArray();
		name("flicker").value(burst.hasFlicker());
		name("trail").value(burst.hasTrail());
		endObject();
		return this;
	}
	
	public JsonWriter writeFirework(FireworkMeta firework) {
		beginObject();
		name("fuse").value(firework.getPower());
		name("bursts").beginArray();
		for (FireworkEffect burst : firework.getEffects())
			writeBurst(burst);
		endArray();
		endObject();
		return this;
	}
	
	public JsonWriter writeMap(Map<String, String> map) {
		beginObject();
		for (Entry<String, String> entry : map.entrySet())
			name(entry.getKey()).value(entry.getValue());
		endObject();
		return this;
	}
	
	public JsonWriter writeLoc(Location loc, boolean round, boolean rotation) throws IOException {
		beginObject();
		name("world").value(loc.getWorld().getName());
		name("x").value(round ? loc.getBlockX() + 0.5 : loc.getX());
		name("y").value(round ? loc.getBlockY() : loc.getY());
		name("z").value(round ? loc.getBlockZ() + 0.5 : loc.getZ());
		if (rotation) {
			name("pitch").value(round ? Math.round(loc.getPitch() * 22.5) / 22.5 : loc.getPitch());
			name("yaw").value(round ? Math.round(loc.getYaw() * 22.5) / 22.5 : loc.getYaw());
		}
		endObject();
		return this;
	}
	
}
