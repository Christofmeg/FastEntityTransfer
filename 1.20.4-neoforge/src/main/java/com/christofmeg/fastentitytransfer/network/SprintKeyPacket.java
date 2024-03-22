package com.christofmeg.fastentitytransfer.network;

import com.christofmeg.fastentitytransfer.CommonClickInteractions;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.Optional;

/**
 * The SprintKeyPacket class represents a network packet for sending the state of the sprint key from the client to the server.
 * This packet is used to handle the sprint key state on the server-side and update the corresponding field in the FastEntityTransfer class.
 */
public class SprintKeyPacket {
    public static final SprintKeyPacket INSTANCE = new SprintKeyPacket();

    public static SprintKeyPacket get() {
        return INSTANCE;
    }

    public void handle(final SprintKeyPayload payload, final PlayPayloadContext context) {
        context.workHandler().submitAsync(() -> {
            Optional<Player> senderOptional = context.player();
            if (senderOptional.isEmpty()) {
                return;
            }
            CommonClickInteractions.isCtrlKeyDown = payload.isSprintKeyDown();
        });
    }
}