package com.garbagemule.MobArena.commands.user;

import com.garbagemule.MobArena.commands.Command;
import com.garbagemule.MobArena.commands.CommandInfo;
import com.garbagemule.MobArena.commands.Commands;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;
import com.garbagemule.MobArena.message.MessageKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(
    name    = "join",
    pattern = "ready|rdy",
    usage   = "/ma ready",
    desc    = "ready to start the battle",
    permission = "mobarena.use.ready"
)
public class ReadyCommand implements Command {
    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        if (!Commands.isPlayer(sender)) {
            am.sendMessage(sender, MessageKey.MISC_NOT_FROM_CONSOLE);
            return true;
        }

        Player p = Commands.unwrap(sender);

        Arena arena = am.getArenaWithPlayer(p);
        if (arena == null) {
            am.sendMessage(p, MessageKey.NOT_IN_LOBBY);
        } else if (!arena.inLobby(p)) {
            arena.tell(p, MessageKey.NOT_IN_LOBBY);
        } else if (arena.getArenaPlayer(p).getArenaClass() != null) {
            arena.tell(p, MessageKey.LOBBY_PLAYER_READY);
            arena.playerReady(p);
        } else {
            arena.tell(p, MessageKey.LOBBY_PICK_CLASS);
        }

        return true;
    }

}
