package com.christofmeg.fastentitytransfer;

import com.christofmeg.fastentitytransfer.network.PacketHandler;
import net.minecraft.world.InteractionResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * The FastEntityTransfer class is the main class of the mod.
 * It initializes the mod, registers event subscribers, and provides event handlers for left and right click events.
 */
@Mod(CommonConstants.MOD_ID)
@Mod.EventBusSubscriber(modid = CommonConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FastEntityTransfer {

    /**
     * Constructs a new instance of the FastEntityTransfer class.
     * This method is invoked by the Forge mod loader when it is ready to load the mod.
     * It initializes the mod, registers packets, and sets up common interactions.
     */
    public FastEntityTransfer() {
        PacketHandler.registerPackets();
        CommonClickInteractions.init();

        IEventBus modbus = FMLJavaModLoadingContext.get().getModEventBus();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> modbus.addListener(ClientEvents::init));
    }

    /**
     * Event handler for the LeftClickBlock event.
     * This method exists as a wrapper for the code in the Common project.
     * It takes Forge's event object and passes the parameters along to the Common listener.
     * @param event The LeftClickBlock event.
     */
    @SuppressWarnings("unused")
    @SubscribeEvent
    public static void onLeftClickBlock(LeftClickBlock event) {
        InteractionResult result = CommonClickInteractions.onLeftClickBlock(event.getEntity(), event.getEntity().getLevel(), event.getHand(), event.getPos(), event.getFace());
        if (result == InteractionResult.CONSUME) {
            event.setCanceled(true);
        }
    }

    /**
     * Event handler for the RightClickBlock event.
     * This method exists as a wrapper for the code in the Common project.
     * It takes Forge's event object and passes the parameters along to the Common listener.
     * @param event The RightClickBlock event.
     */
    @SuppressWarnings("unused")
    @SubscribeEvent
    public static void onRightClickBlock(RightClickBlock event) {
        InteractionResult result = CommonClickInteractions.onRightClickBlock(event.getEntity(), event.getEntity().getLevel(), event.getHand(), event.getHitVec());
        if (result == InteractionResult.CONSUME) {
            event.setCanceled(true);
        }
    }
}