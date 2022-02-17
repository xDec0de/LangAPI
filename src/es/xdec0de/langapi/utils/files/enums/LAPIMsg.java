package es.xdec0de.langapi.utils.files.enums;

public enum LAPIMsg {
	
	// GUI //
	
	LANG_GUI_TITLE("Commands.Lang.GUI.Title"),
	
	// Commands //
	
	LANG_CHANGED("Commands.Lang.Success"),
	LANG_CHANGED_OTHER("Commands.Lang.SuccessOther"),
	LANG_NOT_FOUND("Commands.Lang.NotFound"),
	LANG_USAGE("Commands.Lang.Usage"),
	
	// Core //
	
	UPDATE_AVAILABLE_PLAYER("Core.UpdateAvailable"),
	PLAYER_NOT_FOUND("Core.PlayerNotFound"),
	NO_CONSOLE("Core.NoConsole"),
	ERROR_PREFIX("Core.ErrorPrefix"),
	PREFIX("Core.Prefix");
	
	private String path;
	
	LAPIMsg(String string) {
		this.path = string;
	}
	
	public String getPath() {
		return path;
	}
}
