package me.light.radicalreports.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import net.md_5.bungee.api.ChatColor;

public class LanguageMessageHandler {

	private static LinkedHashMap<String, String> messages = new LinkedHashMap<String, String>();

	public static void reloadLanguageMessages() {
		reloadLanguageMessage("reloaded-plugin", "%prefix% Reloaded plugin.");
		reloadLanguageMessage("wait-before-command", "%prefix% Please wait before using this command.");
		reloadLanguageMessage("player-status-online", "&7[&2Online&7]");
		reloadLanguageMessage("player-status-offline", "&7[&4Offline&7]");
		reloadLanguageMessage("console-not-allowed-to-use-commands",
				"%prefix% The console is not allowed to use commands.");
		reloadLanguageMessage("report-command-invalid-arguments", "%prefix% Use /report <player> <reason>.");
		reloadLanguageMessage("report-command-invalid-player", "%prefix% %player% is not a valid player.");
		reloadLanguageMessage("report-command-cannot-report-self", "%prefix% You may not report yourself.");
		reloadLanguageMessage("report-command-no-reason", "%prefix% You need to put a reason for the report.");
		reloadLanguageMessage("reported-player-message-to-staff", "%prefix% %reporter% has reported %reported% for %reason%.");
		reloadLanguageMessage("reported-player-message-to-reporter", "%prefix% You have reported %reported% for %reason%.");
		reloadLanguageMessage("reports-command-no-permission", "%prefix% You do not have permission to view the reports.");
		reloadLanguageMessage("reports-command-invalid-arguments", "%prefix% Use /reports [reload]");
		reloadLanguageMessage("staff-command-invalid-arguments", "%prefix% Use /staff");
		reloadLanguageMessage("reports-command-main-inventory-name", "Report Manager");
		reloadLanguageMessage("reports-command-main-item-name", "&6Reports");
		reloadLanguageMessage("reports-command-close-inventory-name", "&4Close");
		reloadLanguageMessage("reports-command-next-page-name", "&7Next Page");
		reloadLanguageMessage("reports-command-previous-page-name", "&7Previous Page");
		reloadLanguageMessage("reports-command-history-item-name", "&6Report History");
		reloadLanguageMessage("reports-command-report-click-to-open", "&6Click &7to Open Report");
		reloadLanguageMessage("reports-command-report-drop-to-delete", "&6Drop Key &7to Delete Report");
		reloadLanguageMessage("reports-command-report-reporter", "&7Reporter: &c%reporter% %status%");
		reloadLanguageMessage("reports-command-report-reported", "&7Reported: &4%reported% %status%");
		reloadLanguageMessage("reports-command-report-reason", "&7Reason: &6%reason%");
		reloadLanguageMessage("reports-command-report-date", "&7Date: &e%date%");
		reloadLanguageMessage("reports-command-report-status", "&7Status: &e%status%");
		reloadLanguageMessage("reports-command-report-health", "&7Health: &c%health%");
		reloadLanguageMessage("reports-command-report-gamemode", "&7Gamemode: &c%gamemode%");
		reloadLanguageMessage("reports-command-report-sneaking", "&7Sneaking: &c%sneaking%");
		reloadLanguageMessage("reports-command-report-sprinting", "&7Sprinting: &c%sprinting%");
		reloadLanguageMessage("reports-command-remove-report-name", "&eRemove this Report");
		reloadLanguageMessage("reports-command-report-set-status-name-undecided", "&7Set This Report to &9Undecided");
		reloadLanguageMessage("reports-command-report-status-name-undecided", "&7This Report is &9Undecided");
		reloadLanguageMessage("reports-command-report-set-status-name-guilty", "&7Set This Report to &9Guilty");
		reloadLanguageMessage("reports-command-report-status-name-guilty", "&7This Report is &9Guilty");
		reloadLanguageMessage("reports-command-information", "&7More Information About %reported%");
		reloadLanguageMessage("reports-command-teleport-to-last-position-name", "&7Teleport to %reported%'s Last Location");
		reloadLanguageMessage("reports-command-teleport-to-last-position-lore", "&6Left Click &7to Teleport");
		reloadLanguageMessage("reports-command-status", "&7Status: &c%status%");
		reloadLanguageMessage("view-reports-go-back-name", "&6Click &7to Go Back");
		reloadLanguageMessage("unread-reports-message", "%prefix% You have %amount% unread reports.");
		reloadLanguageMessage("staff-mode-enable-item", "&6Click &7to Enable Staff Mode");
		reloadLanguageMessage("disable-staff-mode-item-name", "&6Click &7to Disable Staff Mode");
		reloadLanguageMessage("teleport-to-where-you-are-looking-name", "&6Click &7to Teleport to Where You Are Looking");
		reloadLanguageMessage("teleport-to-a-random-player-name", "&6Click &7to Teleport to a Random Player");
		reloadLanguageMessage("clicks-per-second-warning-message", "%prefix% %player% has clicked %count% times a second.");
		reloadLanguageMessage("staff-no-permission", "%prefix% You do not have permission to use staff mode.");
		reloadLanguageMessage("enabled-staff-mode-message", "%prefix% You have enabled staff mode.");
		reloadLanguageMessage("disabled-staff-mode-message", "%prefix% You have disabled staff mode.");
		reloadLanguageMessage("staff-online-message", "%prefix% Online staff members:");
		reloadLanguageMessage("no-staff-online-message", "%prefix% Online staff members:");
		try {
			Config.getLangConfig().save(Config.getLangFile());
			Bukkit.getLogger().log(Level.INFO, "Language Messages Loaded");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void reloadLanguageMessage(String key, String value) {
		if (Config.getLangConfig().isSet(key))
			messages.put(key, Config.getLangConfig().getString(key, value));
		else {
			Config.getLangConfig().set(key, value);
			messages.put(key, value);
		}
	}

	public static String parseLanguageMessage(String key, HashMap<String, String> placeholders) {
		String msg = Config.getLangConfig().getString(key);
		msg = msg.replaceAll("%prefix%", (String) ConfigSetting.PREFIX.getSetting());
		if (placeholders != null) {
			for (String keySet : placeholders.keySet()) {
				msg = msg.replaceAll(keySet, placeholders.get(keySet));
			}
		}
		return ChatColor.translateAlternateColorCodes('&', msg);
	}
}
