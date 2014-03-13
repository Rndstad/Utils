package net.amoebaman.utils.chat;

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;


/**
 * Defines color schemes that can be used in conjuction with the formatting methods.  Ideally
 * these can be changed at will, and have all server messages change correspondingly.
 * 
 * @author AmoebaMan
 */
public class Scheme{

	private static final File configFile = new File("plugins/ColorSchemes/config.yml");
	static{
		configFile.getParentFile().mkdirs();
		if(!configFile.exists())
			try{ configFile.createNewFile(); } catch(IOException ioe){ ioe.printStackTrace(); }
	}
	private static final YamlConfiguration config = configFile != null ? YamlConfiguration.loadConfiguration(configFile) : null;
	static{
		config.options().header(
			"This is the chat color scheme configuration file.  All plugins which use the unifying appearence\n" +
			"chat library ought to obey these values in some form.  Some may add their own schemes, which will\n" +
			"appear here as well."
		);
		config.options().copyHeader(true);
		try{ config.save(configFile); } catch(IOException ioe){ ioe.printStackTrace(); }
	}
	
	/** Normal chat colors, designed to sort of blend in with other messages */
	public static final Scheme NORMAL = new Scheme(
		config != null ? new Format(config.getString("normal.normal", "&7")) : new Format(ChatColor.GRAY),
		config != null ? new Format(config.getString("normal.strong", "&8")) : new Format(ChatColor.DARK_GRAY),
		config != null ? config.getString("normal.prefix", "") : "",
		config != null ? config.getString("normal.suffix", "") : "");
	
	/** Exaggerated chat colors, designed the pop and stand out from other messages */
	public static final Scheme HIGHLIGHT = new Scheme(
		config != null ? new Format(config.getString("highlight.normal", "&6")) : new Format(ChatColor.GOLD),
		config != null ? new Format(config.getString("highlight.strong", "&4")) : new Format(ChatColor.DARK_RED),
		config != null ? config.getString("highlight.prefix", "") : "",
		config != null ? config.getString("highlight.suffix", "") : "");
	
	/** Warning chat colors, designed to tell players they're doing something wrong */
	public static final Scheme WARNING = new Scheme(
		config != null ? new Format(config.getString("warning.normal", "&c")) : new Format(ChatColor.RED),
		config != null ? new Format(config.getString("warning.strong", "&8")) : new Format(ChatColor.DARK_GRAY),
		config != null ? config.getString("warning.prefix", "") : "",
		config != null ? config.getString("warning.suffix", "") : "");
	
	/** Error chat colors, designed to hint that something has gone horribly wrong */
	public static final Scheme ERROR = new Scheme(
		config != null ? new Format(config.getString("warning.normal", "&c")) : new Format(ChatColor.RED),
		config != null ? new Format(config.getString("warning.strong", "&8")) : new Format(ChatColor.DARK_GRAY),
		config != null ? config.getString("warning.prefix", "&8[&cERROR&8]&r ") : "",
		config != null ? config.getString("warning.suffix", "") : "");
	
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
