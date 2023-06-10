package me.defineoutside.packeteventstest.update;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketType.Play.Server;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class NEW_WrapperPlayServerEndCombatEvent extends PacketWrapper<NEW_WrapperPlayServerEndCombatEvent> {
    private int duration;
    private Integer entityId;

    public NEW_WrapperPlayServerEndCombatEvent(PacketSendEvent event) {
        super(event);
    }

    public NEW_WrapperPlayServerEndCombatEvent(int duration, @Nullable Integer entityId) {
        super(PacketType.Play.Server.END_COMBAT_EVENT);
        this.duration = duration;
        this.entityId = entityId;
    }

    public void read() {
        this.duration = this.readVarInt();
        if (serverVersion.isOlderThanOrEquals(ServerVersion.V_1_19_4)) {
            this.entityId = this.readInt();
        }
    }

    public void write() {
        this.writeVarInt(this.duration);
        if (serverVersion.isOlderThanOrEquals(ServerVersion.V_1_19_4)) {
            int id = entityId != null ? entityId : 0;
            this.writeInt(id);
        }
    }

    public void copy(NEW_WrapperPlayServerEndCombatEvent wrapper) {
        this.duration = wrapper.duration;
        this.entityId = wrapper.entityId;
    }

    public int getDuration() {
        return this.duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Optional<Integer> getEntityId() {
        return Optional.ofNullable(entityId);
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }
}
