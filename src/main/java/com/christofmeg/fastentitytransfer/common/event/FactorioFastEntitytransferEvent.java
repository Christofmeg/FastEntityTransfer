package com.christofmeg.fastentitytransfer.common.event;

import java.util.Optional;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.crafting.SmokingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlastFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SmokerBlockEntity;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = "fastentitytransfer", bus = EventBusSubscriber.Bus.FORGE)
public class FactorioFastEntitytransferEvent {

    @SuppressWarnings({ "resource", })
    @SubscribeEvent
    public static void FastEntitytransfer(final PlayerInteractEvent.LeftClickBlock event) {
        Player player = (Player) event.getEntity();
        Level level = player.getLevel();
        BlockPos pos = event.getPos();
        InteractionHand hand = event.getHand();
        ItemStack stack = player.getItemInHand(hand);
        boolean isSprintKeyDown = Minecraft.getInstance().options.keySprint.isDown();
        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (!level.isClientSide() && isSprintKeyDown) {

            if (blockEntity instanceof AbstractFurnaceBlockEntity) {
                AbstractFurnaceBlockEntity abstractBlockEntity = ((AbstractFurnaceBlockEntity) blockEntity);
                RecipeType<SmeltingRecipe> recipeType = RecipeType.SMELTING;
                doInteractions(blockEntity, recipeType,
                        level.getRecipeManager().getRecipeFor(recipeType, new SimpleContainer(stack), level),
                        level.getRecipeManager().getRecipeFor(recipeType,
                                new SimpleContainer(abstractBlockEntity.getItem(0)), level),
                        event);
            }
            if (blockEntity instanceof SmokerBlockEntity) {
                SmokerBlockEntity smokerBlockEntity = ((SmokerBlockEntity) blockEntity);
                RecipeType<SmokingRecipe> recipeType = RecipeType.SMOKING;
                doInteractions(blockEntity, recipeType,
                        level.getRecipeManager().getRecipeFor(recipeType, new SimpleContainer(stack), level),
                        level.getRecipeManager().getRecipeFor(recipeType,
                                new SimpleContainer(smokerBlockEntity.getItem(0)), level),
                        event);
            }
            if (blockEntity instanceof BlastFurnaceBlockEntity) {
                BlastFurnaceBlockEntity smokerBlockEntity = ((BlastFurnaceBlockEntity) blockEntity);
                RecipeType<BlastingRecipe> recipeType = RecipeType.BLASTING;
                doInteractions(blockEntity, recipeType,
                        level.getRecipeManager().getRecipeFor(recipeType, new SimpleContainer(stack), level),
                        level.getRecipeManager().getRecipeFor(recipeType,
                                new SimpleContainer(smokerBlockEntity.getItem(0)), level),
                        event);
            }
        }
    }

