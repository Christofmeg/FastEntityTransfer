package com.christofmeg.fastentitytransfer;

import com.christofmeg.fastentitytransfer.network.SprintKeyPacket;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.lwjgl.glfw.GLFW;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;

@SuppressWarnings("unused")
public class ClientEvents implements ClientModInitializer {

    private static KeyMapping fastEntityTransferKey;

    @Override
    public void onInitializeClient(ModContainer mod) {

        AttackBlockCallback.EVENT.register((player, level, hand, pos, direction) -> {
            BlockState state = level.getBlockState(pos);
            Block block = state.getBlock();
            if (block instanceof AbstractFurnaceBlock && !CommonClickInteractions.isCtrlKeyDown) {
                if (fastEntityTransferKey.isDefault() && Minecraft.getInstance().options.keySprint.isDown()) {
                    SprintKeyPacket.send(true);
                } else if (fastEntityTransferKey.isDown()) {
                    SprintKeyPacket.send(true);
                }
            }
            return InteractionResult.PASS;
        });

        UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
            BlockState state = level.getBlockState(hitResult.getBlockPos());
            Block block = state.getBlock();
            if (block instanceof AbstractFurnaceBlock && !CommonClickInteractions.isCtrlKeyDown) {
                if (fastEntityTransferKey.isDefault() && Minecraft.getInstance().options.keySprint.isDown()) {
                    SprintKeyPacket.send(true);
                } else if (fastEntityTransferKey.isDown()) {
                    SprintKeyPacket.send(true);
                }
            }
            return InteractionResult.PASS;
        });

        fastEntityTransferKey = new KeyMapping("key.fastentitytransfer", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_CONTROL, "key.fastentitytransfer");
        KeyBindingHelper.registerKeyBinding(fastEntityTransferKey);
    }

}