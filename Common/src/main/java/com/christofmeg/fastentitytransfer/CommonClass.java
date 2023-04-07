package com.christofmeg.fastentitytransfer;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlastFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SmokerBlockEntity;

import java.util.Map;
import java.util.Optional;

public class CommonClass {

    // This method serves as an initialization hook for the mod. The vanilla
    // game has no mechanism to load tooltip listeners so this must be
    // invoked from a mod loader specific project like Forge or Fabric.
    public static void init() {

    }

    public static InteractionResult onLeftClickBlock(Player player, Level level, InteractionHand hand, BlockPos pos, Direction ignoredDirection) {
        ItemStack stack = player.getItemInHand(hand);
        boolean isSprintKeyDown = Minecraft.getInstance().options.keySprint.isDown();
        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (!level.isClientSide() && isSprintKeyDown) {

            if (blockEntity instanceof SmokerBlockEntity smokerBlockEntity) {
                RecipeType<SmokingRecipe> recipeType = RecipeType.SMOKING;
                return doInteractions(blockEntity, level.getRecipeManager().getRecipeFor(recipeType, new SimpleContainer(stack), level), level.getRecipeManager().getRecipeFor(recipeType, new SimpleContainer(smokerBlockEntity.getItem(0)), level), player, hand);
            } else if (blockEntity instanceof BlastFurnaceBlockEntity smokerBlockEntity) {
                RecipeType<BlastingRecipe> recipeType = RecipeType.BLASTING;
                return doInteractions(blockEntity, level.getRecipeManager().getRecipeFor(recipeType, new SimpleContainer(stack), level), level.getRecipeManager().getRecipeFor(recipeType, new SimpleContainer(smokerBlockEntity.getItem(0)), level), player, hand);
            } else if (blockEntity instanceof AbstractFurnaceBlockEntity abstractBlockEntity) {
                RecipeType<SmeltingRecipe> recipeType = RecipeType.SMELTING;
                return doInteractions(blockEntity, level.getRecipeManager().getRecipeFor(recipeType, new SimpleContainer(stack), level), level.getRecipeManager().getRecipeFor(recipeType, new SimpleContainer(abstractBlockEntity.getItem(0)), level), player, hand);
            }
        }
        return InteractionResult.PASS;
    }

    private static InteractionResult doInteractions(BlockEntity blockEntity, Optional<?> optional, Optional<?> inputSlotOptional, Player player, InteractionHand hand) {
        AbstractFurnaceBlockEntity abstractBlockEntity = ((AbstractFurnaceBlockEntity) blockEntity);
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
        Map<Item, Integer> fuelMap = AbstractFurnaceBlockEntity.getFuel();
        int burnTime = fuelMap.getOrDefault(stack.getItem(), 0);
        int fuelBurnTime = fuelMap.getOrDefault(fuelSlot.getItem(), 0);
        int inputMaxStackSize = inputSlot.getMaxStackSize();
        int inputStackSize = inputSlot.getCount();
        int fuelMaxStackSize = fuelSlot.getMaxStackSize();
        int fuelStackSize = fuelSlot.getCount();
        int stackSize = stack.getCount();

        // if input slot has items no suitable for blasting/smelting/smoking, give them to player
        if (inputSlotHasItemStack && inputSlotOptional.isEmpty()) {
            player.getInventory().add(inputSlot);
            inputSlot.setCount(0);
        }
        // if fuel slot has items without burn time, give them to player
        if (fuelBurnTime == 0) {
            player.getInventory().add(fuelSlot);
            fuelSlot.setCount(0);
        }

        // if output slot has items, give them to player
        if (outputSlotHasItemStack) {
            player.getInventory().add(outputSlot);
        }

        // award experience
        abstractBlockEntity.awardUsedRecipesAndPopExperience((ServerPlayer) player);

        // if item in hand is fuel without a smelting result
        if (burnTime != 0) {

            // if fuel slot is empty, fill it with item in hand
            if (fuelSlot.isEmpty()) {
                newItemStack = stack.copy();
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
                        }
                    }
                }
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
            }

            // if input slot and item in hand matches
            else doIfMatches(player, abstractBlockEntity, stack, item, newItemStack, inputSlotItem, inputMaxStackSize, inputStackSize, stackSize);
        }

        // if item in hand has blasting/smelting/smoking result and has no burn time
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
            else doIfMatches(player, abstractBlockEntity, stack, item, newItemStack, inputSlotItem, inputMaxStackSize, inputStackSize, stackSize);
        }

        return InteractionResult.CONSUME;
    }

    private static void doIfMatches(Player player, AbstractFurnaceBlockEntity abstractBlockEntity, ItemStack stack, Item item, ItemStack newItemStack, Item inputSlotItem, int inputMaxStackSize, int inputStackSize, int stackSize) {
        if (inputSlotItem == item) {

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
            }
        }
    }
}