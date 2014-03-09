package net.amoebaman.utils.chat;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;

import com.google.common.collect.Sets;

/**
 * Consolidated class for storing a compound chat color/style format consisting of a
 * single color (can't display multiple colors at once) and multiple styles.  The elements
 * for the format cannot be changed once it is created.
 * 
 * @author AmoebaMan
 */
public class Format{
	
	private ChatColor color;
	private Set<ChatColor> styles;
	private String format;
	public Format(ChatColor... codes){
		styles = new HashSet<ChatColor>();
		for(ChatColor code : codes)
			if(code.isColor())
				color = code;
			else
				styles.add(code);
		format = "";
		if(color != null)
			format += color;
		for(ChatColor style : styles)
			format += style;
	}
	
	public ChatColor getColor(){ return color; }
	public Set<ChatColor> getStyles(){ return Sets.newHashSet(styles); }
	public String toString(){ return format; }
	
}