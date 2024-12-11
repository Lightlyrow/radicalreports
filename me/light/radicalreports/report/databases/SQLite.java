package me.light.radicalreports.report.databases;

import java.io.File;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import me.light.radicalreports.Main;
import me.light.radicalreports.config.PlayerDataType;
import me.light.radicalreports.report.ReportAttribute;

public class SQLite extends Database {

	public SQLite() throws SQLException {
		setup();
	}

	@Override
	public void setup() throws SQLException {
		database = "reportsdatabase";
		File databaseFolder = new File(Main.getPlugin().getDataFolder(), database + ".db");
		if (!databaseFolder.exists()) {
			try {
				databaseFolder.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			if (connection == null) {
				Class.forName("org.sqlite.JDBC");
				connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFolder);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.err.println("jdbc driver unavailable!");
			return;
		}

		String sql = "CREATE TABLE IF NOT EXISTS reports(id varchar(64), PRIMARY KEY (ID));";
		try {
			connection.close();
			connection = null;
			connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFolder);
			PreparedStatement stmt = connection.prepareStatement(sql);
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		sql = "CREATE TABLE IF NOT EXISTS playerdata(uuid varchar(64), PRIMARY KEY (UUID));";
		try {
			PreparedStatement stmt = connection.prepareStatement(sql);
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		for (PlayerDataType data : PlayerDataType.values()) {
			columnsData.add(data.toString());
			sql = "SELECT " + data.toString() + " FROM playerdata LIMIT 1";
			PreparedStatement stmt;
			try {
				stmt = connection.prepareStatement(sql);
				stmt.executeQuery();
			} catch (SQLException e) {
				stmt = connection.prepareStatement("ALTER TABLE playerdata ADD " + data.toString() + " varchar(255);");
				stmt.executeUpdate();
			}
		}
		for (ReportAttribute name : ReportAttribute.values()) {
			columnsReport.add(name.toString());
			sql = "SELECT " + name.toString() + " FROM reports LIMIT 1";
			PreparedStatement stmt;
			try {
				stmt = connection.prepareStatement(sql);
				stmt.executeQuery();
			} catch (SQLException e) {
				stmt = connection.prepareStatement("ALTER TABLE reports ADD " + name.toString() + " varchar(255);");
				stmt.executeUpdate();
			}
		}
	}
}
