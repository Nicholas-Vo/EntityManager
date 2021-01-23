package entityManager.commands;

import org.bukkit.command.CommandSender;

import entityManager.EntityManager;
import entityManager.SubCommand;
import net.md_5.bungee.api.ChatColor;

public class ReloadConfiguration extends SubCommand {
	
	public ReloadConfiguration(EntityManager plugin) {
		super(plugin, "reload");
	}

	@Override
	public String description() {
		return "Reload the configuration file";
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (!sender.hasPermission("entitymanager.use")) {
			plugin.fail(sender, "You do not have permission to use that command.");
			return;
		}
		
		plugin.reloadConfig();
		sender.sendMessage(plugin.pluginTag + ChatColor.RESET + " Successfully reloaded the configuration file.");
	}

	@Override
	public String usage() {
		return "reload";
	}

}
