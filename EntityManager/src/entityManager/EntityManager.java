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

import entityManager.commands.LookupEntity;
import entityManager.commands.NearbyEntities;
import entityManager.commands.ReloadConfiguration;
import entityManager.commands.RemoveEntities;
import entityManager.commands.WorldReport;

public class EntityManager extends JavaPlugin {
	public static EntityManager plugin;
	public static String version = "1.0.0";
	public String noPermission = ChatColor.RED + "You do not have permission to do that.";
	public String pluginTag = ChatColor.RED + "[EntityManager]";
	private Logger log = Bukkit.getLogger();
	private ArrayList<SubCommand> commands = new ArrayList<SubCommand>();
	private ArrayList<EntityType> excludedEntities = new ArrayList<EntityType>();

	public void onEnable() {
		log.info("Enabling EntityManager v" + version + "...");
		commands.add(new WorldReport(this));
		commands.add(new NearbyEntities(this));
		commands.add(new RemoveEntities(this));
		commands.add(new LookupEntity(this));
		commands.add(new ReloadConfiguration(this));

		initalizeStuff();

		List<String> list = new ArrayList<String>(Arrays.asList("VILLAGER", "ARMOR_STAND"));

		this.getConfig().addDefault("nearbyCommandLimit", 200);
		this.getConfig().addDefault("removeCommandLimit", 30);
		this.getConfig().addDefault("announceRemoval", true);
		this.getConfig().addDefault("excludedEntities", list);
		this.saveConfig();

		for (SubCommand cmd : commands) // Add each subcommand into tab completor
			completeList.add(cmd.getName());
	}

	public void onDisable() {
		log.info("Disabling EntityManager v" + version + "...");
	}

	public void initalizeStuff() {
		saveDefaultConfig();
		parseExcludedEntities();

		if (!getDataFolder().exists()) {
			getDataFolder().mkdir();
			logToConsole("No directory detected for EntityManager " + version + ". Creating directory now...");
		}
	}

	public List<EntityType> getExcludedEntities() {
		return excludedEntities;
	}

	public void parseExcludedEntities() {
		for (String entry : this.getConfig().getStringList("excludedEntities")) {
			EntityType entity = null;

			try {
				entity = EntityType.valueOf(entry);
			} catch (IllegalArgumentException e) {
				logToConsole(
						"Entity \"" + ChatColor.WHITE + entry + "\" from the configuration file isn't a valid entity!");
			}

			if (entity != null)
				excludedEntities.add(EntityType.valueOf(entry));
		}
	}

	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		if (!sender.hasPermission("entitymanager.use")) {
			fail(sender, "You do not have permission to use that command.");
			return false;
		}

		if (args.length == 0) {
			sender.sendMessage(ChatColor.GRAY + " --------" + ChatColor.GOLD + " Entity Manager v" + version
					+ ChatColor.GRAY + " --------");
			for (SubCommand cmd : commands)
				sender.sendMessage(ChatColor.RED + "/em " + ChatColor.GRAY + cmd.usage() + "" + ChatColor.RESET
						+ " - " + cmd.description());
			return true;
		}

		for (SubCommand cmd : commands) {
			if (cmd.getName().equals(args[0])) {
				cmd.execute(sender, args);
				break;
			}
		}
		return false;
	}

	public void logToConsole(String msg) {
		log.info(pluginTag + " " + ChatColor.RESET + msg);
	}

	public void fail(CommandSender sender, String why) {
		sender.sendMessage(ChatColor.RED + "Error: " + ChatColor.RESET + why);
	}

	public List<String> getEntityTypes() {   
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

	private List<String> completeList = new ArrayList<>();

	@Override
	public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] args) {
		if (!commandSender.hasPermission("entitymanager.use"))
			return Arrays.asList();
		List<String> results = new ArrayList<>();
		if (args.length == 1) {
			for (String a : completeList) {
				if (a.toLowerCase().startsWith(args[0].toLowerCase()))
					results.add(a);
			}
			return results;
		}
		if (args.length == 2) {
			if (args[0].equalsIgnoreCase("lookup")) {
				return getResults(args, getEntityTypes());
			}
		}
		return Arrays.asList();
	}

}
