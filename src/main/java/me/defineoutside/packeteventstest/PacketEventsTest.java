package me.defineoutside.packeteventstest;

import com.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import org.bukkit.plugin.java.JavaPlugin;
//This plugin is a tool to help us, developers, test how stable PacketEvents is. It's a heavy plugin, please don't run it on your server.
public final class PacketEventsTest extends JavaPlugin {
    @Override
    public void onLoad() {
        //TODO Create UpdateAdvancements wrapper, update to 1.20, id = 105, writeBoolean was added in 1.20
        //TODO Create UpdateLight wrapper, update to 1.20, id = 39, a boolean was removed, most likely trustEdges
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().getSettings().bStats(true).checkForUpdates(false).debug(true);
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        PacketEvents.getAPI().getEventManager().registerListener(new PacketListener());
        PacketEvents.getAPI().init();

        System.out.println("Server version: " + PacketEvents.getAPI().getServerManager().getVersion());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        PacketEvents.getAPI().terminate();
    }
}
