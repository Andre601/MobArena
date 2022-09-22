package com.garbagemule.MobArena.commands.setup;

import com.garbagemule.MobArena.commands.Command;
import com.garbagemule.MobArena.commands.CommandInfo;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;
import org.bukkit.command.CommandSender;

@CommandInfo(
    name    = "delspawn",
    pattern = "(del(.)*|r(e)?m(ove)?)spawn(point)?",
    usage   = "/ma delspawn <arena> <point>",
    desc    = "delete a spawnpoint",
    permission = "mobarena.setup.spawnpoints"
)
public class RemoveSpawnpointCommand implements Command
{
    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        if (args.length < 1) return false;

        Arena arena;
        String point;
        if (args.length == 1) {
            if (am.getArenas().size() > 1) {
                am.sendMessage(sender, "There are multiple arenas.");
                return true;
            }
            arena = am.getArenas().get(0);
            point = args[0];
        } else {
            arena = am.getArenaWithName(args[0]);
            if (arena == null) {
                am.sendMessage(sender, "There is no arena named " + args[0]);
                return true;
            }
            point = args[1];
        }

        if (arena.getRegion().removeSpawn(point)) {
            am.sendMessage(sender, "Spawnpoint " + point + " removed for arena '" + arena.configName() + "'");
        } else {
            am.sendMessage(sender, "Could not find the spawnpoint " + point + " for the arena '" + arena.configName() + "'");
        }
        return true;
    }
}
