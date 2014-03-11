
package net.amoebaman.utils.nms;

import java.util.Iterator;
import java.util.UUID;

import org.bukkit.inventory.ItemStack;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.collect.Iterators;

import net.amoebaman.utils.nms.NbtFactory.NbtCompound;
import net.amoebaman.utils.nms.NbtFactory.NbtList;

public class Attributes{
	
	public enum Operation{
		ADD_NUMBER,
		MULTIPLY_PERCENTAGE,
		ADD_PERCENTAGE,
		;
	}
	
	public static enum AttributeType{
		
		GENERIC_MAX_HEALTH("generic.maxHealth"),
		GENERIC_FOLLOW_RANGE("generic.followRange"),
		GENERIC_ATTACK_DAMAGE("generic.attackDamage"),
		GENERIC_MOVEMENT_SPEED("generic.movementSpeed"),
		GENERIC_KNOCKBACK_RESISTANCE("generic.knockbackResistance"),
		DUMMY("dummy"),
		;
		
		public String identifier;
		
		private AttributeType(String identifier){ this.identifier = identifier; }
		
		public static AttributeType fromId(String minecraftId){
			for(AttributeType type : values())
				if(type.identifier.equals(minecraftId))
					return type;
			return null;
		}
		
	}
	
	public static class Attribute{
		
		public UUID uuid;
		public AttributeType type;
		public Operation op;
		public String name;
		public double value;
		
		public Attribute(){
			uuid = UUID.randomUUID();
			type = AttributeType.DUMMY;
			op = Operation.ADD_NUMBER;
			name = type.identifier;
			value = 0.0;
		}
		
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
	public NbtList attributes;
	
	public Attributes(ItemStack stack){
		// Create a CraftItemStack (under the hood)
		this.stack = NbtFactory.getCraftItemStack(stack);
		
		// Load NBT
		NbtCompound nbt = NbtFactory.fromItemTag(this.stack);
		this.attributes = nbt.getList("AttributeModifiers", true);
	}
	
	private void updateNbt(){
		NbtCompound nbt = NbtFactory.fromItemTag(stack);
		nbt.put("AttributeModifiers", attributes);
		NbtFactory.setItemTag(stack, nbt);
	}

	public ItemStack getStack(){
		return stack;
	}

	public int size(){
		return attributes.size();
	}
	
	public void add(Attribute attrb){
		attributes.add(attrb.getNbt());
		updateNbt();
	}
	
	public void remove(Attribute attrb){
		for(Iterator<Attribute> it = values().iterator(); it.hasNext();)
			if(Objects.equal(it.next().uuid, attrb.uuid))
				it.remove();
		updateNbt();
	}
	
	public void clear(){
		attributes.clear();
		updateNbt();
	}
	
	public Iterable<Attribute> values(){
		return new Iterable<Attribute>(){
			public Iterator<Attribute> iterator(){
				return Iterators.transform(attributes.iterator(), new Function<Object, Attribute>(){
					public Attribute apply(Object element){
						return new Attribute((NbtCompound) element);
					}
				});
			}
		};
	}
}
