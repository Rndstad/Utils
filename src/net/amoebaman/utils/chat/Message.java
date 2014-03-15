package net.amoebaman.utils.chat;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.amoebaman.utils.JsonWriter;
import net.amoebaman.utils.nms.ReflectionUtil;


/**
 * This class provides a chainable method for constructing messages to be
 * sent to players, or optionally broadcast, including the JSON raw message
 * capabilities included for messages to players.
 * 
 * @author AmoebaMan
 */
public class Message {
	
	private static Class<?> nmsTagCompound = ReflectionUtil.getNMSClass("NBTTagCompound");
	private static Class<?> nmsAchievement = ReflectionUtil.getNMSClass("Achievement");
	private static Class<?> nmsStatistic = ReflectionUtil.getNMSClass("Statistic");
	private static Class<?> nmsItemStack = ReflectionUtil.getNMSClass("ItemStack");
	
	private Class<?> obcStatistic = ReflectionUtil.getOBCClass("CraftStatistic");
	private Class<?> obcItemStack = ReflectionUtil.getOBCClass("inventory.CraftItemStack");
	
	protected List<MessagePart> messageParts = new ArrayList<MessagePart>();
	
	private Scheme scheme;
	private String text = "", jsonText = "";
	private boolean written = false, jsonWritten = false;
	private boolean usesJson = false;
	
	/**
	 * Begins a message.
	 */
	public Message(){}
	
	/**
	 * Begins a Message.
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
	 * Convenience method.  See {@link #then()}.
	 * @return
	 */
	public Message t(Object text){
		return then(text);
	}
	
	/**
	 * Convenience method.  See {@link #alt()}.
	 * @return
	 */
	public Message a(){
		return alt();
	}
	
	/**
	 * Convenience method.  See {@link #strong()}.
	 * @return
	 */
	public Message s(){
		return strong();
	}

	/**
	 * Sets the file path that will be opened when this bit
	 * of text is clicked on in-game.
	 * @param path a filepath
	 * @return the message (for chaining)
	 */
	public Message file(String path) {
		onClick("open_file", path);
		return this;
	}
	
	/**
	 * Sets the URL that will be opened when this bit of
	 * text is clicked on in-game.
	 * @param url a URL
	 * @return the message (for chaining)
	 */
	public Message link(String url) {
		onClick("open_url", url);
		return this;
	}
	
	/**
	 * Sets the command that will be suggested when this
	 * bit of text is clicked on in-game.
	 * @param command a command
	 * @return the message (for chaining)
	 */
	public Message suggest(String command) {
		onClick("suggest_command", command);
		return this;
	}
	
	/**
	 * Sets the command that will be executed when this
	 * bit of text is clicked on in-game.
	 * @param command a command
	 * @return the message (for chaining)
	 */
	public Message command(String command) {
		onClick("run_command", command);
		return this;
	}
	
	/**
	 * Sets the achievement that will be displayed as a tooltip when
	 * this bit of text is hovered over in-game.
	 * @param ach an achievement
	 * @return the message (for chaining)
	 */
	public Message achievementTooltip(Achievement ach) {
		try {
			Object achievement = ReflectionUtil.getMethod(obcStatistic, "getNMSAchievement").invoke(null, ach);
			return achievementTooltip((String) ReflectionUtil.getField(nmsAchievement, "e").get(achievement));
		}
		catch (Exception e) {
			e.printStackTrace();
			return this;
		}
	}
	
	/**
	 * Sets the statistic that will be displayed as a tooltip when
	 * this bit of text is hovered over in-game.  If the statistic
	 * requires additional data, this will fail silently.
	 * @param stat a statistic
	 * @return the message (for chaining)
	 */
	public Message statisticTooltip(Statistic stat) {
		Statistic.Type type = stat.getType();
		if (type != Statistic.Type.UNTYPED)
			return this;
		try {
			Object statistic = ReflectionUtil.getMethod(obcStatistic, "getNMSStatistic").invoke(null, stat);
			return achievementTooltip((String) ReflectionUtil.getField(nmsStatistic, "e").get(statistic));
		}
		catch (Exception e) {
			e.printStackTrace();
			return this;
		}
	}
	
	/**
	 * Sets the statistic that will be displayed as a tooltip when
	 * this bit of text is hovered over in-game.  If the extra data
	 * provided doesn't match what the statistic needs, this will
	 * fail silently.
	 * @param stat a statistic
	 * @param mat a specific material for the statistic if needed
	 * @return the message (for chaining)
	 */
	public Message statisticTooltip(Statistic stat, Material mat) {
		Statistic.Type type = stat.getType();
		if (type == Statistic.Type.UNTYPED || type == Statistic.Type.BLOCK && mat.isBlock() || type == Statistic.Type.ENTITY)
			return this;
		try {
			Object statistic = ReflectionUtil.getMethod(obcStatistic, "getMaterialStatistic").invoke(null, stat, mat);
			return achievementTooltip((String) ReflectionUtil.getField(nmsStatistic, "e").get(statistic));
		}
		catch (Exception e) {
			e.printStackTrace();
			return this;
		}
	}
	
