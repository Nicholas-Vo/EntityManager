package entityManager.commands;

import java.util.ArrayList;
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
import entityManager.Utils;

public class RemoveEntities extends SubCommand {
	private int limit = 30;
	private List<EntityType> excluded = plugin.getExcludedEntities();

	public RemoveEntities(EntityManager plugin) {
		super(plugin, "remove");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			plugin.fail(sender, "That's a player only command.");
			return;
		}

		Player player = (Player) sender;
		if (!player.hasPermission("entitymanager.use")) { // Permission check
			plugin.fail(sender, plugin.noPermission);
			return;
		}

		if (args.length < 2 || !NumberUtils.isParsable(args[1])) {
			sender.sendMessage("Usage: /em " + usage());
			return;
		}

		if (Integer.valueOf(args[1]) != null) {
			int input = Integer.valueOf(args[1]);

			if (input <= limit && input > 0) {
				List<Entity> nearby = player.getNearbyEntities(input, 200, input);
				List<Entity> deleted = new ArrayList<Entity>();
				Map<EntityType, Integer> entityMap = new HashMap<EntityType, Integer>();

				for (Entity entity : nearby) {
					if (excluded.contains(entity.getType()))
						break;
					if (!(entity instanceof Player))
						clear(sender, entity, entityMap, deleted);
				}

				String pluralOrNot = " entities";

				if (plugin.getConfig().getBoolean("announceRemoval")) {
					for (Entity entity : nearby) {
						if (entity instanceof Player)
							entity.sendMessage(ChatColor.RED + "[Notice] " + ChatColor.RESET + player.getName()
									+ " just removed " + ChatColor.RED + deleted.size() + ChatColor.RESET + pluralOrNot + " near you!");
					}
				}
				
				plugin.logToConsole(player.getName() + " cleared " + ChatColor.RED + deleted.size() + ChatColor.RESET
						+ pluralOrNot + " at " + Utils.locationToString(player) + " in the " + Utils.worldToString(player.getWorld()) + ".");
				sender.sendMessage("Removed " + ChatColor.RED + deleted.size() + ChatColor.WHITE + pluralOrNot
						+ " within a radius of " + input + ".");
				for (Entry<EntityType, Integer> entry : entityMap.entrySet()) {
					sender.sendMessage(ChatColor.DARK_GRAY + "- " + ChatColor.GRAY + entityToString(entry) + ": "
							+ ChatColor.WHITE + entry.getValue());
				}
				return;
			}
			plugin.fail(sender, "You can only remove entities within a radius of " + limit + ".");
		}
		return;
	}

	private void clear(CommandSender sender, Entity entity, Map<EntityType, Integer> map, List<Entity> deleted) {
		entity.remove();
		deleted.add(entity);
		count(entity, map);
	}

	public void count(Entity e, Map<EntityType, Integer> map) {
		EntityType type = e.getType();
		if (map.get(type) == null) {
			map.put(type, 1);
		} else {
			map.put(type, map.get(e.getType()) + 1);
		}
	}

	private String entityToString(Entry<EntityType, Integer> entry) {
		return WordUtils.capitalize(entry.getKey().toString().toLowerCase().replace("_", " "));
	}

//	private boolean isProtected(Entity e) {
//		ArrayList<EntityType> protect = new ArrayList<EntityType>();
//		protect.add(EntityType.VILLAGER);
//		protect.add(EntityType.STRIDER);
//		protect.add(EntityType.ARMOR_STAND);
//		protect.add(EntityType.ITEM_FRAME);
//		protect.add(EntityType.WOLF);
//		protect.add(EntityType.BOAT);
//		protect.add(EntityType.HORSE);
//		protect.add(EntityType.ENDER_CRYSTAL);
//		protect.add(EntityType.TRADER_LLAMA);
//		protect.add(EntityType.WANDERING_TRADER);
//		protect.add(EntityType.CAT);
//		protect.add(EntityType.PAINTING);
//		protect.add(EntityType.LEASH_HITCH);
//		protect.add(EntityType.TROPICAL_FISH);
//		protect.add(EntityType.MINECART);
//		protect.add(EntityType.MINECART_CHEST);
//		protect.add(EntityType.MINECART_HOPPER);
//		protect.add(EntityType.MINECART_FURNACE);
//		protect.add(EntityType.ELDER_GUARDIAN);
//		protect.add(EntityType.GUARDIAN);
//		protect.add(EntityType.DOLPHIN);
//		protect.add(EntityType.LLAMA);
//		return protect.contains(e.getType());
//	}

	@Override
	public String description() {
		return "Remove nearby entities";
	}

	@Override
	public String usage() {
		return "remove <radius>";
	}

}
