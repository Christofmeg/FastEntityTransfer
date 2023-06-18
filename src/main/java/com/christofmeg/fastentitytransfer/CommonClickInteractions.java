package com.christofmeg.fastentitytransfer;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.BlastingRecipe;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.SmokingRecipe;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.tileentity.BlastFurnaceTileEntity;
import net.minecraft.tileentity.SmokerTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CommonClickInteractions {

    // This method serves as an initialization hook for the mod. The vanilla
    // game has no mechanism to load tooltip listeners so this must be
    // invoked from a mod loader specific project like Forge or Fabric.
    public static void init() {}

    public static CommonUtils.PrivateInteractionResult onLeftClickBlock(PlayerEntity player, World level, Hand hand, BlockPos pos, Direction ignoredDirection, boolean isSprintKeyDown) {
        ItemStack stack = player.getItemInHand(hand);
        TileEntity blockEntity = level.getBlockEntity(pos);
        if (!level.isClientSide() && isSprintKeyDown) {
            if (blockEntity instanceof SmokerTileEntity) {
                IRecipeType<SmokingRecipe> recipeType = IRecipeType.SMOKING;
                return CommonUtils.doLeftClickInteractions(blockEntity, level.getRecipeManager().getRecipeFor(recipeType, new Inventory(stack), level), level.getRecipeManager().getRecipeFor(recipeType, new Inventory(((AbstractFurnaceTileEntity) blockEntity).getItem(0)), level), player, hand);
            } else if (blockEntity instanceof BlastFurnaceTileEntity) {
                IRecipeType<BlastingRecipe> recipeType = IRecipeType.BLASTING;
                return CommonUtils.doLeftClickInteractions(blockEntity, level.getRecipeManager().getRecipeFor(recipeType, new Inventory(stack), level), level.getRecipeManager().getRecipeFor(recipeType, new Inventory(((AbstractFurnaceTileEntity) blockEntity).getItem(0)), level), player, hand);
            } else if (blockEntity instanceof AbstractFurnaceTileEntity) {
                IRecipeType<FurnaceRecipe> recipeType = IRecipeType.SMELTING;
                return CommonUtils.doLeftClickInteractions(blockEntity, level.getRecipeManager().getRecipeFor(recipeType, new Inventory(stack), level), level.getRecipeManager().getRecipeFor(recipeType, new Inventory(((AbstractFurnaceTileEntity) blockEntity).getItem(0)), level), player, hand);
            }
        }
        return CommonUtils.PrivateInteractionResult.PASS;
    }

    public static CommonUtils.PrivateInteractionResult onRightClickBlock(PlayerEntity player, World level, Hand hand, BlockPos pos, boolean isSprintKeyDown) {
        ItemStack stack = player.getItemInHand(hand);
        TileEntity blockEntity = level.getBlockEntity(pos);
        if (!level.isClientSide() && isSprintKeyDown) {
            if (blockEntity instanceof SmokerTileEntity) {
                IRecipeType<SmokingRecipe> recipeType = IRecipeType.SMOKING;
                return CommonUtils.doRightClickInteractions(blockEntity, level.getRecipeManager().getRecipeFor(recipeType, new Inventory(stack), level), player, hand);
            } else if (blockEntity instanceof BlastFurnaceTileEntity) {
                IRecipeType<BlastingRecipe> recipeType = IRecipeType.BLASTING;
                return CommonUtils.doRightClickInteractions(blockEntity, level.getRecipeManager().getRecipeFor(recipeType, new Inventory(stack), level), player, hand);
            } else if (blockEntity instanceof AbstractFurnaceTileEntity) {
                IRecipeType<FurnaceRecipe> recipeType = IRecipeType.SMELTING;
                return CommonUtils.doRightClickInteractions(blockEntity, level.getRecipeManager().getRecipeFor(recipeType, new Inventory(stack), level), player, hand);
            }
        }
        return CommonUtils.PrivateInteractionResult.PASS;
    }

}