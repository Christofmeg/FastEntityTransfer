package com.christofmeg.fastentitytransfer;

import com.christofmeg.fastentitytransfer.network.PacketHandler;
import com.christofmeg.fastentitytransfer.network.SprintKeyPacket;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * The ClientEvents class handles client-side events related to left and right click interactions.
 * It subscribes to Forge's event bus for handling these events on the client-side.
 */
@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = CommonConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEvents {

    /**
     * Event handler for the LeftClickBlock event on the client-side.
     * Sends a sprint key packet to the server if the sprint key is pressed.
     * @param event The LeftClickBlock event.
     */
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        boolean isCtrlKeyDown = Minecraft.getInstance().options.keySprint.isDown();
        PacketHandler.CHANNEL.sendToServer(new SprintKeyPacket(isCtrlKeyDown));
    }

    /**
     * Event handler for the RightClickBlock event on the client-side.
     * Sends a sprint key packet to the server if the sprint key is pressed.
     * @param event The RightClickBlock event.
     */
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        boolean isCtrlKeyDown = Minecraft.getInstance().options.keySprint.isDown();
        PacketHandler.CHANNEL.sendToServer(new SprintKeyPacket(isCtrlKeyDown));
    }
}