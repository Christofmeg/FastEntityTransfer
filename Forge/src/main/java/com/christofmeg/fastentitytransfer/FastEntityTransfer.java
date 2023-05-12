package com.christofmeg.fastentitytransfer;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(CommonConstants.MOD_ID)
@Mod.EventBusSubscriber(modid = CommonConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FastEntityTransfer {

    public FastEntityTransfer() {

        // This method is invoked by the Forge mod loader when it is ready
        // to load your mod. You can access Forge and Common code in this
        // project.

        // Use Forge to bootstrap the Common mod.
        CommonClickInteractions.init();

    }
    
    // This method exists as a wrapper for the code in the Common project.
    // It takes Forge's event object and passes the parameters along to
    // the Common listener.
    @SubscribeEvent
    public static void onLeftClickBlock(LeftClickBlock event) {
        InteractionResult result = CommonClickInteractions.onLeftClickBlock(event.getPlayer(), event.getPlayer().getLevel(), event.getHand(), event.getPos(), event.getFace());
        if (result == InteractionResult.CONSUME) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onRightClickBlock(RightClickBlock event) {
        InteractionResult result = CommonClickInteractions.onRightClickBlock(event.getPlayer(), event.getPlayer().getLevel(), event.getHand(), event.getHitVec());
        if (result == InteractionResult.CONSUME) event.setCanceled(true);

    }
}