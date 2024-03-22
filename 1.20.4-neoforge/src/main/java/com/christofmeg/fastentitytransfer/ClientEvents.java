package com.christofmeg.fastentitytransfer;

import com.christofmeg.fastentitytransfer.network.SprintKeyPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;

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
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        if (block instanceof AbstractFurnaceBlock && !CommonClickInteractions.isCtrlKeyDown) {
            if (ClientModEvents.fastEntityTransferKey.isDefault() && Minecraft.getInstance().options.keySprint.isDown()) {
                PacketDistributor.SERVER.noArg().send(new SprintKeyPayload(true));
            } else if (ClientModEvents.fastEntityTransferKey.isDown()) {
                PacketDistributor.SERVER.noArg().send(new SprintKeyPayload(true));
            } else {
                PacketDistributor.SERVER.noArg().send(new SprintKeyPayload(false));
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
            if (ClientModEvents.fastEntityTransferKey.isDefault() && Minecraft.getInstance().options.keySprint.isDown()) {
                PacketDistributor.SERVER.noArg().send(new SprintKeyPayload(true));
            } else if (ClientModEvents.fastEntityTransferKey.isDown()) {
                PacketDistributor.SERVER.noArg().send(new SprintKeyPayload(true));
            }
        }
    }

}