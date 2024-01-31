package com.christofmeg.fastentitytransfer.network;

import com.christofmeg.fastentitytransfer.CommonConstants;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {

    private static final String PROTOCOL_VERSION = "1";

    /**
     * The main network channel used for packet communication.
     */
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(CommonConstants.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    /**
     * Registers the network packets.
     * This method should be called during mod initialization.
     * This is required for servers to prevent crashes with client-side calls to Minecraft.getInstance().
     */
    public static void registerPackets() {
        int packetId = 0;
        CHANNEL.registerMessage(packetId++, SprintKeyPacket.class, SprintKeyPacket::encode, SprintKeyPacket::decode, SprintKeyPacket::handle);
    }
}