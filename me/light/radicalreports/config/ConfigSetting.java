package me.light.radicalreports.config;

import java.util.Arrays;

public enum ConfigSetting {
	MYSQL_USERNAME("mysql-username", "username"), MYSQL_PASSWORD("mysql-password", "password"), MYSQL_PORT("mysql-port",
			"port"), MYSQL_DATABASE("mysql-database", "database"), MYSQL_HOST("mysql-host", "host"), PREFIX("prefix",
					"&4[&aReport&4]&2"), REPORT_SELF("report-self", false), REPORTS_COMMAND_PERMISSION(
							"reports-command-permission",
							"radicalreports.reports"), RECEIVE_REPORT_NOTIFICATIONS("report-notifications-permission",
									"radicalreports.notifications"), PLAY_EFFECTS_FOR_REPORTER(
											"play-effects-for-reporter",
											false), USE_MYSQL("use-mysql", false), PLAY_EFFECTS_FOR_STAFF(
													"play-effects-for-all-staff",
													false), BUNGEECORD("bungeecord", false), RUN_COMMANDS_WHEN_GUILTY(
															"run-commands-when-report-set-to-guilty",
															false), CONSOLE_COMMANDS_WHEN_GUILTY(
																	"console-commands-when-guilty",
																	Arrays.asList("ban %reported% %reason%",
																			"broadcast %reported% was found guilty for %reason%")), PLAYER_COMMANDS_WHEN_GUILTY(
																					"player-commands-when-guilty",
																					Arrays.asList(
																							"ban %reported% %reason%",
																							"broadcast %reported% was found guilty for %reason%")), UNREAD_REPORTS_NOTIFICATIONS(
																									"unread-reports-notification",
																									true), VANISH_ENABLE_COMMAND(
																											"vanish-enable-command",
																											"/vanish %player%"), VANISH_DISABLE_COMMAND(
																													"vanish-disable-command",
																													"/vanish %player%"), CLICKS_PER_SECOND_COUNTER(
																															"clicks-per-second-for-warning",
																															10), STAFF_MODE_ALLOWED(
																																	"staff-mode-permission",
																																	"radicalreports.staff"), STAFF_MODE_CUSTOM_ITEM_NAME(
																																			"custom-staff-mode-item-name",
																																			"&6Cuff a Player"), STAFF_MODE_CUSTOM_ITEM_COMMAND(
																																					"custom-staff-mode-item-command",
																																					"/cuff %player%"), GUILTY_STATUS(
																																							"guilty-status-name",
																																							"Guilty"), UNDECIDED_STATUS(
																																									"undecided-status-name",
																																									"Undecided"), STAFF_MODE_CUSTOM_COMMAND("staff-mode-custom-command", "/staff mode");

	private String key;
	private Object value;

	private ConfigSetting(String key, Object value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public Object getValue() {
		return value;
	}

	// Configuration Settings
	public Object getSetting() {
		return Config.configValues.get(this.getKey());
	}
}
