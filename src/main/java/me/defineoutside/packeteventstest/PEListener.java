package me.defineoutside.packeteventstest;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.exception.InvalidHandshakeException;
import com.github.retrooper.packetevents.protocol.world.chunk.*;
import com.github.retrooper.packetevents.protocol.world.chunk.impl.v_1_18.Chunk_v1_18;
import com.github.retrooper.packetevents.protocol.world.chunk.palette.DataPalette;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChunkData;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

public class PEListener implements PacketListener {

    public static void sendEmptyChunk(Player player, World world) {
        try {
            int chunkX = player.getLocation().getBlockX() >> 4;
            int chunkZ = player.getLocation().getBlockZ() >> 4;
            int minHeight = world.getMinHeight();
            int maxHeight = world.getMaxHeight();
            int chunkSections = (maxHeight - minHeight) >> 4;

            BaseChunk[] chunks = new BaseChunk[chunkSections];
            for (int i = 0; i < chunks.length; i++) {
                DataPalette chunkPalette = DataPalette.createForChunk();

                int airId = 0;
                chunkPalette.set(0, 0, 0, airId);

                DataPalette biomePalette = DataPalette.createForBiome();
                int plainsBiomeId = 2;
                biomePalette.set(0, 0, 0, plainsBiomeId);
                chunks[i] = new Chunk_v1_18(0, chunkPalette, biomePalette);
            }

            Map<HeightmapType, long[]> heightmaps = new HashMap<>();
            long[] emptyHeightmap = new long[37];
            Arrays.fill(emptyHeightmap, minHeight);
            heightmaps.put(HeightmapType.MOTION_BLOCKING, emptyHeightmap);
            heightmaps.put(HeightmapType.WORLD_SURFACE, emptyHeightmap);

            BitSet emptyBitSet = new BitSet();
            byte[][] emptyLightArray = new byte[0][];
            LightData lightData = new LightData(
                    true,
                    emptyBitSet,
                    emptyBitSet,
                    emptyBitSet,
                    emptyBitSet,
                    0,
                    0,
                    emptyLightArray,
                    emptyLightArray
            );

            Column column = new Column(chunkX, chunkZ, true, chunks, new TileEntity[0], heightmaps);

            WrapperPlayServerChunkData emptyChunkPacket = new WrapperPlayServerChunkData(column, lightData);
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, emptyChunkPacket);

            Bukkit.getLogger().info("[Limbo] Sent empty chunk to " + player.getName() + " at " + chunkX + ", " + chunkZ);
        } catch (Exception e) {
            Bukkit.getLogger().severe("[Limbo] Failed to send empty chunk to " + player.getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        Class<?> clazz = event.getPacketType().getWrapperClass();
        if (clazz == null) return;

        try {
            Object wrapper = clazz.getConstructor(PacketReceiveEvent.class).newInstance(event);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() == null) return;
        Class<?> clazz = event.getPacketType().getWrapperClass();
        if (clazz == null) return;
        try {
            PacketEvents.getAPI().getLogger().warning("Sending the packet: " + clazz.getSimpleName());
            //Bukkit.broadcastMessage("Sending packet... " + clazz.getSimpleName());
            Object wrapper = clazz.getConstructor(PacketSendEvent.class).newInstance(event);
            event.markForReEncode(true);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
