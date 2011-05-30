package com.garbagemule.MobArena;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;
import org.bukkit.World;
import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.config.Configuration;

public class MAUtils
{
    /* ///////////////////////////////////////////////////////////////////// //
    
            INVENTORY AND REWARD METHODS
    
    // ///////////////////////////////////////////////////////////////////// */

    /* Clears the players inventory and armor slots. */
    public static PlayerInventory clearInventory(Player p)
    {
        PlayerInventory inv = p.getInventory();
        inv.clear();
        inv.setHelmet(null);
        inv.setChestplate(null);
        inv.setLeggings(null);
        inv.setBoots(null);
        return inv;
    }
    
    /* Checks if all inventory and armor slots are empty. */
    public static boolean hasEmptyInventory(Player player)
    {
		ItemStack[] inventory = player.getInventory().getContents();
		ItemStack[] armor     = player.getInventory().getArmorContents();
        
        // For inventory, check for null
        for (ItemStack stack : inventory)
            if (stack != null) return false;
        
        // For armor, check for id 0, or AIR
        for (ItemStack stack : armor)
            if (stack.getTypeId() != 0) return false;
        
        return true;
	}
    
    /* Gives all the items in the input string(s) to the player */
    public static void giveItems(Player p, String... strings)
    {
        // Variables used.
        ItemStack stack;
        int index, id, amount;
        
        PlayerInventory inv = clearInventory(p);
        
        for (String s : strings)
        {
            /* Trim the list, remove possible trailing commas, split by
             * commas, and start the item loop. */
            s = s.trim();
            if (s.endsWith(","))
                s = s.substring(0, s.length()-1);
            String[] items = s.split(",");
            
            // For every item in the list
            for (String i : items)
            {
                /* Take into account possible amount, and if there is
                 * one, set the amount variable to that amount, else 1. */
                i = i.trim();
                String[] item = i.split(":");
                if (item.length == 2 && item[1].matches("[0-9]+"))
                    amount = Integer.parseInt(item[1]);
                else
                    amount = 1;
                
                // Create ItemStack with appropriate constructor.
                if (item[0].matches("[0-9]+"))
                {
                    id = Integer.parseInt(item[0]);
                    stack = new ItemStack(id, amount);
                }
                else
                {
                    stack = makeItemStack(item[0], amount);
                    if (stack == null) continue;
                }
                
                // Put the item in the first empty inventory slot.
                index = inv.firstEmpty();
                inv.setItem(index,stack);
            }
        }
    }
    
    /* Helper method for grabbing a random reward */
    public static String getRandomReward(String rewardlist)
    {
        Random ran = new Random();
        
        String[] rewards = rewardlist.split(",");
        String item = rewards[ran.nextInt(rewards.length)];
        return item.trim();
    }
    
    /* Helper method for making an ItemStack out of a string */
    private static ItemStack makeItemStack(String s, int amount)
    {
        Material mat;
        try
        {
            mat = Material.valueOf(s.toUpperCase());
            return new ItemStack(mat, amount);
        }
        catch (Exception e)
        {
            System.out.println("[MobArena] ERROR! Could not create item " + s + ". Check config.yml");
            return null;
        }
    }
    
    
    
    /* ///////////////////////////////////////////////////////////////////// //
    
            INITIALIZATION METHODS
    
    // ///////////////////////////////////////////////////////////////////// */
    
    /**
     * Creates a Configuration object from the config.yml file.
     */
    public static Configuration getConfig()
    {
        new File("plugins/MobArena").mkdir();
        File configFile = new File("plugins/MobArena/config.yml");
        
		if(!configFile.exists())
        {
		    try
            {
                configFile.createNewFile();
            }
            catch(Exception e)
            {
                System.out.println("[MobArena] ERROR: Config file could not be created.");
                return null;
            }
		}
        
        return new Configuration(configFile);
    }
    
    /**
     * Grabs the world from the config-file, or the "default" world
     * from the list of worlds in the server object.
     */
    public static World getWorld()
    {
        Configuration c = ArenaManager.config;
        c.load();
        
        String world = c.getString("world");
        
        if (world == null)
            return ArenaManager.server.getWorlds().get(0);
        
        return ArenaManager.server.getWorld(world);
    }
    