	/**
	 * Sets the statistic that will be displayed as a tooltip when
	 * this bit of text is hovered over in-game.  If the extra data
	 * provided doesn't match what the statistic needs, this will
	 * fail silently.
	 * @param stat a statistic
	 * @param entity a specific entity type for the statistic if needed
	 * @return the message (for chaining)
	 */
	public Message statisticTooltip(Statistic stat, EntityType entity) {
		org.bukkit.Statistic.Type type = stat.getType();
		if (type == org.bukkit.Statistic.Type.UNTYPED || type != org.bukkit.Statistic.Type.ENTITY)
			return this;
		try {
			Object statistic = ReflectionUtil.getMethod(obcStatistic, "getEntityStatistic").invoke(null, stat, entity);
			return achievementTooltip((String) ReflectionUtil.getField(nmsStatistic, "e").get(statistic));
		}
		catch (Exception e) {
			e.printStackTrace();
			return this;
		}
	}
	
	/**
	 * Sets the item tooltip that will be displayed as a tooltip when
	 * this bit of text is hovered over in-game.
	 * @param itemStack an item
	 * @return the message (for chaining)
	 */
	public Message itemTooltip(ItemStack itemStack) {
		try {
			Object nmsItem = ReflectionUtil.getMethod(obcItemStack, "asNMSCopy", ItemStack.class).invoke(null, itemStack);
			return itemTooltip(ReflectionUtil.getMethod(nmsItemStack, "save").invoke(nmsItem, nmsTagCompound.newInstance()).toString());
		}
		catch (Exception e) {
			e.printStackTrace();
			return this;
		}
	}
	
	/**
	 * Sets the plain-text tooltip that will be displayed when this
	 * bit of text is hovered over in-game.
	 * @param text some text
	 * @return the message (for chaining)
	 */
	public Message tooltip(String... lines) {
		if(lines.length < 1)
			return this;
		else if(lines.length == 1)
			onHover("show_text", lines[0].replace("\"", ""));
		else
			itemTooltip(makeMultilineTooltip(lines));
		return this;
	}
	
	private Message achievementTooltip(String name) {
		onHover("show_achievement", "achievement." + name);
		return this;
	}
	
	private Message itemTooltip(String itemJSON) {
		onHover("show_item", itemJSON);
		return this;
	}
	
	protected String makeMultilineTooltip(String... lines) {
		String fbdn = "\"";
		String escSeq = "\\\"";
		String metaEsc = String.valueOf((char) 128);
		
		JsonWriter json = new JsonWriter();
		try {
			json.beginObject();
			json.name("id").value(1);
			json.name("tag").beginObject();
			json.name("display").beginObject();
			json.name("Name").value(ChatColor.RESET + lines[0]);
			json.name("Lore").beginArray();
			for (int i = 1; i < lines.length; i++)
				json.value(lines[i].isEmpty() ? ChatColor.RESET.toString() : metaEsc + lines[i] + metaEsc);
			json.endArray().endObject().endObject().endObject();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		String result = json.toString();
		json.close();
		return result.replace(fbdn, "").replace(metaEsc, escSeq);
	}
	
	protected void onClick(String name, String data) {
		latest().clickActionName = name;
		latest().clickActionData = data;
		usesJson = true;
		jsonWritten = false;
	}
	
	protected void onHover(String name, String data) {
		latest().hoverActionName = name;
		latest().hoverActionData = data;
		usesJson = true;
		jsonWritten = false;
	}
	
	/**
	 * Gets the full, plain text of this message, excluding any
	 * JSON elements that may be included
	 * @return the message in full text
	 */
	public String getText(){
		if(written && text != null)
			return text;
		
		String str = "";
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
	
	/**
	 * Writes this message to a JSON-formated string, fully compliant with
	 * Minecraft's advanced text formatting, ready to be used.
	 * @return the json translation
	 */
	public String getJson() {
		if (written && jsonWritten && jsonText != null)
			return jsonText;
		
		getText();
		
		JsonWriter json = new JsonWriter();
		try {
			json.beginArray();
			for (MessagePart part : messageParts)
				part.writeJson(json);
			json.endArray();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		jsonText = json.toString().replace(String.valueOf((char) 128), "\\\"");
		if(scheme != null)
			jsonText = scheme.prefix + jsonText + scheme.suffix;
		jsonWritten = true;
		return jsonText;
	}
	
	public boolean usesJson(){ return usesJson; }
	
	public String toString(){ return usesJson ? getJson() : getText(); }
	
	protected MessagePart latest() {
		return messageParts.get(messageParts.size() - 1);
	}
	
	/**
	 * A class that represents a piece of a {@link Message}, including
	 * both its text and its format.
	 * 
	 * @author AmoebaMan
	 */
	public static class MessagePart {
		
		private ChatColor color = ChatColor.RESET;
		private ChatColor[] styles = new ChatColor[0];
		private String text;
		private String clickActionName = null, clickActionData = null, hoverActionName = null, hoverActionData = null;
		
		public MessagePart(String text) { this.text = text; }

		private JsonWriter writeJson(JsonWriter json) {
			try {
				json.beginObject();
				json.name("text").value(text);
				if (color != null)
					json.name("color").value(color.name().toLowerCase());
				if (styles != null)
					for (ChatColor style : styles)
						json.name(style.name().toLowerCase()).value(true);
				if (clickActionName != null && clickActionData != null)
					json.name("clickEvent").beginObject().name("action").value(clickActionName).name("value").value(clickActionData.replace("\\\"", String.valueOf((char) 128))).endObject();
				if (hoverActionName != null && hoverActionData != null)
					json.name("hoverEvent").beginObject().name("action").value(hoverActionName).name("value").value(hoverActionData.replace("\\\"", String.valueOf((char) 128))).endObject();
				json.endObject();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			return json;
		}
		
	}
	
}