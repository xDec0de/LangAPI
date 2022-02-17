package es.xdec0de.langapi.utils.files.enums;

public enum LAPISetting {
	
	MYSQL_ENABLED("MySQL.Enabled"),
	MYSQL_HOST("MySQL.Host"),
	MYSQL_PORT("MySQL.Port"),
	MYSQL_USERNAME("MySQL.Username"),
	MYSQL_PASSWORD("MySQL.Password"),
	MYSQL_DATABASE("MySQL.Database"),
	MYSQL_TABLE("MySQL.Table"),
	MYSQL_OPTIONS("MySQL.Options"),
	MYSQL_ENGINE("MySQL.Engine"),
	MYSQL_ERROR_STOP("MySQL.StopOnError"),
	MYSQL_CACHE_TIME("MySQL.CacheSendTimeInSeconds"),
	
	LANG_USE_GUI("Commands.Lang.GUI.Enabled"),
	LANG_GUI_ROWS("Commands.Lang.GUI.Rows.Amount"),
	LANG_GUI_FILL_ENABLED("Commands.Lang.GUI.Rows.Fill.Enabled"),
	
	REPLACE_ENABLED("Replace.Enabled"),
	
	ALL_LANGUAGES_ENABLED("Using.All"),
	LANGS_USING("Using.List"),
	DEFAULT_LANG("Using.Default"),
	
	DISABLE_AUTOSELECT("ManualSelectDisableAutoSelect"),
	AUTOSELECT_ENABLED("AutoSelect"),
	
	UPDATER_MESSAGE_PERMISSION("Updater.Message.Permission"),
	UPDATER_MESSAGE_PLAYER("Updater.Message.Players"),
	UPDATER_MESSAGE_CONSOLE("Updater.Message.Console"),
	UPDATER_ENABLED("Updater.Enabled");
	
	private String path;
	
	LAPISetting(String string) {
		this.path = string;
	}
	
	public String getPath() {
		return path;
	}
}
