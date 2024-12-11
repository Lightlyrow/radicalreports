package me.light.radicalreports.report;

public enum ReportAttribute {
	ID, STATUS, REASON, REPORTERUUID, REPORTEDUUID, DATE, REPORTED_LOCATION, REPORTED_HEALTH, REPORTED_GAMEMODE, REPORTED_SNEAKING, REPORTED_SPRINTING {
		@Override
		public ReportAttribute next() {
			return null;
		};
	};

	public ReportAttribute next() {
		return values()[ordinal() + 1];
	}
	
	@Override
	public String toString() {
		return this.name().toLowerCase();
	}
}
