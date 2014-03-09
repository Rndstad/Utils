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
	
	protected List<MessagePart> messageParts;
	protected Scheme scheme;
	protected String text;
	protected boolean dirty;
	
	/**
	 * Begins a JsonMessage.
	 * @param first a color scheme
	 */
	public Message(Scheme scheme) {
		messageParts = new ArrayList<MessagePart>();
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
	public Message alternate(){
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
		dirty = true;
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
		dirty = true;
		return this;
	}
	
	/**
	 * Sets all of the formatting for the latest bit of text.
	 * @param format a format
	 * @return the message (for chaining)
	 */
	public Message format(Format format){
		if(format != null){
			color(format.getColor());
			style(format.getStyles().toArray(new ChatColor[0]));
		}
		return this;
	}
	
	/**
	 * Gets the full text of this message.
	 * @return the message in full text
	 */
	public String getText(){
		if(!dirty && text != null)
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
		
		dirty = false;
		return str;
	}
	
	public String toString(){ return getText(); }
	
	private MessagePart latest() {
		return messageParts.get(messageParts.size() - 1);
	}
	
	public static class MessagePart {
		protected ChatColor color = ChatColor.RESET;
		protected ChatColor[] styles = new ChatColor[0];
		protected String text;
		MessagePart(String text) { this.text = text; }
	}
	
}