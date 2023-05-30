package com.christofmeg.fastentitytransfer;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;

import java.util.Map;
import java.util.Optional;

public class CommonUtils {

    public static PrivateInteractionResult doLeftClickInteractions(TileEntity blockEntity, Optional<?> recipe, Optional<?> inputSlotProcessingResult, PlayerEntity player, Hand hand) {
        AbstractFurnaceTileEntity abstractTileEntity = ((AbstractFurnaceTileEntity) blockEntity);
        ItemStack stackInHand = player.getItemInHand(hand);
        ItemStack inputSlotStack = abstractTileEntity.getItem(0);
        ItemStack fuelSlotStack = abstractTileEntity.getItem(1);
        ItemStack outputSlot = abstractTileEntity.getItem(2);
        boolean inputSlotHasItemStack = !inputSlotStack.isEmpty();
        boolean outputSlotHasItemStack = !outputSlot.isEmpty();
        boolean fuelSlotHasItemStack = !fuelSlotStack.isEmpty();
        Map<Item, Integer> fuelMap = AbstractFurnaceTileEntity.getFuel();
        int burnTime = fuelMap.getOrDefault(stackInHand.getItem(), 0);
        int fuelBurnTime = fuelMap.getOrDefault(fuelSlotStack.getItem(), 0);
        int inputMaxStackSize = inputSlotStack.getMaxStackSize();
        int inputSlotStackSize = inputSlotStack.getCount();
        int fuelMaxStackSize = fuelSlotStack.getMaxStackSize();
        int fuelStackSize = fuelSlotStack.getCount();
        int stackSize = stackInHand.getCount();

        // if input slot has items no suitable for blasting/smelting/smoking, give them to player
        if (inputSlotHasItemStack && !inputSlotProcessingResult.isPresent()) {
            player.inventory.add(inputSlotStack);
            inputSlotStack.setCount(0);
        }
        // if fuel slot has items without burn time, give them to player
        if (fuelBurnTime == 0) {
            player.inventory.add(fuelSlotStack);
            fuelSlotStack.setCount(0);
        }

        // if output slot has items, give them to player
        if (outputSlotHasItemStack) {
            player.inventory.add(outputSlot);
        }

        // award experience
        abstractTileEntity.awardResetAndExperience(player);

        // function that performs the transfer
        doTransfers(stackInHand, burnTime, fuelSlotStack, stackSize, stackSize, abstractTileEntity, player, fuelStackSize, fuelMaxStackSize, recipe, inputSlotStack, inputSlotStackSize, inputMaxStackSize, fuelSlotHasItemStack);

        return PrivateInteractionResult.CONSUME;
    }

    public static PrivateInteractionResult doRightClickInteractions(TileEntity blockEntity, Optional<?> recipe, PlayerEntity player, Hand hand) {
        AbstractFurnaceTileEntity abstractTileEntity = ((AbstractFurnaceTileEntity) blockEntity);
        ItemStack stackInHand = player.getItemInHand(hand);
        ItemStack inputSlotStack = abstractTileEntity.getItem(0);
        ItemStack fuelSlotStack = abstractTileEntity.getItem(1);
        boolean fuelSlotHasItemStack = !fuelSlotStack.isEmpty();
        Map<Item, Integer> fuelMap = AbstractFurnaceTileEntity.getFuel();
        int burnTime = fuelMap.getOrDefault(stackInHand.getItem(), 0);
        int inputMaxStackSize = inputSlotStack.getMaxStackSize();
        int inputSlotStackSize = inputSlotStack.getCount();
        int fuelMaxStackSize = fuelSlotStack.getMaxStackSize();
        int fuelStackSize = fuelSlotStack.getCount();
        int stackSize = stackInHand.getCount();
        int half = stackSize / 2;
        if(stackSize == 1) {
            half = 1;
        }

        // function that performs the transfer
        doTransfers(stackInHand, burnTime, fuelSlotStack, stackSize, half, abstractTileEntity, player, fuelStackSize, fuelMaxStackSize, recipe, inputSlotStack, inputSlotStackSize, inputMaxStackSize, fuelSlotHasItemStack);

        return PrivateInteractionResult.CONSUME;
    }

