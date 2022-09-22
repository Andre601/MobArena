package com.garbagemule.MobArena.commands.setup;

import com.garbagemule.MobArena.commands.Command;
import com.garbagemule.MobArena.commands.CommandInfo;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;
import org.bukkit.command.CommandSender;

@CommandInfo(
    name    = "removecontainer",
    pattern = "(del(.)*|r(e)?m(ove)?)(container|chest)",
    usage   = "/ma removecontainer <arena> <chest>",
    desc    = "remove a container from the selected arena",
    permission = "mobarena.setup.containers"
)
public class RemoveContainerCommand implements Command
{
    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        if (args.length < 1) return false;

        Arena arena;
        String chest;
        if (args.length == 1) {
            if (am.getArenas().size() > 1) {
                am.sendMessage(sender, "There are multiple arenas.");
                return true;
            }
            arena = am.getArenas().get(0);
            chest = args[0];
        } else {
            arena = am.getArenaWithName(args[0]);
            if (arena == null) {
                am.sendMessage(sender, "There is no arena named " + args[0]);
                return true;
            }
            chest = args[1];
        }

        if (arena.getRegion().removeChest(chest)) {
            am.sendMessage(sender, "Container " + chest + " removed for arena '" + arena.configName() + "'");
        } else {
            am.sendMessage(sender, "Could not find the container " + chest + " for the arena '" + arena.configName() + "'");
        }
        return true;
    }
}
