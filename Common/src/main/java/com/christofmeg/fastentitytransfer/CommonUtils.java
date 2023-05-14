package com.christofmeg.fastentitytransfer;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

public class CommonUtils {

    public static InteractionResult doLeftClickInteractions(@NotNull BlockEntity blockEntity, @NotNull Optional<?> optional, @NotNull Optional<?> inputSlotOptional, @NotNull Player player, @NotNull InteractionHand hand) {
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

            else doIfHasBurntime(fuelSlotItem, item, fuelStackSize, fuelMaxStackSize, stackSize, newItemStack, abstractBlockEntity, player, stack, optional, inputSlot, inputStackSize, inputMaxStackSize, fuelSlotHasItemStack, inputSlotItem);
        }

        else {
            doIfOptionalIsPresent(optional, inputSlot, newItemStack, stackSize, abstractBlockEntity, player, stack, item, inputSlotItem, inputMaxStackSize, inputStackSize);
        }

        return InteractionResult.CONSUME;
    }

    public static InteractionResult doRightClickInteractions(@NotNull BlockEntity blockEntity, @NotNull Optional<?> optional, @NotNull Player player, @NotNull InteractionHand hand) {
        AbstractFurnaceBlockEntity abstractBlockEntity = ((AbstractFurnaceBlockEntity) blockEntity);
        ItemStack stack = player.getItemInHand(hand);
        Item item = stack.getItem();
        ItemStack inputSlot = abstractBlockEntity.getItem(0);
        ItemStack fuelSlot = abstractBlockEntity.getItem(1);
        ItemStack newItemStack = new ItemStack(item);
        Item inputSlotItem = inputSlot.getItem();
        Item fuelSlotItem = fuelSlot.getItem();
        boolean fuelSlotHasItemStack = !fuelSlot.isEmpty();
        Map<Item, Integer> fuelMap = AbstractFurnaceBlockEntity.getFuel();
        int burnTime = fuelMap.getOrDefault(stack.getItem(), 0);
        int inputMaxStackSize = inputSlot.getMaxStackSize();
        int inputStackSize = inputSlot.getCount();
        int fuelMaxStackSize = fuelSlot.getMaxStackSize();
        int fuelStackSize = fuelSlot.getCount();
        int stackSize = stack.getCount();
        int half = stackSize / 2;
        if(stackSize == 1) {
            half = 1;
        }

        // if item in hand is fuel without a smelting result
        if (burnTime != 0) {

            // if fuel slot is empty, fill it with item in hand
            if (fuelSlot.isEmpty()) {
                newItemStack = stack.copy();
                newItemStack.setCount(half);
                abstractBlockEntity.setItem(1, newItemStack);
                if (!player.isCreative()) {
                    stack.shrink(half);
                }
            }

            else doIfHasBurntime(fuelSlotItem, item, fuelStackSize, fuelMaxStackSize, half, newItemStack, abstractBlockEntity, player, stack, optional, inputSlot, inputStackSize, inputMaxStackSize, fuelSlotHasItemStack, inputSlotItem);
        }

        else {
            doIfOptionalIsPresent(optional, inputSlot, newItemStack, half, abstractBlockEntity, player, stack, item, inputSlotItem, inputMaxStackSize, inputStackSize);
        }

        return InteractionResult.CONSUME;
    }

    public static void doIfHasBurntime(Item fuelSlotItem, Item item, int fuelStackSize, int fuelMaxStackSize, int half, ItemStack newItemStack, AbstractFurnaceBlockEntity abstractBlockEntity, Player player, ItemStack stack, Optional<?> optional, ItemStack inputSlot, int inputStackSize, int inputMaxStackSize, boolean fuelSlotHasItemStack, Item inputSlotItem){
        // if fuel slot and hand item matches
        if (fuelSlotItem == item) {

            // if stack is full cancel
            if (fuelStackSize != fuelMaxStackSize) {

                // if item count of both stacks exceed max stack size, merge the stacks, and
                // keep the rest in player inventory
                if ((fuelStackSize + half) > fuelMaxStackSize) {
                    newItemStack.setCount(fuelMaxStackSize);
                    abstractBlockEntity.setItem(1, newItemStack);
                    if (!player.isCreative()) {
                        stack.setCount((fuelStackSize + half) - fuelMaxStackSize);
                    }
                }

                // if item in fuel slot is the same, merge the stacks
                else {
                    newItemStack.setCount(fuelStackSize + half);
                    abstractBlockEntity.setItem(1, newItemStack);

                    if (!player.isCreative()) {
                        stack.shrink(half);
                    }
                }
            }

            // Insert smeltable fuel in input slot if fuel slot is already full of it
            else if (optional.isPresent()) {
                ItemStack resultItem = ((AbstractCookingRecipe) optional.get()).getResultItem();
                if (!resultItem.isEmpty()) {

                    // if input slot empty and fuel slot full, put smeltable fuel in input slot
                    if (inputSlot.isEmpty()) {
                        newItemStack.setCount(inputStackSize + half);
                        abstractBlockEntity.setItem(0, newItemStack);

                        if (!player.isCreative()) {
                            stack.shrink(half);
                        }
                    }

                    // if stack is full cancel
                    if (inputStackSize != inputMaxStackSize) {

                        // if item count of both stacks exceed max stack size, merge the stacks, and
                        // keep the rest in player inventory
                        if ((inputStackSize + half) > inputMaxStackSize) {
                            newItemStack.setCount(inputMaxStackSize);
                            abstractBlockEntity.setItem(0, newItemStack);
                            if (!player.isCreative()) {
                                stack.setCount((inputStackSize + half) - inputMaxStackSize);
                            }
                        }

                        // if item in slot is the same, merge the stacks
                        else {
                            newItemStack.setCount(inputStackSize + half);
                            abstractBlockEntity.setItem(0, newItemStack);

                            if (!player.isCreative()) {
                                stack.shrink(half);
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
                    newItemStack.setCount(half);
                    abstractBlockEntity.setItem(0, newItemStack);
                    if (!player.isCreative()) {
                        stack.shrink(half);
                    }
                }
            }
        }

        // if input slot and item in hand matches
        else CommonUtils.doIfMatches(player, abstractBlockEntity, stack, item, newItemStack, inputSlotItem, inputMaxStackSize, inputStackSize, half);
    }

    public static void doIfOptionalIsPresent(Optional<?> optional, ItemStack inputSlot, ItemStack newItemStack, int half, AbstractFurnaceBlockEntity abstractBlockEntity, Player player, ItemStack stack, Item item, Item inputSlotItem, int inputMaxStackSize, int inputStackSize) {
        // if item in hand has blasting/smelting/smoking result and has no burn time
        if (optional.isPresent()) {

            // if input slot empty, fill with item in hand
            if (inputSlot.isEmpty()) {
                newItemStack.setCount(half);
                abstractBlockEntity.setItem(0, newItemStack);
                if (!player.isCreative()) {
                    stack.shrink(half);
                }
            }

            // if input slot and item in hand matches
            else CommonUtils.doIfMatches(player, abstractBlockEntity, stack, item, newItemStack, inputSlotItem, inputMaxStackSize, inputStackSize, half);
        }
    }

    public static void doIfMatches(Player player, AbstractFurnaceBlockEntity abstractBlockEntity, ItemStack stack, Item item, ItemStack newItemStack, Item inputSlotItem, int inputMaxStackSize, int inputStackSize, int stackSize) {
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
