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
import net.minecraftforge.network.PacketDistributor;

/**
 * The ClientEvents class handles client-side events related to left and right click interactions.
 * It subscribes to Forge's event bus for handling these events on the client-side.
 */
@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = CommonConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEvents {

    private static KeyMapping fastEntityTransferKey;

    /**
     * Event handler for the LeftClickBlock event on the client-side.
     * Sends a sprint key packet to the server if the sprint key is pressed.
     * @param event The LeftClickBlock event.
     */
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        if (block instanceof AbstractFurnaceBlock && !CommonClickInteractions.isCtrlKeyDown) {
            if (fastEntityTransferKey.isDefault() && Minecraft.getInstance().options.keySprint.isDown()) {
                PacketHandler.CHANNEL.send(new SprintKeyPacket(true), PacketDistributor.SERVER.noArg());
            } else if (fastEntityTransferKey.isDown()) {
                PacketHandler.CHANNEL.send(new SprintKeyPacket(true), PacketDistributor.SERVER.noArg());
            } else {
                PacketHandler.CHANNEL.send(new SprintKeyPacket(false), PacketDistributor.SERVER.noArg());
            }
        }
    }

    /**
     * Event handler for the RightClickBlock event on the client-side.
     * Sends a sprint key packet to the server if the sprint key is pressed.
     * @param event The RightClickBlock event.
     */
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        if (block instanceof AbstractFurnaceBlock && !CommonClickInteractions.isCtrlKeyDown) {
            if (fastEntityTransferKey.isDefault() && Minecraft.getInstance().options.keySprint.isDown()) {
                PacketHandler.CHANNEL.send(new SprintKeyPacket(true), PacketDistributor.SERVER.noArg());
            } else if (fastEntityTransferKey.isDown()) {
                PacketHandler.CHANNEL.send(new SprintKeyPacket(true), PacketDistributor.SERVER.noArg());
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