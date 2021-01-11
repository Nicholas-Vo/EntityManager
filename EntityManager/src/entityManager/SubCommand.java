package entityManager;

import org.bukkit.command.CommandSender;

public abstract class SubCommand {
	protected EntityManager plugin;
	private String name;

	public SubCommand(EntityManager plugin, String name) {
		this.name = name;
		this.plugin = plugin;
	}

	public String getName() {
		return name;
	}
	
	public abstract String description();

	public abstract void execute(CommandSender sender, String[] args);
}