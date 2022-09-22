package com.garbagemule.MobArena.commands.setup;

import com.garbagemule.MobArena.commands.Command;
import com.garbagemule.MobArena.commands.CommandInfo;
import com.garbagemule.MobArena.framework.ArenaMaster;
import org.bukkit.command.CommandSender;

import java.util.Set;

@CommandInfo(
    name    = "listclasses",
    pattern = "(list)?classes(.)*",
    usage   = "/ma listclasses",
    desc    = "list all current classes",
    permission = "mobarena.setup.classes"
)
public class ListClassesCommand implements Command
{
    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        am.sendMessage(sender, "Current classes:");
        Set<String> classes = am.getClasses().keySet();
        if (classes == null || classes.isEmpty()) {
            am.sendMessage(sender, "<none>");
            return true;
        }

        for (String c : classes) {
            am.sendMessage(sender, "- " + c);
        }
        return true;
    }
}
