package com.garbagemule.MobArena.events;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.Wave;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;


/**
 * Called when a new wave starts in an Arena.
 */
public class NewWaveEvent extends Event implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();
    private final Arena arena;
    private boolean cancelled;

    private final Wave wave;
    private final int waveNo;

    public NewWaveEvent(Arena arena, Wave wave, int waveNo) {
        this.arena  = arena;
        this.wave   = wave;
        this.waveNo = waveNo;
    }
    
    /**
     * The next {@link Wave} that starts.
     * @return Next wave.
     */
    public Wave getWave() {
        return wave;
    }
    
    /**
     * The current Wave number.
     * 
     * @return Current Wave number.
     */
    public int getWaveNumber() {
        return waveNo;
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
    
    @Override
    @Nonnull
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
