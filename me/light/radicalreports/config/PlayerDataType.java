package me.light.radicalreports.config;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

import me.light.radicalreports.Main;

public enum PlayerDataType {
	UUID {
		@Override
		public Object getAttribute(Player p) {
			return p.getUniqueId().toString();
		}
	},
	GAMEMODE {
		@Override
		public Object getAttribute(Player p) {
			return StringUtils.capitalize(p.getGameMode().toString().toLowerCase());
		}
	},
	SNEAKING {
		@Override
		public Object getAttribute(Player p) {
			return p.isSneaking();
		}
	},
	SPRINTING {
		@Override
		public Object getAttribute(Player p) {
			return p.isSprinting();
		}
	},
	HEALTH {
		@Override
		public Object getAttribute(Player p) {
			return ((Double) p.getHealth()).toString();
		}
	},
	IP {
		@Override
		public Object getAttribute(Player p) {
			return p.getAddress().getHostName();
		}
	},
	X {
		@Override
		public Object getAttribute(Player p) {
			return p.getLocation().getBlockX();
		}
	},
	Y {
		@Override
		public Object getAttribute(Player p) {
			return p.getLocation().getBlockY();
		}
	},
	Z {
		@Override
		public Object getAttribute(Player p) {
			return p.getLocation().getBlockZ();
		}
	},
	WORLD {
		@Override
		public Object getAttribute(Player p) {
			return p.getLocation().getWorld().getName();
		}

	},
	VIEWED_REPORTS {
		@Override
		public Object getAttribute(Player p) {
			ResultSet r = Main.getDB().executeQuery(
					"SELECT " + this.toString() + " from playerdata WHERE uuid = '" + p.getUniqueId().toString() + "';");

			try {
				r.next();
				return r.getObject(1);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return r;
		}

		@Override
		public PlayerDataType next() {
			return null;
		};
	};

	public PlayerDataType next() {
		return values()[ordinal() + 1];
	}

	@Override
	public String toString() {
		return this.name().toLowerCase();
	}

	public Object getAttribute(Player p) {
		return this.getAttribute(p);
	}

	public Object getAttributeFromDB(Player p) throws SQLException {
		ResultSet r = Main.getDB().executeQuery(
				"SELECT " + this.toString() + " from playerdata WHERE uuid = '" + p.getUniqueId().toString() + "';");

		r.next();
		return r.getObject(1);
	}

}
