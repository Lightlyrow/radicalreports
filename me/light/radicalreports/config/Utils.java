package me.light.radicalreports.config;

import java.util.HashMap;

public class Utils {

	public static HashMap<String, String> createHashMap(String k1, String v1) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(k1, v1);
		return map;
	}

	public static HashMap<String, String> createHashMap(String k1, String v1, String k2, String v2) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(k1, v1);
		map.put(k2, v2);
		return map;
	}

	public static HashMap<String, String> createHashMap(String k1, String v1, String k2, String v2, String k3, String v3) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(k1, v1);
		map.put(k2, v2);
		map.put(k3, v3);
		return map;
	}
}
