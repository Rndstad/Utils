
package net.amoebaman.amoebautils.nms;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.inventory.ItemStack;

import net.amoebaman.amoebautils.nms.NbtFactory.NbtCompound;
import net.amoebaman.amoebautils.nms.NbtFactory.NbtList;

/**
 * A class to ease the task of manipulating and editing the attributes system
 * attached to the NBT data of item stacks.
 * 
 * @author AmoebaMan
 */
public class Attributes{
	
	/**
	 * Represents the types of operations that Minecraft uses to determine the
	 * way the values of attributes are applied.
	 * 
	 * @author AmoebaMan
	 */
	public enum Operation{
		
		/** The value is used as an additive increase */
		ADD_NUMBER,
		
		/** The value is used as a multiplicative decimal percent, and its
		 * effects stack multiplicatively with other similar attributes */
		MULTIPLY_PERCENTAGE,
		
		/** The value is used as a multiplicative decimal percent, and its
		 * effects stack additively with other similar attributes */
		ADD_PERCENTAGE,
		;
		
	}
	
	/**
	 * Represents all of Minecraft's native attributes that are applicable to
	 * all living entities.
	 * 
	 * @author AmoebaMan
	 */
	public static enum AttributeType{
		
		/** Attribute that increases an entity's maximum health */
		GENERIC_MAX_HEALTH("generic.maxHealth"),
		
		/** Attribute that increases how far away the entity can follow a
		 * target using its pathfinder */
		GENERIC_FOLLOW_RANGE("generic.followRange"),
		
		/** Attribute that increases the damage an entity deals */
		GENERIC_ATTACK_DAMAGE("generic.attackDamage"),
		
		/** Attribute that increases an entity's movement speed */
		GENERIC_MOVEMENT_SPEED("generic.movementSpeed"),
		
		/** Attribute that decreases the amount of knockback an entity
		 * experiences when they take damage */
		GENERIC_KNOCKBACK_RESISTANCE("generic.knockbackResistance"),
		DUMMY("dummy"),
		;
		
		/** The identifier that Minecraft uses to determine what this
		 * attribute actually does */
		public String identifier;
		
		private AttributeType(String identifier){ this.identifier = identifier; }
		
		/**
		 * Matches an attribute from it's Minecraft identifier
		 * @param id the Minecraft identifier
		 * @return the matching {@link AttributeType}, or null if no match
		 * can be found
		 */
		public static AttributeType fromId(String id){
			for(AttributeType type : values())
				if(type.identifier.equals(id))
					return type;
			return null;
		}
		
	}

	/**
	 * Stores all the information found within an attribute, and conveniently
	 * extracts and writes to NBT maps.
	 * 
	 * @author AmoebaMan
	 */
	public static class Attribute{
		
		/** The UUID attached to this attribute, which may or may not be
		 * defined by Minecraft */
		public UUID uuid;
		
		/** The type of this attribute, defining what it actually effects */
		public AttributeType type;
		
		/** The operation this attribute uses applies its value */
		public Operation op;
		
		/** The name of this attribute, basically without function */
		public String name;
		
		/** The value of this attribute, self-explantory */
		public double value;
		
		/**
		 * Constructs a blank attribute with no effects and a random UUID.
		 */
		public Attribute(){
			uuid = UUID.randomUUID();
			type = AttributeType.DUMMY;
			op = Operation.ADD_NUMBER;
			name = type.identifier;
			value = 0.0;
		}
		
		/**
		 * Constructs an attribute and copies all components from a pre-
		 * existing NBT compound.
		 * @param nbt an NBT compound
		 */
		public Attribute(NbtCompound nbt){
			this();
			if(nbt == null)
				return;
			uuid = new UUID(nbt.getLong("UUIDMost", 0L), nbt.getLong("UUIDLeast", 0L));
			type = AttributeType.fromId(nbt.getString("AttributeName", "dummy"));
			op = Operation.values()[nbt.getInteger("Operation", 0)];
			name = nbt.getString("Name", "dummy");
			value = nbt.getDouble("Amount", 0.0);
		}
		
		/**
		 * Writes this attribute to an NBT compound, ready to be assigned
		 * to an item's NBT structure
		 * @return an NBT compound containing all of this attribute's data
		 */
		public NbtCompound getNbt(){
			if(uuid == null || type == null || op == null || name == null)
				throw new IllegalStateException("attribute components cannot be null");
			NbtCompound nbt = NbtFactory.createCompound();
			nbt.put("UUIDMost", uuid.getMostSignificantBits());
			nbt.put("UUIDLeast", uuid.getLeastSignificantBits());
			nbt.put("AttributeName", type.identifier);
			nbt.put("Operation", op.ordinal());
			nbt.put("Name", name);
			nbt.put("Amount", value);
			return nbt;
		}
		
	}
	
	private ItemStack stack;
	private NbtList attributes;
	
	/**
	 * Constructs an Attributes containing all the attribute data found in the
	 * NBT structure of the given {@link ItemStack}.
	 * 
	 * @param stack an item
	 */
	public Attributes(ItemStack stack){
		this.stack = NbtFactory.getCraftItemStack(stack);
		NbtCompound nbt = NbtFactory.fromItemTag(this.stack);
		attributes = nbt.getList("AttributeModifiers", true);
	}
	
	private void updateNbt(){
		NbtCompound nbt = NbtFactory.fromItemTag(stack);
		nbt.put("AttributeModifiers", attributes);
		NbtFactory.setItemTag(stack, nbt);
	}

	/**
	 * Retrieves the {@link ItemStack} whose attributes are being altered by
	 * this instance.
	 * @return the item
	 */
	public ItemStack getStack(){
		updateNbt();
		return stack;
	}
	
	/**
	 * Gets the list of {@link Attribute}{@code s} stored by this instance and
	 * in turn attached to the internal {@link ItemStack}.
	 * @return the list of attributes
	 */
	public List<Attribute> getAttributes(){
		List<Attribute> attrbs = new ArrayList<Attribute>();
		for(Object each : attributes)
			attrbs.add(new Attribute((NbtCompound) each));
		return attrbs;
	}
	
	/**
	 * Adds an attribute to the internal item.
	 * @param attrb an attribute
	 */
	public void add(Attribute attrb){
		attributes.add(attrb.getNbt());
		updateNbt();
	}
	
	/**
	 * Removes an attribute from the internal item.  Equality is checked by
	 * attribute UUID.
	 * @param attrb an attribute
	 */
	public void remove(Attribute attrb){
		for(Iterator<Attribute> it = getAttributes().iterator(); it.hasNext();)
			if(it.next().uuid.equals(attrb.uuid))
				it.remove();
		updateNbt();
	}
	
	/**
	 * Updates the value of an attribute.  Equality is checked by attribute UUID.
	 * @param attrb an attribute
	 */
	public void update(Attribute attrb){
		remove(attrb);
		add(attrb);
		//Updating NBT would be redundant, since both remove and add already did it
	}
	
	/**
	 * Clears all attributes from the internal item.
	 */
	public void clear(){
		attributes.clear();
		updateNbt();
	}
	
	/**
	 * Gets an attribute by its UUID.
	 * @param id a UUID
	 * @return the attribute with a matching UUID, or null if none was found
	 */
	public Attribute getAttribute(UUID id) {
		for(Attribute attrb : getAttributes())
			if(attrb.uuid.equals(id))
				return attrb;
		return null;
	}
	
}
