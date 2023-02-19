package com.christofmeg.fastentitytransfer.common.event;

import java.util.Optional;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.BlastingRecipe;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.SmokingRecipe;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.tileentity.BlastFurnaceTileEntity;
import net.minecraft.tileentity.SmokerTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = "fastentitytransfer", bus = EventBusSubscriber.Bus.FORGE)
public class FactorioFastEntitytransferEvent {

    @SuppressWarnings("resource")
    @SubscribeEvent
    public static void FastEntitytransfer(final PlayerInteractEvent.LeftClickBlock event) {
        PlayerEntity player = (PlayerEntity) event.getEntity();
        World level = event.getWorld();
        BlockPos pos = event.getPos();
        Hand hand = event.getHand();
        ItemStack stack = player.getHeldItem(hand);
        boolean isSprintKeyDown = Minecraft.getInstance().gameSettings.keyBindSprint.isKeyDown();
        TileEntity blockEntity = level.getTileEntity(pos);
        if (!level.isRemote && isSprintKeyDown) {
            if (blockEntity instanceof AbstractFurnaceTileEntity) {
                AbstractFurnaceTileEntity abstractBlockEntity = ((AbstractFurnaceTileEntity) blockEntity);
                IRecipeType<FurnaceRecipe> recipeType = IRecipeType.SMELTING;
                doInteractions(blockEntity, recipeType,
                        level.getRecipeManager().getRecipe(recipeType, new Inventory(stack), level),
                        level.getRecipeManager().getRecipe(recipeType,
                                new Inventory(abstractBlockEntity.getStackInSlot(0)), level),
                        event);
            }
            if (blockEntity instanceof SmokerTileEntity) {
                SmokerTileEntity smokerBlockEntity = ((SmokerTileEntity) blockEntity);
                IRecipeType<SmokingRecipe> recipeType = IRecipeType.SMOKING;
                doInteractions(blockEntity, recipeType,
                        level.getRecipeManager().getRecipe(recipeType, new Inventory(stack), level),
                        level.getRecipeManager().getRecipe(recipeType,
                                new Inventory(smokerBlockEntity.getStackInSlot(0)), level),
                        event);
            }
            if (blockEntity instanceof BlastFurnaceTileEntity) {
                BlastFurnaceTileEntity smokerBlockEntity = ((BlastFurnaceTileEntity) blockEntity);
                IRecipeType<BlastingRecipe> recipeType = IRecipeType.BLASTING;
                doInteractions(blockEntity, recipeType,
                        level.getRecipeManager().getRecipe(recipeType, new Inventory(stack), level),
                        level.getRecipeManager().getRecipe(recipeType,
                                new Inventory(smokerBlockEntity.getStackInSlot(0)), level),
                        event);
            }
        }
    }

