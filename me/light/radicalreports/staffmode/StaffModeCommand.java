package me.light.radicalreports.staffmode;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.light.radicalreports.config.ConfigSetting;
import me.light.radicalreports.config.LanguageMessageHandler;

public class StaffModeCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(
					LanguageMessageHandler.parseLanguageMessage("console-not-allowed-to-use-commands", null));
			return true;
		}
		Player p = (Player) sender;
		if (args.length == 0) {
			ArrayList<Player> members = new ArrayList<Player>();
			for (Player staff : Bukkit.getOnlinePlayers()) {
				if (staff.hasPermission((String) ConfigSetting.REPORTS_COMMAND_PERMISSION.getSetting())) {
					members.add(staff);
				}
			}
			if (!members.isEmpty()) {
				p.sendMessage(LanguageMessageHandler.parseLanguageMessage("staff-online-message", null));
				for (Player staff : members) {
					p.sendMessage(ChatColor.GRAY + staff.getName());
				}
			} else
				p.sendMessage(LanguageMessageHandler.parseLanguageMessage("no-staff-online-message", null));
			return true;
		} else if (args[0].equalsIgnoreCase("mode")) {
			if (!p.hasPermission((String) ConfigSetting.STAFF_MODE_ALLOWED.getSetting())) {
				sender.sendMessage(LanguageMessageHandler.parseLanguageMessage("staff-no-permission", null));
				return true;
			}
			if (StaffMode.enableStaffMode(p)) {
				p.sendMessage(LanguageMessageHandler.parseLanguageMessage("enabled-staff-mode-message", null));
			} else {
				StaffMode.disableStaffMode(p);
				p.sendMessage(LanguageMessageHandler.parseLanguageMessage("disabled-staff-mode-message", null));
			}
			return true;
		}
		sender.sendMessage(LanguageMessageHandler.parseLanguageMessage("staff-command-invalid-arguments", null));
		return true;
	}

}
