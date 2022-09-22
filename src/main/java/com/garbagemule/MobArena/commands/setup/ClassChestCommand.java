package com.garbagemule.MobArena.commands.setup;

import com.garbagemule.MobArena.ArenaClass;
import com.garbagemule.MobArena.commands.Command;
import com.garbagemule.MobArena.commands.CommandInfo;
import com.garbagemule.MobArena.commands.Commands;
import com.garbagemule.MobArena.framework.ArenaMaster;
import com.garbagemule.MobArena.message.MessageKey;
import com.garbagemule.MobArena.util.Slugs;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.garbagemule.MobArena.util.config.ConfigUtils.setLocation;

@CommandInfo(
    name    = "classchest",
    pattern = "classchest",
    usage   = "/ma classchest <class>",
    desc    = "link a chest to a class",
    permission = "mobarena.setup.classchest"
)
public class ClassChestCommand implements Command {
    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        if (!Commands.isPlayer(sender)) {
            am.sendMessage(sender, MessageKey.MISC_NOT_FROM_CONSOLE);
            return true;
        }

        // Require a class name
        if (args.length != 1) return false;

        String slug = Slugs.create(args[0]);
        ArenaClass ac = am.getClasses().get(slug);
        if (ac == null) {
            am.sendMessage(sender, "Class not found.");
            return true;
        }

        Player p = Commands.unwrap(sender);
        Block b = p.getTargetBlock((Set<Material>) null, 10);

        switch (b.getType()) {
            case CHEST:
            case ENDER_CHEST:
            case TRAPPED_CHEST:
                break;
            default:
                am.sendMessage(sender, "You must look at a chest.");
                return true;
        }

        setLocation(am.getPlugin().getConfig(), "classes." + ac.getConfigName() + ".classchest", b.getLocation());
        am.saveConfig();
        am.sendMessage(sender, "Class chest updated for class " + ac.getConfigName());
        am.loadClasses();
        return true;
    }

    @Override
    public List<String> tab(ArenaMaster am, Player player, String... args) {
        if (args.length > 1) {
            return Collections.emptyList();
        }

        String prefix = Slugs.create(args[0]);

        Collection<ArenaClass> classes = am.getClasses().values();

        return classes.stream()
            .filter(cls -> cls.getSlug().startsWith(prefix))
            .map(ArenaClass::getSlug)
            .collect(Collectors.toList());
    }
}
