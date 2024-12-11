package me.light.radicalreports.report;

import java.util.ArrayList;
import java.util.HashMap;

import me.light.radicalreports.Main;

public class Report {
	private HashMap<ReportAttribute, Object> attributes = new HashMap<ReportAttribute, Object>();

	public Report(Integer ID, String status, String reason, String reporterUUID, String reportedUUID,
			String date, String location, Double health, String gamemode, Boolean sneaking, Boolean sprinting) {
		attributes.put(ReportAttribute.ID, ID);
		attributes.put(ReportAttribute.STATUS, status);
		attributes.put(ReportAttribute.REASON, reason);
		attributes.put(ReportAttribute.REPORTERUUID, reporterUUID);
		attributes.put(ReportAttribute.REPORTEDUUID, reportedUUID);
		attributes.put(ReportAttribute.DATE, date);
		attributes.put(ReportAttribute.REPORTED_LOCATION, location);
		attributes.put(ReportAttribute.REPORTED_HEALTH, health);
		attributes.put(ReportAttribute.REPORTED_GAMEMODE, gamemode);
		attributes.put(ReportAttribute.REPORTED_SNEAKING, sneaking);
		attributes.put(ReportAttribute.REPORTED_SPRINTING, sprinting);
	}

	public Report(ArrayList<Object> data) {
		int i = 0;
		for (ReportAttribute type : ReportAttribute.values()) {
			attributes.put(type, data.get(i));
			i++;
		}
	}

	public Object getAttribute(ReportAttribute attribute) {
		return attributes.get(attribute);
	}
	
	public void setAttribute(ReportAttribute attribute, Object value) {
		this.attributes.replace(attribute, value);
		Main.getDB().setReportAttribute(this, attribute);
	}

}
