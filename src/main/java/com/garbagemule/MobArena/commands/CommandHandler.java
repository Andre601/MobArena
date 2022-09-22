package com.garbagemule.MobArena.commands;

import com.garbagemule.MobArena.ConfigError;
import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.commands.admin.*;
import com.garbagemule.MobArena.commands.setup.*;
import com.garbagemule.MobArena.commands.user.*;
import com.garbagemule.MobArena.framework.ArenaMaster;
import com.garbagemule.MobArena.message.MessageKey;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.conversations.Conversable;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class CommandHandler implements CommandExecutor, TabCompleter
{
    private MobArena plugin;

    private Map<String,Command> commands;

    public CommandHandler(MobArena plugin) {
        this.plugin = plugin;

        registerCommands();
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command bcmd, String label, String[] args) {
        // Grab the base and arguments.
        String base = (args.length > 0 ? args[0] : "").toLowerCase();
        String last = (args.length > 0 ? args[args.length - 1] : "").toLowerCase();

        // If the player is in a convo (Setup Mode), bail
        if (sender instanceof Conversable && ((Conversable) sender).isConversing()) {
            return true;
        }

        // If there's no base argument, show a helpful message.
        if (base.equals("")) {
            return safeTell(sender, MessageKey.MISC_HELP);
        }

        // Reloads are special
        if (base.equals("reload") || (base.equals("config") && args.length > 1 && args[1].equals("reload"))) {
            return reload(sender);
        }

        Throwable lastFailureCause = plugin.getLastFailureCause();
        if (lastFailureCause != null) {
            plugin.sendMessage(sender, "MobArena is disabled. Reason:\n" + ChatColor.RED + lastFailureCause.getMessage());
            return true;
        }

        // The help command is a little special
        if (base.equals("?") || base.equals("help")) {
            showHelp(sender);
            return true;
        }

        ArenaMaster am = plugin.getArenaMaster();

        // Get all commands that match the base.
        List<Command> matches = getMatchingCommands(base);

        // If there's more than one match, display them.
        if (matches.size() > 1) {
            am.sendMessage(sender, MessageKey.MISC_MULTIPLE_MATCHES);
            for (Command cmd : matches) {
                showUsage(cmd, sender, false);
            }
            return true;
        }

        // If there are no matches at all, notify.
        if (matches.size() == 0) {
            am.sendMessage(sender, MessageKey.MISC_NO_MATCHES);
            return true;
        }

        // Grab the only match.
        Command command  = matches.get(0);
        CommandInfo info = command.getClass().getAnnotation(CommandInfo.class);

        // First check if the sender has permission.
        if (!sender.hasPermission(info.permission())) {
            am.sendMessage(sender, MessageKey.MISC_NO_ACCESS);
            return true;
        }

        // Check if the last argument is a ?, in which case, display usage and description
        if (last.equals("?") || last.equals("help")) {
            showUsage(command, sender, true);
            return true;
        }

        // Otherwise, execute the command!
        String[] params = trimFirstArg(args);
        if (!command.execute(am, sender, params)) {
            showUsage(command, sender, true);
        }
        return true;
    }

    private boolean reload(CommandSender sender) {
        if (!sender.hasPermission("mobarena.setup.load") && !sender.hasPermission("mobarena.setup.config")) {
            return safeTell(sender, MessageKey.MISC_NO_ACCESS);
        }
        try {
            plugin.reload();
            plugin.sendMessage(sender, "Reload complete.");
        } catch (ConfigError e) {
            plugin.sendMessage(sender, "Reload failed due to config-file error:\n" + ChatColor.RED + e.getMessage());
        } catch (Exception e) {
            plugin.sendMessage(sender, "Reload failed:\n" + ChatColor.RED + e.getMessage());
        }
        return true;
    }

    private boolean safeTell(CommandSender sender, MessageKey msg) {
        ArenaMaster am = plugin.getArenaMaster();
        if (am != null) {
            am.sendMessage(sender, msg);
        } else {
            plugin.sendMessage(sender, msg);
        }
        return true;
    }

    /**
     * Get all commands that match a given string.
     * @param arg the given string
     * @return a list of commands whose patterns match the given string
     */
    private List<Command> getMatchingCommands(String arg) {
        List<Command> result = new ArrayList<>();

        // Grab the commands that match the argument.
        for (Entry<String,Command> entry : commands.entrySet()) {
            if (arg.matches(entry.getKey())) {
                result.add(entry.getValue());
            }
        }

        return result;
    }

    /**
     * Show the usage and description messages of a command to a player.
     * The usage will only be shown, if the player has permission for the command.
     * @param cmd a Command
     * @param sender a CommandSender
     */
    private void showUsage(Command cmd, CommandSender sender, boolean prefix) {
        CommandInfo info = cmd.getClass().getAnnotation(CommandInfo.class);
        if (!sender.hasPermission(info.permission())) return;

        sender.sendMessage((prefix ? "Usage: " : "") + info.usage() + " " + ChatColor.YELLOW + info.desc());
    }

    /**
     * Remove the first argument of a string. This is because the very first
     * element of the arguments array will be the command itself.
     * @param args an array of length n
     * @return the same array minus the first element, and thus of length n-1
     */
    private String[] trimFirstArg(String[] args) {
        return Arrays.copyOfRange(args, 1, args.length);
    }

    /**
     * List all the available MobArena commands for the CommandSender.
     * @param sender a player or the console
     */
    private void showHelp(CommandSender sender) {
        StringBuilder user = new StringBuilder();
        StringBuilder admin = new StringBuilder();
        StringBuilder setup = new StringBuilder();

        for (Command cmd : commands.values()) {
            CommandInfo info = cmd.getClass().getAnnotation(CommandInfo.class);
            if (!sender.hasPermission(info.permission())) continue;

            StringBuilder buffy;
            if (info.permission().startsWith("mobarena.admin")) {
                buffy = admin;
            } else if (info.permission().startsWith("mobarena.setup")) {
                buffy = setup;
            } else {
                buffy = user;
            }
            buffy.append("\n")
                 .append(ChatColor.RESET).append(info.usage()).append(" ")
                 .append(ChatColor.YELLOW).append(info.desc());
        }

        ArenaMaster am = plugin.getArenaMaster();

        if (admin.length() == 0 && setup.length() == 0) {
            am.sendMessage(sender, "Available commands: " + user.toString());
        } else {
            am.sendMessage(sender, "User commands: " + user.toString());
            if (admin.length() > 0) am.sendMessage(sender, "Admin commands: " + admin.toString());
            if (setup.length() > 0) am.sendMessage(sender, "Setup commands: " + setup.toString());
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command bcmd, String alias, String[] args) {
        // Only players can tab complete
        if (!(sender instanceof Player)) {
            return null;
        }
        Player player = (Player) sender;

        // If the player is in a convo (Setup Mode), bail
        if (player.isConversing()) {
            return null;
        }

        // Grab the base argument.
        String base = (args.length > 0 ? args[0] : "").toLowerCase();

        // If there's no base argument, show it all
        if (base.equals("")) {
            return commands.values()
                .stream()
                .map(cmd -> cmd.getClass().getAnnotation(CommandInfo.class))
                .filter(info -> info != null && player.hasPermission(info.permission()))
                .map(CommandInfo::name)
                .sorted()
                .collect(Collectors.toList());
        }

        // Reloads are terminal
        if (base.equals("reload") || (base.equals("config") && args.length > 1 && args[1].equals("reload"))) {
            return Collections.emptyList();
        }

        // If we only have the base, terminate
        if (args.length == 1) {
            return commands.values().stream()
                .map(cmd -> cmd.getClass().getAnnotation(CommandInfo.class))
                .map(CommandInfo::name)
                .filter(name -> name.startsWith(base))
                .sorted()
                .collect(Collectors.toList());
        }

        // Otherwise, find the command
        List<Command> matches = getMatchingCommands(base);
        if (matches.size() != 1) {
            return Collections.emptyList();
        }

        // And pass completion
        Command command = matches.get(0);
        String[] params = trimFirstArg(args);
        return command.tab(plugin.getArenaMaster(), player, params);
    }

    /**
     * Register all the commands directly.
     * This could also be done with a somewhat dirty classloader/resource reader
     * method, but this is neater, albeit more manual work.
     */
    private void registerCommands() {
        commands = new LinkedHashMap<>();

        // mobarena.use
        register(JoinCommand.class);
        register(LeaveCommand.class);
        register(SpecCommand.class);
        register(ArenaListCommand.class);
        register(PlayerListCommand.class);
        register(NotReadyCommand.class);
        register(PickClassCommand.class);
        register(ReadyCommand.class);

        // mobarena.admin
        register(EnableCommand.class);
        register(DisableCommand.class);
        register(ForceCommand.class);
        register(KickCommand.class);
        register(RestoreCommand.class);
        register(AddRewardCommand.class);

        // mobarena.setup
        register(SetupCommand.class);
        register(SettingCommand.class);

        register(AddArenaCommand.class);
        register(RemoveArenaCommand.class);
        register(EditArenaCommand.class);
        register(CheckDataCommand.class);

        register(RemoveSpawnpointCommand.class);
        register(CheckSpawnsCommand.class);
        register(RemoveContainerCommand.class);

        register(ListClassesCommand.class);
        register(ClassChestCommand.class);

        register(RemoveLeaderboardCommand.class);
        register(AutoGenerateCommand.class);
    }

    /**
     * Register a command.
     * The Command's CommandInfo annotation is queried to find its pattern
     * string, which is used to map the commands.
     * @param c a Command
     */
    public void register(Class<? extends Command> c) {
        try {
            Command command = c.newInstance();
            register(command);
        } catch (ReflectiveOperationException e) {
            throw new IllegalArgumentException("Failed to instantiate Command class: " + c.getName(), e);
        }
    }

    /**
     * Register a command instance.
     * <p>
     * Adds the given command to MobArena's internal command handler as a
     * subcommand, overwriting any existing subcommand mappings. This means
     * that the method is safe to call on reloads as long as a reload does
     * not change the pattern of a registered command.
     *
     * @param command the Command instance to register
     * @throws IllegalArgumentException if the CommandInfo annotation is
     * missing from the class of the Command instance
     */
    public void register(Command command) {
        Class<?> cls = command.getClass();
        CommandInfo info = cls.getAnnotation(CommandInfo.class);
        if (info == null) {
            throw new IllegalArgumentException("Missing CommandInfo annotation on class " + cls.getName());
        }

        commands.put(info.pattern(), command);
    }

}
