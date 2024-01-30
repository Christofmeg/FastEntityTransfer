package com.christofmeg.fastentitytransfer.network;

import com.christofmeg.fastentitytransfer.FastEntityTransfer;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public class PacketHandler {

    public static void registerPackets() {
        ServerPlayNetworking.registerGlobalReceiver(FastEntityTransfer.CTRL_KEY_PACKET_ID, PacketHandler::handle);
    }

    private static void handle(MinecraftServer minecraftServer, ServerPlayer serverPlayer, ServerGamePacketListenerImpl serverGamePacketListener, FriendlyByteBuf buf, PacketSender packetSender) {
        boolean isCtrlKeyDown = buf.readBoolean();
        if(isCtrlKeyDown) {
            FastEntityTransfer.isSprintKeyDown = true;
        }
    }

}