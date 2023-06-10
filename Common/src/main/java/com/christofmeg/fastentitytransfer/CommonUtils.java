package com.christofmeg.fastentitytransfer;

import net.minecraft.nbt.CompoundTag;
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

    /**
     * Performs left-click interactions on a block entity in the game.
     * This method handles various checks and transfers related to the left-click interaction.
     *
     * @param blockEntity            The block entity to interact with.
     * @param recipe                 An optional recipe associated with the interaction.
     * @param inputSlotProcessingResult  An optional processing result of the input slot.
     * @param player                 The player performing the interaction.
     * @param hand                   The hand used for the interaction.
     * @return                       The result of the interaction.
     */
    public static InteractionResult doLeftClickInteractions(
            @NotNull BlockEntity blockEntity,
            @NotNull Optional<?> recipe,
            @NotNull Optional<?> inputSlotProcessingResult,
            @NotNull Player player,
            @NotNull InteractionHand hand) {

        AbstractFurnaceBlockEntity abstractBlockEntity = ((AbstractFurnaceBlockEntity) blockEntity);
        ItemStack stackInHand = player.getItemInHand(hand);
        ItemStack inputSlotStack = abstractBlockEntity.getItem(0);
        ItemStack fuelSlotStack = abstractBlockEntity.getItem(1);
        ItemStack outputSlot = abstractBlockEntity.getItem(2);

        boolean inputSlotHasItemStack = !inputSlotStack.isEmpty();
        boolean outputSlotHasItemStack = !outputSlot.isEmpty();
        boolean fuelSlotHasItemStack = !fuelSlotStack.isEmpty();

        Map<Item, Integer> fuelMap = AbstractFurnaceBlockEntity.getFuel();
        int burnTime = fuelMap.getOrDefault(stackInHand.getItem(), 0);
        int fuelBurnTime = fuelMap.getOrDefault(fuelSlotStack.getItem(), 0);

        int inputMaxStackSize = inputSlotStack.getMaxStackSize();
        int inputSlotStackSize = inputSlotStack.getCount();
        int fuelMaxStackSize = fuelSlotStack.getMaxStackSize();
        int fuelStackSize = fuelSlotStack.getCount();
        int stackSize = stackInHand.getCount();

        // If the input slot has items not suitable for blasting/smelting/smoking, give them to the player
        if (inputSlotHasItemStack && inputSlotProcessingResult.isEmpty()) {
            player.getInventory().add(inputSlotStack);
            inputSlotStack.setCount(0);
        }

        // If the fuel slot has items without burn time, give them to the player
        if (fuelBurnTime == 0) {
            player.getInventory().add(fuelSlotStack);
            fuelSlotStack.setCount(0);
        }

        // If the output slot has items, give them to the player
        if (outputSlotHasItemStack) {
            player.getInventory().add(outputSlot);
        }

        // Award experience
        abstractBlockEntity.awardUsedRecipesAndPopExperience((ServerPlayer) player);

        // Function that performs the transfer
        doTransfers(stackInHand, burnTime, fuelSlotStack, stackSize, stackSize, abstractBlockEntity, player, fuelStackSize, fuelMaxStackSize, recipe, inputSlotStack, inputSlotStackSize, inputMaxStackSize, fuelSlotHasItemStack);

        return InteractionResult.CONSUME;
    }

    /**
     * Performs right-click interactions on a block entity in the game.
     * This method handles various checks and transfers related to the right-click interaction.
     *
     * @param blockEntity  The block entity to interact with.
     * @param recipe       An optional recipe associated with the interaction.
     * @param player       The player performing the interaction.
     * @param hand         The hand used for the interaction.
     * @return             The result of the interaction.
     */
    public static InteractionResult doRightClickInteractions(
            @NotNull BlockEntity blockEntity,
            @NotNull Optional<?> recipe,
            @NotNull Player player,
            @NotNull InteractionHand hand) {

        AbstractFurnaceBlockEntity abstractBlockEntity = ((AbstractFurnaceBlockEntity) blockEntity);
        ItemStack stackInHand = player.getItemInHand(hand);
        ItemStack inputSlotStack = abstractBlockEntity.getItem(0);
        ItemStack fuelSlotStack = abstractBlockEntity.getItem(1);

        boolean fuelSlotHasItemStack = !fuelSlotStack.isEmpty();
        Map<Item, Integer> fuelMap = AbstractFurnaceBlockEntity.getFuel();
        int burnTime = fuelMap.getOrDefault(stackInHand.getItem(), 0);
        int inputMaxStackSize = inputSlotStack.getMaxStackSize();
        int inputSlotStackSize = inputSlotStack.getCount();
        int fuelMaxStackSize = fuelSlotStack.getMaxStackSize();
        int fuelStackSize = fuelSlotStack.getCount();
        int stackSize = stackInHand.getCount();
        int half = stackSize / 2;
        if (stackSize == 1) {
            half = 1;
        }

        // Function that performs the transfer
        doTransfers(stackInHand, burnTime, fuelSlotStack, stackSize, half, abstractBlockEntity, player, fuelStackSize, fuelMaxStackSize, recipe, inputSlotStack, inputSlotStackSize, inputMaxStackSize, fuelSlotHasItemStack);

        return InteractionResult.CONSUME;
    }

    private static void doTransfers(ItemStack stackInHand, int burnTime, ItemStack fuelSlotStack, int stackSize, int half, AbstractFurnaceBlockEntity abstractBlockEntity, Player player, int fuelStackSize, int fuelMaxStackSize, Optional<?> recipe, ItemStack inputSlotStack, int inputSlotStackSize, int inputMaxStackSize, boolean fuelSlotHasItemStack) {
        //transfer nbt tags to new item stack
        ItemStack newItemStack = stackInHand.copy();

        // if item in hand is fuel without a smelting result
        if (burnTime != 0) {

            // if fuel slot is empty, fill it with item in hand
            if (fuelSlotStack.isEmpty()) {
                newItemStack.setCount(half);
                abstractBlockEntity.setItem(1, newItemStack);
                if (!player.isCreative()) {
                    stackInHand.shrink(half);
                }
            }

            else {
                doIfHasBurntime(fuelSlotStack, stackInHand, fuelStackSize, fuelMaxStackSize, abstractBlockEntity, player, recipe, inputSlotStack, inputSlotStackSize, inputMaxStackSize, fuelSlotHasItemStack, inputSlotStack, stackSize, half, newItemStack);
            }
        }

        // if item in hand is item without burntime
        else {
            doIfRecipeIsPresent(recipe, inputSlotStack, newItemStack, abstractBlockEntity, player, stackInHand, inputMaxStackSize, inputSlotStackSize, stackSize, half);
        }
    }

    private static void doIfHasBurntime(ItemStack fuelSlotStack, ItemStack stackInHand, int fuelStackSize, int fuelMaxStackSize, AbstractFurnaceBlockEntity abstractBlockEntity, Player player, Optional<?> recipe, ItemStack inputSlot, int inputSlotStackSize, int inputMaxStackSize, boolean fuelSlotHasItemStack, ItemStack inputSlotStack, int stackSize, int half, ItemStack newItemStack) {

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
                    abstractBlockEntity.setItem(1, newItemStack);
                    if (!player.isCreative()) {
                        stackInHand.setCount((fuelStackSize + stackSize) - fuelMaxStackSize);
                    }
                }

                // if item in fuel slot is the same, merge the stacks
                else {
                    newItemStack.setCount(fuelStackSize + half);
                    abstractBlockEntity.setItem(1, newItemStack);
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
                        abstractBlockEntity.setItem(0, newItemStack);
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
                                abstractBlockEntity.setItem(0, newItemStack);
                                if (!player.isCreative()) {
                                    stackInHand.setCount(inputSlotStackSize + stackSize - inputMaxStackSize);
                                }
                            }

                            // if item in slot is the same, merge the stacks
                            else {
                                newItemStack.setCount(inputSlotStackSize + half);
                                abstractBlockEntity.setItem(0, newItemStack);
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
                    abstractBlockEntity.setItem(0, newItemStack);
                    if (!player.isCreative()) {
                        stackInHand.shrink(half);
                    }
                }
            }

            // if input slot and item in hand matches
            else {
                doIfMatches(player, abstractBlockEntity, stackInHand, newItemStack, inputSlotStack, inputMaxStackSize, inputSlotStackSize, stackSize, half);
            }
        }
    }

    private static void doIfRecipeIsPresent(Optional<?> recipe, ItemStack inputSlotStack, ItemStack newItemStack, AbstractFurnaceBlockEntity abstractBlockEntity, Player player, ItemStack stackInHand, int inputMaxStackSize, int inputSlotStackSize, int stackSize, int half) {
        // if item in hand has blasting/smelting/smoking result and has no burn time
        if (recipe.isPresent()) {

            // if input slot empty, fill with item in hand
            if (inputSlotStack.isEmpty()) {
                newItemStack.setCount(half);
                abstractBlockEntity.setItem(0, newItemStack);
                if (!player.isCreative()) {
                    stackInHand.shrink(half);
                }
            }

            // if input slot and item in hand matches
            else {
                doIfMatches(player, abstractBlockEntity, stackInHand, newItemStack, inputSlotStack, inputMaxStackSize, inputSlotStackSize, stackSize, half);
            }
        }
    }

    private static void doIfMatches(Player player, AbstractFurnaceBlockEntity abstractBlockEntity, ItemStack stackInHand, ItemStack newItemStack, ItemStack inputSlotStack, int inputMaxStackSize, int inputSlotStackSize, int stackSize, int half) {

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
                    abstractBlockEntity.setItem(0, newItemStack);
                    if (!player.isCreative()) {
                        stackInHand.setCount(inputSlotStackSize + stackSize - inputMaxStackSize);
                    }
                }

                // if item in input slot and inventory are the same, merge the stacks
                else {
                    newItemStack.setCount(inputSlotStackSize + half);
                    abstractBlockEntity.setItem(0, newItemStack);
                    if (!player.isCreative()) {
                        stackInHand.shrink(half);
                    }
                }
            }
        }
    }

    private static boolean compareItemStackTags(ItemStack stack1, ItemStack stack2) {
        CompoundTag tag1 = stack1.getTag();
        CompoundTag tag2 = stack2.getTag();

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

    private static boolean compareTags(CompoundTag tag1, CompoundTag tag2) {
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

}