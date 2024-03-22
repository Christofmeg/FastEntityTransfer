package com.christofmeg.fastentitytransfer.network;

import com.christofmeg.fastentitytransfer.CommonClickInteractions;
import com.christofmeg.fastentitytransfer.FastEntityTransfer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.quiltmc.qsl.networking.api.PacketSender;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;

public class PacketHandler {

    public static void registerPackets() {
        ServerPlayNetworking.registerGlobalReceiver(FastEntityTransfer.CTRL_KEY_PACKET_ID, PacketHandler::handle);
    }

    private static void handle(MinecraftServer minecraftServer, ServerPlayer serverPlayer, ServerGamePacketListenerImpl serverGamePacketListener, FriendlyByteBuf buf, PacketSender packetSender) {
        boolean isCtrlKeyDown = buf.readBoolean();
        if (isCtrlKeyDown) {
            CommonClickInteractions.isCtrlKeyDown = true;
        }
    }

}