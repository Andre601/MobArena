package com.garbagemule.MobArena.commands.user;

import com.garbagemule.MobArena.MAUtils;
import com.garbagemule.MobArena.commands.Command;
import com.garbagemule.MobArena.commands.CommandInfo;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;
import com.garbagemule.MobArena.message.MessageKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;

@CommandInfo(
    name    = "playerlist",
    pattern = "players|playerlist|player-list",
    usage   = "/ma players (<arena>)",
    desc    = "lists players in an arena",
    permission = "mobarena.use.playerlist"
)
public class PlayerListCommand implements Command
{
    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        // Grab the argument, if any.
        String arg1 = (args.length > 0 ? args[0] : "");

        String list = null;
        if (!arg1.equals("")) {
            Arena arena = am.getArenaWithName(arg1);

            if (arena == null) {
                am.sendMessage(sender, MessageKey.ARENA_DOES_NOT_EXIST);
                return false;
            }

            list = MAUtils.listToString(arena.getPlayersInArena(), am.getPlugin());
        } else {
            StringBuilder buffy = new StringBuilder();
            List<Player> players = new LinkedList<>();

            for (Arena arena : am.getArenas()) {
                players.addAll(arena.getPlayersInArena());
            }

            buffy.append(MAUtils.listToString(players, am.getPlugin()));
            list = buffy.toString();
        }

        am.sendMessage(sender, MessageKey.MISC_LIST_PLAYERS, list);
        return true;
    }
}
