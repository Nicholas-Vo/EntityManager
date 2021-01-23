package entityManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class Utils {

	public static String copyableLocation(Player p) {
		Location l = p.getLocation();
		return l.getBlockX() + " " + l.getBlockY() + " " + l.getBlockZ();
	}
	public static String copyableLocation(Location l) {
		return l.getBlockX() + " " + l.getBlockY() + " " + l.getBlockZ();
	}
	public static String locationToString(Player p) {
		Location l = p.getLocation();
		return "x" + l.getBlockX() + ", y" + l.getBlockY() + ", z" + l.getBlockZ();
	}
	public static String locationToString(Location l) {
		return "x" + l.getBlockX() + ", y" + l.getBlockY() + ", z" + l.getBlockZ();
	}
	public static String blockToString(Block block) {
		return block.getType().toString().toLowerCase().replace("_", " ");
	}
	
	public static String worldToString(World w) {
		if (w.getEnvironment().equals(Environment.NORMAL))
			return "overworld";
		if (w.getEnvironment().equals(Environment.NETHER))
			return "nether";
		if (w.getEnvironment().equals(Environment.THE_END))
			return "end";
		else
			return "null";
	}

	public static Environment getPlayerWorld(Player p) {
		return p.getWorld().getEnvironment();
	}
	public static boolean inventoryFull(Player p) {
		return p.getInventory().firstEmpty() == -1;
	}
	public static void saveFile(YamlConfiguration config, File file) {
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static DecimalFormat numberFormat = new DecimalFormat("#,###");
	static Date date = new Date();
	static SimpleDateFormat niceLookingDate = new SimpleDateFormat("MM-dd-yyyy");
	static String format = niceLookingDate.format(date);
	
	public static void logToFile(String message, String fileName) {
		File dataFolder = EntityManager.plugin.getDataFolder();
		try {
			if (!dataFolder.exists())
				dataFolder.mkdir();
			File saveTo = new File(dataFolder,
					fileName + ".txt");
			if (!saveTo.exists())
				saveTo.createNewFile();
			FileWriter fw = new FileWriter(saveTo, true);
			PrintWriter pw = new PrintWriter(fw);
			pw.println("[" + format.toString() + "] " + message);
			pw.flush();
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
