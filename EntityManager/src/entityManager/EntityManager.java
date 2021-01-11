package entityManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

import entityManager.commands.LagReport;
import entityManager.commands.NearbyEntites;
import entityManager.commands.RemoveEntities;
import entityManager.commands.SearchEntity;

public class EntityManager extends JavaPlugin {
	public static EntityManager plugin;
	public static String version = "1.0.0";
	public Logger log = Bukkit.getLogger();
	public ArrayList<SubCommand> commands = new ArrayList<SubCommand>();
	public String noPermission = ChatColor.RED + "You do not have permission to do that.";

	public void onEnable() {
		log.info("Enabling EntityManager v" + version + "...");
		// plugin.getCommand("entity").setExecutor(this);
		commands.add(new LagReport(this));
		commands.add(new NearbyEntites(this));
		commands.add(new RemoveEntities(this));
		commands.add(new SearchEntity(this));
	}

	public void onDisable() {
		log.info("Disabling EntityManager v" + version + "...");
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (!sender.hasPermission("entitymanager.use")) {
			sender.sendMessage(ChatColor.RED + "You do not have permission to use that command.");
			return false;
		}

		if (args.length == 0) {
			sender.sendMessage(
					ChatColor.GRAY + " --------" + ChatColor.GOLD + " Entity Manager " + ChatColor.GRAY + "--------");
			for (SubCommand command : commands)
				sender.sendMessage(
						ChatColor.RED + "/" + command.getName() + "" + ChatColor.RESET + command.description());
			return true;
		}
//		sender.sendMessage(left + "/entity report" + right + " - Display top entities for the world you're in");
//		sender.sendMessage(left + "/entity search " + middle + "<entityType>" + right
//				+ " - Display players with most entities");
//		sender.sendMessage(left + "/entity near " + middle + "<radius>" + right + " - Display nearby entities");
//			sender.sendMessage(left + "/entity remove " + middle + "<radius>" + right + " - Remove nearby entities "
//					+ Chat.red + "(admin command)");

		for (SubCommand command : commands) {
			if (command.getName().equals(args[0])) {
				command.execute(sender, args);
				break;
			}
		}
		return false;
	}

	public static List<String> getEntityTypes() {
		ArrayList<String> results = new ArrayList<String>();
		for (EntityType m : EntityType.values())
			results.add(m.name());
		return results;
	}

	private List<String> getResults(String[] args, List<String> toSearch) {
		List<String> results = new ArrayList<>();
		for (String lemonade : toSearch) {
			if (lemonade.toLowerCase().startsWith(args[1].toLowerCase()))
				results.add(lemonade);
		}
		return results;
	}

	List<String> myList = new ArrayList<>(Arrays.asList("report", "near", "remove", "search"));

	@Override
	public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] args) {
		if (!commandSender.hasPermission("entitymanager.use"))
			return Arrays.asList();
		List<String> results = new ArrayList<>();
		if (args.length == 1) {
			for (String a : myList) {
				if (a.toLowerCase().startsWith(args[0].toLowerCase()))
					results.add(a);
			}
			return results;
		}
		if (args.length == 2) {
			if (args[0].equalsIgnoreCase("search")) {
				return getResults(args, getEntityTypes());
			}
		}
		return Arrays.asList();
	}

}
