package com.christofmeg.fastentitytransfer;

import com.christofmeg.fastentitytransfer.network.PacketHandler;
import com.christofmeg.fastentitytransfer.network.SprintKeyPacket;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * The ClientEvents class handles client-side events related to left and right click interactions.
 * It subscribes to Forge's event bus for handling these events on the client-side.
 */
@Mod.EventBusSubscriber(modid = CommonConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEvents {

    private static KeyMapping fastEntityTransferKey;

    /**
     * Event handler for the LeftClickBlock event on the client-side.
     * Sends a sprint key packet to the server if the sprint key is pressed.
     * @param event The LeftClickBlock event.
     */
    @SuppressWarnings("unused")
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        if (block instanceof AbstractFurnaceBlock && !CommonClickInteractions.isCtrlKeyDown) {
            if (fastEntityTransferKey.isDefault() && Minecraft.getInstance().options.keySprint.isDown()) {
                PacketHandler.CHANNEL.sendToServer(new SprintKeyPacket(true));
            } else if (fastEntityTransferKey.isDown()) {
                PacketHandler.CHANNEL.sendToServer(new SprintKeyPacket(true));
            } else {
                PacketHandler.CHANNEL.sendToServer(new SprintKeyPacket(false));
            }
        }
    }

    /**
     * Event handler for the RightClickBlock event on the client-side.
     * Sends a sprint key packet to the server if the sprint key is pressed.
     * @param event The RightClickBlock event.
     */
    @SuppressWarnings("unused")
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        if (block instanceof AbstractFurnaceBlock && !CommonClickInteractions.isCtrlKeyDown) {
            if (fastEntityTransferKey.isDefault() && Minecraft.getInstance().options.keySprint.isDown()) {
                PacketHandler.CHANNEL.sendToServer(new SprintKeyPacket(true));
            } else if (fastEntityTransferKey.isDown()) {
                PacketHandler.CHANNEL.sendToServer(new SprintKeyPacket(true));
            }
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public static void init(RegisterKeyMappingsEvent event) {
        fastEntityTransferKey = new KeyMapping("key.fastentitytransfer", KeyConflictContext.IN_GAME, InputConstants.getKey("key.keyboard.left.control"), "key.fastentitytransfer");
        event.register(fastEntityTransferKey);
    }

}