package net.amoebaman.utils.nms;

import java.util.UUID;

import org.bukkit.inventory.ItemStack;

import net.amoebaman.utils.nms.Attributes.Attribute;

/**
 * A class that eases the manipulation of item attributes to attach
 * persistent data to items, for plugin use.
 * 
 * @author AmoebaMan
 */
public class AttributeStorage {
	
	public static UUID addData(ItemStack item, String data){
		UUID id = UUID.randomUUID();
		setData(item, id, data);
		return id;
	}
	
	public static void setData(ItemStack item, UUID uuid, String data){
		Attributes attrbs = new Attributes(item);
		Attribute attrb = getAttribute(attrbs, uuid);
		
		if(attrb == null){
			attrb = new Attribute();
			attrb.uuid = uuid;
			attrb.name = data;
			attrbs.add(attrb);
		}
		else{
			attrb.name = data;
			attrbs.update(attrb);
		}
	}
	
	public static String getData(ItemStack item, UUID uuid){
		Attribute attrb = getAttribute(new Attributes(item), uuid);
		return attrb != null ? attrb.name : null;
	}
	
	private static Attribute getAttribute(Attributes attrbs, UUID id) {
		for(Attribute attrb : attrbs.getAttributes())
			if(attrb.uuid.equals(id))
				return attrb;
		return null;
	}
	
}
