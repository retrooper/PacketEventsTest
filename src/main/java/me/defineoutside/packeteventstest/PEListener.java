package me.defineoutside.packeteventstest;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.world.chunk.*;
import com.github.retrooper.packetevents.protocol.world.chunk.impl.v_1_18.Chunk_v1_18;
import com.github.retrooper.packetevents.protocol.world.chunk.palette.DataPalette;
import com.github.retrooper.packetevents.wrapper.handshaking.client.WrapperHandshakingClientHandshake;
import com.github.retrooper.packetevents.wrapper.login.client.WrapperLoginClientEncryptionResponse;
import com.github.retrooper.packetevents.wrapper.login.client.WrapperLoginClientLoginStart;
import com.github.retrooper.packetevents.wrapper.login.client.WrapperLoginClientPluginResponse;
import com.github.retrooper.packetevents.wrapper.login.server.*;
import com.github.retrooper.packetevents.wrapper.play.client.*;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import com.github.retrooper.packetevents.wrapper.status.client.WrapperStatusClientPing;
import com.github.retrooper.packetevents.wrapper.status.server.WrapperStatusServerPong;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;

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
        if (event.getPacketType() == PacketType.Handshaking.Client.HANDSHAKE) {
            WrapperHandshakingClientHandshake packet = new WrapperHandshakingClientHandshake(event);
        }
        if (event.getPacketType() == PacketType.Login.Client.ENCRYPTION_RESPONSE) {
            WrapperLoginClientEncryptionResponse packet = new WrapperLoginClientEncryptionResponse(event);
        }
        if (event.getPacketType() == PacketType.Login.Client.LOGIN_START) {
            WrapperLoginClientLoginStart packet = new WrapperLoginClientLoginStart(event);
        }
        if (event.getPacketType() == PacketType.Login.Client.LOGIN_PLUGIN_RESPONSE) {
            WrapperLoginClientPluginResponse packet = new WrapperLoginClientPluginResponse(event);
        }
        if (event.getPacketType() == PacketType.Status.Client.PING) {
            WrapperStatusClientPing packet = new WrapperStatusClientPing(event);
        }
        if (event.getPacketType() == PacketType.Play.Client.ADVANCEMENT_TAB) {
            WrapperPlayClientAdvancementTab wrapper = new WrapperPlayClientAdvancementTab(event);
        }
        if (event.getPacketType() == PacketType.Play.Client.ANIMATION) {
            WrapperPlayClientAnimation wrapper = new WrapperPlayClientAnimation(event);
        }
        if (event.getPacketType() == PacketType.Play.Client.CHAT_MESSAGE) {
            WrapperPlayClientChatMessage wrapper = new WrapperPlayClientChatMessage(event);
/*

            BaseChunk[] chunks = new BaseChunk[16];
            for (int i = 0; i < chunks.length; i++) {
                chunks[i] = BaseChunk.create();
                for (int x = 0; x < 16; x++) {
                    for (int y = 0; y < 16; y++) {
                        for (int z = 0; z < 16; z++) {
                            chunks[i].set(x, y, z, 0);
                            ((Chunk_v1_18) chunks[i]).getBiomeData().set(0, 0, 0, 0);
                        }
                    }
                }
            }

            Player player = event.getPlayer();
            LightData lightData = new LightData(true, new BitSet(), new BitSet(), new BitSet(), new BitSet(), 0, 0, new byte[0][0], new byte[0][0]);
            Column column = new Column(player.getLocation().getBlockX() >> 4, player.getLocation().getBlockZ() >> 4, true, chunks, new TileEntity[0]);
            WrapperPlayServerChunkData cd = new WrapperPlayServerChunkData(column, lightData);

            event.getUser().sendPacket(cd);*/

            Player player = event.getPlayer();

        }
        if (event.getPacketType() == PacketType.Play.Client.CLICK_WINDOW) {
            WrapperPlayClientClickWindow wrapper = new WrapperPlayClientClickWindow(event);
        }
        if (event.getPacketType() == PacketType.Play.Client.CLICK_WINDOW_BUTTON) {
            WrapperPlayClientClickWindowButton wrapper = new WrapperPlayClientClickWindowButton(event);
        }
        if (event.getPacketType() == PacketType.Play.Client.CLIENT_STATUS) {
            WrapperPlayClientClientStatus wrapper = new WrapperPlayClientClientStatus(event);
        }
        if (event.getPacketType() == PacketType.Play.Client.CLOSE_WINDOW) {
            WrapperPlayClientCloseWindow wrapper = new WrapperPlayClientCloseWindow(event);
        }
        if (event.getPacketType() == PacketType.Play.Client.CREATIVE_INVENTORY_ACTION) {
            WrapperPlayClientCreativeInventoryAction wrapper = new WrapperPlayClientCreativeInventoryAction(event);
        }
        if (event.getPacketType() == PacketType.Play.Client.ENTITY_ACTION) {
            WrapperPlayClientEntityAction wrapper = new WrapperPlayClientEntityAction(event);
        }
        if (event.getPacketType() == PacketType.Play.Client.GENERATE_STRUCTURE) {
            WrapperPlayClientGenerateStructure wrapper = new WrapperPlayClientGenerateStructure(event);
        }
        if (event.getPacketType() == PacketType.Play.Client.HELD_ITEM_CHANGE) {
            WrapperPlayClientHeldItemChange wrapper = new WrapperPlayClientHeldItemChange(event);
        }
        if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
            WrapperPlayClientInteractEntity wrapper = new WrapperPlayClientInteractEntity(event);
        }
        if (event.getPacketType() == PacketType.Play.Client.KEEP_ALIVE) {
            WrapperPlayClientKeepAlive wrapper = new WrapperPlayClientKeepAlive(event);
        }
        if (event.getPacketType() == PacketType.Play.Client.LOCK_DIFFICULTY) {
            WrapperPlayClientLockDifficulty wrapper = new WrapperPlayClientLockDifficulty(event);
        }
        if (event.getPacketType() == PacketType.Play.Client.NAME_ITEM) {
            WrapperPlayClientNameItem wrapper = new WrapperPlayClientNameItem(event);
        }
        if (event.getPacketType() == PacketType.Play.Client.PICK_ITEM) {
            WrapperPlayClientPickItem wrapper = new WrapperPlayClientPickItem(event);
        }
        if (event.getPacketType() == PacketType.Play.Client.PLAYER_ABILITIES) {
            WrapperPlayClientPlayerAbilities wrapper = new WrapperPlayClientPlayerAbilities(event);
        }
        if (event.getPacketType() == PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT) {
            WrapperPlayClientPlayerBlockPlacement wrapper = new WrapperPlayClientPlayerBlockPlacement(event);
        }
        if (event.getPacketType() == PacketType.Play.Client.PLAYER_DIGGING) {
            WrapperPlayClientPlayerDigging wrapper = new WrapperPlayClientPlayerDigging(event);
        }
        if (event.getPacketType() == PacketType.Play.Client.PLAYER_FLYING) {
            WrapperPlayClientPlayerFlying wrapper = new WrapperPlayClientPlayerFlying(event);
        }
        if (event.getPacketType() == PacketType.Play.Client.PLAYER_POSITION) {
            WrapperPlayClientPlayerPosition wrapper = new WrapperPlayClientPlayerPosition(event);
        }
        if (event.getPacketType() == PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION) {
            WrapperPlayClientPlayerPositionAndRotation wrapper = new WrapperPlayClientPlayerPositionAndRotation(event);
        }
        if (event.getPacketType() == PacketType.Play.Client.PLAYER_ROTATION) {
            WrapperPlayClientPlayerRotation wrapper = new WrapperPlayClientPlayerRotation(event);
        }
        if (event.getPacketType() == PacketType.Play.Client.PLUGIN_MESSAGE) {
            WrapperPlayClientPluginMessage wrapper = new WrapperPlayClientPluginMessage(event);
        }
        if (event.getPacketType() == PacketType.Play.Client.PONG) {
            WrapperPlayClientPong wrapper = new WrapperPlayClientPong(event);
        }
        if (event.getPacketType() == PacketType.Play.Client.QUERY_BLOCK_NBT) {
            WrapperPlayClientQueryBlockNBT wrapper = new WrapperPlayClientQueryBlockNBT(event);
        }
        if (event.getPacketType() == PacketType.Play.Client.QUERY_ENTITY_NBT) {
            WrapperPlayClientQueryEntityNBT wrapper = new WrapperPlayClientQueryEntityNBT(event);
        }
        if (event.getPacketType() == PacketType.Play.Client.RESOURCE_PACK_STATUS) {
            WrapperPlayClientResourcePackStatus wrapper = new WrapperPlayClientResourcePackStatus(event);
        }
        if (event.getPacketType() == PacketType.Play.Client.SELECT_TRADE) {
            WrapperPlayClientSelectTrade wrapper = new WrapperPlayClientSelectTrade(event);
        }
        if (event.getPacketType() == PacketType.Play.Client.SET_BEACON_EFFECT) {
            WrapperPlayClientSetBeaconEffect wrapper = new WrapperPlayClientSetBeaconEffect(event);
        }
        if (event.getPacketType() == PacketType.Play.Client.SET_DIFFICULTY) {
            WrapperPlayClientSetDifficulty wrapper = new WrapperPlayClientSetDifficulty(event);
        }
        if (event.getPacketType() == PacketType.Play.Client.CLIENT_SETTINGS) {
            WrapperPlayClientSettings wrapper = new WrapperPlayClientSettings(event);
        }
        if (event.getPacketType() == PacketType.Play.Client.SPECTATE) {
            WrapperPlayClientSpectate wrapper = new WrapperPlayClientSpectate(event);
        }
        if (event.getPacketType() == PacketType.Play.Client.STEER_BOAT) {
            WrapperPlayClientSteerBoat wrapper = new WrapperPlayClientSteerBoat(event);
        }
        if (event.getPacketType() == PacketType.Play.Client.STEER_VEHICLE) {
            WrapperPlayClientSteerVehicle wrapper = new WrapperPlayClientSteerVehicle(event);
        }
        if (event.getPacketType() == PacketType.Play.Client.TAB_COMPLETE) {
            WrapperPlayClientTabComplete wrapper = new WrapperPlayClientTabComplete(event);
        }
        if (event.getPacketType() == PacketType.Play.Client.TELEPORT_CONFIRM) {
            WrapperPlayClientTeleportConfirm wrapper = new WrapperPlayClientTeleportConfirm(event);
        }
        if (event.getPacketType() == PacketType.Play.Client.UPDATE_SIGN) {
            WrapperPlayClientUpdateSign wrapper = new WrapperPlayClientUpdateSign(event);
        }
        if (event.getPacketType() == PacketType.Play.Client.USE_ITEM) {
            WrapperPlayClientUseItem wrapper = new WrapperPlayClientUseItem(event);
        }
        if (event.getPacketType() == PacketType.Play.Client.VEHICLE_MOVE) {
            WrapperPlayClientVehicleMove wrapper = new WrapperPlayClientVehicleMove(event);
        }
        if (event.getPacketType() == PacketType.Play.Client.WINDOW_CONFIRMATION) {
            WrapperPlayClientWindowConfirmation wrapper = new WrapperPlayClientWindowConfirmation(event);
        }
        if (event.getPacketType() == PacketType.Play.Client.CHANGE_GAME_MODE) {
            WrapperPlayClientChangeGameMode wrapper = new WrapperPlayClientChangeGameMode(event);
        }
        if (event.getPacketType() == PacketType.Play.Client.CUSTOM_CLICK_ACTION) {
            WrapperPlayClientCustomClickAction wrapper = new WrapperPlayClientCustomClickAction(event);
        }
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() == null) return;
        if (event.getPacketType() == PacketType.Login.Server.DISCONNECT) {
            WrapperLoginServerDisconnect wrapper = new WrapperLoginServerDisconnect(event);
        }
        if (event.getPacketType() == PacketType.Login.Server.ENCRYPTION_REQUEST) {
            WrapperLoginServerEncryptionRequest wrapper = new WrapperLoginServerEncryptionRequest(event);
        }
        if (event.getPacketType() == PacketType.Login.Server.LOGIN_SUCCESS) {
            WrapperLoginServerLoginSuccess wrapper = new WrapperLoginServerLoginSuccess(event);
        }
        if (event.getPacketType() == PacketType.Login.Server.LOGIN_PLUGIN_REQUEST) {
            WrapperLoginServerPluginRequest wrapper = new WrapperLoginServerPluginRequest(event);
        }
        if (event.getPacketType() == PacketType.Status.Server.PONG) {
            WrapperStatusServerPong wrapper = new WrapperStatusServerPong(event);
        }
        if (event.getPacketType() == PacketType.Login.Server.SET_COMPRESSION) {
            WrapperLoginServerSetCompression wrapper = new WrapperLoginServerSetCompression(event);
        }
        if (event.getPacketType() == PacketType.Play.Server.ACKNOWLEDGE_PLAYER_DIGGING) {
            WrapperPlayServerAcknowledgePlayerDigging packet = new WrapperPlayServerAcknowledgePlayerDigging(event);
        }
        if (event.getPacketType() == PacketType.Play.Server.ATTACH_ENTITY) {
            WrapperPlayServerAttachEntity packet = new WrapperPlayServerAttachEntity(event);
        }
        if (event.getPacketType() == PacketType.Play.Server.BLOCK_ACTION) {
            WrapperPlayServerBlockAction packet = new WrapperPlayServerBlockAction(event);
        }
        if (event.getPacketType() == PacketType.Play.Server.BLOCK_BREAK_ANIMATION) {
            WrapperPlayServerBlockBreakAnimation packet = new WrapperPlayServerBlockBreakAnimation(event);
        }
        if (event.getPacketType() == PacketType.Play.Server.BLOCK_CHANGE) {
            WrapperPlayServerBlockChange packet = new WrapperPlayServerBlockChange(event);
        }
        if (event.getPacketType() == PacketType.Play.Server.CHANGE_GAME_STATE) {
            WrapperPlayServerChangeGameState packet = new WrapperPlayServerChangeGameState(event);
        }
        if (event.getPacketType() == PacketType.Play.Server.CHAT_MESSAGE) {
            WrapperPlayServerChatMessage packet = new WrapperPlayServerChatMessage(event);
        }
        if (event.getPacketType() == PacketType.Play.Server.CHUNK_DATA) {
            WrapperPlayServerChunkData packet = new WrapperPlayServerChunkData(event);
        }
        if (event.getPacketType() == PacketType.Play.Server.CLOSE_WINDOW) {
            WrapperPlayServerCloseWindow packet = new WrapperPlayServerCloseWindow(event);
        }
        if (event.getPacketType() == PacketType.Play.Server.COLLECT_ITEM) {
            WrapperPlayServerCollectItem packet = new WrapperPlayServerCollectItem(event);
        }
        if (event.getPacketType() == PacketType.Play.Server.DECLARE_RECIPES) {
            //TODO Rewrite Recipes? How outdated is it? Doesn't even work on 1.18.
            //WrapperPlayServerDeclareRecipes packet = new WrapperPlayServerDeclareRecipes(event);
        }
        if (event.getPacketType() == PacketType.Play.Server.DESTROY_ENTITIES) {
            WrapperPlayServerDestroyEntities packet = new WrapperPlayServerDestroyEntities(event);
        }
        if (event.getPacketType() == PacketType.Play.Server.SHOW_DIALOG) {
            WrapperPlayServerShowDialog wrapper = new WrapperPlayServerShowDialog(event);
        }
        if (event.getPacketType() == PacketType.Play.Server.CLEAR_DIALOG) {
            WrapperPlayServerClearDialog wrapper = new WrapperPlayServerClearDialog(event);
        }
        if (event.getPacketType() == PacketType.Play.Server.SERVER_DIFFICULTY) {
            WrapperPlayServerDifficulty packet = new WrapperPlayServerDifficulty(event);
        }
        if (event.getPacketType() == PacketType.Play.Server.DISCONNECT) {
            WrapperPlayServerDisconnect packet = new WrapperPlayServerDisconnect(event);
        }
        if (event.getPacketType() == PacketType.Play.Server.DISPLAY_SCOREBOARD) {
            WrapperPlayServerDisplayScoreboard packet = new WrapperPlayServerDisplayScoreboard(event);
        }
        if (event.getPacketType() == PacketType.Play.Server.ENTITY_ANIMATION) {
            WrapperPlayServerEntityAnimation packet = new WrapperPlayServerEntityAnimation(event);
        }
        if (event.getPacketType() == PacketType.Play.Server.ENTITY_EFFECT) {
            WrapperPlayServerEntityEffect packet = new WrapperPlayServerEntityEffect(event);
        }
        if (event.getPacketType() == PacketType.Play.Server.ENTITY_EQUIPMENT) {
            WrapperPlayServerEntityEquipment packet = new WrapperPlayServerEntityEquipment(event);
        }
        if (event.getPacketType() == PacketType.Play.Server.ENTITY_HEAD_LOOK) {
            WrapperPlayServerEntityHeadLook packet = new WrapperPlayServerEntityHeadLook(event);
        }
        if (event.getPacketType() == PacketType.Play.Server.ENTITY_METADATA) {
            WrapperPlayServerEntityMetadata packet = new WrapperPlayServerEntityMetadata(event);
        }
        if (event.getPacketType() == PacketType.Play.Server.UPDATE_ATTRIBUTES) {
            WrapperPlayServerUpdateAttributes packet = new WrapperPlayServerUpdateAttributes(event);
        }
        if (event.getPacketType() == PacketType.Play.Server.ENTITY_RELATIVE_MOVE) {
            WrapperPlayServerEntityRelativeMove packet = new WrapperPlayServerEntityRelativeMove(event);
        }
        if (event.getPacketType() == PacketType.Play.Server.ENTITY_RELATIVE_MOVE_AND_ROTATION) {
            WrapperPlayServerEntityRelativeMoveAndRotation packet = new WrapperPlayServerEntityRelativeMoveAndRotation(event);
        }
        if (event.getPacketType() == PacketType.Play.Server.ENTITY_ROTATION) {
            WrapperPlayServerEntityRotation packet = new WrapperPlayServerEntityRotation(event);
        }
        if (event.getPacketType() == PacketType.Play.Server.ENTITY_STATUS) {
            WrapperPlayServerEntityStatus packet = new WrapperPlayServerEntityStatus(event);
        }
        if (event.getPacketType() == PacketType.Play.Server.ENTITY_TELEPORT) {
            WrapperPlayServerEntityTeleport packet = new WrapperPlayServerEntityTeleport(event);
        }
        if (event.getPacketType() == PacketType.Play.Server.ENTITY_VELOCITY) {
            WrapperPlayServerEntityVelocity packet = new WrapperPlayServerEntityVelocity(event);
        }
        if (event.getPacketType() == PacketType.Play.Server.EXPLOSION) {
            WrapperPlayServerExplosion packet = new WrapperPlayServerExplosion(event);
        }
        if (event.getPacketType() == PacketType.Play.Server.FACE_PLAYER) {
            WrapperPlayServerFacePlayer packet = new WrapperPlayServerFacePlayer(event);
        }
        if (event.getPacketType() == PacketType.Play.Server.HELD_ITEM_CHANGE) {
            WrapperPlayServerHeldItemChange packet = new WrapperPlayServerHeldItemChange(event);
        }
        if (event.getPacketType() == PacketType.Play.Server.KEEP_ALIVE) {
            WrapperPlayServerKeepAlive packet = new WrapperPlayServerKeepAlive(event);
        }
        if (event.getPacketType() == PacketType.Play.Server.MULTI_BLOCK_CHANGE) {
            WrapperPlayServerMultiBlockChange packet = new WrapperPlayServerMultiBlockChange(event);
        }
        if (event.getPacketType() == PacketType.Play.Server.OPEN_HORSE_WINDOW) {
            WrapperPlayServerOpenHorseWindow packet = new WrapperPlayServerOpenHorseWindow(event);
        }
        if (event.getPacketType() == PacketType.Play.Server.OPEN_SIGN_EDITOR) {
            WrapperPlayServerOpenSignEditor packet = new WrapperPlayServerOpenSignEditor(event);
        }
        if (event.getPacketType() == PacketType.Play.Server.OPEN_WINDOW) {
            WrapperPlayServerOpenWindow packet = new WrapperPlayServerOpenWindow(event);
        }
        if (event.getPacketType() == PacketType.Play.Server.PARTICLE) {
            WrapperPlayServerParticle packet = new WrapperPlayServerParticle(event);
        }
        if (event.getPacketType() == PacketType.Play.Server.PING) {
            WrapperPlayServerPing packet = new WrapperPlayServerPing(event);
        }
        if (event.getPacketType() == PacketType.Play.Server.PLAYER_ABILITIES) {
            WrapperPlayServerPlayerAbilities packet = new WrapperPlayServerPlayerAbilities(event);
        }
        if (event.getPacketType() == PacketType.Play.Server.END_COMBAT_EVENT) {
            WrapperPlayServerEndCombatEvent packet = new WrapperPlayServerEndCombatEvent(event);
        }
        if (event.getPacketType() == PacketType.Play.Server.DEATH_COMBAT_EVENT) {
            WrapperPlayServerDeathCombatEvent packet = new WrapperPlayServerDeathCombatEvent(event);
        }
        if (event.getPacketType() == PacketType.Play.Server.PLAYER_INFO) {
            WrapperPlayServerPlayerInfo packet = new WrapperPlayServerPlayerInfo(event);
        }
        if (event.getPacketType() == PacketType.Play.Server.PLAYER_LIST_HEADER_AND_FOOTER) {
            WrapperPlayServerPlayerListHeaderAndFooter packet = new WrapperPlayServerPlayerListHeaderAndFooter(event);
        }
        if (event.getPacketType() == PacketType.Play.Server.PLAYER_POSITION_AND_LOOK) {
            WrapperPlayServerPlayerPositionAndLook packet = new WrapperPlayServerPlayerPositionAndLook(event);
        }
        if (event.getPacketType() == PacketType.Play.Server.PLUGIN_MESSAGE) {
            WrapperPlayServerPluginMessage packet = new WrapperPlayServerPluginMessage(event);
        }
        if (event.getPacketType() == PacketType.Play.Server.REMOVE_ENTITY_EFFECT) {
            WrapperPlayServerRemoveEntityEffect packet = new WrapperPlayServerRemoveEntityEffect(event);
        }
        if (event.getPacketType() == PacketType.Play.Server.RESPAWN) {
            WrapperPlayServerRespawn packet = new WrapperPlayServerRespawn(event);
        }
        /*if (event.getPacketType() == PacketType.Play.Server.SCOREBOARD_OBJECTIVE) {
            WrapperPlayServerScoreboardObjective packet = new WrapperPlayServerScoreboardObjective(event);
        }*/
        if (event.getPacketType() == PacketType.Play.Server.SET_COOLDOWN) {
            WrapperPlayServerSetCooldown packet = new WrapperPlayServerSetCooldown(event);
        }
        if (event.getPacketType() == PacketType.Play.Server.JOIN_GAME) {
            WrapperPlayServerJoinGame packet = new WrapperPlayServerJoinGame(event);
        }
        if (event.getPacketType() == PacketType.Play.Server.SET_EXPERIENCE) {
            WrapperPlayServerSetExperience packet = new WrapperPlayServerSetExperience(event);
        }
        if (event.getPacketType() == PacketType.Play.Server.SET_SLOT) {
            WrapperPlayServerSetSlot packet = new WrapperPlayServerSetSlot(event);
        }
        if (event.getPacketType() == PacketType.Play.Server.SOUND_EFFECT) {
            WrapperPlayServerSoundEffect packet = new WrapperPlayServerSoundEffect(event);
        }
        if (event.getPacketType() == PacketType.Play.Server.SPAWN_ENTITY) {
            WrapperPlayServerSpawnEntity packet = new WrapperPlayServerSpawnEntity(event);
        }
        if (event.getPacketType() == PacketType.Play.Server.SPAWN_LIVING_ENTITY) {
            WrapperPlayServerSpawnLivingEntity packet = new WrapperPlayServerSpawnLivingEntity(event);
        }
        if (event.getPacketType() == PacketType.Play.Server.SPAWN_PLAYER) {
            WrapperPlayServerSpawnPlayer packet = new WrapperPlayServerSpawnPlayer(event);
        }
        if (event.getPacketType() == PacketType.Play.Server.TAB_COMPLETE) {
            WrapperPlayServerTabComplete packet = new WrapperPlayServerTabComplete(event);
        }
        if (event.getPacketType() == PacketType.Play.Server.TAGS) {
            WrapperPlayServerTags packet = new WrapperPlayServerTags(event);
        }
        if (event.getPacketType() == PacketType.Play.Server.TEAMS) {
            WrapperPlayServerTeams packet = new WrapperPlayServerTeams(event);
        }
        if (event.getPacketType() == PacketType.Play.Server.UNLOAD_CHUNK) {
            WrapperPlayServerUnloadChunk packet = new WrapperPlayServerUnloadChunk(event);
        }
        if (event.getPacketType() == PacketType.Play.Server.UPDATE_HEALTH) {
            WrapperPlayServerUpdateHealth packet = new WrapperPlayServerUpdateHealth(event);
        }
        if (event.getPacketType() == PacketType.Play.Server.UPDATE_SCORE) {
            WrapperPlayServerUpdateScore packet = new WrapperPlayServerUpdateScore(event);
        }
        if (event.getPacketType() == PacketType.Play.Server.UPDATE_SIMULATION_DISTANCE) {
            WrapperPlayServerUpdateSimulationDistance packet = new WrapperPlayServerUpdateSimulationDistance(event);
        }
        if (event.getPacketType() == PacketType.Play.Server.VEHICLE_MOVE) {
            WrapperPlayServerVehicleMove packet = new WrapperPlayServerVehicleMove(event);
        }
        if (event.getPacketType() == PacketType.Play.Server.WINDOW_CONFIRMATION) {
            WrapperPlayServerWindowConfirmation packet = new WrapperPlayServerWindowConfirmation(event);
        }
        if (event.getPacketType() == PacketType.Play.Server.WINDOW_ITEMS) {
            WrapperPlayServerWindowItems packet = new WrapperPlayServerWindowItems(event);
        }
        if (event.getPacketType() == PacketType.Play.Server.WAYPOINT) {
            WrapperPlayServerWaypoint packet = new WrapperPlayServerWaypoint(event);
        }
    }
}
