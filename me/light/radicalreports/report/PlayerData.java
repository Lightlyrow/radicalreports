package me.light.radicalreports.report;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.entity.Player;

import me.light.radicalreports.Main;
import me.light.radicalreports.config.PlayerDataType;

public class PlayerData {

	private HashMap<PlayerDataType, Object> types = new HashMap<PlayerDataType, Object>();

	public PlayerData(Player p) {
		types.put(PlayerDataType.UUID, p.getUniqueId().toString());
		types.put(PlayerDataType.GAMEMODE,
				p.getGameMode().toString().substring(0, 1).toUpperCase() + p.getGameMode().toString().substring(1));
		types.put(PlayerDataType.SNEAKING, p.isSneaking());
		types.put(PlayerDataType.SPRINTING, p.isSprinting());
		types.put(PlayerDataType.HEALTH, ((Double) p.getHealth()).toString());
		types.put(PlayerDataType.IP, p.getAddress().getHostName());
		types.put(PlayerDataType.X, p.getLocation().getBlockX());
		types.put(PlayerDataType.Y, p.getLocation().getBlockY());
		types.put(PlayerDataType.Z, p.getLocation().getBlockZ());
		types.put(PlayerDataType.WORLD, p.getLocation().getWorld().getName());
		try {
			types.put(PlayerDataType.VIEWED_REPORTS, PlayerDataType.VIEWED_REPORTS.getAttributeFromDB(p));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public PlayerData(ArrayList<Object> data) {
		int i = 0;
		for (PlayerDataType type : PlayerDataType.values()) {
			types.put(type, data.get(i));
			i++;
		}
	}

	public Object getAttribute(PlayerDataType type) {
		return types.get(type) == null ? null : types.get(type);
	}

	public void setAttribute(PlayerDataType type, Object value) {
		this.types.replace(type, value);
		Main.getDB().setPlayerAttribute(this, type);
	}
}
