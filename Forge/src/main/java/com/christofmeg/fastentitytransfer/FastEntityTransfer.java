package com.christofmeg.fastentitytransfer;

import net.minecraft.world.InteractionResult;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(CommonConstants.MOD_ID)
public class FastEntityTransfer {

    public FastEntityTransfer() {

        // This method is invoked by the Forge mod loader when it is ready
        // to load your mod. You can access Forge and Common code in this
        // project.

        // Use Forge to bootstrap the Common mod.
        CommonLeftClickInteractions.init();
        CommonRightClickInteractions.init();

        // Some code like events require special initialization from the
        // loader specific code.
        MinecraftForge.EVENT_BUS.addListener(this::onLeftClickBlock);
        MinecraftForge.EVENT_BUS.addListener(this::onRightClickBlock);
    }
    
    // This method exists as a wrapper for the code in the Common project.
    // It takes Forge's event object and passes the parameters along to
    // the Common listener.
    private void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        InteractionResult result = CommonLeftClickInteractions.onLeftClickBlock(event.getEntity(), event.getLevel(), event.getHand(), event.getPos(), event.getFace());
        if (result == InteractionResult.CONSUME) event.setCanceled(true);
    }
    private void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        InteractionResult result = CommonRightClickInteractions.onRightClickBlock(event.getEntity(), event.getLevel(), event.getHand(), event.getPos(), event.getFace());
        if (result == InteractionResult.CONSUME) event.setCanceled(true);
    }
}