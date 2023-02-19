package com.christofmeg.fastentitytransfer;

import net.minecraft.world.InteractionResult;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class FastEntityTransfer {

    public FastEntityTransfer() {

        // This method is invoked by the Forge mod loader when it is ready
        // to load your mod. You can access Forge and Common code in this
        // project.

        // Use Forge to bootstrap the Common mod.
//        Constants.LOG.info("Hello Forge world!");
        CommonClass.init();

        // Some code like events require special initialization from the
        // loader specific code.
        MinecraftForge.EVENT_BUS.addListener(this::onLeftClickBlock);

    }
    
    // This method exists as a wrapper for the code in the Common project.
    // It takes Forge's event object and passes the parameters along to
    // the Common listener.
    private void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {

        InteractionResult result = CommonClass.onLeftClickBlock(event.getEntity(), event.getLevel(), event.getHand(), event.getPos(), event.getFace());
        if (result == InteractionResult.CONSUME) event.setCanceled(true);
    }
}