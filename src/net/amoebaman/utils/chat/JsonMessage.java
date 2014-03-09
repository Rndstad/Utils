package net.amoebaman.utils.chat;

import net.amoebaman.utils.JsonWriter;
import net.amoebaman.utils.nms.ReflectionUtil;

import org.bukkit.Achievement;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;


/**
 * Expanded {@link Message} adopted from mkremins's "fanciful", this class provides the ability to
 * generate messages that take full advantage of Minecraft's JSON message formatting for tooltips,
 * on-click actions, formatting, and whatnot, all with convenient chaining capability.
 * 
 * @author AmoebaMan
 */
public class JsonMessage extends Message{
	
	protected String jsonText;
	protected boolean jsonDirty;
	
	private Class<?> nmsTagCompound = ReflectionUtil.getNMSClass("NBTTagCompound");
	private Class<?> nmsAchievement = ReflectionUtil.getNMSClass("Achievement");
	private Class<?> nmsStatistic = ReflectionUtil.getNMSClass("Statistic");
	private Class<?> nmsItemStack = ReflectionUtil.getNMSClass("ItemStack");
	
	private Class<?> obcStatistic = ReflectionUtil.getOBCClass("CraftStatistic");
	private Class<?> obcItemStack = ReflectionUtil.getOBCClass("inventory.CraftItemStack");
	
	public JsonMessage(Scheme scheme) {
		super(scheme);
		jsonText = null;
		dirty = false;
	}
	
	public JsonMessage then(Object text) {
		messageParts.add(new JsonMessagePart(text.toString()));
		if(scheme != null)
			format(scheme.normal);
		return this;
	}
	
	public JsonMessage alternate(){ return (JsonMessage) super.alternate(); }
	public JsonMessage strong(){ return (JsonMessage) super.strong(); }
	public JsonMessage format(Format format){ return (JsonMessage) super.format(format); }
	public JsonMessage color(ChatColor color) { jsonDirty = true; return (JsonMessage) super.color(color); }
	public JsonMessage style(ChatColor... styles) { jsonDirty = true; return (JsonMessage) super.style(styles); }

	/**
	 * Sets the file path that will be opened when this bit
	 * of text is clicked on in-game.
	 * @param path a filepath
	 * @return the message (for chaining)
	 */
	public JsonMessage file(String path) {
		onClick("open_file", path);
		return this;
	}
	
	/**
	 * Sets the URL that will be opened when this bit of
	 * text is clicked on in-game.
	 * @param url a URL
	 * @return the message (for chaining)
	 */
	public JsonMessage link(String url) {
		onClick("open_url", url);
		return this;
	}
	
	/**
	 * Sets the command that will be suggested when this
	 * bit of text is clicked on in-game.
	 * @param command a command
	 * @return the message (for chaining)
	 */
	public JsonMessage suggest(String command) {
		onClick("suggest_command", command);
		return this;
	}
	
	/**
	 * Sets the command that will be executed when this
	 * bit of text is clicked on in-game.
	 * @param command a command
	 * @return the message (for chaining)
	 */
	public JsonMessage command(String command) {
		onClick("run_command", command);
		return this;
	}
	
	/**
	 * Sets the achievement that will be displayed as a tooltip when
	 * this bit of text is hovered over in-game.
	 * @param ach an achievement
	 * @return the message (for chaining)
	 */
	public JsonMessage achievementTooltip(Achievement ach) {
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
	public JsonMessage statisticTooltip(Statistic stat) {
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
	public JsonMessage statisticTooltip(Statistic stat, Material mat) {
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
	public JsonMessage statisticTooltip(Statistic stat, EntityType entity) {
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
	public JsonMessage itemTooltip(ItemStack itemStack) {
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
	public JsonMessage tooltip(String text) {
		String[] lines = text.split("\\n");
		if (lines.length <= 1) {
			onHover("show_text", text);
		}
		else {
			itemTooltip(makeMultilineTooltip(lines));
		}
		return this;
	}
	
	/**
	 * Writes this message to a JSON-formated string, fully compliant with
	 * Minecraft's advanced text formatting, ready to be used.
	 * @return the json translation
	 */
	public String toString() {
		if (!jsonDirty && jsonText != null)
			return jsonText;
		
		JsonWriter json = new JsonWriter();
		try {
			if (messageParts.size() == 1) {
				latest().writeJson(json);
			}
			else {
				json.beginObject();
				json.name("text").value("");
				json.name("extra").beginArray();
				for (MessagePart part : messageParts)
					if(part instanceof JsonMessagePart)
						((JsonMessagePart) part).writeJson(json);
				json.endArray();
				json.endObject();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		jsonText = json.toString();
		if(scheme != null)
			jsonText = scheme.prefix + jsonText + scheme.suffix;
		
		jsonDirty = false;
		return jsonText;
	}
	
	private JsonMessage achievementTooltip(String name) {
		onHover("show_achievement", "achievement." + name);
		return this;
	}
	
	private JsonMessage itemTooltip(String itemJSON) {
		onHover("show_item", itemJSON);
		return this;
	}
	
	private String makeMultilineTooltip(String[] lines) {
		JsonWriter json = new JsonWriter();
		try {
			json.beginObject();
			json.name("id").value(1);
			json.name("tag").beginObject();
			json.name("display").beginObject();
			json.name("Name").value("\\u00A7f" + lines[0].replace("\"", "\\\""));
			json.name("Lore").beginArray();
			for (int i = 1; i < lines.length; i++)
				json.value(lines[i].isEmpty() ? " " : lines[i].replace("\"", "\\\""));
			json.endArray().endObject().endObject().endObject();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		String result = json.toString();
		json.close();
		return result;
	}

	private JsonMessagePart latest() {
		return (JsonMessagePart) messageParts.get(messageParts.size() - 1);
	}
	
	private void onClick(String name, String data) {
		latest().clickActionName = name;
		latest().clickActionData = data;
		jsonDirty = true;
	}
	
	private void onHover(String name, String data) {
		latest().hoverActionName = name;
		latest().hoverActionData = data;
		jsonDirty = true;
	}
	
	public static class JsonMessagePart extends MessagePart {

		protected String clickActionName = null, clickActionData = null, hoverActionName = null, hoverActionData = null;
		JsonMessagePart(String text) { super(text); }
		
		JsonWriter writeJson(JsonWriter json) {
			try {
				json.beginObject();
				json.name("text").value(text);
				if (color != null)
					json.name("color").value(color.name().toLowerCase());
				if (styles != null)
					for (ChatColor style : styles)
						json.name(style.name().toLowerCase()).value(true);
				if (clickActionName != null && clickActionData != null)
					json.name("clickEvent").beginObject().name("action").value(clickActionName).name("value").value(clickActionData).endObject();
				if (hoverActionName != null && hoverActionData != null)
					json.name("hoverEvent").beginObject().name("action").value(hoverActionName).name("value").value(hoverActionData).endObject();
				json.endObject();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			return json;
		}
		
	}
	
}