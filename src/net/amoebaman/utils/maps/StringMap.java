package net.amoebaman.utils.maps;

import java.util.Map;
import java.util.Map.Entry;

/**
 * A class that provides nearly the exact same functionality as a HashMap<String, V>.  The only difference is
 * that all String keys are checked without regard to case.
 * <br><br>
 * You can also specify a default value when the map is initialized that will
 * be returned instead of null if the key you're looking for doesn't exist within the map.
 * 
 * @author AmoebaMan
 *
 * @param <V> whatever you want to store
 */
public class StringMap<V> extends DefaultedMap<String, V> {

    private static final long serialVersionUID = 6529987734197001528L;
    
    public StringMap(V value){ super(value); }

	public boolean containsKey(Object key) {
		if(!(key instanceof String))
			return false;
		for(String each : keySet())
			if(each.equalsIgnoreCase((String) key))
				return true;
		return false;
	}

	public V get(Object key) {
		if(!(key instanceof String))
			return getDefaultValue();
		V value = null;
		for(String each : keySet())
			if(each.equalsIgnoreCase((String) key))
				value = super.get(each);
		return value == null ? getDefaultValue() : value;
	}

	public V remove(Object key) {
		if(!(key instanceof String))
			return getDefaultValue();
		V value = get(key);
		for(String each : keySet())
			if(each.equalsIgnoreCase((String) key))
				value = super.remove(each);
		return value;
	}

    public V put(String key, V value) {
		V old = remove(key);
		super.put(key, value);
	    return old;
    }

    public void putAll(Map<? extends String, ? extends V> m) {
    	for(Entry<? extends String, ? extends V> entry : m.entrySet())
    		put(entry.getKey(), entry.getValue());
	}
	
}
