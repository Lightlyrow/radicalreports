package me.light.radicalreports.report;

import me.light.radicalreports.config.ConfigSetting;

public enum ReportStatus {
	UNDECIDED((String) ConfigSetting.UNDECIDED_STATUS.getSetting()), GUILTY(
			(String) ConfigSetting.GUILTY_STATUS.getSetting());

	private String type;

	private ReportStatus(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
}
