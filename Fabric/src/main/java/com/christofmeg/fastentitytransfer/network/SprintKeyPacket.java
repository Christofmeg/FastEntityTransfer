package com.christofmeg.fastentitytransfer.network;

import com.christofmeg.fastentitytransfer.FastEntityTransfer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;

public class SprintKeyPacket {

    public static void send(boolean valueToSend) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeBoolean(valueToSend);
        ClientPlayNetworking.send(FastEntityTransfer.CTRL_KEY_PACKET_ID, buf);
    }

}
