package com.christofmeg.fastentitytransfer.network;

import com.christofmeg.fastentitytransfer.CommonConstants;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.SimpleChannel;

public class PacketHandler {

    private static final int PROTOCOL_VERSION = 1;

    /**
     * The main network channel used for packet communication.
     */
    public static final SimpleChannel CHANNEL = ChannelBuilder.named(
            new ResourceLocation(CommonConstants.MOD_ID, "main"))
            .serverAcceptedVersions(((status, version) -> true))
            .clientAcceptedVersions(((status, version) -> true))
            .networkProtocolVersion(PROTOCOL_VERSION)
            .simpleChannel();

    /**
     * Registers the network packets.
     * This method should be called during mod initialization.
     * This is required for servers to prevent crashes with client-side calls to Minecraft.getInstance().
     */
    public static void registerPackets() {
        CHANNEL.messageBuilder(SprintKeyPacket.class)
                .encoder(SprintKeyPacket::encode)
                .decoder(SprintKeyPacket::decode)
                .consumerMainThread(SprintKeyPacket::handle)
                .add();
    }
}