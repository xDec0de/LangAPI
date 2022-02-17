package es.xdec0de.langapi.api;
import java.util.UUID;

/**
 * Object used to store player settings.
 * 
 * @since LangAPI v1.0
 */
public class Settings {
	
	private final UUID uuid;
	private Lang lang;
	private boolean autoSelect;
	
	/**
	 * Creates a new {@link Settings} object. This should not be used unless you want to overwrite offline players data.
	 * 
	 * @param uuid
	 * The {@link UUID} of the player.
	 * 
	 * @param lang
	 * The {@link Lang} of the player.
	 * 
	 * @param autoSelect
	 * Whether if AutoSelect will be enabled or not for this player.
	 * 
	 * @throws IllegalArgumentException
	 * If a {@link Settings} object with the same {@link UUID} is already on the settings cache (Player is online).
	 * 
	 * @since LangAPI v1.0
	 */
	public Settings(UUID uuid, Lang lang, boolean autoSelect) throws IllegalArgumentException {
		if(LangAPI.settingsCache.get(uuid) == null) {
			this.uuid = uuid;
			this.lang = lang;
			this.autoSelect = autoSelect;
		} else {
			throw new IllegalArgumentException("A Settings object with that UUID already exists. Do not create Settings objects of online players manually! Use LAPI.getAPI().getSettings(UUID) instead!");
		}
	}
	
	/**
	 * Gets the {@link UUID} of the player that owns a {@link Settings} object.
	 * 
	 * @return
	 * The {@link UUID} of the player.
	 * 
	 * @since LangAPI v1.0
	 */
	public UUID getUUID() {
		return uuid;
	}
	
	/**
	 * Gets the {@link Lang} of a {@link Settings} object.
	 * 
	 * @return
	 * The {@link Lang} of the player.
	 * 
	 * @see
	 * #getUUID()
	 * 
	 * @since LangAPI v1.0
	 */
	public Lang getLang() {
		return lang;
	}
	
	/**
	 * Changes the {@link Lang} of a {@link Settings} object.
	 * 
	 * @see
	 * #getUUID()
	 * 
	 * @since LangAPI v1.0
	 */
	public void setLang(Lang lang) {
		this.lang = lang;
	}
	
	/**
	 * Gets if AutoSelect is enabled on a {@link Settings} object.
	 * 
	 * @return
	 * true if enabled, false otherwise.
	 * 
	 * @see
	 * #getUUID()
	 * 
	 * @since LangAPI v1.0
	 */
	public boolean getAutoSelect() {
		return autoSelect;
	}
	
	/**
	 * Changes if AutoSelect is enabled on a {@link Settings} object.
	 * 
	 * @param autoSelect
	 * Whether if AutoSelect is enabled or not.
	 * 
	 * @see
	 * #getUUID()
	 * 
	 * @since LangAPI v1.0
	 */
	public void setAutoSelect(boolean autoSelect) {
		this.autoSelect = autoSelect;
	}
}