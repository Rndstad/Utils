package net.amoebaman.amoebautils.maps;

import java.util.HashMap;

/**
 * Extended {@link HashMap} with the ability to store a default value, to be
 * returned in lieu of a null when attempting to get a key that has not yet been
 * set.
 * <p>
 * <b>WARNING:</b> the provided default value <b>is not cloned</b> when returned
 * in absence of a defined value. Two different requests for absent keys will
 * return the same object as the default value.  Be very careful when using
 * mutable objects as default values.
 * 
 * @author AmoebaMan
 */
public class DefaultedMap<K, V> extends HashMap<K, V>{
	
	private static final long serialVersionUID = -4535849613745037964L;
	private V defaultValue;
	
	/**
	 * Constructs a blank map with no default value.
	 */
	public DefaultedMap(){
		defaultValue = null;
	}
	
	/**
	 * Constructs a blank map with the given default value.
	 * 
	 * @param defaultValue the default for this map
	 */
	public DefaultedMap(V defaultValue){
		this.defaultValue = defaultValue;
	}
	
	/**
	 * Gets this map's default value
	 * 
	 * @return the default
	 */
	public V getDefaultValue(){
		return defaultValue;
	}
	
	/**
	 * Sets this map's default value
	 * 
	 * @param defaultValue a value
	 */
	public void setDefaultValue(V defaultValue){
		this.defaultValue = defaultValue;
	}
	
	/**
	 * See {@link HashMap#get(Object)}. If this request would return null and
	 * the map has a default value set, it will instead the default value.
	 */
	public V get(Object key){
		V result = super.get(key);
		return (result == null) ? defaultValue : result;
	}
}
