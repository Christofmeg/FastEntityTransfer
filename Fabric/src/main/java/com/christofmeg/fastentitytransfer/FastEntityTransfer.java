package com.christofmeg.fastentitytransfer;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;

public class FastEntityTransfer implements ModInitializer {
    
    @Override
    public void onInitialize() {
        
        // This method is invoked by the Fabric mod loader when it is ready
        // to load your mod. You can access Fabric and Common code in this
        // project.

        // Use Fabric to bootstrap the Common mod.
//        Constants.LOG.info("Hello Fabric world!");
        CommonLeftClickInteractions.init();
        CommonRightClickInteractions.init();
        
        // Some code like events require special initialization from the
        // loader specific code.
        AttackBlockCallback.EVENT.register(CommonLeftClickInteractions::onLeftClickBlock);
        AttackBlockCallback.EVENT.register(CommonRightClickInteractions::onRightClickBlock);
    }
}
