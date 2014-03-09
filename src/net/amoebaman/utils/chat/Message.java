package net.amoebaman.utils.chat;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;


/**
 * This class provides a chainable method for 
 * 
 * @author AmoebaMan
 */
public class Message {
	
	protected List<MessagePart> messageParts = new ArrayList<MessagePart>();
	protected Scheme scheme;
	protected String text = "";
	protected boolean written = false;
	
	/**
	 * Begins a JsonMessage.
	 * @param first a color scheme
	 */
	public Message(Scheme scheme) {
		this.scheme = scheme;
	}
	
	public void send(Player player){
		Chat.send(player, this);
	}
	
	public void broadcast(){
		Chat.broadcast(this);
	}
	
	/**
	 * Adds the next bit of text to this message.  If a ColorScheme is in effect, it will
	 * automatically be formatted to the normal format of the ColorScheme.
	 * @param text some text
	 * @return the message (for chaining)
	 */
	public Message then(Object text) {
		messageParts.add(new MessagePart(text.toString()));
		if(scheme != null)
			format(scheme.normal);
		return this;
	}
	
	/**
	 * Sets the formatting for the latest bit to the alternate format of the scheme.
	 * @return the message (for chaining)
	 */
	public Message alt(){
		if(scheme != null)
			format(scheme.alternate);
		return this;
	}
	
	/**
	 * Sets the formatting for the latest bit to the strong format of the scheme.
	 * @return the message (for chaining)
	 */
	public Message strong(){
		if(scheme != null)
			format(scheme.strong);
		return this;
	}
	
	/**
	 * Sets the color for the latest bit of text.  Using null will remove the color
	 * and use the last set color and formatting.
	 * @param color a color
	 * @return the message (for chaining)
	 */
	public Message color(ChatColor color) {
		if (color != null && !color.isColor())
			return this;
		latest().color = color;
		written = false;
		return this;
	}
	
	/**
	 * Sets the styles for the latest bit of text.  If any of the
	 * formatting sytles submitted are actually colors, this method
	 * will fail silently.
	 * @param styles some styles
	 * @return the message (for chaining)
	 */
	public Message style(ChatColor... styles) {
		for (ChatColor style : styles)
			if (style == null || !style.isFormat())
				return this;
		latest().styles = styles;
		written = false;
		return this;
	}
	
	/**
	 * Sets all of the formatting for the latest bit of text.
	 * @param format a format
	 * @return the message (for chaining)
	 */
	public Message format(Format format){
		if(format != null){
			color(format.color());
			style(format.styles());
		}
		return this;
	}
	
	/**
	 * Gets the full text of this message.
	 * @return the message in full text
	 */
	public String getText(){
		if(written && text != null)
			return text;
		
		String str = "";
		if(scheme != null)
			str += scheme.prefix;
		for(MessagePart part : messageParts){
			if(part.color != null)
				str += part.color;
			if(part.styles != null)
				for(ChatColor style : part.styles)
					str += style;
			str += part.text;
		}
		if(scheme != null)
			str += scheme.suffix;
		
		text = str;
		if(scheme != null)
			text = scheme.prefix + text + scheme.suffix;
		written = true;
		return text;
	}
	
	public String toString(){ return getText(); }
	
	private MessagePart latest() {
		return messageParts.get(messageParts.size() - 1);
	}
	
	/**
	 * A class that represents a piece of a {@link Message}, including
	 * both its text and its format
	 * 
	 * @author AmoebaMan
	 */
	public static class MessagePart {
		protected ChatColor color = ChatColor.RESET;
		protected ChatColor[] styles = new ChatColor[0];
		protected String text;
		protected MessagePart(String text) { this.text = text; }
	}
	
}