package entityManager.commands;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import entityManager.EntityManager;
import entityManager.SubCommand;
import entityManager.Utils;

public class LookupEntity extends SubCommand {
	
	public LookupEntity(EntityManager entityManager) {
		super(entityManager, "lookup");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			plugin.fail(sender, "That's a player only command.");
			return;
		}

		Player p = (Player) sender;
		List<Player> playersInWorld = p.getWorld().getPlayers();

		if (args.length < 2) {
			sender.sendMessage("Usage: /em lookup" + usage());
			return;
		}

		if (args.length > 1) {
			String input = args[1].toUpperCase();
			EntityType toSearch = null;

			try {
				toSearch = EntityType.valueOf(input);
			} catch (IllegalArgumentException e) {
				plugin.fail(sender, "Sorry, you cannot lookup that entity.");
				return;
			}

			HashMap<Player, Integer> entityList = new HashMap<Player, Integer>();
			HashMap<Player, Location> locMap = new HashMap<Player, Location>();

			for (Player player : playersInWorld) {
				List<Entity> nearby = player.getNearbyEntities(200, 150, 200);
				for (Entity e : nearby) {
					if (e.getType() == toSearch) {
						count(player, entityList);
						locMap.put(player, e.getLocation());
					}
				}
			}

			entityList = sort(entityList);
			String lookedUp = toSearch.toString().toLowerCase().replace("_", " ");
			if (entityList.size() == 0) {
				sender.sendMessage("No entities of that type were found.");
			} else {
				sender.sendMessage("Player(s) with the most " + lookedUp + "s:");
			}

			int limit = 0;
			for (Entry<Player, Integer> entry : entityList.entrySet()) {
				int amount = entry.getValue();
				if (limit < 12) {
					sender.sendMessage(ChatColor.DARK_GRAY + "- " + ChatColor.GRAY + entry.getKey().getName() + ": "
							+ ChatColor.RED + amount + " " + ChatColor.WHITE
							+ Utils.locationToString(locMap.get(entry.getKey())));
				}
				limit++;
			}
		}
		return;
	}

	private HashMap<Player, Integer> sort(HashMap<Player, Integer> map) {
		List<Map.Entry<Player, Integer>> list = new LinkedList<Map.Entry<Player, Integer>>(map.entrySet());

		Collections.sort(list, new Comparator<Map.Entry<Player, Integer>>() {
			public int compare(Map.Entry<Player, Integer> o1, Map.Entry<Player, Integer> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});

		HashMap<Player, Integer> sorted = new LinkedHashMap<Player, Integer>();
		for (Map.Entry<Player, Integer> entry : list)
			sorted.put(entry.getKey(), entry.getValue());
		return sorted;
	}

	private void count(Player p, Map<Player, Integer> list) {
		if (list.get(p) == null) {
			list.put(p, 1);
		} else {
			list.put(p, list.get(p) + 1);
		}
	}

	@Override
	public String description() {
		return "Lookup top entity count per player for a specific entity";
	}
	
	@Override
	public String usage() {
		return "lookup <entity>";
	}

}
