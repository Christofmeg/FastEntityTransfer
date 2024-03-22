package com.christofmeg.fastentitytransfer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.crafting.SmokingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlastFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SmokerBlockEntity;
import net.minecraft.world.phys.BlockHitResult;

public class CommonClickInteractions {

    // This method serves as an initialization hook for the mod. The vanilla
    // game has no mechanism to load tooltip listeners so this must be
    // invoked from a mod loader specific project like Forge or Fabric.
    public static void init() {}

    public static InteractionResult onLeftClickBlock(Player player, Level level, InteractionHand hand, BlockPos pos, Direction ignoredDirection, boolean isSprintKeyDown) {
        ItemStack stack = player.getItemInHand(hand);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!level.isClientSide() && isSprintKeyDown) {
            if (blockEntity instanceof SmokerBlockEntity) {
                RecipeType<SmokingRecipe> recipeType = RecipeType.SMOKING;
                return CommonUtils.doLeftClickInteractions(blockEntity, level.getRecipeManager().getRecipeFor(recipeType, new SimpleContainer(stack), level), level.getRecipeManager().getRecipeFor(recipeType, new SimpleContainer(((SmokerBlockEntity) blockEntity).getItem(0)), level), player, hand);
            } else if (blockEntity instanceof BlastFurnaceBlockEntity) {
                RecipeType<BlastingRecipe> recipeType = RecipeType.BLASTING;
                return CommonUtils.doLeftClickInteractions(blockEntity, level.getRecipeManager().getRecipeFor(recipeType, new SimpleContainer(stack), level), level.getRecipeManager().getRecipeFor(recipeType, new SimpleContainer(((BlastFurnaceBlockEntity) blockEntity).getItem(0)), level), player, hand);
            } else if (blockEntity instanceof AbstractFurnaceBlockEntity) {
                RecipeType<SmeltingRecipe> recipeType = RecipeType.SMELTING;
                return CommonUtils.doLeftClickInteractions(blockEntity, level.getRecipeManager().getRecipeFor(recipeType, new SimpleContainer(stack), level), level.getRecipeManager().getRecipeFor(recipeType, new SimpleContainer(((AbstractFurnaceBlockEntity) blockEntity).getItem(0)), level), player, hand);
            }
        }
        return InteractionResult.PASS;
    }

    public static InteractionResult onRightClickBlock(Player player, Level level, InteractionHand hand, BlockHitResult blockHitResult, boolean isSprintKeyDown) {
        ItemStack stack = player.getItemInHand(hand);
        BlockPos pos = blockHitResult.getBlockPos();
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!level.isClientSide() && isSprintKeyDown) {
            if (blockEntity instanceof SmokerBlockEntity) {
                RecipeType<SmokingRecipe> recipeType = RecipeType.SMOKING;
                return CommonUtils.doRightClickInteractions(blockEntity, level.getRecipeManager().getRecipeFor(recipeType, new SimpleContainer(stack), level), player, hand);
            } else if (blockEntity instanceof BlastFurnaceBlockEntity) {
                RecipeType<BlastingRecipe> recipeType = RecipeType.BLASTING;
                return CommonUtils.doRightClickInteractions(blockEntity, level.getRecipeManager().getRecipeFor(recipeType, new SimpleContainer(stack), level), player, hand);
            } else if (blockEntity instanceof AbstractFurnaceBlockEntity) {
                RecipeType<SmeltingRecipe> recipeType = RecipeType.SMELTING;
                return CommonUtils.doRightClickInteractions(blockEntity, level.getRecipeManager().getRecipeFor(recipeType, new SimpleContainer(stack), level), player, hand);
            }
        }
        return InteractionResult.PASS;
    }

}