package com.christofmeg.fastentitytransfer.network;

import com.christofmeg.fastentitytransfer.FastEntityTransfer;
import net.minecraft.network.FriendlyByteBuf;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;

public class SprintKeyPacket {

    public static void send(boolean valueToSend) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeBoolean(valueToSend);
        ClientPlayNetworking.send(FastEntityTransfer.CTRL_KEY_PACKET_ID, buf);
    }

}