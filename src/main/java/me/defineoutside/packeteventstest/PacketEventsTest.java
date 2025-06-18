package me.defineoutside.packeteventstest;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import org.bukkit.plugin.java.JavaPlugin;
//This plugin is a tool to help us, developers, test how stable PacketEvents is. It's a heavy plugin, please don't run it on your server.
public final class PacketEventsTest extends JavaPlugin {
    private static PacketEventsTest INSTANCE = null;
    @Override
    public void onLoad() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        PacketEvents.getAPI().getEventManager().registerListener(new PEListener(), PacketListenerPriority.NORMAL);

        System.out.println("Server version: " + PacketEvents.getAPI().getServerManager().getVersion());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static PacketEventsTest getInstance() {
        return INSTANCE;
    }
}
