package com.christofmeg.fastentitytransfer;

import com.christofmeg.fastentitytransfer.network.PacketHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(CommonConstants.MOD_ID)
@Mod.EventBusSubscriber(modid = CommonConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FastEntityTransfer {

    public static boolean isCtrlKeyDown = false;

    public FastEntityTransfer() {
        PacketHandler.registerPackets();
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
        CommonUtils.PrivateInteractionResult result = CommonClickInteractions.onLeftClickBlock((PlayerEntity) event.getEntity(), event.getWorld(), event.getHand(), event.getPos(), event.getFace(), isCtrlKeyDown);
        if (result == CommonUtils.PrivateInteractionResult.CONSUME) {
            isCtrlKeyDown = false;
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRightClickBlock(RightClickBlock event) {
        CommonUtils.PrivateInteractionResult result = CommonClickInteractions.onRightClickBlock(event.getPlayer(), event.getWorld(), event.getHand(), event.getHitVec(), isCtrlKeyDown);
        if (result == CommonUtils.PrivateInteractionResult.CONSUME) {
            isCtrlKeyDown = false;
            event.setCanceled(true);
        }

    }

}