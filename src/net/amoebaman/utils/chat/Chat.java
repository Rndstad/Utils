package net.amoebaman.utils.chat;

import java.util.ArrayList;
import java.util.List;

import net.amoebaman.utils.nms.ReflectionUtil;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * This class contains utilities for working with Minecraft's chat system, to make pretty and understandable
 * text easier to code.
 * <br><br>
 * All of the text-alignment functions are based off of Minecraft's default chat font widths.  They will most
 * likely be slightly off if you use a texture pack that changes the font.
 * 
 * @author AmoebaMan
 */
public class Chat {
	
	private static Class<?> nmsPacketPlayOutChat = ReflectionUtil.getNMSClass("PacketPlayOutChat");
	private static Class<?> nmsChatSerializer = ReflectionUtil.getNMSClass("ChatSerializer");
	
	/**
	 * Sends a message, or many messages, to the player (or console).  Various types of objects will be automatically
	 * used in the most appropriate way possible, in order to make this method as dynamic and universally applicable
	 * as possible.
	 * <br><br>
	 * {@link Iterable}{@code s} and arrays will be automatically iterated over, calling this same method for each element
	 * within them.  This check is recursive - if the contents of the iterable or array are more iterables or arrays, they
	 * will be iterated over as well.
	 * <br><br>
	 * {@link JsonMessage}{@code s} will be parsed out to their JSON plaintext and be sent to the player using the proper
	 * reflection packet sending to ensure the message is fully displayed.
	 * <br><br>
	 * Any other {@link Object}{@code s} will be displayed to the player using {@link String#valueOf(Object)}.  For {@link
	 * String}{@code s} and {@link Message}{@code s}, this automatically restores them to String form.
	 * 
	 * @param player the recipient
	 * @param messages some messages
	 */
	public static void send(CommandSender player, Object... messages){
		for(Object message : expand(messages))
			if(message instanceof JsonMessage)
				if(player instanceof Player){
					try {
						Object connection = ReflectionUtil.getField(ReflectionUtil.getHandle(player).getClass(), "playerConnection").get(ReflectionUtil.getHandle(player));
						Object packet = nmsPacketPlayOutChat.getConstructor(ReflectionUtil.getNMSClass("IChatBaseComponent")).newInstance(ReflectionUtil.getMethod(nmsChatSerializer, "a", String.class).invoke(null, ChatColor.translateAlternateColorCodes('&', message.toString())));
						ReflectionUtil.getMethod(connection.getClass(), "sendPacket").invoke(connection, packet);
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
				else
					player.sendMessage(format(((JsonMessage) message).getText()));
			else
				player.sendMessage(format(String.valueOf(message)));
	}
	
	/**
	 * Sends a message, or many messages to all players on the server and the console.  See {@link Chat#send(CommandSender, Object...)}
	 * @param messages some messages
	 */
	public static void broadcast(Object... messages){
		for(Player player : Bukkit.getOnlinePlayers())
			send(player, messages);
		send(Bukkit.getConsoleSender(), messages);
	}
	
	/**
	 * Makes a progress bar, capped by square brackets and filled with colored pipes.  If the number of
	 * listed colors exceeds the number of listed values, and the listed values do not add up to the total
	 * value, the first unmatched color will be used to complete the bar.
	 * @param length the number of colored pipes that will be used in the bar
	 * @param total the total value of all values
	 * @param colors a list of colors to be used
	 * @param values a list of values corresponding with colors
	 * @return the progress bar, ready for display
	 */
	public static String makeProgressBar(int length, int total, List<ChatColor> colors, List<Integer> values){
		if(colors.size() < values.size())
			return ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + "ERROR" + ChatColor.DARK_GRAY + "]";
		String bar = ChatColor.DARK_GRAY + "[";
		int pipes = 0;
		for(int i = 0; i < values.size(); i++){
			bar += colors.get(i);
			for(int j = 0; j < (1f * values.get(i) / total) * length; j++){
				bar += "|";
				pipes++;
			}
		}
		if(pipes < length && colors.size() > values.size()){
			bar += colors.get(values.size());
			for(int i = pipes; i < length; i++)
				bar += "|";
		}
		bar += ChatColor.DARK_GRAY + "]";
		return bar;
	}
	
	/**
	 * Gets the closest possible interpretation of a dye color as a chat color, for Minecraft defaults.
	 * @param color a dye color
	 * @return the corresponding chat color
	 */
	public static ChatColor dyeToChatColor(DyeColor color){
		if(color == null) return ChatColor.RESET;
		switch(color){
		case BLACK: return ChatColor.BLACK;
		case BLUE: return ChatColor.BLUE;
		case BROWN: return ChatColor.BOLD;
		case CYAN: return ChatColor.DARK_AQUA;
		case GRAY: return ChatColor.DARK_GRAY;
		case GREEN: return ChatColor.DARK_GREEN;
		case LIGHT_BLUE: return ChatColor.BLUE;
		case LIME: return ChatColor.GREEN;
		case MAGENTA: return ChatColor.LIGHT_PURPLE;
		case ORANGE: return ChatColor.GOLD;
		case PINK: return ChatColor.RED;
		case PURPLE: return ChatColor.DARK_PURPLE;
		case RED: return ChatColor.RED;
		case SILVER: return ChatColor.GRAY;
		case WHITE: return ChatColor.WHITE;
		case YELLOW: return ChatColor.YELLOW;
		default: return ChatColor.RESET;
		}
	}
	
	/**
	 * Abbreviated method for translating alternate color codes, with the typical standard
	 * of the ampersand (&) filling in for the section character.
	 * 
	 * @param str a string
	 * @return the color formatted string
	 */
	public static String format(Object obj){
		return ChatColor.translateAlternateColorCodes('&', String.valueOf(obj));
	}
	
	/**
	 * Quickie method for formatting a string according to a color scheme, using Minecraft's
	 * built-in color code formation.  "&x" is replaced by the scheme's normal format, "&y" is
	 * replaced by the scheme's alternate format, and "&z" is replaced by the scheme's strong
	 * format.
	 * 
	 * @param str a string
	 * @param scheme a color scheme
	 * @return the formatted string
	 */
	public static String format(Object obj, Scheme scheme){
		return scheme.prefix + format(obj).replace("&x", scheme.normal.toString()).replace("&y", scheme.alternate.toString()).replace("&z", scheme.strong.toString()) + scheme.suffix;
	}
	
	public static List<Object> expand(Object... objects){
		List<Object> list = new ArrayList<Object>();
		for(Object object : objects)
			if(object instanceof Iterable)
				for(Object each : (Iterable<Object>) object)
					list.add(each);
			else if(object instanceof Object[])
				for(Object each : (Object[]) object)
					list.add(each);
			else
				list.add(object);
		return list;
	}
	
}