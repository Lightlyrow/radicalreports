package me.light.radicalreports.commands;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import me.light.radicalreports.Main;
import me.light.radicalreports.Sounds;
import me.light.radicalreports.Sounds.Sound_1_7;
import me.light.radicalreports.Sounds.Sound_1_9;
import me.light.radicalreports.config.ConfigSetting;
import me.light.radicalreports.config.LanguageMessageHandler;
import me.light.radicalreports.config.Utils;
import me.light.radicalreports.report.Report;
import me.light.radicalreports.report.ReportAttribute;
import me.light.radicalreports.report.ReportStatus;

public class ReportCommand implements CommandExecutor, Listener {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(
					LanguageMessageHandler.parseLanguageMessage("console-not-allowed-to-use-commands", null));
			return true;
		} else if (args.length == 0) {
			sender.sendMessage(LanguageMessageHandler.parseLanguageMessage("report-command-invalid-arguments", null));
			return true;
		}
		Player p = Bukkit.getPlayer(args[0]);
		if (p == null) {
			sender.sendMessage(LanguageMessageHandler.parseLanguageMessage("report-command-invalid-player",
					Utils.createHashMap("%player%", args[0])));
			return true;
		} else if (!Boolean.parseBoolean(((Boolean) ConfigSetting.REPORT_SELF.getSetting()).toString())
				&& p.getName().equalsIgnoreCase(sender.getName())) {
			sender.sendMessage(LanguageMessageHandler.parseLanguageMessage("report-command-cannot-report-self", null));
			return true;
		} else if (args.length == 1) {
			sender.sendMessage(LanguageMessageHandler.parseLanguageMessage("report-command-no-reason", null));
			return true;
		} else {
			Player reporter = (Player) sender;
			List<String> reasons = new ArrayList<String>();
			for (int i = 0; i < args.length; i++) {
				if (i >= 1)
					reasons.add(args[i]);
			}
			String reasonsString = StringUtils.join(reasons, " ");
			reasonsString = reasonsString.replace("\"", "\\\"").replace("'", "''");
			String loc = p.getLocation().toString();
			Report report = new Report(Main.getDB().getNextID(), ReportStatus.UNDECIDED.getType(), reasonsString,
					reporter.getUniqueId().toString(), p.getUniqueId().toString(),
					(new SimpleDateFormat("MM/dd/yyyy hh:mm:ss")).format(new Date(((new Date()).getTime() + 86400000))),
					loc, p.getHealth(), StringUtils.capitalize(p.getGameMode().toString()), p.isSneaking(),
					p.isSprinting());
			// edited
			Main.getDB().addReport(report);
			for (Player staff : Bukkit.getOnlinePlayers()) {
				if (staff.hasPermission((String) ConfigSetting.RECEIVE_REPORT_NOTIFICATIONS.getSetting())) {
					staff.sendMessage(LanguageMessageHandler.parseLanguageMessage("reported-player-message-to-staff",
							Utils.createHashMap("%reporter%", reporter.getName(), "%reported%", p.getName(), "%reason%",
									(String) report.getAttribute(ReportAttribute.REASON))));
					if ((boolean) ConfigSetting.PLAY_EFFECTS_FOR_STAFF.getSetting()) {
						if (staff != p)
							playEffects(staff);
					}
				}
			}
			if ((boolean) ConfigSetting.BUNGEECORD.getSetting()) {
				ByteArrayDataOutput out = ByteStreams.newDataOutput();
				out.writeUTF("Forward"); // So BungeeCord knows to forward it
				out.writeUTF("ALL");
				out.writeUTF("reportChannel"); // The channel name to check if
												// this your data

				ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
				DataOutputStream msgout = new DataOutputStream(msgbytes);
				try {
					msgout.writeUTF("%reporter%||" + reporter.getName() + "||%reported%||" + p.getName()
							+ "||%reason%||" + (String) report.getAttribute(ReportAttribute.REASON));
				} catch (IOException e) {
					e.printStackTrace();
				}

				out.writeShort(msgbytes.toByteArray().length);
				out.write(msgbytes.toByteArray());
				Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);

				player.sendPluginMessage(Main.getPlugin(), "BungeeCord", out.toByteArray());
			}
			reporter.sendMessage(LanguageMessageHandler.parseLanguageMessage("reported-player-message-to-reporter",
					Utils.createHashMap("%reported%", p.getName(), "%reason%",
							(String) report.getAttribute(ReportAttribute.REASON))));
			if ((boolean) ConfigSetting.PLAY_EFFECTS_FOR_REPORTER.getSetting()) {
				playEffects(reporter);
			}
		}
		return true;
	}

	@EventHandler
	public void on(InventoryClickEvent e) {
		if (e.getClickedInventory() == null || e.getCurrentItem() == null)
			return;
		if (!e.getCurrentItem().hasItemMeta())
			return;
		if (!e.getCurrentItem().getItemMeta().hasDisplayName())
			return;
	}

	public static void playEffects(Player reporter) {
		Location loc = reporter.getLocation();
		loc.setY(loc.getY() + 2);
		loc.getWorld().playEffect(loc, Effect.MOBSPAWNER_FLAMES, null);
		if (Bukkit.getVersion().contains("1.9") || Bukkit.getVersion().contains("1.10")
				|| Bukkit.getVersion().contains("1.11"))
			loc.getWorld().playSound(loc, Sounds.getSound(Sound_1_9.ENTITY_CAT_HISS), 0.5F, 1);
		else
			loc.getWorld().playSound(loc, Sounds.getSound(Sound_1_7.CAT_HISS), 0.5F, 1);
	}
}
