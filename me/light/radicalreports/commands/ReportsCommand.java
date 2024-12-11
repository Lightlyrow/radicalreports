package me.light.radicalreports.commands;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.light.radicalreports.Glow;
import me.light.radicalreports.Main;
import me.light.radicalreports.config.Config;
import me.light.radicalreports.config.ConfigSetting;
import me.light.radicalreports.config.ItemBuilder;
import me.light.radicalreports.config.LanguageMessageHandler;
import me.light.radicalreports.config.PlayerDataType;
import me.light.radicalreports.config.Utils;
import me.light.radicalreports.report.PlayerData;
import me.light.radicalreports.report.Report;
import me.light.radicalreports.report.ReportAttribute;
import me.light.radicalreports.report.ReportStatus;

public class ReportsCommand implements CommandExecutor, Listener {
	YamlConfiguration data = YamlConfiguration
			.loadConfiguration(new File(Main.getPlugin().getDataFolder(), "data.yml"));
	File dataF = new File(Main.getPlugin().getDataFolder(), "data.yml");
	private static HashMap<Player, Integer> pages = new HashMap<Player, Integer>();

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(
					LanguageMessageHandler.parseLanguageMessage("console-not-allowed-to-use-commands", null));
			return true;
		}
		Player p = (Player) sender;
		if (!p.hasPermission((String) ConfigSetting.REPORTS_COMMAND_PERMISSION.getSetting())) {
			sender.sendMessage(LanguageMessageHandler.parseLanguageMessage("reports-command-no-permission", null));
			return true;
		}
		if (args.length == 0) {
			pages.put(p, 1);
			setupReportsMain(p);
			return true;
		}
		if (args[0].equalsIgnoreCase("reload")) {
			Config.reloadConfig();
			Main.getDB().reloadReports();
			sender.sendMessage(LanguageMessageHandler.parseLanguageMessage("reloaded-plugin", null));
			return true;
		}
		sender.sendMessage(LanguageMessageHandler.parseLanguageMessage("reports-command-invalid-arguments", null));
		return true;
	}

	@EventHandler
	public void on(PlayerQuitEvent e) {
		if (pages.containsKey(e.getPlayer()))
			pages.remove(e.getPlayer());
	}

	private void setupReportsMain(Player p) {
		Inventory inv = Bukkit.getServer().createInventory(null, 54,
				LanguageMessageHandler.parseLanguageMessage("reports-command-main-inventory-name", null));

		ItemStack titleItem = new ItemBuilder(Material.BOOK, 1)
				.setName(LanguageMessageHandler.parseLanguageMessage("reports-command-main-item-name", null))
				.toItemStack();

		inv.setItem(4, titleItem);

		// ItemStack reportsHistoryItem = new ItemBuilder(Material.BOOKSHELF, 1)
		// .setName(LanguageMessageHandler.parseLanguageMessage("reports-command-history-item-name",
		// null))
		// .toItemStack();
		//
		// inv.setItem(8, reportsHistoryItem);

		for (int i = 9; i < 18; i++) {
			if (i == 13) {
				inv.setItem(i,
						new ItemBuilder(Material.STICK, 1)
								.setName(LanguageMessageHandler.parseLanguageMessage("staff-mode-enable-item", null))
								.toItemStack());
			} else {
				inv.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE, 1).setDurability((short) 5).setName(" ")
						.toItemStack());
			}
		}

		for (int i = 45; i < 54; i++) {
			if (i == 47) {
				if (pages.get(p) == 1) {
					inv.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE, 1).setDurability((short) 5).setName(" ")
							.toItemStack());
				} else
					inv.setItem(i,
							new ItemBuilder(Material.ARROW)
									.setName(LanguageMessageHandler
											.parseLanguageMessage("reports-command-previous-page-name", null))
									.toItemStack());
			} else if (i == 49) {
				inv.setItem(i,
						new ItemBuilder(Material.BEDROCK)
								.setName(LanguageMessageHandler
										.parseLanguageMessage("reports-command-close-inventory-name", null))
								.toItemStack());
			} else if (i == 51) {
				inv.setItem(i,
						new ItemBuilder(Material.ARROW).setName(
								LanguageMessageHandler.parseLanguageMessage("reports-command-next-page-name", null))
								.toItemStack());
			} else {
				inv.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE, 1).setDurability((short) 5).setName(" ")
						.toItemStack());
			}
		}

		addReportsToInventory(inv, p);

		p.openInventory(inv);
	}

	public static void addReportsToInventory(Inventory inv, Player p) {
		for (int i = 18; i < 45; i++) {
			inv.setItem(i, new ItemStack(Material.AIR));
		}

		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		new BukkitRunnable() {

			@Override
			public void run() {
				for (Report report : Main.getDB().getReports().values()) {
					OfflinePlayer reporter = Bukkit.getOfflinePlayer(
							UUID.fromString((String) report.getAttribute(ReportAttribute.REPORTERUUID)));
					OfflinePlayer reported = Bukkit.getOfflinePlayer(
							UUID.fromString((String) report.getAttribute(ReportAttribute.REPORTEDUUID)));
					ArrayList<String> lore = new ArrayList<String>();
					lore.add("");
					lore.add(LanguageMessageHandler.parseLanguageMessage("reports-command-report-reporter",
							Utils.createHashMap("%reporter%", reporter.getName(), "%status%", reporter.isOnline()
									? LanguageMessageHandler.parseLanguageMessage("player-status-online", null)
									: LanguageMessageHandler.parseLanguageMessage("player-status-offline", null))));
					lore.add(LanguageMessageHandler.parseLanguageMessage("reports-command-report-reported",
							Utils.createHashMap("%reported%", reported.getName(), "%status%", reported.isOnline()
									? LanguageMessageHandler.parseLanguageMessage("player-status-online", null)
									: LanguageMessageHandler.parseLanguageMessage("player-status-offline", null))));
					lore.add(LanguageMessageHandler.parseLanguageMessage("reports-command-report-reason",
							Utils.createHashMap("%reason%", (String) report.getAttribute(ReportAttribute.REASON))));
					lore.add(LanguageMessageHandler.parseLanguageMessage("reports-command-status",
							Utils.createHashMap("%status%", (String) report.getAttribute(ReportAttribute.STATUS))));
					lore.add("");
					lore.add(LanguageMessageHandler.parseLanguageMessage("reports-command-report-click-to-open", null));
					lore.add(
							LanguageMessageHandler.parseLanguageMessage("reports-command-report-drop-to-delete", null));
					if (((String) report.getAttribute(ReportAttribute.STATUS)).equalsIgnoreCase(ReportStatus.GUILTY.getType())) {
						items.add(new ItemBuilder(Material.LAVA_BUCKET).setName(ChatColor.GRAY + "Report "
								+ ChatColor.DARK_RED + "#" + report.getAttribute(ReportAttribute.ID)).setLore(lore)
								.toItemStack());
					} else {
						items.add(
								new ItemBuilder(Material.PAPER).setName(ChatColor.GRAY + "Report " + ChatColor.DARK_RED
										+ "#" + report.getAttribute(ReportAttribute.ID)).setLore(lore).toItemStack());
					}
				}

				if (items.isEmpty())
					return;
				int page = pages.get(p);
				int entries = 27;
				int index = (page - 1) * entries;
				int endIndex = index + entries;
				if (endIndex > items.size())
					endIndex = items.size();
				try {
					for (ItemStack item : items.subList(index, endIndex)) {
						inv.setItem((index % 27) + 18, item);
						index++;
					}
				} catch (IllegalArgumentException e) {

				}
			}
		}.runTaskLater(Main.getPlugin(), 1L);
	}

	@SuppressWarnings("unchecked")
	@EventHandler
	public void on(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		if (e.getClickedInventory() == null)
			return;
		if (e.getInventory().getTitle().equalsIgnoreCase(
				LanguageMessageHandler.parseLanguageMessage("reports-command-main-inventory-name", null))) {
			e.setCancelled(true);
			if (e.getClickedInventory().getTitle().equalsIgnoreCase(
					LanguageMessageHandler.parseLanguageMessage("reports-command-main-inventory-name", null))) {
				if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR)
					return;
				if (e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().hasDisplayName()) {
					ItemStack i = e.getCurrentItem();
					if (i.getType() == Material.BEDROCK
							&& i.getItemMeta().getDisplayName().equalsIgnoreCase(LanguageMessageHandler
									.parseLanguageMessage("reports-command-close-inventory-name", null))) {
						p.closeInventory();
					}
					if ((i.getType() == Material.PAPER) || (i.getType() == Material.LAVA_BUCKET)) {
						Integer ID = Integer.valueOf(ChatColor.stripColor(i.getItemMeta().getDisplayName()
								.replaceAll(ChatColor.GRAY + "Report " + ChatColor.DARK_RED + "#", "")));
						Report report = Main.getDB().getReports().get(ID);
						if (e.getClick() == ClickType.DROP) {
							Main.getDB().removeReport(ID);
							addReportsToInventory(e.getClickedInventory(), p);
						} else {
							p.closeInventory();
							// Create the inventory for a specific report ID
							Inventory inv = Bukkit.createInventory(null, 54, ChatColor.DARK_GRAY + "Report "
									+ ChatColor.DARK_RED + "#" + report.getAttribute(ReportAttribute.ID));

							setupSpecificReportInventory(inv, report);
							try {
								Main.getDB().getPlayerData().get(p.getUniqueId()).setAttribute(
										PlayerDataType.VIEWED_REPORTS,
										PlayerDataType.VIEWED_REPORTS.getAttributeFromDB(p) + ","
												+ report.getAttribute(ReportAttribute.ID));
							} catch (SQLException e1) {
								e1.printStackTrace();
							}
							p.openInventory(inv);
						}
					}
					if (i.getType() == Material.ARROW && i.getItemMeta().getDisplayName().equalsIgnoreCase(
							LanguageMessageHandler.parseLanguageMessage("reports-command-next-page-name", null))) {
						pages.put(p, pages.get(p) + 1);

						setupReportsMain(p);
					}
					if (i.getType() == Material.ARROW && i.getItemMeta().getDisplayName().equalsIgnoreCase(
							LanguageMessageHandler.parseLanguageMessage("reports-command-previous-page-name", null))) {
						pages.put(p, pages.get(p) - 1);

						setupReportsMain(p);
					}
					if (i.getType() == Material.STICK && i.getItemMeta().getDisplayName().equalsIgnoreCase(
							LanguageMessageHandler.parseLanguageMessage("staff-mode-enable-item", null))) {
						p.performCommand(ConfigSetting.STAFF_MODE_CUSTOM_COMMAND.getSetting().toString().replace("/", ""));
					}

				}
			}
		} else if (e.getInventory().getTitle().contains(ChatColor.DARK_GRAY + "Report " + ChatColor.DARK_RED + "#")) {
			e.setCancelled(true);
			if (e.getClickedInventory().getTitle()
					.contains(ChatColor.DARK_GRAY + "Report " + ChatColor.DARK_RED + "#")) {
				if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR)
					return;
				if (e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().hasDisplayName()) {
					ItemStack i = e.getCurrentItem();

					if (i.getType() == Material.BEDROCK
							&& i.getItemMeta().getDisplayName().equalsIgnoreCase(LanguageMessageHandler
									.parseLanguageMessage("reports-command-close-inventory-name", null))) {
						p.closeInventory();
					}
					if (i.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GOLD + "Reports")
							&& i.getItemMeta().hasLore() && i.getItemMeta().getLore().get(0).equalsIgnoreCase(
									LanguageMessageHandler.parseLanguageMessage("view-reports-go-back-name", null))) {
						p.closeInventory();
						pages.put(p, 1);
						setupReportsMain(p);
					}
					Inventory inv = e.getClickedInventory();
					Report report = Main.getDB().getReports()
							.get(Integer.valueOf(ChatColor.stripColor(e.getClickedInventory().getTitle()
									.replaceAll(ChatColor.DARK_GRAY + "Report " + ChatColor.DARK_RED + "#", ""))));
					if (i.getType() == Material.FEATHER
							&& i.getItemMeta().getDisplayName().equalsIgnoreCase(LanguageMessageHandler
									.parseLanguageMessage("reports-command-report-set-status-name-undecided", null))) {
						report.setAttribute(ReportAttribute.STATUS, ReportStatus.UNDECIDED.getType());

					} else if (i.getType() == Material.FLINT_AND_STEEL
							&& i.getItemMeta().getDisplayName().equalsIgnoreCase(LanguageMessageHandler
									.parseLanguageMessage("reports-command-report-set-status-name-guilty", null))) {
						report.setAttribute(ReportAttribute.STATUS, ReportStatus.GUILTY.getType());
						if ((boolean) ConfigSetting.RUN_COMMANDS_WHEN_GUILTY.getSetting()) {
							for (String cmd : (ArrayList<String>) ConfigSetting.CONSOLE_COMMANDS_WHEN_GUILTY
									.getSetting()) {
								Bukkit.dispatchCommand(
										Bukkit.getConsoleSender(), cmd
												.replaceAll("%reported%",
														Bukkit.getOfflinePlayer(UUID.fromString((String) report
																.getAttribute(ReportAttribute.REPORTEDUUID))).getName())
												.replaceAll("%reason%",
														(String) report.getAttribute(ReportAttribute.REASON)));
							}
							for (String cmd : (ArrayList<String>) ConfigSetting.PLAYER_COMMANDS_WHEN_GUILTY
									.getSetting()) {
								p.performCommand(cmd
										.replaceAll("%reported%",
												Bukkit.getOfflinePlayer(UUID.fromString(
														(String) report.getAttribute(ReportAttribute.REPORTEDUUID)))
														.getName())
										.replaceAll("%reason%", (String) report.getAttribute(ReportAttribute.REASON)));
							}
						}

					} else if (i.getType() == Material.FLINT
							&& i.getItemMeta().getDisplayName()
									.equalsIgnoreCase(LanguageMessageHandler
											.parseLanguageMessage("reports-command-remove-report-name", null))
							&& e.getClick() == ClickType.DROP) {
						Main.getDB().removeReport(Integer.valueOf(report.getAttribute(ReportAttribute.ID).toString()));
						p.closeInventory();
						setupReportsMain(p);
					} else if (i.getType() == Material.COMPASS
							&& i.getItemMeta().getDisplayName()
									.equalsIgnoreCase(
											LanguageMessageHandler
													.parseLanguageMessage(
															"reports-command-teleport-to-last-position-name", Utils
																	.createHashMap(
																			"%reported%", Bukkit
																					.getOfflinePlayer(
																							UUID.fromString(report
																									.getAttribute(
																											ReportAttribute.REPORTEDUUID)
																									.toString()))
																					.getName())))
							&& e.getClick() == ClickType.LEFT) {
						PlayerData reportedData = Main.getDB().getPlayerData()
								.get(UUID.fromString(report.getAttribute(ReportAttribute.REPORTEDUUID).toString()));
						Integer x = Integer.valueOf(reportedData.getAttribute(PlayerDataType.X).toString());
						Integer y = Integer.valueOf(reportedData.getAttribute(PlayerDataType.Y).toString());
						Integer z = Integer.valueOf(reportedData.getAttribute(PlayerDataType.Z).toString());
						String world = reportedData.getAttribute(PlayerDataType.WORLD).toString();
						Location loc = p.getLocation();
						loc.setX(x);
						loc.setY(y);
						loc.setZ(z);
						loc.setWorld(Bukkit.getWorld(world));
						p.closeInventory();
						p.teleport(loc);
						System.out.println(loc.toString());
					}
					setupSpecificReportInventory(inv, report);

				}
			}
		}
	}

	public static void setupSpecificReportInventory(Inventory inv, Report report) {
		String goBackItemLore = LanguageMessageHandler.parseLanguageMessage("view-reports-go-back-name", null);
		inv.setItem(0, new ItemBuilder(Material.BOOK).setName(ChatColor.GOLD + "Reports")
				.setLore(Arrays.asList(goBackItemLore)).toItemStack());
		OfflinePlayer reporter = Bukkit
				.getOfflinePlayer(UUID.fromString((String) report.getAttribute(ReportAttribute.REPORTERUUID)));
		OfflinePlayer reported = Bukkit
				.getOfflinePlayer(UUID.fromString((String) report.getAttribute(ReportAttribute.REPORTEDUUID)));
		ArrayList<String> lore = new ArrayList<String>();
		lore.add("");
		lore.add(LanguageMessageHandler.parseLanguageMessage("reports-command-report-reporter",
				Utils.createHashMap("%reporter%", reporter.getName(), "%status%",
						reporter.isOnline() ? LanguageMessageHandler.parseLanguageMessage("player-status-online", null)
								: LanguageMessageHandler.parseLanguageMessage("player-status-offline", null))));
		lore.add(LanguageMessageHandler.parseLanguageMessage("reports-command-report-reported",
				Utils.createHashMap("%reported%", reported.getName(), "%status%",
						reported.isOnline() ? LanguageMessageHandler.parseLanguageMessage("player-status-online", null)
								: LanguageMessageHandler.parseLanguageMessage("player-status-offline", null))));
		lore.add(LanguageMessageHandler.parseLanguageMessage("reports-command-report-reason",
				Utils.createHashMap("%reason%", (String) report.getAttribute(ReportAttribute.REASON))));
		lore.add("");
		lore.add(LanguageMessageHandler.parseLanguageMessage("reports-command-report-date",
				Utils.createHashMap("%date%", (String) report.getAttribute(ReportAttribute.DATE))));
		lore.add(LanguageMessageHandler.parseLanguageMessage("reports-command-report-status",
				Utils.createHashMap("%status%", (String) report.getAttribute(ReportAttribute.STATUS))));
		lore.add("");
		lore.add(LanguageMessageHandler.parseLanguageMessage("reports-command-report-health",
				Utils.createHashMap("%health%", report.getAttribute(ReportAttribute.REPORTED_HEALTH).toString())));
		lore.add(LanguageMessageHandler.parseLanguageMessage("reports-command-report-gamemode",
				Utils.createHashMap("%gamemode%",
						StringUtils.capitalize((String) report.getAttribute(ReportAttribute.REPORTED_GAMEMODE)))));
		lore.add(LanguageMessageHandler.parseLanguageMessage("reports-command-report-sneaking",
				Utils.createHashMap("%sneaking%",
						StringUtils.capitalize(report.getAttribute(ReportAttribute.REPORTED_SNEAKING).toString()))));
		lore.add(LanguageMessageHandler.parseLanguageMessage("reports-command-report-sprinting",
				Utils.createHashMap("%sprinting%",
						StringUtils.capitalize(report.getAttribute(ReportAttribute.REPORTED_SPRINTING).toString()))));
		inv.setItem(22,
				new ItemBuilder(Material.BOOK_AND_QUILL)
						.setName(LanguageMessageHandler.parseLanguageMessage("reports-command-information",
								Utils.createHashMap("%reported%", reported.getName())))
						.setLore(lore).toItemStack());
		lore = new ArrayList<String>();
		lore.add(LanguageMessageHandler.parseLanguageMessage("reports-command-teleport-to-last-position-lore", null));
		inv.setItem(26, new ItemBuilder(Material.COMPASS)
				.setName(LanguageMessageHandler.parseLanguageMessage("reports-command-teleport-to-last-position-name",
						Utils.createHashMap("%reported%", reported.getName())))
				.setLore(lore).toItemStack());
		lore = new ArrayList<String>();
		lore.add("");
		lore.add(LanguageMessageHandler.parseLanguageMessage("reports-command-report-reporter",
				Utils.createHashMap("%reporter%", reporter.getName(), "%status%",
						reporter.isOnline() ? LanguageMessageHandler.parseLanguageMessage("player-status-online", null)
								: LanguageMessageHandler.parseLanguageMessage("player-status-offline", null))));
		lore.add(LanguageMessageHandler.parseLanguageMessage("reports-command-report-reported",
				Utils.createHashMap("%reported%", reported.getName(), "%status%",
						reported.isOnline() ? LanguageMessageHandler.parseLanguageMessage("player-status-online", null)
								: LanguageMessageHandler.parseLanguageMessage("player-status-offline", null))));
		lore.add(LanguageMessageHandler.parseLanguageMessage("reports-command-report-reason",
				Utils.createHashMap("%reason%", (String) report.getAttribute(ReportAttribute.REASON))));
		inv.setItem(4,
				new ItemBuilder(Material.PAPER).setName(
						ChatColor.GRAY + "Report " + ChatColor.DARK_RED + "#" + report.getAttribute(ReportAttribute.ID))
						.setLore(lore).toItemStack());
		lore.add("");
		lore.add(LanguageMessageHandler.parseLanguageMessage("reports-command-report-date",
				Utils.createHashMap("%date%", (String) report.getAttribute(ReportAttribute.DATE))));
		lore.add(LanguageMessageHandler.parseLanguageMessage("reports-command-report-status",
				Utils.createHashMap("%status%", (String) report.getAttribute(ReportAttribute.STATUS))));
		inv.setItem(27,
				new ItemBuilder(Material.PAPER).setName(
						ChatColor.GRAY + "Report " + ChatColor.DARK_RED + "#" + report.getAttribute(ReportAttribute.ID))
						.setLore(lore).toItemStack());
		// Item for setting status
		if (report.getAttribute(ReportAttribute.STATUS).toString().equalsIgnoreCase(ReportStatus.UNDECIDED.getType())) {
			// Status is undecided
			inv.setItem(30,
					new ItemBuilder(Material.FEATHER)
							.setName(LanguageMessageHandler
									.parseLanguageMessage("reports-command-report-status-name-undecided", null))
							.addEnchant(new Glow(80), 1).toItemStack());
		} else {
			// Status is not undecided
			inv.setItem(30,
					new ItemBuilder(Material.FEATHER)
							.setName(LanguageMessageHandler
									.parseLanguageMessage("reports-command-report-set-status-name-undecided", null))
							.toItemStack());
		}
		if (report.getAttribute(ReportAttribute.STATUS).toString().equalsIgnoreCase(ReportStatus.GUILTY.getType())) {
			// Status is guilty
			inv.setItem(32,
					new ItemBuilder(Material.FLINT_AND_STEEL)
							.setName(LanguageMessageHandler
									.parseLanguageMessage("reports-command-report-status-name-guilty", null))
							.addEnchant(new Glow(80), 1).toItemStack());
		} else {
			// Status is not guilty
			inv.setItem(32,
					new ItemBuilder(Material.FLINT_AND_STEEL)
							.setName(LanguageMessageHandler
									.parseLanguageMessage("reports-command-report-set-status-name-guilty", null))
							.toItemStack());
		}
		inv.setItem(37, new ItemBuilder(Material.FLINT)
				.setLore(LanguageMessageHandler.parseLanguageMessage("reports-command-report-drop-to-delete", null))
				.setName(LanguageMessageHandler.parseLanguageMessage("reports-command-remove-report-name", null))
				.toItemStack());
		for (int slot = 9; slot < 18; slot++) {
			inv.setItem(slot, new ItemBuilder(Material.STAINED_GLASS_PANE, 1).setDurability((short) 5).setName(" ")
					.toItemStack());
		}

		for (int slot = 45; slot < 54; slot++) {
			if (slot == 49) {
				inv.setItem(slot,
						new ItemBuilder(Material.BEDROCK)
								.setName(LanguageMessageHandler
										.parseLanguageMessage("reports-command-close-inventory-name", null))
								.toItemStack());
			} else {
				inv.setItem(slot, new ItemBuilder(Material.STAINED_GLASS_PANE, 1).setDurability((short) 5).setName(" ")
						.toItemStack());
			}
		}
	}
}
