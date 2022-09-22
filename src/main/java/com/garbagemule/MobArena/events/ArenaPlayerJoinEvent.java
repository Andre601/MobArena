package com.garbagemule.MobArena.events;

import com.garbagemule.MobArena.framework.Arena;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when a player joins an arena.
 */
public class ArenaPlayerJoinEvent extends Event implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final Arena arena;
    private boolean cancelled;

    public ArenaPlayerJoinEvent(Player player, Arena arena) {
        this.player = player;
        this.arena =  arena;
        this.cancelled = false;
    }
    
    /**
     * The player that joined the arena.
     * 
     * @return The player that joined the arena.
     */
    public Player getPlayer() {
        return player;
    }
    
    /**
     * The Arena where the event happened in.
     * 
     * @return An Arena.
     */
    public Arena getArena() {
        return arena;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
