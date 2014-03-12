package net.amoebaman.utils.nms;

import java.util.UUID;

import org.bukkit.inventory.ItemStack;

import net.amoebaman.utils.nms.Attributes.Attribute;

/**
 * A class that eases the manipulation of item attributes to attach
 * persistent data to items, for plugin use.  Data stored in this
 * manner is keyed by {@link UUID}.
 * 
 * @author AmoebaMan
 */
public class AttributeStorage {
	
	/**
	 * Adds a data string to an {@link ItemStack}, and gives back the
	 * UUID that it's been stored under.
	 * 
	 * @param item an item
	 * @param data a data string
	 * @return the UUID that can be used to retrieve the data later
	 */
	public static UUID addData(ItemStack item, String data){
		UUID id = UUID.randomUUID();
		setData(item, id, data);
		return id;
	}
	
	/**
	 * Sets the data string to an {@link ItemStack} under an already-known
	 * UUID.
	 * 
	 * @param item an item
	 * @param uuid the UUID for the data
	 * @param data a data string
	 */
	public static void setData(ItemStack item, UUID uuid, String data){
		Attributes attrbs = new Attributes(item);
		Attribute attrb = attrbs.getAttribute(uuid);
		
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
	
	/**
	 * Gets a data string from an {@link ItemStack} using the UUID that was
	 * used to store it.
	 * 
	 * @param item an item
	 * @param uuid a UUID
	 * @return the data stored on the item using the UUID, or null if none was found
	 */
	public static String getData(ItemStack item, UUID uuid){
		Attribute attrb = new Attributes(item).getAttribute(uuid);
		return attrb != null ? attrb.name : null;
	}
	
}
