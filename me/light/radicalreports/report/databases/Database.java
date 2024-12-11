package me.light.radicalreports.report.databases;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.common.base.Joiner;

import me.light.radicalreports.config.PlayerDataType;
import me.light.radicalreports.report.PlayerData;
import me.light.radicalreports.report.Report;
import me.light.radicalreports.report.ReportAttribute;

public abstract class Database {

	protected ArrayList<String> columnsReport = new ArrayList<String>();
	protected ArrayList<String> columnsData = new ArrayList<String>();
	protected Connection connection = null;
	protected HashMap<Integer, Report> reports = new HashMap<Integer, Report>();
	protected HashMap<UUID, PlayerData> playerdata = new HashMap<UUID, PlayerData>();
	protected String database;

	public abstract void setup() throws SQLException;

	public void reloadReports() {
		reports.clear();
		ResultSet c = executeQuery("SELECT * FROM reports;");
		try {
			while (c.next()) {
				// reports.put(Integer.parseInt(c.getString(1)),
				// new Report(Integer.parseInt(c.getString(1)), c.getString(2),
				// c.getString(3), c.getString(4),
				// c.getString(5), c.getString(6), c.getString(7),
				// c.getDouble(8), c.getString(9),
				// c.getBoolean(10), c.getBoolean(11)));
				ArrayList<Object> data = new ArrayList<Object>();
				for (int i = 1; i <= ReportAttribute.values().length; i++) {
					data.add(c.getObject(i));
				}
				reports.put(Integer.parseInt(c.getString(1)), new Report(data));
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void close() {

		try {
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void executeUpdate(String sql) {
		PreparedStatement stmt = null;
		try {
			stmt = connection.prepareStatement(sql);
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	public ResultSet executeQuery(String sql) {
		PreparedStatement stmt = null;
		try {
			stmt = connection.prepareStatement(sql);
			return stmt.executeQuery();
		} catch (SQLException e1) {
			e1.printStackTrace();
			return null;
		}
	}

	public void addReport(Report report) {
		String query = "INSERT INTO reports(";
		query = query + Joiner.on(", ").join(columnsReport) + ") VALUES (";
		ArrayList<String> attributeList = new ArrayList<String>();
		for (ReportAttribute a : ReportAttribute.values()) {
			attributeList.add("'" + report.getAttribute(a).toString() + "'");
		}
		query = query + Joiner.on(", ").join(attributeList) + ");";
		executeUpdate(query);
		reloadReports();
	}

	public void removeReport(Integer ID) {
		String query = "DELETE FROM reports WHERE id = " + ID.toString() + ";";
		executeUpdate(query);
		query = "SELECT * FROM playerdata";
		ResultSet r = executeQuery(query);
		try {
			while (r.next()) {
				String viewed = r.getString(11);
				if (viewed == null)
					continue;
				List<String> items = Arrays.asList(viewed.split(","));
				if (items.contains(ID.toString())) {
					items.remove(ID);
				}
				String s = "";
				for (String item : items) {
					s = item + ",";
				}

				query = "UPDATE playerdata SET viewed_reports = '" + s
						+ "' WHERE uuid = '" + r.getString(1) + "';";
				executeUpdate(query);
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		reloadReports();
	}

	public void setReportAttribute(Report report, ReportAttribute attribute) {
		String query = "UPDATE reports SET " + attribute.toString() + " = '" + report.getAttribute(attribute)
				+ "' WHERE id = " + report.getAttribute(ReportAttribute.ID) + ";";
		executeUpdate(query);
		reloadReports();
	}

	public void setPlayerAttribute(PlayerData playerData, PlayerDataType type) {
		String query = "UPDATE playerdata SET " + type.toString() + " = '" + playerData.getAttribute(type)
				+ "' WHERE uuid = '" + playerData.getAttribute(PlayerDataType.UUID) + "';";
		executeUpdate(query);
		reloadData();
	}

	public Integer getNextID() {
		Integer id = 0;
		for (Integer i : reports.keySet()) {
			id = i;
		}
		return id + 1;
	}

	public void reloadData() {
		ResultSet c = executeQuery("SELECT * FROM playerdata;");
		try {
			while (c.next()) {
				ArrayList<Object> data = new ArrayList<Object>();
				for (int i = 1; i <= PlayerDataType.values().length; i++) {
					data.add(c.getObject(i));
				}
				playerdata.put(UUID.fromString(c.getString(1)), new PlayerData(data));
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (!playerdata.containsKey(p.getUniqueId())) {

				playerdata.put(p.getUniqueId(), new PlayerData(p));
				String types = StringUtils.join(PlayerDataType.values(), ',');
				String valueList = "";
				for (PlayerDataType dataType : PlayerDataType.values()) {
					if (dataType.next() != null)
						valueList = valueList + "'" + dataType.getAttribute(p) + "', ";
					else
						valueList = valueList + "'" + dataType.getAttribute(p) + "'";
				}

				String sql = "INSERT INTO playerdata(" + types + ") VALUES (" + valueList + ");";
				executeUpdate(sql);
			} else {

				String valueList = "";
				for (PlayerDataType dataType : PlayerDataType.values()) {
					if (dataType.next() != null)
						valueList = valueList + dataType.toString() + " = '" + dataType.getAttribute(p) + "', ";
					else
						valueList = valueList + dataType.toString() + " = '" + dataType.getAttribute(p) + "'";
				}

				String sql = "UPDATE playerdata SET " + valueList + " WHERE uuid = '" + p.getUniqueId().toString()
						+ "';";
				executeUpdate(sql);
			}
		}
	}

	public HashMap<Integer, Report> getReports() {
		return reports;
	}

	public HashMap<UUID, PlayerData> getPlayerData() {
		return playerdata;
	}

}