    private static void doTransfers(ItemStack stackInHand, int burnTime, ItemStack fuelSlotStack, int stackSize, int half, AbstractFurnaceTileEntity abstractTileEntity, PlayerEntity player, int fuelStackSize, int fuelMaxStackSize, Optional<?> recipe, ItemStack inputSlotStack, int inputSlotStackSize, int inputMaxStackSize, boolean fuelSlotHasItemStack) {
        //transfer nbt tags to new item stack
        ItemStack newItemStack = stackInHand.copy();

        // if item in hand is fuel without a smelting result
        if (burnTime != 0) {

            // if fuel slot is empty, fill it with item in hand
            if (fuelSlotStack.isEmpty()) {
                newItemStack.setCount(half);
                abstractTileEntity.setItem(1, newItemStack);
                if (!player.isCreative()) {
                    stackInHand.shrink(half);
                }
            }

            else {
                doIfHasBurntime(fuelSlotStack, stackInHand, fuelStackSize, fuelMaxStackSize, abstractTileEntity, player, recipe, inputSlotStack, inputSlotStackSize, inputMaxStackSize, fuelSlotHasItemStack, inputSlotStack, stackSize, half, newItemStack);
            }
        }

        // if item in hand is item without burntime
        else {
            doIfRecipeIsPresent(recipe, inputSlotStack, newItemStack, abstractTileEntity, player, stackInHand, inputMaxStackSize, inputSlotStackSize, stackSize, half);
        }
    }

    private static void doIfHasBurntime(ItemStack fuelSlotStack, ItemStack stackInHand, int fuelStackSize, int fuelMaxStackSize, AbstractFurnaceTileEntity abstractTileEntity, PlayerEntity player, Optional<?> recipe, ItemStack inputSlot, int inputSlotStackSize, int inputMaxStackSize, boolean fuelSlotHasItemStack, ItemStack inputSlotStack, int stackSize, int half, ItemStack newItemStack) {

        ItemStack tempItemStack = stackInHand.copy();
        ItemStack tempFuelSlotStack= fuelSlotStack.copy();
        ItemStack tempInputSlotStack= inputSlotStack.copy();
        tempItemStack.setCount(1);
        tempFuelSlotStack.setCount(1);
        tempInputSlotStack.setCount(1);

        // if fuel slot and hand item matches
        if (compareItemStackTags(tempFuelSlotStack, tempItemStack) && fuelSlotStack.getItem() == stackInHand.getItem()) {

            // if stack is full cancel
            if (fuelStackSize != fuelMaxStackSize) {

                // if item count of both stacks exceed max stack size, merge the stacks, and
                // keep the rest in player inventory
                if ((fuelStackSize + half) > fuelMaxStackSize) {
                    newItemStack.setCount(fuelMaxStackSize);
                    abstractTileEntity.setItem(1, newItemStack);
                    if (!player.isCreative()) {
                        stackInHand.setCount((fuelStackSize + stackSize) - fuelMaxStackSize);
                    }
                }

                // if item in fuel slot is the same, merge the stacks
                else {
                    newItemStack.setCount(fuelStackSize + half);
                    abstractTileEntity.setItem(1, newItemStack);
                    if (!player.isCreative()) {
                        stackInHand.shrink(half);
                    }
                }
            }

            // Insert smeltable fuel in input slot if fuel slot is already full of it
            else if (recipe.isPresent()) {
                ItemStack resultItem = ((AbstractCookingRecipe) recipe.get()).getResultItem();
                if (!resultItem.isEmpty()) {

                    // if input slot is empty and fuel slot is full, put smeltable fuel in input slot
                    if (inputSlot.isEmpty()) {
                        newItemStack.setCount(half);
                        abstractTileEntity.setItem(0, newItemStack);
                        if (!player.isCreative()) {
                            stackInHand.shrink(half);
                        }
                    }

                    // if input slot and hand item matches
                    else if (compareItemStackTags(tempInputSlotStack, tempItemStack) && inputSlotStack.getItem() == stackInHand.getItem()) {

                        // if stack in input slot is full cancel
                        if (inputSlotStackSize != inputMaxStackSize) {

                            // if item count of both stacks exceed max stack size, merge the stacks, and
                            // keep the rest in player inventory
                            if ((inputSlotStackSize + half) > inputMaxStackSize) {
                                newItemStack.setCount(inputMaxStackSize);
                                abstractTileEntity.setItem(0, newItemStack);
                                if (!player.isCreative()) {
                                    stackInHand.setCount(inputSlotStackSize + stackSize - inputMaxStackSize);
                                }
                            }

                            // if item in slot is the same, merge the stacks
                            else {
                                newItemStack.setCount(inputSlotStackSize + half);
                                abstractTileEntity.setItem(0, newItemStack);
                                if (!player.isCreative()) {
                                    stackInHand.shrink(half);
                                }
                            }
                        }
                    }
                }
            }
        }

        // if fuel slot has fuel, input slot is empty and item is smeltable put item
        // in input slot
        else if (recipe.isPresent()) {
            if (fuelSlotHasItemStack && inputSlot.isEmpty()) {
                ItemStack resultItem = ((AbstractCookingRecipe) recipe.get()).getResultItem();
                if (!resultItem.isEmpty()) {
                    newItemStack.setCount(half);
                    abstractTileEntity.setItem(0, newItemStack);
                    if (!player.isCreative()) {
                        stackInHand.shrink(half);
                    }
                }
            }

            // if input slot and item in hand matches
            else {
                doIfMatches(player, abstractTileEntity, stackInHand, newItemStack, inputSlotStack, inputMaxStackSize, inputSlotStackSize, stackSize, half);
            }
        }
    }

