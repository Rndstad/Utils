package net.amoebaman.utils.chat;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;


/**
 * Defines color schemes that can be used in conjuction with the formatting methods.  Ideally
 * these can be changed at will, and have all server messages change correspondingly.
 * 
 * @author AmoebaMan
 */
public class Scheme{

	/*
	 * Gets the configuration file
	 */
	private static File configFile = new File("plugins/ColorSchemes/config.yml");
	static{
		configFile.getParentFile().mkdirs();
		if(!configFile.exists())
			try{ configFile.createNewFile(); } catch(IOException ioe){ ioe.printStackTrace(); }
	}
	
	/*
	 * Loads up the configuration and adds defaults
	 */
	private static YamlConfiguration config = configFile != null ? YamlConfiguration.loadConfiguration(configFile) : null;
	static{
		addDefaults();
		loadSchemes();
	}
	
	/*
	 * Do some sketchy stuff to effect a chat reload command
	 */
	static{
		Bukkit.getPluginManager().registerEvents(new SketchyListener(), Bukkit.getPluginManager().getPlugins()[0]);
	}
	
	private static class SketchyListener implements Listener{
		
		@EventHandler
		public void commandPreProcess(PlayerCommandPreprocessEvent event){
			if(event.getMessage().contains("colorscheme -reload") && event.getPlayer().isOp()){
				loadSchemes();
				new Message(NORMAL).then("Chat color schemes have been reloaded").send(event.getPlayer());
				event.setCancelled(true);
			}
		}
		
	}

	private static void loadSchemes(){
		
		config = configFile != null ? YamlConfiguration.loadConfiguration(configFile) : null;
		
		NORMAL = new Scheme(
			config != null ? new Format(config.getString("normal.normal")) : new Format(ChatColor.GRAY),
			config != null ? new Format(config.getString("normal.strong")) : new Format(ChatColor.DARK_GRAY),
			config != null ? config.getString("normal.prefix") : "",
			config != null ? config.getString("normal.suffix") : "");
		HIGHLIGHT = new Scheme(
			config != null ? new Format(config.getString("highlight.normal")) : new Format(ChatColor.GOLD),
			config != null ? new Format(config.getString("highlight.strong")) : new Format(ChatColor.DARK_RED),
			config != null ? config.getString("highlight.prefix") : "",
			config != null ? config.getString("highlight.suffix") : "");
		WARNING = new Scheme(
			config != null ? new Format(config.getString("warning.normal")) : new Format(ChatColor.RED),
			config != null ? new Format(config.getString("warning.strong")) : new Format(ChatColor.DARK_GRAY),
			config != null ? config.getString("warning.prefix") : "",
			config != null ? config.getString("warning.suffix") : "");
		ERROR = new Scheme(
			config != null ? new Format(config.getString("error.normal")) : new Format(ChatColor.RED),
			config != null ? new Format(config.getString("error.strong")) : new Format(ChatColor.DARK_GRAY),
			config != null ? config.getString("error.prefix") : "",
			config != null ? config.getString("error.suffix") : "");
		
	}
	
	private static void addDefaults(){
		
		config.options().header(
			"This is the chat color scheme configuration file.  All plugins which use the unifying appearence\n" +
			"chat library ought to obey these values in some form.  Some may add their own schemes, which will\n" +
			"appear here as well."
		);
		config.addDefault("normal.normal", "&7"); config.addDefault("normal.strong", "&8"); config.addDefault("normal.prefix", ""); config.addDefault("normal.suffix", "");
		config.addDefault("highlight.normal", "&6"); config.addDefault("highlight.strong", "&4"); config.addDefault("highlight.prefix", ""); config.addDefault("highlight.suffix", "");
		config.addDefault("warning.normal", "&c"); config.addDefault("warning.strong", "&8"); config.addDefault("warning.prefix", ""); config.addDefault("warning.suffix", "");
		config.addDefault("error.normal", "&c"); config.addDefault("error.strong", "&8"); config.addDefault("error.prefix", "&8[&cERROR&8]&r "); config.addDefault("error.suffix", "");
		
		config.options().copyDefaults(true);
		config.options().copyHeader(true);
		try{
			config.save(configFile);
			config.load(configFile);
		}
		catch(Exception e){ e.printStackTrace(); }
		
	}
	
	/** Normal chat colors, designed to sort of blend in with other messages */
	public static Scheme NORMAL;
	
	/** Exaggerated chat colors, designed the pop and stand out from other messages */
	public static Scheme HIGHLIGHT;
	
	/** Warning chat colors, designed to tell players they're doing something wrong */
	public static Scheme WARNING;
	
	/** Error chat colors, designed to hint that something has gone horribly wrong */
	public static Scheme ERROR;
	
	/** That standard text format for this scheme */
	public Format normal;
	
	/** An additional text format for this scheme */
	public Format alternate;
	
	/** An special stand-out text format for this scheme */
	public Format strong;
	
	/** The text that will be prepended to the beginning of messages */
	public String prefix;
	
	/** The text that will be prepended to the ending of messages */
	public String suffix;
	
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
		this.prefix = prefix == null ? "" : Chat.format(prefix);
		this.suffix = suffix == null ? "" : Chat.format(suffix);
	}
	
	/**
	 * Construct a ColorScheme for later use (this will NOT be stored automatically).
	 * @param normal the normal and alternate chat color and styles
	 * @param strong the emphasis chat color and styles
	 * @param prefix the prefix for messages in this scheme
	 * @param suffix the suffix for messages in this scheme
	 */
	public Scheme(Format normal, Format strong, String prefix, String suffix){
		this(normal, normal, strong, prefix, suffix);
	}
	
}
