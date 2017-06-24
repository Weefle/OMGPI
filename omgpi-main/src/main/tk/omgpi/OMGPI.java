package tk.omgpi;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import tk.omgpi.commands.OMGCommand;
import tk.omgpi.files.Mainfig;
import tk.omgpi.files.OMGKit;
import tk.omgpi.files.OMGLoot;
import tk.omgpi.game.Game;
import tk.omgpi.game.GameWorld;
import tk.omgpi.game.OMGPlayer;
import tk.omgpi.game.OMGTeam;
import tk.omgpi.utils.MySQL;
import tk.omgpi.utils.OMGList;
import tk.omgpi.utils.Strings;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Main OMGPI class. Contains logging utils and loads games.
 */
public class OMGPI extends JavaPlugin {
    public static ConsoleCommandSender logger = Bukkit.getConsoleSender();

    /**
     * Instance of OMGPI for hanging registering onto OMGPI.
     */
    public static OMGPI instance;

    /**
     * The game world, where game happens.
     */
    public static GameWorld gameworld;

    /**
     * Mainfig containing OMGPI data.
     */
    public static Mainfig mainfig;

    /**
     * Current game.
     */
    public static Game g;

    public void onEnable() {
        instance = this;
        iLog("Setting up...");

        gameworld = new GameWorld();
        mainfig = new Mainfig();
        init();
    }

    public void onDisable() {
        instance = null;
        MySQL.onDisable();
    }

    /**
     * Register all systems.
     */
    @SuppressWarnings("ConstantConditions")
    public void init() {
        OMGCommand.omgpi_register();
        if (mainfig.contains("mysql"))
            new MySQL(mainfig.getString("mysql.hostname"), mainfig.getString("mysql.port"), mainfig.getString("mysql.database"), mainfig.getString("mysql.username"), mainfig.getString("mysql.password"));
        iLog("Loading games...");
        OMGList<String> games = new OMGList<>();
        File gdir = new File(getDataFolder() + File.separator + "games");
        if (!gdir.exists() && gdir.mkdir()) iLog("Created games folder.");
        String[] files = gdir.list();
        if (files != null && files.length > 0) Collections.addAll(games, files);
        iLog("Games folder: " + Strings.join(games, ", "));
        games.removeIf(s -> !s.endsWith(".jar"));
        if (games.isEmpty()) {
            wLog("No game jars found in games folder. Please add GameName.jar file to /plugins/OMGPI/games/.");
            return;
        }
        String game = mainfig.getString("selectedGame", "random");
        try {
            if (game == null || !games.contains(game + ".jar")) loadGame(null);
            else loadGame(game);
        } catch (InvalidDescriptionException | InvalidPluginException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load game from name.
     *
     * @param game Name of a game.
     * @return Module plugin.
     * @throws InvalidDescriptionException Plugin.yml is wrong
     * @throws InvalidPluginException      Plugin cannot be loaded
     */
    public Game loadGame(String game) throws InvalidDescriptionException, InvalidPluginException {
        iLog("Loading game " + game + "...");
        Plugin p;
        if (game == null) {
            OMGList<String> games = new OMGList<>();
            String[] list = new File(getDataFolder() + File.separator + "games").list();
            if (list != null) Collections.addAll(games, list);
            games.removeIf(s -> !s.endsWith(".jar"));
            iLog("Last game: " + mainfig.getString("lastGame"));
            if (games.size() > 1 && mainfig.contains("lastGame"))
                games.removeIf(s -> mainfig.getString("lastGame").equals(s.replaceAll("\\.jar", "")));
            iLog("List of runnable games: " + Strings.join(games, ", "));
            String gameName = games.get(new Random().nextInt(games.size()));
            p = Bukkit.getPluginManager().loadPlugin(new File(getDataFolder() + File.separator + "games" + File.separator + gameName));
            mainfig.set("lastGame", gameName.replaceAll("\\.jar", ""));
            mainfig.save();
        } else {
            p = Bukkit.getPluginManager().loadPlugin(new File(getDataFolder() + File.separator + "games" + File.separator + game + ".jar"));
            mainfig.set("lastGame", game);
            mainfig.save();
        }
        g = (Game) p;
        getServer().getPluginManager().enablePlugin(p);
        return (Game) p;
    }

    /**
     * Log an info message.
     *
     * @param o Object to log using .toString()
     */
    public static void iLog(Object o) {
        logger.sendMessage(ChatColor.GREEN + "OMGPI > " + o);
    }

    /**
     * Log a warning message.
     *
     * @param o Object to log using .toString()
     */
    public static void wLog(Object o) {
        logger.sendMessage(ChatColor.YELLOW + "OMGPI > " + o);
    }

    /**
     * Log an error message.
     *
     * @param o Object to log using .toString()
     */
    public static void eLog(Object o) {
        logger.sendMessage(ChatColor.RED + "OMGPI > " + o);
    }

    /**
     * Restart game and reset OMGPI registered systems.
     */
    @SuppressWarnings("unchecked")
    public void reload() {
        Bukkit.getScheduler().cancelAllTasks();
        OMGPlayer.link.values().forEach(OMGPlayer::remove);
        OMGTeam.registeredTeams.clear();
        OMGKit.kits.clear();
        OMGLoot.loots.clear();
        OMGCommand.unregisterAll();
        gameworld.unload();
        Bukkit.getServer().getPluginManager().disablePlugin(g);
        try {
            Field pl = SimplePluginManager.class.getDeclaredField("plugins");
            pl.setAccessible(true);
            ((List<Plugin>) pl.get(Bukkit.getServer().getPluginManager())).remove(g);
            Field ln = SimplePluginManager.class.getDeclaredField("lookupNames");
            ln.setAccessible(true);
            ((Map<String, Plugin>) ln.get(Bukkit.getServer().getPluginManager())).remove(g.getDescription().getName());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        HandlerList.unregisterAll();
        init();
    }
}