    private static void doIfRecipeIsPresent(Optional<?> recipe, ItemStack inputSlotStack, ItemStack newItemStack, AbstractFurnaceTileEntity abstractTileEntity, PlayerEntity player, ItemStack stackInHand, int inputMaxStackSize, int inputSlotStackSize, int stackSize, int half) {
        // if item in hand has blasting/smelting/smoking result and has no burn time
        if (recipe.isPresent()) {

            // if input slot empty, fill with item in hand
            if (inputSlotStack.isEmpty()) {
                newItemStack.setCount(half);
                abstractTileEntity.setItem(0, newItemStack);
                if (!player.isCreative()) {
                    stackInHand.shrink(half);
                }
            }

            // if input slot and item in hand matches
            else {
                doIfMatches(player, abstractTileEntity, stackInHand, newItemStack, inputSlotStack, inputMaxStackSize, inputSlotStackSize, stackSize, half);
            }
        }
    }

    private static void doIfMatches(PlayerEntity player, AbstractFurnaceTileEntity abstractTileEntity, ItemStack stackInHand, ItemStack newItemStack, ItemStack inputSlotStack, int inputMaxStackSize, int inputSlotStackSize, int stackSize, int half) {

        ItemStack tempItemStack = stackInHand.copy();
        ItemStack tempInputSlotStack = inputSlotStack.copy();
        tempInputSlotStack.setCount(1);
        tempItemStack.setCount(1);

        if (compareItemStackTags(tempInputSlotStack, tempItemStack) && inputSlotStack.getItem() == stackInHand.getItem()) {

            // if input stack is full cancel
            if (inputSlotStackSize != inputMaxStackSize) {

                // if item count of both stacks exceed max stack size, merge the stacks, and
                // keep the rest in player inventory
                if (inputSlotStackSize + half > inputMaxStackSize) {
                    newItemStack.setCount(inputMaxStackSize);
                    abstractTileEntity.setItem(0, newItemStack);
                    if (!player.isCreative()) {
                        stackInHand.setCount(inputSlotStackSize + stackSize - inputMaxStackSize);
                    }
                }

                // if item in input slot and inventory are the same, merge the stacks
                else {
                    newItemStack.setCount(inputSlotStackSize + half);
                    abstractTileEntity.setItem(0, newItemStack);
                    if (!player.isCreative()) {
                        stackInHand.shrink(half);
                    }
                }
            }
        }
    }

    private static boolean compareItemStackTags(ItemStack stack1, ItemStack stack2) {
        CompoundNBT tag1 = stack1.getTag();
        CompoundNBT tag2 = stack2.getTag();

        // Check if both tags are null or equal
        if (tag1 == tag2) {
            return true;
        }

        // If only one of the tags is null, they are not equal
        if (tag1 == null || tag2 == null) {
            return false;
        }

        // Compare all tags recursively
        return compareTags(tag1, tag2);
    }

    private static boolean compareTags(CompoundNBT tag1, CompoundNBT tag2) {
        // Compare all keys in tag1
        for (String key : tag1.getAllKeys()) {
            if (!tag2.contains(key)) {
                return false;
            }

            if (!tag1.get(key).equals(tag2.get(key))) {
                return false;
            }
        }

        // Compare all keys in tag2
        for (String key : tag2.getAllKeys()) {
            if (!tag1.contains(key)) {
                return false;
            }

            if (!tag1.get(key).equals(tag2.get(key))) {
                return false;
            }
        }

        return true;
    }

    public static enum PrivateInteractionResult {
        CONSUME,
        PASS,
    }

}
