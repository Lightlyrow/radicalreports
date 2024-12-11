package me.light.radicalreports;

import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.light.radicalreports.config.ConfigSetting;
import me.light.radicalreports.config.LanguageMessageHandler;
import me.light.radicalreports.config.PlayerDataType;
import me.light.radicalreports.config.Utils;
import me.light.radicalreports.report.Report;
import me.light.radicalreports.report.ReportAttribute;

public class PlayerDataListener implements Listener {

	@EventHandler
	public void on(PlayerJoinEvent e) {
		if (e.getPlayer().hasPermission((String) ConfigSetting.RECEIVE_REPORT_NOTIFICATIONS.getSetting())) {
			new BukkitRunnable() {

				@Override
				public void run() {
					if ((boolean) ConfigSetting.UNREAD_REPORTS_NOTIFICATIONS.getSetting()) {
						Integer i = 0;
						Player p = e.getPlayer();
						String viewed = (String) Main.getDB().getPlayerData().get(p.getUniqueId())
								.getAttribute(PlayerDataType.VIEWED_REPORTS);
						if (viewed != null) {
							List<String> items = Arrays.asList(viewed.split(","));
							for (Report report : Main.getDB().getReports().values()) {
								if (!items.contains((String) report.getAttribute(ReportAttribute.ID))) {
									i++;
								}
							}
							if (i > 0) {
								p.sendMessage(LanguageMessageHandler.parseLanguageMessage("unread-reports-message",
										Utils.createHashMap("%amount%", i.toString())));
							}
						}
					}
				}
			}.runTaskLater(Main.getPlugin(), 5L);
		}
		Main.getDB().reloadData();
	}

	@EventHandler
	public void on(PlayerQuitEvent e) {
		Main.getDB().reloadData();
	}
}
