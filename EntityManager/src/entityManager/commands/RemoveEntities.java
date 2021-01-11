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

public class RemoveEntities extends SubCommand {
	private boolean includeVillagers = false;
	private int max = 30;

	public RemoveEntities(EntityManager plugin) {
		super(plugin, "remove");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Sorry, that only works as a player.");
			return;
		}
		
		Player player = (Player) sender;
		if (!player.hasPermission("entitymanager.use")) {
			sender.sendMessage(plugin.noPermission);
			return;
		}
		
		if (args.length < 2) {
			sender.sendMessage("Usage: /entity remove <radius>");
			return;
		}

		if (!NumberUtils.isParsable(args[1])) {
			sender.sendMessage("Usage: /entity remove <radius>");
			return;
		}

		if (args.length > 1) {
			if (args[1].equals("includevillagers")) {
				includeVillagers = true;
			}
		}

		if (Integer.valueOf(args[1]) != null) {
			int input = Integer.valueOf(args[1]);
			if (input <= max && input > -1) {
				List<Entity> nearby = player.getNearbyEntities(input, 200, input);
				List<Entity> deleted = new ArrayList<Entity>();
				Map<EntityType, Integer> entityMap = new HashMap<EntityType, Integer>();

				for (Entity entity : nearby) {
					if (!(entity instanceof Player))
						clear(sender, entity, entityMap, deleted);
				}

				if (deleted.size() == 1) {
					sender.sendMessage(
							"Removed " + ChatColor.RED + "1" + ChatColor.WHITE + " entity within a radius of " + input + ".");
				} else {
					sender.sendMessage("Removed " + ChatColor.RED + deleted.size() + ChatColor.WHITE
							+ " entities within a radius of " + input + ".");
				}
				for (Entry<EntityType, Integer> entry : entityMap.entrySet()) {
					sender.sendMessage(ChatColor.DARK_GRAY + "- " + ChatColor.GRAY + entityToString(entry) + ": " + ChatColor.WHITE
							+ entry.getValue());
				}
			} else {
				sender.sendMessage("You can only remove entities within a radius of " + max + ".");
				return;
			}
		}
		return;
	}
	
	private void clear(CommandSender sender, Entity e, Map<EntityType, Integer> map, List<Entity> deleted) {
		if (includeVillagers) {
			if (e.getType() == EntityType.VILLAGER) {
				e.remove();
				deleted.add(e);
				count(e, map);
			}
		}
		if (!isProtected(e)) {
			e.remove();
			deleted.add(e);
			count(e, map);
		}
	}
	
	public void count(Entity e, Map<EntityType, Integer> map) {
		if (map.get(e.getType()) == null) {
			map.put(e.getType(), 1);
		} else {
			map.put(e.getType(), map.get(e.getType()) + 1);
		}
	}

	private String entityToString(Entry<EntityType, Integer> entry) {
		return WordUtils.capitalize(entry.getKey().toString().toLowerCase().replace("_", " "));
	}

	private boolean isProtected(Entity e) {
		ArrayList<EntityType> protect = new ArrayList<EntityType>();
		protect.add(EntityType.VILLAGER);
		protect.add(EntityType.STRIDER);
		protect.add(EntityType.ARMOR_STAND);
		protect.add(EntityType.ITEM_FRAME);
		protect.add(EntityType.WOLF);
		protect.add(EntityType.BOAT);
		protect.add(EntityType.HORSE);
		protect.add(EntityType.ENDER_CRYSTAL);
		protect.add(EntityType.TRADER_LLAMA);
		protect.add(EntityType.WANDERING_TRADER);
		protect.add(EntityType.CAT);
		protect.add(EntityType.PAINTING);
		protect.add(EntityType.LEASH_HITCH);
		protect.add(EntityType.TROPICAL_FISH);
		protect.add(EntityType.MINECART);
		protect.add(EntityType.MINECART_CHEST);
		protect.add(EntityType.MINECART_HOPPER);
		protect.add(EntityType.MINECART_FURNACE);
		protect.add(EntityType.ELDER_GUARDIAN);
		protect.add(EntityType.GUARDIAN);
		protect.add(EntityType.DOLPHIN);
		protect.add(EntityType.LLAMA);
		return protect.contains(e.getType());
	}

	@Override
	public String description() {
		return ChatColor.RED + "remove " + ChatColor.RESET + "- Remove nearby entities";
	}

}
