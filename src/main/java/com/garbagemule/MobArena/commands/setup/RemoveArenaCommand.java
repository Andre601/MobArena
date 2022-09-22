package com.garbagemule.MobArena.commands.setup;

import com.garbagemule.MobArena.commands.Command;
import com.garbagemule.MobArena.commands.CommandInfo;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@CommandInfo(
    name    = "removearena",
    pattern = "(del(.)*|r(e)?m(ove)?)arena",
    usage   = "/ma removearena <arena>",
    desc    = "remove an arena",
    permission = "mobarena.setup.removearena"
)
public class RemoveArenaCommand implements Command
{
    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        // Require an arena name
        if (args.length != 1) return false;

        if (am.getArenas().size() == 1) {
            am.sendMessage(sender, "At least one arena must exist.");
            return true;
        }

        Arena arena = am.getArenaWithName(args[0]);
        if (arena == null) {
            am.sendMessage(sender, "There is no arena with that name.");
            return true;
        }
        am.removeArenaNode(arena);
        am.sendMessage(sender, "Arena '" + arena.configName() + "' deleted.");
        return true;
    }

    @Override
    public List<String> tab(ArenaMaster am, Player player, String... args) {
        if (args.length > 1) {
            return Collections.emptyList();
        }

        String prefix = args[0].toLowerCase();

        List<Arena> arenas = am.getArenas();

        return arenas.stream()
            .filter(arena -> arena.getSlug().startsWith(prefix))
            .map(Arena::getSlug)
            .collect(Collectors.toList());
    }
}
