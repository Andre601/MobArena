package com.garbagemule.MobArena.events;

import com.garbagemule.MobArena.framework.Arena;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when an Arena starts.
 */
public class ArenaStartEvent extends Event implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();
    private final Arena arena;
    private boolean cancelled;

    public ArenaStartEvent(Arena arena) {
        this.arena = arena;
        this.cancelled = false;
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