    private static void doInteractions(TileEntity blockEntity, IRecipeType<?> recipeType, Optional<?> optional,
            Optional<?> inputSlotOptional, final LeftClickBlock event) {
        AbstractFurnaceTileEntity abstractBlockEntity = ((AbstractFurnaceTileEntity) blockEntity);
        PlayerEntity player = (PlayerEntity) event.getEntity();
        Hand hand = event.getHand();
        ItemStack stack = player.getHeldItem(hand);
        Item item = stack.getItem();
        ItemStack inputSlot = abstractBlockEntity.getStackInSlot(0);
        ItemStack fuelSlot = abstractBlockEntity.getStackInSlot(1);
        ItemStack outputSlot = abstractBlockEntity.getStackInSlot(2);
        ItemStack newItemStack = new ItemStack(item);
        Item inputSlotItem = inputSlot.getItem();
        Item fuelSlotItem = fuelSlot.getItem();
        boolean inputSlotHasItemStack = !inputSlot.isEmpty();
        boolean outputSlotHasItemStack = !outputSlot.isEmpty();
        boolean fuelSlotHasItemStack = !fuelSlot.isEmpty();
        int burnTime = ForgeHooks.getBurnTime(stack);
        int fuelBurnTime = ForgeHooks.getBurnTime(fuelSlot);
        int inputMaxStackSize = inputSlot.getMaxStackSize();
        int inputStackSize = inputSlot.getCount();
        int fuelMaxStackSize = fuelSlot.getMaxStackSize();
        int fuelStackSize = fuelSlot.getCount();
        int stackSize = stack.getCount();

        // if input slot has items blasting/smelting/smoking recipe, give them to player
        if (inputSlotHasItemStack && !inputSlotOptional.isPresent()) {
            player.inventory.add(inputStackSize, inputSlot);
            inputSlot.setCount(0);
        }

        // if fuel slot has items without burntime, give them to player
        if (fuelBurnTime == 0) {
            player.inventory.add(fuelStackSize, fuelSlot);
            fuelSlot.setCount(0);
        }

        // if output slot has items results, give them to player
        if (outputSlotHasItemStack) {
            player.inventory.add(outputSlot.getCount(), outputSlot);
        }

        // award experience
        abstractBlockEntity.func_235645_d_(player);

        // if item in hand is fuel without a smelting result
        if (burnTime != 0) {

            // if fuel slot is empty, fill it with item in hand
            if (fuelSlot.isEmpty()) {
                newItemStack.setCount(stackSize);
                abstractBlockEntity.setInventorySlotContents(1, newItemStack);
                if (!player.isCreative()) {
                    stack.shrink(stackSize);
                }
            }

            // if fuel slot and hand item matches
            else if (fuelSlotItem == item) {

                // if stack is full cancel
                if (fuelStackSize != fuelMaxStackSize) {

                    // if item count of both stacks exceed max stack size, merge the stacks, and
                    // keep the rest in player inventory
                    if ((fuelStackSize + stackSize) > fuelMaxStackSize) {
                        newItemStack.setCount(fuelMaxStackSize);
                        abstractBlockEntity.setInventorySlotContents(1, newItemStack);
                        if (!player.isCreative()) {
                            stack.setCount((fuelStackSize + stackSize) - fuelMaxStackSize);
                        }
                    }

                    // if item in fuel slot is the same, merge the stacks
                    else {
                        newItemStack.setCount(fuelStackSize + stackSize);
                        abstractBlockEntity.setInventorySlotContents(1, newItemStack);

                        if (!player.isCreative()) {
                            stack.shrink(stackSize);
                        }
                    }
                    event.setCanceled(true);
                }

                // Insert smeltable fuel in input slot if fuel slot is already full of it
                else if (optional.isPresent()) {
                    ItemStack resultItem = ((AbstractCookingRecipe) optional.get()).getRecipeOutput();
                    if (!resultItem.isEmpty()) {

                        // if input slot empty and fuel slot full, put smeltable fuel in input slot
                        if (inputSlot.isEmpty()) {
                            newItemStack.setCount(inputStackSize + stackSize);
                            abstractBlockEntity.setInventorySlotContents(0, newItemStack);

                            if (!player.isCreative()) {
                                stack.shrink(stackSize);
                            }
                        }

                        // if stack is full cancel
                        if (inputStackSize != inputMaxStackSize) {

                            // if item count of both stacks exceed max stack size, merge the stacks, and
                            // keep the rest in player inventory
                            if ((inputStackSize + stackSize) > inputMaxStackSize) {
                                newItemStack.setCount(inputMaxStackSize);
                                abstractBlockEntity.setInventorySlotContents(0, newItemStack);
                                if (!player.isCreative()) {
                                    stack.setCount((inputStackSize + stackSize) - inputMaxStackSize);
                                }
                            }

                            // if item in slot is the same, merge the stacks
                            else {
                                newItemStack.setCount(inputStackSize + stackSize);
                                abstractBlockEntity.setInventorySlotContents(0, newItemStack);

                                if (!player.isCreative()) {
                                    stack.shrink(stackSize);
                                }
                            }
                            event.setCanceled(true);
                        }
                        event.setCanceled(true);
                    }
                }
                event.setCanceled(true);
            }

            // if fuel slot has fuel, input slot is empty and item is smeltable put item
            // in input slot
            else if (fuelSlotHasItemStack && inputSlot.isEmpty()) {
                if (optional.isPresent()) {
                    ItemStack resultItem = ((AbstractCookingRecipe) optional.get()).getRecipeOutput();
                    if (!resultItem.isEmpty()) {
                        newItemStack.setCount(stackSize);
                        abstractBlockEntity.setInventorySlotContents(0, newItemStack);
                        if (!player.isCreative()) {
                            stack.shrink(stackSize);
                        }
                    }
                }
                event.setCanceled(true);
            }

            // if input slot and item in hand matches
            else if (inputSlotItem == item) {

                // if input stack is full cancel
                if (inputStackSize != inputMaxStackSize) {

                    // if item count of both stacks exceed max stack size, merge the stacks, and
                    // keep the rest in player inventory
                    if (inputStackSize + stackSize > inputMaxStackSize) {
                        newItemStack.setCount(inputMaxStackSize);
                        abstractBlockEntity.setInventorySlotContents(0, newItemStack);
                        if (!player.isCreative()) {
                            stack.setCount(inputStackSize + stackSize - inputMaxStackSize);
                        }
                    }

                    // if item in input slot and inventory are the same, merge the stacks
                    else {
                        newItemStack.setCount(inputStackSize + stackSize);
                        abstractBlockEntity.setInventorySlotContents(0, newItemStack);
                        if (!player.isCreative()) {
                            stack.shrink(stackSize);
                        }
                    }
                    event.setCanceled(true);
                }
                event.setCanceled(true);
            }
            event.setCanceled(true);
        }

        // if item in hand has blasting/smelting/smoking result and has no burntime
        else if (optional.isPresent()) {

            // if input slot empty, fill with item in hand
            if (inputSlot.isEmpty()) {
                newItemStack.setCount(stackSize);
                abstractBlockEntity.setInventorySlotContents(0, newItemStack);
                if (!player.isCreative()) {
                    stack.shrink(stackSize);
                }
            }

            // if input slot and item in hand matches
            else if (inputSlotItem == item) {

                // if input stack is full cancel
                if (inputStackSize != inputMaxStackSize) {

                    // if item count of both stacks exceed max stack size, merge the stacks, and
                    // keep the rest in player inventory
                    if (inputStackSize + stackSize > inputMaxStackSize) {
                        newItemStack.setCount(inputMaxStackSize);
                        abstractBlockEntity.setInventorySlotContents(0, newItemStack);
                        if (!player.isCreative()) {
                            stack.setCount(inputStackSize + stackSize - inputMaxStackSize);
                        }
                    }

                    // if item in input slot and inventory are the same, merge the stacks
                    else {
                        newItemStack.setCount(inputStackSize + stackSize);
                        abstractBlockEntity.setInventorySlotContents(0, newItemStack);
                        if (!player.isCreative()) {
                            stack.shrink(stackSize);
                        }
                    }
                    event.setCanceled(true);
                }
                event.setCanceled(true);
            }
            event.setCanceled(true);
        }
        event.setCanceled(true);
    }

}
