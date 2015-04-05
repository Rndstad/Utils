package net.amoebaman.amoebautils;

import java.io.StringReader;
import java.util.*;

import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.google.gson.stream.JsonToken;

import net.amoebaman.amoebautils.nms.*;
import net.amoebaman.amoebautils.nms.Attributes.Attribute;
import net.amoebaman.amoebautils.nms.Attributes.AttributeType;
import net.amoebaman.amoebautils.nms.Attributes.Operation;

public class JsonReader extends com.google.gson.stream.JsonReader{
	
	public JsonReader(String str){
		super(new StringReader(str));
	}
	
	public void close(){
		try{
			super.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public List<ItemStack> readItemList(){
		List<ItemStack> items = new ArrayList<ItemStack>();
		try{
			beginArray();
			while(hasNext() && peek() != JsonToken.END_ARRAY)
				items.add(readItem());
			endArray();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return items;
	}
	
	public ItemStack readItem(){
		
		ItemStack item = new ItemStack(Material.AIR);
		try{
			beginObject();
			
			while(hasNext() && peek() != JsonToken.END_OBJECT){
				String name = nextName();
				if(name.equals("type"))
					item.setType(Material.getMaterial(nextString()));
				if(name.equals("data"))
					item.setDurability((short) nextInt());
				if(name.equals("amount"))
					item.setAmount(nextInt());
				if(name.equals("enchants")){
					beginObject();
					ItemMeta meta = item.getItemMeta();
					while(peek() != JsonToken.END_OBJECT)
						if(meta instanceof EnchantmentStorageMeta)
							((EnchantmentStorageMeta) meta).addEnchant(Enchantment.getByName(nextName()), nextInt(), true);
						else
							meta.addEnchant(Enchantment.getByName(nextName()), nextInt(), true);
					item.setItemMeta(meta);
					endObject();
				}
				
				if(name.equals("meta")){
					beginObject();
					ItemMeta meta = item.getItemMeta();
					while(peek() != JsonToken.END_OBJECT){
						name = nextName();
						if(name.equals("name"))
							meta.setDisplayName(nextString());
						if(name.equals("lore")){
							beginArray();
							List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<String>();
							while(peek() != JsonToken.END_ARRAY)
								lore.add(nextString());
							meta.setLore(lore);
							endArray();
						}
						if(name.equals("color"))
							((LeatherArmorMeta) meta).setColor(Color.fromRGB(nextInt()));
						if(name.equals("skull"))
							((SkullMeta) meta).setOwner(nextString());
						if(name.equals("map"))
							((MapMeta) meta).setScaling(nextBoolean());
						if(name.equals("effects")){
							beginArray();
							while(peek() != JsonToken.END_ARRAY)
								((PotionMeta) meta).addCustomEffect(readEffect(), true);
							endArray();
						}
						if(name.equals("book"))
							readBook((BookMeta) meta);
						if(name.equals("burst"))
							((FireworkEffectMeta) meta).setEffect(readBurst());
						if(name.equals("firework"))
							readFirework((FireworkMeta) meta);
					}
					item.setItemMeta(meta);
					endObject();
				}
				
				if(name.equals("attributes")){
					beginArray();
					Attributes attrbs = new Attributes(item);
					while(peek() != JsonToken.END_ARRAY)
						attrbs.add(readAttribute());
					item = attrbs.getStack();
					endArray();
				}
			}
			endObject();
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return item;
	}
	
	public PotionEffect readEffect(){
		PotionEffectType type = null;
		int duration = 0;
		int amplifier = 0;
		try{
			beginObject();
			while(peek() != JsonToken.END_OBJECT){
				String name = nextName();
				if(name.equals("type"))
					type = PotionEffectType.getByName(nextString());
				if(name.equals("duration"))
					duration = nextInt();
				if(name.equals("amplifier"))
					amplifier = nextInt();
			}
			endObject();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return new PotionEffect(type, duration, amplifier);
	}
	
	public BookMeta readBook(BookMeta book){
		try{
			beginObject();
			while(peek() != JsonToken.END_OBJECT){
				String name = nextName();
				if(name.equals("title"))
					book.setTitle(nextString());
				if(name.equals("author"))
					book.setAuthor(nextString());
				if(name.equals("pages")){
					beginArray();
					while(peek() != JsonToken.END_ARRAY)
						book.addPage(nextString());
					endArray();
				}
			}
			endObject();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return book;
	}
	
	public BookMeta readBook(){
		return readBook((BookMeta) Bukkit.getItemFactory().getItemMeta(Material.WRITTEN_BOOK));
	}
	
	public FireworkEffect readBurst(){
		FireworkEffect.Builder burst = FireworkEffect.builder();
		try{
			beginObject();
			while(peek() != JsonToken.END_OBJECT){
				String name = nextName();
				if(name.equals("type"))
					burst.with(FireworkEffect.Type.valueOf(nextString()));
				if(name.equals("primary")){
					beginArray();
					while(peek() != JsonToken.END_ARRAY)
						burst.withColor(Color.fromRGB(nextInt()));
					endArray();
				}
				if(name.equals("fade")){
					beginArray();
					while(peek() != JsonToken.END_ARRAY)
						burst.withFade(Color.fromRGB(nextInt()));
					endArray();
				}
				if(name.equals("flicker") && nextBoolean())
					burst.withFlicker();
				if(name.equals("trail") && nextBoolean())
					burst.withTrail();
			}
			endObject();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		try{
			return burst.build();
		}
		catch(Exception e){
			return null;
		}
	}
	
	public FireworkMeta readFirework(FireworkMeta firework){
		try{
			beginObject();
			while(peek() != JsonToken.END_OBJECT){
				String name = nextName();
				if(name.equals("fuse"))
					firework.setPower(nextInt());
				if(name.equals("bursts")){
					beginArray();
					while(peek() != JsonToken.END_ARRAY)
						firework.addEffect(readBurst());
					endArray();
				}
			}
			endObject();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return firework;
	}
	
	public FireworkMeta readFirework(){
		return readFirework((FireworkMeta) Bukkit.getItemFactory().getItemMeta(Material.FIREWORK));
	}
	
	public Map<String, String> readMap(){
		Map<String, String> map = new HashMap<String, String>();
		try{
			beginObject();
			while(peek() != JsonToken.END_OBJECT)
				map.put(nextName(), nextString());
			endObject();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return map;
	}
	
	public Location readLoc(){
		Location loc = Bukkit.getWorlds().get(0).getSpawnLocation();
		try{
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
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return loc;
	}
	
	public Attribute readAttribute(){
		Attribute attrb = new Attribute();
		try{
			beginObject();
			while(peek() != JsonToken.END_OBJECT){
				String name = nextName();
				if(name.equals("uuid"))
					attrb.uuid = UUID.fromString(nextString());
				if(name.equals("name"))
					attrb.name = nextString();
				if(name.equals("attrb"))
					attrb.type = AttributeType.fromId(nextString());
				if(name.equals("op"))
					attrb.op = Operation.valueOf(nextString());
				if(name.equals("value"))
					attrb.value = nextDouble();
			}
			endObject();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return attrb;
	}
	
}
