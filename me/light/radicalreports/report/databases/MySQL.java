package me.light.radicalreports.report.databases;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import me.light.radicalreports.config.ConfigSetting;
import me.light.radicalreports.config.PlayerDataType;
import me.light.radicalreports.report.ReportAttribute;

public class MySQL extends Database {
	private String username;
	private String password;
	private int port;
	private String host;
	public static final String MYSQL_AUTO_RECONNECT = "autoReconnect";

	public MySQL() throws SQLException {
		setup();
	}

	@Override
	public void setup() throws SQLException {

		username = (String) ConfigSetting.MYSQL_USERNAME.getSetting();
		password = (String) ConfigSetting.MYSQL_PASSWORD.getSetting();
		try {
			port = (Integer) ConfigSetting.MYSQL_PORT.getSetting();
		} catch (ClassCastException e) {
			System.out.println("Are you sure you've input the port?");
			e.printStackTrace();
		}
		host = (String) ConfigSetting.MYSQL_HOST.getSetting();
		database = (String) ConfigSetting.MYSQL_DATABASE.getSetting();

		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.err.println("jdbc driver unavailable!");
			return;
		}
		try {
			Properties connProperties = new Properties();
		    connProperties.put("user", username);
		    connProperties.put("password", password);
			connProperties.put(MYSQL_AUTO_RECONNECT, "true");
			connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database
					+ "?autoReconnect=true?useUnicode=true&characterEncoding=UTF-8", connProperties);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		String sql = "CREATE TABLE IF NOT EXISTS reports(id varchar(64), PRIMARY KEY (ID));";
		try {
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
			ResultSet rs = executeQuery(
					"SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='playerdata' AND column_name='"
							+ data.toString() + "'");
			rs.next();
			if (rs.getInt(1) == 0) {
				executeUpdate("ALTER TABLE playerdata ADD " + data.toString() + " varchar(255);");
			}
		}

		for (ReportAttribute name : ReportAttribute.values()) {
			columnsReport.add(name.toString());
			ResultSet rs = executeQuery(
					"SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='reports' AND column_name='"
							+ name.toString() + "'");
			rs.next();
			if (rs.getInt(1) == 0) {
				executeUpdate("ALTER TABLE reports ADD " + name.toString() + " varchar(255);");
			}
		}
	}

}