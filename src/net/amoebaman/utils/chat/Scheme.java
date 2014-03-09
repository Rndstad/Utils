package net.amoebaman.utils.chat;

import org.bukkit.ChatColor;


/**
 * Defines color schemes that can be used in conjuction with the formatting methods.  Ideally
 * these can be changed at will, and have all server messages change correspondingly.
 * 
 * @author AmoebaMan
 */
public class Scheme{
	
	/** Normal chat colors, designed to sort of blend in with other messages */
	public static final Scheme NORMAL = new Scheme(new Format(ChatColor.GRAY), new Format(ChatColor.DARK_GRAY), "", "");
	
	/** Exaggerated chat colors, designed the pop and stand out from other messages */
	public static final Scheme HIGHLIGHT = new Scheme(new Format(ChatColor.GOLD), new Format(ChatColor.DARK_RED), "", "");
	
	/** Warning chat colors, designed to tell players they're doing something wrong */
	public static final Scheme WARNING = new Scheme(new Format(ChatColor.RED), new Format(ChatColor.DARK_GRAY), "", "");
	
	/** Error chat colors, designed to hint that something has gone horribly wrong */
	public static final Scheme ERROR = new Scheme(new Format(ChatColor.RED), new Format(ChatColor.DARK_GRAY), ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + "ERROR" + ChatColor.DARK_GRAY + "] ", "");
	
	/** That standard text format for this scheme */
	public final Format normal;
	
	/** An additional text format for this scheme */
	public final Format alternate;
	
	/** An special stand-out text format for this scheme */
	public final Format strong;
	
	/** The text that will be prepended to the beginning of messages */
	public final String prefix;
	
	/** The text that will be prepended to the ending of messages */
	public final String suffix;
	
	protected Scheme(){ normal = null; alternate = null; strong = null; prefix = null; suffix = null; }
	
	/**
	 * Construct a ColorScheme for later use (this will NOT be stored automatically).
	 * @param normal the normal chat color and styles
	 * @param alternate an alternate chat color and styles
	 * @param strong the emphasis chat color and styles
	 * @param prefix the prefix for messages in this scheme
	 * @param suffix the suffix for messages in this scheme
	 */
	public Scheme(Format normal, Format alternate, Format strong, String prefix, String suffix){
		this.normal = normal == null ? new Format() : normal;
		this.alternate = alternate == null ? new Format() : alternate;
		this.strong = strong == null ? new Format() : strong;
		this.prefix = prefix == null ? "" : prefix;
		this.suffix = suffix == null ? "" : suffix;
	}
	
	/**
	 * Construct a ColorScheme for later use (this will NOT be stored automatically).
	 * @param normal the normal and alternate chat color and styles
	 * @param strong the emphasis chat color and styles
	 * @param prefix the prefix for messages in this scheme
	 * @param suffix the suffix for messages in this scheme
	 */
	public Scheme(Format normal, Format strong, String prefix, String suffix){
		this.normal = normal == null ? new Format() : normal;
		this.alternate = this.normal;
		this.strong = strong == null ? new Format() : strong;
		this.prefix = prefix == null ? "" : prefix;
		this.suffix = suffix == null ? "" : suffix;
	}
	
}
