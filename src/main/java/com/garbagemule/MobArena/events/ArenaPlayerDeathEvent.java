package com.garbagemule.MobArena.events;

import com.garbagemule.MobArena.framework.Arena;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when a player died in an Arena.
 */
public class ArenaPlayerDeathEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final Arena arena;
    private final boolean last;

    public ArenaPlayerDeathEvent(Player player, Arena arena, boolean last) {
        this.player = player;
        this.arena  = arena;
        this.last   = last;
    }
    
    /**
     * Get the player that died.
     * 
     * @return The player that died.
     */
    public Player getPlayer() {
        return player;
    }
    
    /**
     * Get the Arena this event was called for.
     * 
     * @return An Arena.
     */
    public Arena getArena() {
        return arena;
    }
    
    /**
     * Returns whether the player in this event was the last one in the arena.
     * 
     * @return True or false depending on if the player was the last one.
     */
    public boolean wasLastPlayerStanding() {
        return last;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
