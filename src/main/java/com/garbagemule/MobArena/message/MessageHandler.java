package com.garbagemule.MobArena.message;

import com.garbagemule.MobArena.Messenger;
import com.garbagemule.MobArena.announce.Announcer;
import com.garbagemule.MobArena.events.MobArenaPreMessageEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class MessageHandler{
    
    private final Map<String, String> messages = new HashMap<>();
    
    public MessageHandler(ConfigurationSection section) {
        populateMap(section);
    }
    
    public void sendMessage(CommandSender sender, MessageKey key, Messenger messenger){
        sendMessage(sender, key, null, messenger);
    }
    
    public void sendAnnouncement(Player player, MessageKey key, Announcer announcer) {
        sendAnnouncement(player, key, null, announcer);
    }
    
    public void sendAnnouncement(Player player, MessageKey key, String s, Announcer announcer) {
        String msg = getMessage(player, key);
        if(msg == null || msg.isEmpty())
            return;
        
        announcer.announce(player, s == null ? msg : msg.replace("%", s));
    }
    
    public void sendMessage(CommandSender sender, MessageKey key, String s, Messenger messenger) {
        String msg = getMessage(sender, key);
        if(msg == null || msg.isEmpty())
            return;
        
        messenger.tell(sender, msg, s);
    }
    
    public String getMessage(MessageKey key) {
        return messages.get(key.getKey());
    }
    
    private String getMessage(CommandSender sender, MessageKey key) {
        String msg = messages.get(key.getKey());
        if( msg == null || msg.isEmpty())
            return null;
        
        MobArenaPreMessageEvent event = new MobArenaPreMessageEvent(sender, msg);
        Bukkit.getPluginManager().callEvent(event);
        
        return event.getMessage();
    }
    
    private void populateMap(ConfigurationSection section) {
        messages.clear();
        
        for(String key : section.getKeys(false)) {
            String str = section.getString(key);
            if(str == null)
                continue;
            
            messages.put(key, str);
        }
    }
}
