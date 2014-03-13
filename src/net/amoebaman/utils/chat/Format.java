package net.amoebaman.utils.chat;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;

import net.minecraft.util.com.google.common.collect.Lists;

/**
 * Consolidated class for storing a compound chat color/style format consisting of a
 * single color (can't display multiple colors at once) and multiple styles.
 * 
 * @author AmoebaMan
 */
public class Format{
	
	/** The color this format uses */
	private ChatColor color;
	
	/** The styles this format uses */
	private Set<ChatColor> styles;
	
	private String string;
	
	/**
	 * Constructs a format from component color codes
	 * @param codes some color codes
	 */
	public Format(ChatColor... codes){
		styles = new HashSet<ChatColor>();
		for(ChatColor code : codes)
			if(code != null && code.isColor())
				color = code;
			else
				styles.add(code);
		
		string = "";
		if(color != null)
			string += color;
		for(ChatColor style : styles)
			string += style;
	}
	
	public Format(String sequence){
		this(Lists.newArrayList(ChatColor.getLastColors(ChatColor.translateAlternateColorCodes('&', sequence)).split(String.valueOf(ChatColor.COLOR_CHAR))).toArray(new ChatColor[0]));
	}
	
	public ChatColor color(){ return color; }
	public ChatColor[] styles(){ return styles.toArray(new ChatColor[0]); }
	
	public String toString(){ return string; }
	
}