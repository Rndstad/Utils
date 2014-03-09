package net.amoebaman.utils.chat;

import java.util.HashMap;
import java.util.List;

import net.amoebaman.utils.chat.Message.MessagePart;
import net.minecraft.util.com.google.common.collect.Lists;

import org.bukkit.ChatColor;

/**
 * Contains static values and operations designed to assist with aligning text
 * 
 * @author AmoebaMan
 */
public class Align{
	
	/** The default Minecraft chat box width, in font-pixels */
	private static final int SCREEN_WIDTH = 316;
	
	/** Most characters in Minecraft's default font are this many font-pixels wide */
	private static final int DEFAULT_CHAR_WIDTH = 6;
	
	/** A map of the width of all irregular characters in Minecraft's default font */
	private static final HashMap<Character, Integer> IRREG_CHAR_WIDTH = new HashMap<Character, Integer>();
	static{
		IRREG_CHAR_WIDTH.put(' ', 4);
		IRREG_CHAR_WIDTH.put('i', 2);
		IRREG_CHAR_WIDTH.put('I', 4);
		IRREG_CHAR_WIDTH.put('k', 5);
		IRREG_CHAR_WIDTH.put('l', 3);
		IRREG_CHAR_WIDTH.put('t', 4);
		IRREG_CHAR_WIDTH.put('!', 2);
		IRREG_CHAR_WIDTH.put('(', 5);
		IRREG_CHAR_WIDTH.put(')', 5);
		IRREG_CHAR_WIDTH.put('~', 7);
		IRREG_CHAR_WIDTH.put(',', 2);
		IRREG_CHAR_WIDTH.put('.', 2);
		IRREG_CHAR_WIDTH.put('<', 5);
		IRREG_CHAR_WIDTH.put('>', 5);
		IRREG_CHAR_WIDTH.put(':', 2);
		IRREG_CHAR_WIDTH.put(';', 2);
		IRREG_CHAR_WIDTH.put('"', 5);
		IRREG_CHAR_WIDTH.put('[', 4);
		IRREG_CHAR_WIDTH.put(']', 4);
		IRREG_CHAR_WIDTH.put('{', 5);
		IRREG_CHAR_WIDTH.put('}', 5);
		IRREG_CHAR_WIDTH.put('|', 2);
		IRREG_CHAR_WIDTH.put('`', 0);
		IRREG_CHAR_WIDTH.put('\'', 2);
		IRREG_CHAR_WIDTH.put('\u2591', 8);
		IRREG_CHAR_WIDTH.put('\u2592', 9);
		IRREG_CHAR_WIDTH.put('\u2593', 9);
		IRREG_CHAR_WIDTH.put('\u2588', 9);
		IRREG_CHAR_WIDTH.put(ChatColor.COLOR_CHAR, 0);
	}
	
	/**
	 * Gets the width of a character in Minecraft's default font, in font-pixels.
	 * @param value a character
	 * @param bold whether this character is in bold style (+1 px)
	 * @return the width of the character
	 */
	private static int getCharWidth(char value, boolean bold){
		if(IRREG_CHAR_WIDTH.containsKey(value))
			return IRREG_CHAR_WIDTH.get(value) + (bold ? 1 : 0);
		return DEFAULT_CHAR_WIDTH + (bold ? 1 : 0);
	}
	
	/**
	 * Gets the total width of some text in font-pixels, the sum of its characters.
	 * @param str some text
	 * @return the width of the text
	 */
	private static int getStringWidth(String str){
		int length = 0;
		boolean bold = false;
		for(int i = 0; i < str.length(); i++)
			if(str.charAt(i) != ChatColor.COLOR_CHAR)
				if(i == 0)
					length += getCharWidth(str.charAt(i), bold);
				else
					if(str.charAt(i - 1) != ChatColor.COLOR_CHAR)
						length += getCharWidth(str.charAt(i), bold);
					else
						if(str.charAt(i) == 'l')
							bold = true;
						else if(!Lists.newArrayList('m', 'n', 'o').contains(str.charAt(i)))
							bold = false;
		return length;
	}
	
	/**
	 * Gets a line that when broadcast in chat will result in an empty line, since empty messages don't display at all.
	 * @return a spacer line
	 */
	public static String spacerLine(){
		return ChatColor.RESET.toString();
	}
	
	/**
	 * Gets a line of text, repeating the given pattern, that will fill one line of the chat box as completely as
	 * possible without spilling to the next line.
	 * @param pattern a pattern
	 * @return a filler line of the pattern repeated
	 */
	public static String fillerLine(String pattern){
		if(pattern.isEmpty())
			return "";
		float length = getStringWidth(pattern);
		int iterations = (int) (SCREEN_WIDTH / length);
		String line = "";
		for(int i = 0; i < iterations; i++)
			line += pattern;
		return center(line, "");
	}
	
	/**
	 * Adds spaces the the beginning of a line of text to make it display as closely as possible to the center of chat box.
	 * @param text some text
	 * @param border a small pattern to border the edges of the chat box
	 * @return the formatted text for center-align
	 */
	public static String center(String text, String border){
		text = text.trim();
		int width = getStringWidth(text) + (getStringWidth(border) * 2);
		int numSpaces = (SCREEN_WIDTH - width) / getCharWidth(' ', false);
		for(int i = 0; i < numSpaces / 2; i++)
			text = " " + text;
		for(int i = numSpaces / 2; i < numSpaces; i++)
			text = text + " ";
		return border + text + border;
	}
	
	public static Message center(Message message, String border){
		if(border == null)
			border = "";
		int width = getStringWidth(message.getText()) + (getStringWidth(border) * 2);
		int numSpaces = (SCREEN_WIDTH - width) / getCharWidth(' ', false);
		String spaces = "";
		for(int i = 0; i < numSpaces; i++)
			spaces += " ";
		message.messageParts.add(0, new MessagePart(border + spaces));
		message.messageParts.add(new MessagePart(spaces + border));
		return message;
	}
	
	public static List<String> box(List<String> text, String border){
		text = Lists.newArrayList(text);
		for(int i = 0; i < text.size(); i++)
			text.set(i, center(text.get(i), border));
		text.add(0, center("", border));
		if(border != null && !border.isEmpty())
			text.add(0, fillerLine(border));
		text.add(center("", border));
		if(border != null && !border.isEmpty())
			text.add(fillerLine(border));
		return text;
	}
	
}
