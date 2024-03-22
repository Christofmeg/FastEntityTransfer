package com.christofmeg.fastentitytransfer.network;

import com.christofmeg.fastentitytransfer.CommonConstants;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

public class PacketHandler {

    /**
     * Registers the network packets.
     * This method should be called during mod initialization.
     * This is required for servers to prevent crashes with client-side calls to Minecraft.getInstance().
     */
    public static void registerPackets(final RegisterPayloadHandlerEvent event) {

        final IPayloadRegistrar registrar = event.registrar(CommonConstants.MOD_ID);

        //Going to Server
        registrar.play(SprintKeyPayload.ID, SprintKeyPayload::new, handler -> handler.server(SprintKeyPacket.get()::handle));
    }
}