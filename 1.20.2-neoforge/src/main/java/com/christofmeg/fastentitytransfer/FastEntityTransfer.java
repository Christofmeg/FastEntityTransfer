package com.christofmeg.fastentitytransfer;

import com.christofmeg.fastentitytransfer.network.PacketHandler;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * The FastEntityTransfer class is the main class of the mod.
 * It initializes the mod, registers event subscribers, and provides event handlers for left and right click events.
 */
@SuppressWarnings("unused")
@Mod(CommonConstants.MOD_ID)
@Mod.EventBusSubscriber(modid = CommonConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FastEntityTransfer {

    /**
     * Represents the state of the control key.
     */
    public static boolean isCtrlKeyDown = false;

    /**
     * Constructs a new instance of the FastEntityTransfer class.
     * This method is invoked by the Forge mod loader when it is ready to load the mod.
     * It initializes the mod, registers packets, and sets up common interactions.
     */
    public FastEntityTransfer() {
        PacketHandler.registerPackets();
        // This method is invoked by the Forge mod loader when it is ready
        // to load your mod. You can access Forge and Common code in this
        // project.

        // Use Forge to bootstrap the Common mod.
        CommonClickInteractions.init();
    }

    /**
     * Event handler for the LeftClickBlock event.
     * This method exists as a wrapper for the code in the Common project.
     * It takes Forge's event object and passes the parameters along to the Common listener.
     * @param event The LeftClickBlock event.
     */
    @SubscribeEvent
    public static void onLeftClickBlock(LeftClickBlock event) {
        Level level = event.getLevel();
        InteractionResult result = CommonClickInteractions.onLeftClickBlock(event.getEntity(), level, event.getHand(), event.getPos(), event.getFace(), isCtrlKeyDown, level.registryAccess());
        if (result == InteractionResult.CONSUME) {
            isCtrlKeyDown = false;
            event.setCanceled(true);
        }
    }

    /**
     * Event handler for the RightClickBlock event.
     * This method exists as a wrapper for the code in the Common project.
     * It takes Forge's event object and passes the parameters along to the Common listener.
     * @param event The RightClickBlock event.
     */
    @SubscribeEvent
    public static void onRightClickBlock(RightClickBlock event) {
        Level level = event.getLevel();
        InteractionResult result = CommonClickInteractions.onRightClickBlock(event.getEntity(), level, event.getHand(), event.getHitVec(), isCtrlKeyDown, level.registryAccess());
        if (result == InteractionResult.CONSUME) {
            isCtrlKeyDown = false;
            event.setCanceled(true);
        }
    }
}