package me.defineoutside.packeteventstest.update;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketType.Play.Server;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;

public class NEW_WrapperPlayServerOpenSignEditor extends PacketWrapper<NEW_WrapperPlayServerOpenSignEditor> {
    private Vector3i position;
    private boolean isFrontText = true;

    public NEW_WrapperPlayServerOpenSignEditor(PacketSendEvent event) {
        super(event);
    }

    public NEW_WrapperPlayServerOpenSignEditor(Vector3i position, boolean isFrontText) {
        super(PacketType.Play.Server.OPEN_SIGN_EDITOR);
        this.position = position;
        this.isFrontText = isFrontText;
    }

    public void read() {
        if (this.serverVersion.isNewerThanOrEquals(ServerVersion.V_1_8)) {
            this.position = new Vector3i(this.readLong());
        } else {
            int x = this.readInt();
            int y = this.readInt();
            int z = this.readInt();
            this.position = new Vector3i(x, y, z);
        }
        if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_20)) {
            isFrontText = readBoolean();
        }
    }

    public void write() {
        if (this.serverVersion.isNewerThanOrEquals(ServerVersion.V_1_8)) {
            long positionVector = this.position.getSerializedPosition();
            this.writeLong(positionVector);
        } else {
            this.writeInt(this.position.x);
            this.writeInt(this.position.y);
            this.writeInt(this.position.z);
        }

        if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_20)) {
            writeBoolean(isFrontText);
        }
    }

    public void copy(NEW_WrapperPlayServerOpenSignEditor wrapper) {
        this.position = wrapper.position;
    }

    public Vector3i getPosition() {
        return this.position;
    }

    public void setPosition(Vector3i position) {
        this.position = position;
    }

    public boolean isFrontText() {
        return isFrontText;
    }

    public void setFrontText(boolean frontText) {
        isFrontText = frontText;
    }
}
