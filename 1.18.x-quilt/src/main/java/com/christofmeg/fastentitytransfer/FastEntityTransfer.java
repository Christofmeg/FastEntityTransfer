package com.christofmeg.fastentitytransfer;

import com.christofmeg.fastentitytransfer.network.PacketHandler;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

public class FastEntityTransfer implements ModInitializer {

    public static final ResourceLocation CTRL_KEY_PACKET_ID = new ResourceLocation(CommonConstants.MOD_ID, "ctrl_key_is_down");

    public static boolean isSprintKeyDown = false;

    @Override
    public void onInitialize(ModContainer mod) {
        PacketHandler.registerPackets();
        CommonClickInteractions.init();

        AttackBlockCallback.EVENT.register((player, level, hand, pos, direction) -> {
            if(isSprintKeyDown) {
                InteractionResult result = CommonClickInteractions.onLeftClickBlock(player, level, hand, pos, direction, true);
                if (result == InteractionResult.CONSUME) {
                    isSprintKeyDown = false;
                    return result;
                }
            }
            return InteractionResult.PASS;
        });

        UseBlockCallback.EVENT.register((player, level, hand, blockHitResult) -> {
            if(isSprintKeyDown) {
                InteractionResult result = CommonClickInteractions.onRightClickBlock(player, level, hand, blockHitResult, true);
                if (result == InteractionResult.CONSUME) {
                    isSprintKeyDown = false;
                    return result;
                }
            }
            return InteractionResult.PASS;
        });

    }

}