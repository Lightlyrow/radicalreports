package me.light.radicalreports.staffmode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.light.radicalreports.Main;
import me.light.radicalreports.config.ConfigSetting;
import me.light.radicalreports.config.ItemBuilder;
import me.light.radicalreports.config.LanguageMessageHandler;
import me.light.radicalreports.config.Utils;

public class StaffMode implements Listener {
	private static HashSet<Player> players = new HashSet<Player>();
	private static HashMap<Player, Integer> count = new HashMap<Player, Integer>();

	public static boolean isPlayerInStaffMode(Player p) {
		return players.contains(p);
	}

	public static boolean disableStaffMode(Player p) {
		if (players.remove(p)) {

			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (String) ConfigSetting.VANISH_DISABLE_COMMAND.getSetting()
					.toString().replace("/", "").replace("%player%", p.getName()));
			p.getInventory().clear();

			return true;
		} else {
			return false;
		}
	}

	public static boolean enableStaffMode(Player p) {
		if (players.add(p)) {

			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (String) ConfigSetting.VANISH_ENABLE_COMMAND.getSetting()
					.toString().replace("/", "").replace("%player%", p.getName()));
			p.getInventory().clear();
			p.closeInventory();
			Inventory inv = p.getInventory();
			// Add disable staff mode item, etc
			inv.setItem(0,
					new ItemBuilder(Material.STICK, 1)
							.setName(LanguageMessageHandler.parseLanguageMessage("disable-staff-mode-item-name", null))
							.toItemStack());

			inv.setItem(1, new ItemBuilder(Material.FLINT, 1)
					.setName(ChatColor.translateAlternateColorCodes('&', (String) ConfigSetting.STAFF_MODE_CUSTOM_ITEM_NAME.getSetting())).toItemStack());
			inv.setItem(2, new ItemBuilder(Material.COMPASS, 1)
					.setName(
							LanguageMessageHandler.parseLanguageMessage("teleport-to-where-you-are-looking-name", null))
					.toItemStack());
			inv.setItem(3,
					new ItemBuilder(Material.ARROW, 1).setName(
							LanguageMessageHandler.parseLanguageMessage("teleport-to-a-random-player-name", null))
							.toItemStack());
			return true;
		} else {
			return false;
		}
	}

	@EventHandler
	public void on(PlayerInteractEvent e) {
		if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
			count.put(e.getPlayer(), count.containsKey(e.getPlayer()) ? count.get(e.getPlayer()) + 1 : 1);
		}
		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (isPlayerInStaffMode(e.getPlayer())) {
				if (e.getItem() == null || e.getItem().getType() == Material.AIR)
					return;
				staffModeClick(e.getPlayer(), e.getItem());
			}
		}
	}

	private void staffModeClick(Player p, ItemStack i) {

		if (i.hasItemMeta() && i.getItemMeta().hasDisplayName()) {

			if (i.getItemMeta().getDisplayName().equalsIgnoreCase(
					LanguageMessageHandler.parseLanguageMessage("disable-staff-mode-item-name", null))) {
				disableStaffMode(p);
			}
			if (i.getItemMeta().getDisplayName()
					.equalsIgnoreCase((String) ConfigSetting.STAFF_MODE_CUSTOM_ITEM_NAME.getSetting())) {
				p.performCommand((String) ConfigSetting.STAFF_MODE_CUSTOM_ITEM_COMMAND.getSetting());
			}
			if (i.getItemMeta().getDisplayName().equalsIgnoreCase(
					LanguageMessageHandler.parseLanguageMessage("teleport-to-where-you-are-looking-name", null))) {
				Set<Material> blockIDs = new HashSet<Material>();
				blockIDs.add(Material.AIR);
				Location loc = p.getTargetBlock((Set<Material>) blockIDs, 100).getLocation().add(0, 1, 0);
				loc.setPitch(p.getLocation().getPitch());
				loc.setYaw(p.getLocation().getYaw());
				p.teleport(loc);
			}
			if (i.getItemMeta().getDisplayName().equalsIgnoreCase(
					LanguageMessageHandler.parseLanguageMessage("teleport-to-a-random-player-name", null))) {
				ArrayList<Player> players = new ArrayList<Player>();
				for (Player e : Bukkit.getOnlinePlayers())
					players.add(e);
				Player randomPlayer = players.get(new Random().nextInt(players.size()));
				p.teleport(randomPlayer.getLocation());
			}
		}
	}

	@EventHandler
	public void on(PlayerDropItemEvent e) {
		if (isPlayerInStaffMode(e.getPlayer())) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void on(PlayerQuitEvent e) {
		count.remove(e.getPlayer());
		disableStaffMode(e.getPlayer());
	}

	public static void setup() {
		new BukkitRunnable() {
			@Override
			public void run() {
				if ((Integer) ConfigSetting.CLICKS_PER_SECOND_COUNTER.getSetting() > 1) {
					Iterator<Entry<Player, Integer>> it = count.entrySet().iterator();
					while (it.hasNext()) {
						Entry<Player, Integer> item = it.next();
						if ((item.getValue() % 20) > (Integer) ConfigSetting.CLICKS_PER_SECOND_COUNTER.getSetting()) {
							for (Player staff : Bukkit.getOnlinePlayers()) {
								if (isPlayerInStaffMode(staff)) {
									staff.sendMessage(LanguageMessageHandler.parseLanguageMessage(
											"clicks-per-second-warning-message", Utils.createHashMap("%player%",
													item.getKey().getName(), "%count%", item.getValue().toString())));
								}
							}
						}
						it.remove();
					}
				}
			}
		}.runTaskTimer(Main.getPlugin(), 1L, 20L);
	}

}
