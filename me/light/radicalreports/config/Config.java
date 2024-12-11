package me.light.radicalreports.config;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import me.light.radicalreports.Main;

public class Config {
	private static File pluginFolder;
	private static File reportsFolder;
	private static File config = new File(Main.getPlugin().getDataFolder().toString(), "config.yml");
	private static File langMessage = new File(Main.getPlugin().getDataFolder().toString(), "lang.yml");
	private static YamlConfiguration yamlConfig;
	private static YamlConfiguration yamllangMessage;
	public static HashMap<String, Object> configValues = new HashMap<String, Object>();
	

	public static void reloadConfig() {
		if (pluginFolder == null) {
			pluginFolder = new File(Main.getPlugin().getDataFolder().toString());
			pluginFolder.mkdirs();
		}

		if (!config.exists()) {
			try {
				config.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (!langMessage.exists()) {
			try {
				langMessage.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		yamlConfig = YamlConfiguration.loadConfiguration(new File(Main.getPlugin().getDataFolder().toString(), "config.yml"));
		yamllangMessage = YamlConfiguration.loadConfiguration(new File(Main.getPlugin().getDataFolder().toString(), "lang.yml"));
		
		LanguageMessageHandler.reloadLanguageMessages();
		for (ConfigSetting setting : ConfigSetting.values()) {
			setConfigSettingIfNotSet(setting.getKey(), setting.getValue());
		}
		
		try {
			getConfig().save(getConfigFile());
			Bukkit.getLogger().log(Level.INFO, "Loaded configuration file.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void setConfigSettingIfNotSet(String key, Object value) {
		if (!getConfig().isSet(key))
			setConfigSetting(key, value);
		else
			configValues.put(key, getConfig().get(key));
	}

	public static void setConfigSetting(String key, Object value) {
		getConfig().set(key, value);
		configValues.put(key, value);
	}

	public static File getReportsFolder() {
		return reportsFolder;
	}

	public static YamlConfiguration getConfig() {
		return yamlConfig;
	}

	public static YamlConfiguration getLangConfig() {
		return yamllangMessage;
	}

	public static File getConfigFile() {
		return config;
	}

	public static File getLangFile() {
		return langMessage;
	}
	
}
