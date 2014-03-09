package net.amoebaman.utils.chat;

import java.util.List;

import net.amoebaman.utils.nms.ReflectionUtil;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * This class contains utilities for working with Minecraft's chat system, to make pretty and understandable
 * text easier to code.  The {@link Scheme} enum is specifically designed so that multiple plugins can
 * use the same color scheme, which in theory can later be changed at will via a configuration.
 * <br><br>
 * All of the text-alignment functions are based off of Minecraft's default chat font widths.  They will most
 * likely be slightly off if you use a texture pack that changes the font.
 * 
 * @author AmoebaMan
 */
public class Chat {
	
	public static void send(CommandSender player, Iterable<Object> messages){
		for(Object message : messages)
			send(player, message);
	}
	
	private static Class<?> nmsPacketPlayOutChat = ReflectionUtil.getNMSClass("PacketPlayOutChat");
	private static Class<?> nmsChatSerializer = ReflectionUtil.getNMSClass("ChatSerializer");
	public static void send(CommandSender player, Object... messages){
		for(Object message : messages)
			if(message instanceof Iterable)
				for(Object each : (Iterable) message)
					send(player, each);
			else if(message instanceof Object[])
				for(Object each : (Iterable) message)
					send(player, each);
			else if(message instanceof JsonMessage)
				if(player instanceof Player){
					try {
						Object connection = ReflectionUtil.getField(ReflectionUtil.getHandle(player).getClass(), "playerConnection").get(ReflectionUtil.getHandle(player));
						Object packet = nmsPacketPlayOutChat.getConstructor(ReflectionUtil.getNMSClass("IChatBaseComponent")).newInstance(
								ReflectionUtil.getMethod(nmsChatSerializer, "a", String.class).invoke(null, ChatColor.translateAlternateColorCodes('&', message.toString())));
						ReflectionUtil.getMethod(connection.getClass(), "sendPacket").invoke(connection, packet);
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
				else
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', ((JsonMessage) message).getText()));
			else
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', "" + message));
	}
	
	public static void broadcast(Object... messages){
		for(Player player : Bukkit.getOnlinePlayers())
			send(player, messages);
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
	
}
