package com.christofmeg.fastentitytransfer;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;

@Mod.EventBusSubscriber(modid = CommonConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {

    public static KeyMapping fastEntityTransferKey;

    @SuppressWarnings("unused")
    @SubscribeEvent
    public static void init(RegisterKeyMappingsEvent event) {
        fastEntityTransferKey = new KeyMapping("key.fastentitytransfer", KeyConflictContext.IN_GAME, InputConstants.getKey("key.keyboard.left.control"), "key.fastentitytransfer");
        event.register(fastEntityTransferKey);
    }

}
