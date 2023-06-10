package me.defineoutside.packeteventstest.update;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketType.Play.Server;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class NEW_WrapperPlayServerDeathCombatEvent extends PacketWrapper<NEW_WrapperPlayServerDeathCombatEvent> {
    private int playerId;
    private Integer entityId;
    private Component deathMessage;

    public NEW_WrapperPlayServerDeathCombatEvent(PacketSendEvent event) {
        super(event);
    }

    public NEW_WrapperPlayServerDeathCombatEvent(int playerId, @Nullable Integer entityId, Component deathMessage) {
        super(PacketType.Play.Server.DEATH_COMBAT_EVENT);
        this.playerId = playerId;
        this.entityId = entityId;
        this.deathMessage = deathMessage;
    }

    public void read() {
        this.playerId = this.readVarInt();
        if (serverVersion.isOlderThanOrEquals(ServerVersion.V_1_19_4)) {
            this.entityId = this.readInt();
        }
        this.deathMessage = this.readComponent();
    }

    public void write() {
        this.writeVarInt(this.playerId);
        if (serverVersion.isOlderThanOrEquals(ServerVersion.V_1_19_4)) {
            int id = entityId != null ? entityId : 0;
            this.writeInt(id);
        }
        this.writeComponent(this.deathMessage);
    }

    public void copy(NEW_WrapperPlayServerDeathCombatEvent wrapper) {
        this.playerId = wrapper.playerId;
        this.entityId = wrapper.entityId;
        this.deathMessage = wrapper.deathMessage;
    }

    public int getPlayerId() {
        return this.playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public Optional<Integer> getEntityId() {
        return Optional.ofNullable(entityId);
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public Component getDeathMessage() {
        return this.deathMessage;
    }

    public void setDeathMessage(Component deathMessage) {
        this.deathMessage = deathMessage;
    }
}
