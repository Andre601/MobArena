package com.garbagemule.MobArena;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;

public class MACommands implements CommandExecutor
{
    /**
     * Handles all command parsing.
     * Unrecognized commands return false, giving the sender a list of
     * valid commands (from plugin.yml).
     */
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args)
    {
        // Only accept commands from players.
        if ((sender == null) || !(sender instanceof Player))
        {
            System.out.println("Only players can use these commands, silly.");
            return true;
        }
        
        // Cast the sender to a Player object.
        Player p = (Player) sender;
        
        /* If more than one argument, must be an advanced command.
         * Only allow operators to access these commands. */
        if (args.length > 1)
        {
            if (p.isOp())
                return advancedCommands(p, args);
            
            ArenaManager.tellPlayer(p, "Must be operator for advanced commands.");
            return true;
        }
        
        // If not exactly one argument, must be an invalid command.
        if (args.length != 1)
            return false;
        
        // Exactly one argument, return whatever simpleCommands returns.
        return basicCommands(p, args[0].toLowerCase());
    }
    
    /**
     * Handles basic commands.
     */
    private boolean basicCommands(Player p, String cmd)
    {
        if (cmd.equals("join") || cmd.equals("j"))
        {
            ArenaManager.playerJoin(p);
            return true;
        }
        
        if (cmd.equalsIgnoreCase("leave") || cmd.equalsIgnoreCase("l"))
        {
            ArenaManager.playerLeave(p);
            return true;
        }
        
        if (cmd.equalsIgnoreCase("list") || cmd.equalsIgnoreCase("who"))
        {
            ArenaManager.playerList(p);
            return true;
        }

        if (cmd.equalsIgnoreCase("spectate") || cmd.equalsIgnoreCase("spec"))
        {
            ArenaManager.playerSpectate(p);
            return true;
        }
        
        return false;
    }
    
    /**
     * Handles advanced commands, mainly for setting up the arena.
     */
    private boolean advancedCommands(Player p, String[] args)
    {        
        String cmd = args[0].toLowerCase();
        String arg = args[1].toLowerCase();
        
        // ma setwarp arena/lobby/spectator
        if (cmd.equals("setwarp"))
        {
            if (!arg.equals("arena") && !arg.equals("lobby") && !arg.equals("spectator"))
                return false;
            
            // Write the coordinate data to the config-file.
            MAUtils.setCoords(arg, p.getLocation());
            
            ArenaManager.tellPlayer(p, "Warp point \"" + arg + "\" set.");
            return true;
        }
        
        // ma addspawn <name>
        if (cmd.equals("addspawn"))
        {
            // The name must start with a letter, followed by any letter(s) or number(s).
            if (!arg.matches("[a-z]+([[0-9][a-z]])*"))
            {
                ArenaManager.tellPlayer(p, "Name must consist of only letters a-z and numbers 0-9");
                return true;
            }
            
            // Write the coordinate data to the config-file.
            MAUtils.setCoords("spawnpoints." + arg, p.getLocation());
            
            ArenaManager.tellPlayer(p, "Spawn point with name \"" + arg + "\" added.");
            return true;
        }
        
        // ma delspawn <name>
        if (cmd.equals("delspawn"))
        {
            // The name must start with a letter, followed by any letter(s) or number(s).
            if (!arg.matches("[a-z]+([[0-9][a-z]])*"))
            {
                ArenaManager.tellPlayer(p, "Name must consist of only letters a-z and numbers 0-9");
                return true;
            }
            
            // If the spawnpoint does not exist, notify the player.
            if (MAUtils.getCoords("spawnpoints." + arg) == null)
            {
                ArenaManager.tellPlayer(p, "Couldn't find spawnpoint \"" + arg + "\".");
                return true;
            }
            
            MAUtils.delCoords("spawnpoints." + arg);
            
            ArenaManager.tellPlayer(p, "Spawn point with name \"" + arg + "\" removed.");
            return true;
        }
        
        // ma setregionpoint p1/p2
        if (cmd.equalsIgnoreCase("setregion"))
        {            
            if (arg.equals("p1") || arg.equals("p2"))
            {
                MAUtils.setCoords(arg, p.getLocation());
                MAUtils.fixCoords();
                
                ArenaManager.tellPlayer(p, "Region point \"" + arg + "\" set.");
                return true;
            }
            ArenaManager.tellPlayer(p, "/ma setregionpoint p1, or /ma setregionpoint p2");
            return true;
        }
        
        // ma expandregion up/down/out <number>
        if (cmd.equalsIgnoreCase("expandregion"))
        {
            if (arg.equals("up") || arg.equals("down") || arg.equals("out"))
            {
                if (args.length != 3 || !args[2].matches("[0-9]+"))
                    return false;
                
                int i = Integer.parseInt(args[2]);
                MAUtils.expandRegion(arg, i);
                
                ArenaManager.tellPlayer(p, "Region expanded " + arg + " by " + i + " blocks.");
                return true;
            }
            ArenaManager.tellPlayer(p, "Region point \"" + arg + "\" set.");
            return true;
        }
        
        return false;
    }
}