package com.garbagemule.MobArena.commands.admin;

import com.garbagemule.MobArena.commands.Command;
import com.garbagemule.MobArena.commands.CommandInfo;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;
import com.garbagemule.MobArena.message.MessageKey;
import com.garbagemule.MobArena.things.Thing;
import com.garbagemule.MobArena.things.ThingPicker;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@CommandInfo(
    name    = "addreward",
    pattern = "addreward",
    usage   = "/ma addreward <player> <thing>",
    desc    = "add a reward to an arena player's rewards list",
    permission = "mobarena.admin.addreward"
)
public class AddRewardCommand implements Command {

    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        if (args.length < 2) {
            return false;
        }

        Player player = am.getPlugin().getServer().getPlayer(args[0]);
        if (player == null) {
            am.sendMessage(sender, "Player not found.");
            return true;
        }

        Arena arena = am.getArenaWithPlayer(player);
        if (arena == null) {
            am.sendMessage(sender, "That player is not in an arena.");
            return true;
        }
        if (!arena.isRunning()) {
            am.sendMessage(sender, "That arena is not running.");
            return true;
        }
        if (!arena.getPlayersInArena().contains(player)) {
            am.sendMessage(sender, "That player is not an arena player.");
            return true;
        }

        String[] rest = Arrays.copyOfRange(args, 1, args.length);
        String input = String.join(" ", rest);

        Thing thing;
        try {
            ThingPicker picker = am.getPlugin().getThingPickerManager().parse(input);
            thing = picker.pick();
        } catch (Exception e) {
            am.sendMessage(sender, e.getMessage());
            return true;
        }

        arena.getRewardManager().addReward(player, thing);
        arena.tell(player, MessageKey.MISC_REWARD_ADDED, thing.toString());

        String msg = "Added " + ChatColor.YELLOW + thing + ChatColor.RESET + " to " + ChatColor.YELLOW + player.getName() + "'s" + ChatColor.RESET + " rewards.";
        am.sendMessage(sender, msg);

        return true;
    }

    @Override
    public List<String> tab(ArenaMaster am, Player player, String... args) {
        if (args.length > 1) {
            return Collections.emptyList();
        }

        String prefix = args[0].toLowerCase();

        List<Player> players = am.getAllPlayers();

        return players.stream()
            .filter(p -> p.getName().toLowerCase().startsWith(prefix))
            .map(Player::getName)
            .collect(Collectors.toList());
    }

}
