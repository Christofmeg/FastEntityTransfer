package com.christofmeg.fastentitytransfer;

import com.christofmeg.fastentitytransfer.network.SprintKeyPacket;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionResult;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;

@SuppressWarnings("unused")
public class ClientEvents implements ClientModInitializer {

    @Override
    public void onInitializeClient(ModContainer mod) {

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