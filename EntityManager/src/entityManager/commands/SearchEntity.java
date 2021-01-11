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
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.text.WordUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import entityManager.EntityManager;
import entityManager.SubCommand;
import entityManager.Utils;

public class SearchEntity extends SubCommand {
	private EntityType toSearch = EntityType.VILLAGER;
	
	public SearchEntity(EntityManager entityManager) {
		super(entityManager, "search");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Player only command.");
			return;
		}
			
		Player p = (Player) sender;
		//String worldToString = Utils.worldToString(p.getWorld());
		List<Player> playersInWorld = p.getWorld().getPlayers();

		if (args.length < 2) {
			sender.sendMessage("Usage: /entity search <entity>");
			return;
		}

		if (args.length > 1) {
			if (EntityType.valueOf(args[1]) == null) {
				p.sendMessage(ChatColor.RED + "That is not a searchable entity.");
				return;
			}
			
			toSearch = EntityType.valueOf(args[1]);

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
			String lookedUp = WordUtils.capitalize(toSearch.toString().toLowerCase().replace("_", " "));
			if (entityList.size() == 0) {
				sender.sendMessage("No entities of that type were found.");
			} else {
				sender.sendMessage("Players with the most " + lookedUp + "s:");
			}
			
			int limit = 0;
			for (Entry<Player, Integer> entry : entityList.entrySet()) {
				//String entity = WordUtils.capitalize(entry.getKey().toString().toLowerCase().replace("_", " "));
				int amount = entry.getValue();
				if (limit < 12) {
					sender.sendMessage(ChatColor.DARK_GRAY + "- " + ChatColor.GRAY + entry.getKey().getName() + ": " + ChatColor.RED + amount
							+ " " + ChatColor.WHITE + Utils.locationToString(locMap.get(entry.getKey())));
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
		for (Map.Entry<Player, Integer> entry : list) {
			sorted.put(entry.getKey(), entry.getValue());
		}
		return sorted;
	}

	private void count(Player p, Map<Player, Integer> entityList) {
		if (entityList.get(p) == null) {
			entityList.put(p, 1);
		} else {
			entityList.put(p, entityList.get(p) + 1);
		}
	}
	
	@Override
	public String description() {
		return ChatColor.RED + "search " + ChatColor.RESET + "- Lookup top entity count per player for a specific entity";
	}

}
