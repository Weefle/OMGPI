package tk.omgpi.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_11_R1.CraftServer;
import tk.omgpi.commands.management.*;
import tk.omgpi.commands.player.*;
import tk.omgpi.utils.OMGList;

import java.util.Arrays;

/**
 * Custom command class for easy command setup.
 */
public class OMGCommand {
    /**
     * List of commands registered in OMGPI system
     */
    public static OMGList<OMGCommand> registeredCommands = new OMGList<>();

    /**
     * Command used to register in bukkit systems.
     */
    public Command bukkit;

    /**
     * Name used only on register.
     */
    public String name;

    /**
     * Permission to check.
     */
    public String permission;

    /**
     * Aliases for the command used only on register.
     */
    public String[] aliases;

    /**
     * Set usage for error message if you have any useful additions.
     */
    public String usage;

    /**
     * Use if use call() with less args but you need to use full message anyway.<br>
     * Example: You have call(arg1) because 0-args is not-enough-args error message.
     * So not to do call() for each args amount or overhaul with call(args...) use this.
     */
    public String[] lastargscall;

    /**
     * Register all OMGPI commands.
     */
    public static void omgpi_register() {
        new SetGame();
        new SetKit();
        new SetMap();
        new SetRequestedTeam();
        new SetTeam();
        new SetTime();
        new SetupMode();
        new SkipDiscovery();
        new StartGame();
        new StopGame();
        new GameShop();
        new HotbarEditor();
        new Join();
        new Options();
        new RequestKit();
        new RequestTeam();
        new Spectate();
        new Vote();
        new Debug();
    }

    /**
     * Unregister all OMGCommands.
     */
    public static void unregisterAll() {
        registeredCommands.forEach(OMGCommand::unregister);
    }

    /**
     * Create and register a new command.
     *
     * @param name Command name used by default in /&lt;name&gt;.
     * @param permission Permission needed to execute the command.
     * @param aliases Aliases which can be used to execute the command like /&lt;alias&gt;.
     */
    public OMGCommand(String name, String permission, String... aliases) {
        this.name = name;
        this.permission = permission;
        this.aliases = aliases;
        registeredCommands.add(this);
        bukkit = new Command(name, "", "", Arrays.asList(aliases)) {
            public boolean execute(CommandSender sender, String label, String... args) {
                if (permission == null || sender.hasPermission(permission)) {
                    lastargscall = args;
                    if (args.length == 0) call(sender, label);
                    if (args.length == 1) call(sender, label, args[0]);
                    if (args.length == 2) call(sender, label, args[0], args[1]);
                    if (args.length == 3) call(sender, label, args[0], args[1], args[2]);
                    if (args.length == 4) call(sender, label, args[0], args[1], args[2], args[3]);
                    call(sender, label, args);
                } else
                    sender.sendMessage(ChatColor.DARK_AQUA + "You don't have enough permissions to execute this command.");
                return true;
            }
        };
        ((CraftServer) Bukkit.getServer()).getCommandMap().register("omgpi", bukkit);
    }

    /**
     * Unregister the command from bukkit.
     */
    public void unregister() {
        bukkit.unregister(((CraftServer) Bukkit.getServer()).getCommandMap());
    }

    /**
     * Called when command is executed. This is 0 args version.
     *
     * @param s Sender that executes the command
     * @param label Alias or name used to execute the command.
     */
    public void call(CommandSender s, String label) {
        s.sendMessage(ChatColor.RED + "Not enough arguments." + (usage == null ? "" : " " + usage));
    }

    /**
     * Called when command is executed. This is 1 arg version.
     *
     * @param s Sender that executes the command
     * @param label Alias or name used to execute the command.
     * @param arg1 First argument
     */
    public void call(CommandSender s, String label, String arg1) {
        call(s, label);
    }

    /**
     * Called when command is executed. This is 2 args version.
     *
     * @param s Sender that executes the command
     * @param label Alias or name used to execute the command.
     * @param arg1 First argument
     * @param arg2 Second argument
     */
    public void call(CommandSender s, String label, String arg1, String arg2) {
        call(s, label, arg1);
    }

    /**
     * Called when command is executed. This is 3 args version.
     *
     * @param s Sender that executes the command
     * @param label Alias or name used to execute the command.
     * @param arg1 First argument
     * @param arg2 Second argument
     * @param arg3 Third argument
     */
    public void call(CommandSender s, String label, String arg1, String arg2, String arg3) {
        call(s, label, arg1, arg2);
    }

    /**
     * Called when command is executed. This is 4 args version.
     *
     * @param s Sender that executes the command
     * @param label Alias or name used to execute the command.
     * @param arg1 First argument
     * @param arg2 Second argument
     * @param arg3 Third argument
     * @param arg4 Fourth argument
     */
    public void call(CommandSender s, String label, String arg1, String arg2, String arg3, String arg4) {
        call(s, label, arg1, arg2, arg3);
    }

    /**
     * Called when command is executed. This is any amount of args version. Called along with others - use for complete overhaul.
     *
     * @param s Sender that executes the command
     * @param label Alias or name used to execute the command.
     * @param args All arguments used when executing
     */
    public void call(CommandSender s, String label, String... args) {
    }
}