    private static void doInteractions(BlockEntity blockEntity, RecipeType<?> recipeType, Optional<?> optional,
            Optional<?> inputSlotOptional, final LeftClickBlock event) {
        AbstractFurnaceBlockEntity abstractBlockEntity = ((AbstractFurnaceBlockEntity) blockEntity);
        Player player = (Player) event.getEntity();
        InteractionHand hand = event.getHand();
        ItemStack stack = player.getItemInHand(hand);
        Item item = stack.getItem();
        ItemStack inputSlot = abstractBlockEntity.getItem(0);
        ItemStack fuelSlot = abstractBlockEntity.getItem(1);
        ItemStack outputSlot = abstractBlockEntity.getItem(2);
        ItemStack newItemStack = new ItemStack(item);
        Item inputSlotItem = inputSlot.getItem();
        Item fuelSlotItem = fuelSlot.getItem();
        boolean inputSlotHasItemStack = !inputSlot.isEmpty();
        boolean outputSlotHasItemStack = !outputSlot.isEmpty();
        boolean fuelSlotHasItemStack = !fuelSlot.isEmpty();
        int burnTime = ForgeHooks.getBurnTime(stack, recipeType);
        int fuelBurnTime = ForgeHooks.getBurnTime(fuelSlot, recipeType);
        int inputMaxStackSize = inputSlot.getMaxStackSize();
        int inputStackSize = inputSlot.getCount();
        int fuelMaxStackSize = fuelSlot.getMaxStackSize();
        int fuelStackSize = fuelSlot.getCount();
        int stackSize = stack.getCount();

        // if input slot has items blasting/smelting/smoking recipe, give them to player
        if (inputSlotHasItemStack && !inputSlotOptional.isPresent()) {
            player.getInventory().add(inputSlot);
            inputSlot.setCount(0);
        }

        // if fuel slot has items without burntime, give them to player
        if (fuelBurnTime == 0) {
            player.getInventory().add(fuelSlot);
            fuelSlot.setCount(0);
        }

        // if output slot has items results, give them to player
        if (outputSlotHasItemStack) {
            player.getInventory().add(outputSlot);
        }

        abstractBlockEntity.awardUsedRecipesAndPopExperience((ServerPlayer) player);

        // if item in hand is fuel without a smelting result
        if (burnTime != 0) {

            // if fuel slot is empty, fill it with item in hand
            if (fuelSlot.isEmpty()) {
                newItemStack.setCount(stackSize);
                abstractBlockEntity.setItem(1, newItemStack);
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
                        abstractBlockEntity.setItem(1, newItemStack);
                        if (!player.isCreative()) {
                            stack.setCount((fuelStackSize + stackSize) - fuelMaxStackSize);
                        }
                    }

                    // if item in fuel slot is the same, merge the stacks
                    else {
                        newItemStack.setCount(fuelStackSize + stackSize);
                        abstractBlockEntity.setItem(1, newItemStack);

                        if (!player.isCreative()) {
                            stack.shrink(stackSize);
                        }
                    }
                    event.setCanceled(true);
                }

                // Insert smeltable fuel in input slot if fuel slot is already full of it
                else if (optional.isPresent()) {
                    ItemStack resultItem = ((AbstractCookingRecipe) optional.get()).getResultItem();
                    if (!resultItem.isEmpty()) {

                        // if input slot empty and fuel slot full, put smeltable fuel in input slot
                        if (inputSlot.isEmpty()) {
                            newItemStack.setCount(inputStackSize + stackSize);
                            abstractBlockEntity.setItem(0, newItemStack);

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
                                abstractBlockEntity.setItem(0, newItemStack);
                                if (!player.isCreative()) {
                                    stack.setCount((inputStackSize + stackSize) - inputMaxStackSize);
                                }
                            }

                            // if item in slot is the same, merge the stacks
                            else {
                                newItemStack.setCount(inputStackSize + stackSize);
                                abstractBlockEntity.setItem(0, newItemStack);

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
                    ItemStack resultItem = ((AbstractCookingRecipe) optional.get()).getResultItem();
                    if (!resultItem.isEmpty()) {
                        newItemStack.setCount(stackSize);
                        abstractBlockEntity.setItem(0, newItemStack);
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
                        abstractBlockEntity.setItem(0, newItemStack);
                        if (!player.isCreative()) {
                            stack.setCount(inputStackSize + stackSize - inputMaxStackSize);
                        }
                    }

                    // if item in input slot and inventory are the same, merge the stacks
                    else {
                        newItemStack.setCount(inputStackSize + stackSize);
                        abstractBlockEntity.setItem(0, newItemStack);
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
                abstractBlockEntity.setItem(0, newItemStack);
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
                        abstractBlockEntity.setItem(0, newItemStack);
                        if (!player.isCreative()) {
                            stack.setCount(inputStackSize + stackSize - inputMaxStackSize);
                        }
                    }

                    // if item in input slot and inventory are the same, merge the stacks
                    else {
                        newItemStack.setCount(inputStackSize + stackSize);
                        abstractBlockEntity.setItem(0, newItemStack);
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
