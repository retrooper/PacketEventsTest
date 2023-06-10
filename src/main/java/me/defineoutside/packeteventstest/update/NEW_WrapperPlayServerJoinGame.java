package me.defineoutside.packeteventstest.update;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.GameMode;
import com.github.retrooper.packetevents.protocol.world.*;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NEW_WrapperPlayServerJoinGame extends PacketWrapper<NEW_WrapperPlayServerJoinGame> {
    private int entityID;
    private boolean hardcore;
    private GameMode gameMode;

    @Nullable
    private GameMode previousGameMode;

    private List<String> worldNames;
    private NBTCompound dimensionCodec;
    private Dimension dimension;
    private Difficulty difficulty;
    private String worldName;
    private long hashedSeed;
    private int maxPlayers;
    private int viewDistance;
    private int simulationDistance;
    private boolean reducedDebugInfo;
    private boolean enableRespawnScreen;
    private boolean isDebug;
    private boolean isFlat;
    private WorldBlockPosition lastDeathPosition;
    private @Nullable Integer portalCooldown;

    public NEW_WrapperPlayServerJoinGame(PacketSendEvent event) {
        super(event);
    }

    public NEW_WrapperPlayServerJoinGame(int entityID, boolean hardcore, GameMode gameMode,
                                     @Nullable GameMode previousGameMode, List<String> worldNames,
                                     NBTCompound dimensionCodec, Dimension dimension,
                                     Difficulty difficulty, String worldName, long hashedSeed,
                                     int maxPlayers, int viewDistance, int simulationDistance,
                                     boolean reducedDebugInfo, boolean enableRespawnScreen,
                                     boolean isDebug, boolean isFlat, WorldBlockPosition lastDeathPosition, @Nullable Integer portalCooldown) {
        super(PacketType.Play.Server.JOIN_GAME);
        this.entityID = entityID;
        this.hardcore = hardcore;
        this.gameMode = gameMode;
        this.previousGameMode = previousGameMode;
        this.worldNames = worldNames;
        this.dimensionCodec = dimensionCodec;
        this.dimension = dimension;
        this.difficulty = difficulty;
        this.worldName = worldName;
        this.hashedSeed = hashedSeed;
        this.maxPlayers = maxPlayers;
        this.viewDistance = viewDistance;
        this.simulationDistance = simulationDistance;
        this.reducedDebugInfo = reducedDebugInfo;
        this.enableRespawnScreen = enableRespawnScreen;
        this.isDebug = isDebug;
        this.isFlat = isFlat;
        this.lastDeathPosition = lastDeathPosition;
        this.portalCooldown = portalCooldown;
    }

    @Override
    public void read() {
        entityID = readInt();
        boolean v1_19 = serverVersion.isNewerThanOrEquals(ServerVersion.V_1_19);
        boolean v1_18 = serverVersion.isNewerThanOrEquals(ServerVersion.V_1_18);
        boolean v1_16 = serverVersion.isNewerThanOrEquals(ServerVersion.V_1_16);
        boolean v1_15 = serverVersion.isNewerThanOrEquals(ServerVersion.V_1_15);
        boolean v1_14 = serverVersion.isNewerThanOrEquals(ServerVersion.V_1_14);
        if (v1_16) {
            hardcore = readBoolean();
            gameMode = readGameMode();
        } else {
            int gameModeId = readUnsignedByte();
            hardcore = (gameModeId & 0x8) == 0x8;
            gameMode = GameMode.getById(gameModeId & -0x9);
        }
        if (v1_16) {
            previousGameMode = readGameMode();
            int worldCount = readVarInt();
            worldNames = new ArrayList<>(worldCount);
            for (int i = 0; i < worldCount; i++) {
                worldNames.add(readString());
            }
            dimensionCodec = readNBT();
            dimension = readDimension();
            worldName = readString();
        } else {
            previousGameMode = gameMode;
            dimensionCodec = new NBTCompound();
            dimension = new Dimension(serverVersion.isNewerThanOrEquals(ServerVersion.V_1_9_2) ? readInt() : readByte());
            if (!v1_14) {
                difficulty = Difficulty.getById(readByte());
            }
        }
        if (v1_15) {
            hashedSeed = readLong();
        }
        if (v1_16) {
            maxPlayers = readVarInt();
            viewDistance = readVarInt();
            if (v1_18) simulationDistance = readVarInt();
            reducedDebugInfo = readBoolean();
            enableRespawnScreen = readBoolean();
            isDebug = readBoolean();
            isFlat = readBoolean();
        } else {
            maxPlayers = readUnsignedByte();
            String levelType = readString(16);
            isFlat = DimensionType.isFlat(levelType);
            isDebug = DimensionType.isDebug(levelType);
            if (v1_14) {
                viewDistance = readVarInt();
            }
            reducedDebugInfo = readBoolean();
            if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_15)) {
                enableRespawnScreen = readBoolean();
            }
        }
        if (v1_19) {
            lastDeathPosition = readOptional(PacketWrapper::readWorldBlockPosition);
        }
        if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_20)) {
            portalCooldown = readVarInt();
        }
    }

    @Override
    public void write() {
        writeInt(entityID);
        boolean v1_19 = serverVersion.isNewerThanOrEquals(ServerVersion.V_1_19);
        boolean v1_18 = serverVersion.isNewerThanOrEquals(ServerVersion.V_1_18);
        boolean v1_16 = serverVersion.isNewerThanOrEquals(ServerVersion.V_1_16);
        boolean v1_14 = serverVersion.isNewerThanOrEquals(ServerVersion.V_1_14);
        boolean v1_15 = serverVersion.isNewerThanOrEquals(ServerVersion.V_1_15);
        if (v1_16) {
            writeBoolean(hardcore);
            writeGameMode(gameMode);
        } else {
            int gameModeId = gameMode.getId();
            if (hardcore) {
                gameModeId |= 0x8;
            }
            writeByte(gameModeId);
        }
        if (v1_16) {
            if (previousGameMode == null) {
                previousGameMode = gameMode;
            }
            writeGameMode(previousGameMode);
            writeVarInt(worldNames.size());
            for (String name : worldNames) {
                writeString(name);
            }
            writeNBT(dimensionCodec);
            writeDimension(dimension);
            writeString(worldName);
        } else {
            previousGameMode = gameMode;
            if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_9)) {
                writeInt(dimension.getId());
            } else {
                writeByte(dimension.getId());
            }
            if (!v1_14) {
                writeByte(difficulty.getId());
            }
        }
        if (v1_15) {
            writeLong(hashedSeed);
        }
        if (v1_16) {
            writeVarInt(maxPlayers);
            writeVarInt(viewDistance);
            if (v1_18) writeVarInt(simulationDistance);
            writeBoolean(reducedDebugInfo);
            writeBoolean(enableRespawnScreen);
            writeBoolean(isDebug);
            writeBoolean(isFlat);
        } else {
            writeByte(maxPlayers);
            String levelType;
            //TODO Proper backwards compatibility for level type
            if (isFlat) {
                levelType = WorldType.FLAT.getName();
            } else if (isDebug) {
                levelType = WorldType.DEBUG_ALL_BLOCK_STATES.getName();
            } else {
                levelType = WorldType.DEFAULT.getName();
            }
            writeString(levelType, 16);
            if (v1_14) {
                writeVarInt(viewDistance);
            }
            writeBoolean(reducedDebugInfo);
            if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_15)) {
                writeBoolean(enableRespawnScreen);
            }
        }
        if (v1_19) {
            writeOptional(lastDeathPosition, PacketWrapper::writeWorldBlockPosition);
        }
        if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_20)) {
            int pCooldown = portalCooldown != null ? portalCooldown : 0;
            writeVarInt(pCooldown);
        }
    }

    @Override
    public void copy(NEW_WrapperPlayServerJoinGame wrapper) {
        entityID = wrapper.entityID;
        hardcore = wrapper.hardcore;
        gameMode = wrapper.gameMode;
        previousGameMode = wrapper.previousGameMode;
        worldNames = wrapper.worldNames;
        dimensionCodec = wrapper.dimensionCodec;
        dimension = wrapper.dimension;
        difficulty = wrapper.difficulty;
        worldName = wrapper.worldName;
        hashedSeed = wrapper.hashedSeed;
        maxPlayers = wrapper.maxPlayers;
        viewDistance = wrapper.viewDistance;
        simulationDistance = wrapper.simulationDistance;
        reducedDebugInfo = wrapper.reducedDebugInfo;
        enableRespawnScreen = wrapper.enableRespawnScreen;
        isDebug = wrapper.isDebug;
        isFlat = wrapper.isFlat;
        lastDeathPosition = wrapper.lastDeathPosition;
    }

    public int getEntityId() {
        return entityID;
    }

    public void setEntityId(int entityID) {
        this.entityID = entityID;
    }

    public boolean isHardcore() {
        return hardcore;
    }

    public void setHardcore(boolean hardcore) {
        this.hardcore = hardcore;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    @Nullable
    public GameMode getPreviousGameMode() {
        return previousGameMode;
    }

    public void setPreviousGameMode(@Nullable GameMode previousGameMode) {
        this.previousGameMode = previousGameMode;
    }

    public List<String> getWorldNames() {
        return worldNames;
    }

    public void setWorldNames(List<String> worldNames) {
        this.worldNames = worldNames;
    }

    public NBTCompound getDimensionCodec() {
        return dimensionCodec;
    }

    public void setDimensionCodec(NBTCompound dimensionCodec) {
        this.dimensionCodec = dimensionCodec;
    }

    public Dimension getDimension() {
        return dimension;
    }

    public void setDimension(Dimension dimension) {
        this.dimension = dimension;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public String getWorldName() {
        return worldName;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public long getHashedSeed() {
        return hashedSeed;
    }

    public void setHashedSeed(long hashedSeed) {
        this.hashedSeed = hashedSeed;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public int getViewDistance() {
        return viewDistance;
    }

    public void setViewDistance(int viewDistance) {
        this.viewDistance = viewDistance;
    }

    public int getSimulationDistance() {
        return simulationDistance;
    }

    public void setSimulationDistance(int simulationDistance) {
        this.simulationDistance = simulationDistance;
    }

    public boolean isReducedDebugInfo() {
        return reducedDebugInfo;
    }


    public void setReducedDebugInfo(boolean reducedDebugInfo) {
        this.reducedDebugInfo = reducedDebugInfo;
    }

    public boolean isRespawnScreenEnabled() {
        return enableRespawnScreen;
    }

    public void setRespawnScreenEnabled(boolean enableRespawnScreen) {
        this.enableRespawnScreen = enableRespawnScreen;
    }

    public boolean isDebug() {
        return isDebug;
    }

    public void setDebug(boolean isDebug) {
        this.isDebug = isDebug;
    }

    public boolean isFlat() {
        return isFlat;
    }

    public void setFlat(boolean isFlat) {
        this.isFlat = isFlat;
    }

    public @Nullable WorldBlockPosition getLastDeathPosition() {
        return lastDeathPosition;
    }

    public void setLastDeathPosition(@Nullable WorldBlockPosition lastDeathPosition) {
        this.lastDeathPosition = lastDeathPosition;
    }

    public Optional<Integer> getPortalCooldown() {
        return Optional.ofNullable(portalCooldown);
    }

    public void setPortalCooldown(int portalCooldown) {
        this.portalCooldown = portalCooldown;
    }
}
