package me.light.radicalreports;

import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import me.light.radicalreports.commands.ReportCommand;
import me.light.radicalreports.commands.ReportsCommand;
import me.light.radicalreports.config.Config;
import me.light.radicalreports.config.ConfigSetting;
import me.light.radicalreports.config.LanguageMessageHandler;
import me.light.radicalreports.config.Utils;
import me.light.radicalreports.report.ReportManager;
import me.light.radicalreports.report.databases.Database;
import me.light.radicalreports.staffmode.StaffMode;
import me.light.radicalreports.staffmode.StaffModeCommand;
import me.light.radicalreports.stats.MetricsLite;
import net.milkbowl.vault.permission.Permission;

public class Main extends JavaPlugin implements Listener, PluginMessageListener {
	private static PluginManager manager;
	private static ReportCommand reportCommand;
	private static ReportsCommand reportsCommand;
	private static PlayerDataListener playerDataListener;
	private static StaffMode staffmode;
	private static StaffModeCommand staffModeCommand;
	private static JavaPlugin plugin;
	private static boolean permsEnabled = false;
	public static Permission perms = null;
	private static Database database;

	@Override
	public void onEnable() {
		plugin = this;
		manager = Bukkit.getServer().getPluginManager();
		reportCommand = new ReportCommand();
		reportsCommand = new ReportsCommand();
		playerDataListener = new PlayerDataListener();
		playerDataListener = new PlayerDataListener();
		staffmode = new StaffMode();
		staffModeCommand = new StaffModeCommand();
		Config.reloadConfig();
		getCommand("report").setExecutor(reportCommand);
		getCommand("reports").setExecutor(reportsCommand);
		getCommand("staff").setExecutor(staffModeCommand);
		if (setupPermissions()) {
			permsEnabled = true;
			getLogger().info("Vault found, enabling permissions integration.");
		} else
			getLogger().info("Vault not found, disabling permissions integration.");
		setupPermissions();
		manager.registerEvents(reportsCommand, plugin);
		manager.registerEvents(playerDataListener, plugin);
		manager.registerEvents(staffmode, plugin);
		registerGlow();
		ReportManager.setup();
		if ((boolean) ConfigSetting.BUNGEECORD.getSetting()) {
			this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
			this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);
		}
		new MetricsLite();
		
		StaffMode.setup();

	}

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if (!channel.equals("BungeeCord")) {
			return;
		}
		ByteArrayDataInput in = ByteStreams.newDataInput(message);
		String subchannel = in.readUTF();
		if (subchannel.equals("reportChannel")) {
			for (Player staff : Bukkit.getOnlinePlayers()) {
				if (staff.hasPermission((String) ConfigSetting.RECEIVE_REPORT_NOTIFICATIONS.getSetting())) {
					in.readShort();
					String[] info = in.readUTF().split("\\|\\|");
					String reporterPlaceholder = info[0];
					String reporterName = info[1];
					String reportedPlaceholder = info[2];
					String reportedName = info[3];
					String reasonPlaceholder = info[4];
					String reasonName = info[5];

					staff.sendMessage(LanguageMessageHandler.parseLanguageMessage("reported-player-message-to-staff",
							Utils.createHashMap(reporterPlaceholder, reporterName, reportedPlaceholder, reportedName,
									reasonPlaceholder, reasonName)));
					if ((boolean) ConfigSetting.PLAY_EFFECTS_FOR_STAFF.getSetting()) {
						ReportCommand.playEffects(staff);
					}
				}
			}

			Main.getDB().reloadReports();
			Main.getDB().reloadData();

		}
	}

	public void registerGlow() {
		try {
			Field f = Enchantment.class.getDeclaredField("acceptingNew");
			f.setAccessible(true);
			f.set(null, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			Glow glow = new Glow(70);
			Enchantment.registerEnchantment(glow);
		} catch (IllegalArgumentException e) {
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDisable() {
		Main.getDB().close();
	}

	private boolean setupPermissions() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
		perms = rsp.getProvider();
		return perms != null;
	}

	public static boolean getPermsEnabled() {
		return permsEnabled;
	}

	public static JavaPlugin getPlugin() {
		return plugin;
	}

	public static PluginManager getManager() {
		return manager;
	}

	public static Database getDB() {
		return database;
	}

	public static void setDatabase(Database d) {
		database = d;
	}
}
