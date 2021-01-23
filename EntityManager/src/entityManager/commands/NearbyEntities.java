package entityManager.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import entityManager.EntityManager;
import entityManager.SubCommand;

public class NearbyEntities extends SubCommand {
	private int limit = 200;
	private int input = 10; // input defaults to 10
	
	public NearbyEntities(EntityManager entityManager) {
		super(entityManager, "nearby");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Error: That's a player only command.");
			return;
		}
		
		Player nick = (Player) sender;
		if (args.length == 2) {
			if (!NumberUtils.isParsable(args[1])) {
				sender.sendMessage(args[1] + " isn't applicable - needs to be a number.");
				return;
			}
			input = Integer.valueOf(args[1]);
		}

		if (input > limit && input > 0) {
			plugin.fail(sender, "You can only search for entities within a radius of " + limit + ".");
			return;
		}

		List<Entity> nearby = nick.getNearbyEntities(input, 100, input);
		Map<EntityType, Integer> entityMap = new HashMap<EntityType, Integer>();

		for (Entity entity : nearby) {
			if (!(entity instanceof Player)) {
				count(entity, entityMap);
			}
		}

		if (nearby.size() == 1) {
			sender.sendMessage("There is " + ChatColor.RED + "1 " + ChatColor.RESET
					+ "entity nearby within a radius of " + input + ".");
		} else {
			sender.sendMessage("There are " + ChatColor.RED + nearby.size() + ChatColor.RESET
					+ " entities nearby within a radius of " + input + ".");
		}

		for (Entry<EntityType, Integer> entry : entityMap.entrySet()) {
			sender.sendMessage(
					ChatColor.DARK_GRAY + "- " + ChatColor.GRAY + entityToString(entry) + ": " + ChatColor.RESET + entry.getValue());
		}
		return;
	}

	private static void count(Entity e, Map<EntityType, Integer> map) {
		if (map.get(e.getType()) == null) {
			map.put(e.getType(), 1);
		} else {
			map.put(e.getType(), map.get(e.getType()) + 1);
		}
	}

	private static String entityToString(Entry<EntityType, Integer> entry) {
		return WordUtils.capitalize(entry.getKey().toString().toLowerCase().replace("_", " "));
	}
	
	@Override
	public String description() {
		return "Display nearby entities";
	}

	@Override
	public String usage() {
		return "nearby";
	}

}
