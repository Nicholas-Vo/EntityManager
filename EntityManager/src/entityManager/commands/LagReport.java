package entityManager.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import entityManager.EntityManager;
import entityManager.SubCommand;
import entityManager.Utils;

public class LagReport extends SubCommand {

	public LagReport(EntityManager plugin) {
		super(plugin, "report");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player p = (Player) sender;
		List<Entity> world = p.getWorld().getEntities();
		HashMap<EntityType, Integer> entityList = new HashMap<EntityType, Integer>();

		for (Entity entity : world) {
			if (!entityIgnored(entity))
				count(entity, entityList);
		}

		entityList = sort(entityList);

		int limit = 0;
		sender.sendMessage("Entity report for " + Utils.worldToString(p.getWorld()) + ":");
		for (Entry<EntityType, Integer> entry : entityList.entrySet()) {
			String entityToString = entry.getKey().toString().toLowerCase().replace("_", " ");
			String entity = WordUtils.capitalize(entityToString);
			int amount = entry.getValue();
			if (limit < 12)
				sender.sendMessage(
						ChatColor.DARK_GRAY + "- " + ChatColor.GRAY + entity + ": " + ChatColor.RESET + amount);
			limit++;
		}
		return;
	}

	private void count(Entity e, Map<EntityType, Integer> map) {
		if (map.get(e.getType()) == null) {
			map.put(e.getType(), 1);
		} else {
			map.put(e.getType(), map.get(e.getType()) + 1);
		}
	}

	private boolean entityIgnored(Entity entity) {
		ArrayList<EntityType> ignored = new ArrayList<EntityType>();
		ignored.add(EntityType.ITEM_FRAME);
		return ignored.contains(entity.getType());
	}

	public HashMap<EntityType, Integer> sort(HashMap<EntityType, Integer> map) {
		List<Map.Entry<EntityType, Integer>> list = new LinkedList<Map.Entry<EntityType, Integer>>(map.entrySet());

		Collections.sort(list, new Comparator<Map.Entry<EntityType, Integer>>() {
			public int compare(Map.Entry<EntityType, Integer> o1, Map.Entry<EntityType, Integer> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});

		HashMap<EntityType, Integer> sorted = new LinkedHashMap<EntityType, Integer>();
		for (Map.Entry<EntityType, Integer> entry : list) {
			sorted.put(entry.getKey(), entry.getValue());
		}
		return sorted;
	}
	
	@Override
	public String description() {
		return ChatColor.RED + "remove " + ChatColor.RESET + "- Display top entities for the world you're in";
	}

}
