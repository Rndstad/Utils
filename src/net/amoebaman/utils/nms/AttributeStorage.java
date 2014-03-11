package net.amoebaman.utils.nms;

import java.util.UUID;

import org.bukkit.inventory.ItemStack;

import net.amoebaman.utils.nms.Attributes.Attribute;

public class AttributeStorage {
	
	private ItemStack stack;
	private final UUID uuid;
	
	private AttributeStorage(ItemStack stack, UUID uuid) {
		if(stack == null || uuid == null)
			throw new IllegalStateException("item and uuid cannot be null");
		this.stack = stack;
		this.uuid = uuid;
	}
	
	public static AttributeStorage getStorage(ItemStack stack, UUID uuid) {
		return new AttributeStorage(stack, uuid);
	}
	
	public String getData() {
		Attribute current = getAttribute(new Attributes(stack), uuid);
		return current != null ? current.name : null;
	}
	
	public void setData(String data) {
		Attributes attributes = new Attributes(stack);
		Attribute current = getAttribute(attributes, uuid);
		
		if(current == null) {
			Attribute attrb = new Attribute();
			attrb.uuid = uuid;
			attrb.name = data;
			attributes.add(attrb);
		}
		else
			current.name = data;
		this.stack = attributes.getStack();
	}
	
	public ItemStack getTarget() {
		return stack;
	}
	
	private Attribute getAttribute(Attributes attrbs, UUID id) {
		for(Attribute attrb : attrbs.values())
			if(attrb.uuid.equals(id))
				return attrb;
		return null;
	}
	
}
