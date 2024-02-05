package com.christofmeg.fastentitytransfer.network;

import com.christofmeg.fastentitytransfer.FastEntityTransfer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkContext;
import net.minecraftforge.network.NetworkInstance;

/**
 * The SprintKeyPacket class represents a network packet for sending the state of the sprint key from the client to the server.
 * This packet is used to handle the sprint key state on the server-side and update the corresponding field in the FastEntityTransfer class.
 */
public class SprintKeyPacket {

    private final boolean isSprintKeyDown;

    /**
     * Constructs a new SprintKeyPacket.
     * @param isSprintKeyDown The state of the sprint key (true if the sprint key is pressed, false otherwise).
     */
    public SprintKeyPacket(boolean isSprintKeyDown) {
        this.isSprintKeyDown = isSprintKeyDown;
    }

    /**
     * Encodes the SprintKeyPacket into the provided buffer.
     * @param packet The SprintKeyPacket to encode.
     * @param buffer The buffer to write the encoded data into.
     */
    public static void encode(SprintKeyPacket packet, FriendlyByteBuf buffer) {
        buffer.writeBoolean(packet.isSprintKeyDown);
    }

    /**
     * Decodes a SprintKeyPacket from the provided buffer.
     * @param buffer The buffer to read the encoded data from.
     * @return The decoded SprintKeyPacket.
     */
    public static SprintKeyPacket decode(FriendlyByteBuf buffer) {
        boolean isSprintKeyDown = buffer.readBoolean();
        return new SprintKeyPacket(isSprintKeyDown);
    }

    /**
     * Handles the received SprintKeyPacket on the server-side.
     * @param packet The received SprintKeyPacket.
     * @param contextSupplier A supplier for obtaining the network event context.
     */
    public static void handle(SprintKeyPacket packet, NetworkContext contextSupplier) {
        contextSupplier.
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            // Process the packet on the server-side
            FastEntityTransfer.isCtrlKeyDown = packet.isSprintKeyDown;
        });
        context.setPacketHandled(true);
    }
}