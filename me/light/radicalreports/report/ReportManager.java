package me.light.radicalreports.report;

import java.sql.SQLException;

import org.bukkit.scheduler.BukkitRunnable;

import me.light.radicalreports.Main;
import me.light.radicalreports.config.ConfigSetting;
import me.light.radicalreports.report.databases.MySQL;
import me.light.radicalreports.report.databases.SQLite;

public class ReportManager {

	public static void setup() {
		new BukkitRunnable() {
			
			@Override
			public void run() {
				if ((boolean) ConfigSetting.USE_MYSQL.getSetting()) {
					try {
						Main.setDatabase(new MySQL());
					} catch (SQLException e) {
						e.printStackTrace();
					}
				} else {
					try {
						Main.setDatabase(new SQLite());
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				Main.getDB().reloadReports();
				Main.getDB().reloadData();
			}
		}.runTask(Main.getPlugin());

	}

}