    /**
     * Grabs the list of classes from the config-file. If no list is
     * found, generate a set of default classes.
     */
    public static List<String> getClasses()
    {
        Configuration c = ArenaManager.config;
        c.load();
        
        if (c.getKeys("classes") == null)
        {
            c.setProperty("classes.Archer.items", "wood_sword, bow, arrow:64, arrow:64, grilled_pork");
            c.setProperty("classes.Archer.armor", "298,299,300,301");
            c.setProperty("classes.Knight.items", "diamond_sword, grilled_pork");
            c.setProperty("classes.Knight.armor", "306,307,308,309");
            c.setProperty("classes.Tank.items",   "iron_sword, grilled_pork:2");
            c.setProperty("classes.Tank.armor",   "310,311,312,313");
            c.setProperty("classes.Oddjob.items", "stone_sword, flint_and_steal, netherrack:2, wood_door, fishing_rod, apple, grilled_pork:3");
            c.setProperty("classes.Oddjob.armor", "298,299,300,301");
            c.setProperty("classes.Chef.items",   "stone_sword, bread:6, grilled_pork:4, mushroom_soup, cake:3, cookie:12");
            c.setProperty("classes.Chef.armor",   "314,315,316,317");
            
            c.save();
        }
        
        return c.getKeys("classes");
    }
    
    /**
     * Generates a map of class names and class items based on the
     * type of items ("items" or "armor") and the config-file.
     * Will explode if the classes aren't well-defined.
     */
    public static Map<String,String> getClassItems(String type)
    {
        Configuration c = ArenaManager.config;
        c.load();
        
        Map<String,String> result = new HashMap<String,String>();
        
        // Assuming well-defined classes.
        List<String> classes = c.getKeys("classes");
        for (String s : classes)
        {
            result.put(s, c.getString("classes." + s + "." + type, null));
        }
        
        return result;
    }
    
    /**
     * Generates a map of wave numbers and rewards based on the
     * type of wave ("after" or "every") and the config-file. If
     * no keys exist in the config-file, an empty map is returned.
     */
    public static Map<Integer,String> getWaveMap(String type)
    {
        Configuration c = ArenaManager.config;
        c.load();
        
        // Set up variables and resulting map.
        Map<Integer,String> result = new HashMap<Integer,String>();
        int wave;
        String rewards;
        
        /* Check if the keys exist in the config-file, if not,
         * simply return the empty map. */
        List<String> waves = c.getKeys("rewards.waves." + type);
        if (waves == null)
            return result;
        
        // Else, put all the rewards in the map.
        for (String n : waves)
        {
            if (!n.matches("[0-9]+"))
                continue;
            
            wave = Integer.parseInt(n);
            rewards = c.getString("rewards.waves." + type + "." + n);
            
            result.put(wave,rewards);
        }
        
        // And return the resulting map.
        return result;
    }
    
    /**
     * Grabs all the spawnpoints from the config-file. IF no points
     * are found, an empty list is returned.
     */
    public static List<Location> getSpawnPoints()
    {
        Configuration c = ArenaManager.config;
        c.load();
        
        List<String> spawnpoints = c.getKeys("coords.spawnpoints");
        if (spawnpoints == null)
            return new LinkedList<Location>();
        
        List<Location> result = new LinkedList<Location>();
        for (String s : spawnpoints)
        {
            Location loc = getCoords("spawnpoints." + s);
            
            if (loc != null)
                result.add(loc);
        }
        
        return result;
    }
    
    /**
     * Grabs the distribution coefficients from the config-file. If
     * no coefficients are found, defaults (10) are added.
     */
    public static int getDistribution(String monster)
    {
        Configuration c = ArenaManager.config;
        c.load();
        
        if (c.getInt("waves.default." + monster, -1) == -1)
        {
            c.setProperty("waves.default." + monster, 10);
            c.save();
        }
        
        return c.getInt("waves.default." + monster, -1);
    }
    
    
    
    /* ///////////////////////////////////////////////////////////////////// //
    
            REGION AND SETUP METHODS
    
    // ///////////////////////////////////////////////////////////////////// */
    
    /**
     * Checks if the Location object is within the arena region.
     */
    public static boolean inRegion(Location loc)
    {
        Location p1 = ArenaManager.p1;
        Location p2 = ArenaManager.p2;
        
        // Return false if the location is outside of the region.
        if ((loc.getX() < p1.getX()) || (loc.getX() > p2.getX()))
            return false;
            
        if ((loc.getZ() < p1.getZ()) || (loc.getZ() > p2.getZ()))
            return false;
            
        if ((loc.getY() < p1.getY()) || (loc.getY() > p2.getY()))
            return false;
            
        return true;
    }
    
