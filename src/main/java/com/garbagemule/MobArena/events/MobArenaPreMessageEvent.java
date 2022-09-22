package com.garbagemule.MobArena.events;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Called BEFORE a message is sent to players or the console.
 * <br>The message can be altered using {@link #setMessage(String)}.
 * 
 * <p>Setting an empty String or {@code null} will make MobArena not send the message.
 */
public class MobArenaPreMessageEvent extends Event
{
    
    private static final HandlerList handlers = new HandlerList();
    
    private final CommandSender sender;
    private String message;
    
    public MobArenaPreMessageEvent(CommandSender sender, String message){
        this.sender = sender;
        this.message = message;
    }
    
    public CommandSender getSender() {
        return sender;
    }
    
    /**
     * The message that should be send to players/the console.
     * 
     * @return The message to send. Can be null or empty
     */
    @Nullable
    public String getMessage() {
        return message;
    }
    
    /**
     * Sets the message to send to the players/console.
     * <br>Set to an empty String or {@code null} to not send the message at all.
     * 
     * @param message The new message to use.
     */
    public void setMessage(String message) {
        this.message = message;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    @Override
    @Nonnull
    public HandlerList getHandlers() {
        return handlers;
    }
}
