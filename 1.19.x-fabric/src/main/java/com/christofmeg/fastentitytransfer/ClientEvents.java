package com.christofmeg.fastentitytransfer;

import com.christofmeg.fastentitytransfer.network.SprintKeyPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionResult;

public class ClientEvents implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            boolean isCtrlKeyDown = Minecraft.getInstance().options.keySprint.isDown();
            if(isCtrlKeyDown) {
                SprintKeyPacket.send(true);
            }
            return InteractionResult.PASS;
        });

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            boolean isCtrlKeyDown = Minecraft.getInstance().options.keySprint.isDown();
            if(isCtrlKeyDown) {
                SprintKeyPacket.send(true);
            }
            return InteractionResult.PASS;
        });

    }
}