    /**
     * Writes coordinate information to the config-file.
     */
    public static void setCoords(String name, Location loc)
    {
        Configuration c = ArenaManager.config;
        c.load();
        
        c.setProperty("coords." + name + ".world", loc.getWorld().getName());
        c.setProperty("coords." + name + ".x",     loc.getX());
        c.setProperty("coords." + name + ".y",     loc.getY());
        c.setProperty("coords." + name + ".z",     loc.getZ());
        c.setProperty("coords." + name + ".yaw",   loc.getYaw());
        c.setProperty("coords." + name + ".pitch", loc.getPitch());
        
        c.save();
        ArenaManager.updateVariables();
    }
    
    /**
     * Removes coordinate information from the config-file.
     */
    public static void delCoords(String name)
    {
        Configuration c = ArenaManager.config;
        c.load();
        
        c.removeProperty(name);
        
        c.save();
        ArenaManager.updateVariables();
    }
    
    /**
     * Grabs coordinate information from the config-file.
     */
    public static Location getCoords(String name)
    {
        Configuration c = ArenaManager.config;
        c.load();
        
        // Return null if coords aren't in the config file.
        if (c.getKeys("coords." + name) == null)
            return null;
        
        double x    = c.getDouble("coords." + name + ".x", 0);
        double y    = c.getDouble("coords." + name + ".y", 0);
        double z    = c.getDouble("coords." + name + ".z", 0);
        
        return new Location(ArenaManager.world, x, y, z);
    }
    
    /**
     * Maintains the invariant that p1's coordinates are of lower
     * values than their respective counter-parts of p2. Makes the
     * inRegion()-method much faster/easier.
     */
    public static void fixCoords()
    {
        Location p1 = getCoords("p1");
        Location p2 = getCoords("p2");
        double tmp;
        
        if (p1 == null || p2 == null)
            return;
            
        if (p1.getX() > p2.getX())
        {
            tmp = p1.getX();
            p1.setX(p2.getX());
            p2.setX(tmp);
        }
        
        if (p1.getY() > p2.getY())
        {
            tmp = p1.getY();
            p1.setY(p2.getY());
            p2.setY(tmp);
        }
        
        if (p1.getZ() > p2.getZ())
        {
            tmp = p1.getZ();
            p1.setZ(p2.getZ());
            p2.setZ(tmp);
        }
        
        setCoords("p1", p1);
        setCoords("p2", p2);
    }
    
    /**
     * Expands the arena region either upwards, downwards, or
     * outwards (meaning on both the X and Z axes).
     */
    public static void expandRegion(String direction, int i)
    {
        Location p1 = ArenaManager.p1;
        Location p2 = ArenaManager.p2;
        
        if (direction.equals("up"))
            p2.setY(p2.getY() + i);
        else if (direction.equals("down"))
            p1.setY(p1.getY() - i);
        else if (direction.equals("out"))
        {
            p1.setX(p1.getX() - i);
            p1.setZ(p1.getZ() - i);
            p2.setX(p2.getX() + i);
            p2.setZ(p2.getZ() + i);
        }
        
        setCoords("p1", p1);
        setCoords("p2", p2);
        fixCoords();
    }
    
    
    
    /* ///////////////////////////////////////////////////////////////////// //
    
            VERIFICATION METHODS
    
    // ///////////////////////////////////////////////////////////////////// */
    
    /**
     * Verifies that all important variables are declared. Returns true
     * if, and only if, the warppoints, region, distribution coefficients,
     * classes and spawnpoints are all set up.
     */
    public static boolean verifyData()
    {
        return ((ArenaManager.arenaLoc     != null) &&
                (ArenaManager.lobbyLoc     != null) &&
                (ArenaManager.spectatorLoc != null) &&
                (ArenaManager.p1           != null) &&
                (ArenaManager.p2           != null) &&
                (ArenaManager.dZombies     != -1)   &&
                (ArenaManager.dSkeletons   != -1)   &&
                (ArenaManager.dSpiders     != -1)   &&
                (ArenaManager.dCreepers    != -1)   &&
                (ArenaManager.classes.size() > 0)   &&
                (ArenaManager.spawnpoints.size() > 0));
                
    }
}