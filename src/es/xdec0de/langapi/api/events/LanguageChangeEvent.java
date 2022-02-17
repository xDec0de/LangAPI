package es.xdec0de.langapi.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import es.xdec0de.langapi.api.Lang;

/**
 * Called when a player changes its language.
 *
 * @author xDec0de_
 *
 * @since LangAPI v1.0
 */
public class LanguageChangeEvent extends Event implements Cancellable {
	
	private Player player;
	private Lang language;
	private boolean cancelled;

	private static final HandlerList HANDLERS = new HandlerList();

	public LanguageChangeEvent(Player player, Lang language) {
		this.player = player;
		this.language = language;
	}

	/**
	 * @return The player that has changed its language.
	 *
	 * @since LangAPI v1.0
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * @return The language that {@link #getPlayer()} will use.
	 *
	 * @since LangAPI v1.0
	 */
	public Lang getLanguage() {
		return language;
	}

	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	/**
	 * @return Whether the event is cancelled or not.
	 * 
	 * @see #setCancelled(boolean)
	 * 
	 * @since LangAPI v1.0
	 */
	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	/**
	 * Sets whether the event is cancelled or not, if canceled, the player won't have its language changed nor receive the message.
	 *
	 * @see #isCancelled
	 *
	 * @since LangAPI v1.0
	 */
	@Override
	public void setCancelled(boolean cancel) {
		cancelled = cancel;
	}
}
