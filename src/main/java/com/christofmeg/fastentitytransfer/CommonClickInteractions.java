package com.christofmeg.fastentitytransfer;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.RecipeType;
import net.minecraftforge.common.crafting.VanillaRecipeTypes;

public class CommonClickInteractions {

    // This method serves as an initialization hook for the mod. The vanilla
    // game has no mechanism to load tooltip listeners so this must be
    // invoked from a mod loader specific project like Forge or Fabric.

    public static void init() {}

    public static CommonUtils.PrivateInteractionResult onLeftClickBlock(EntityPlayer player, World level, EnumHand hand, BlockPos pos) {
        ItemStack stack = player.getHeldItem(hand);
        TileEntity blockEntity = level.getTileEntity(pos);
        boolean isSprintKeyDown = Minecraft.getInstance().gameSettings.keyBindSprint.isKeyDown();
        if (!level.isRemote() && isSprintKeyDown) {
            if (blockEntity instanceof TileEntityFurnace) {
                RecipeType<FurnaceRecipe> recipeType = VanillaRecipeTypes.SMELTING;
                return CommonUtils.doLeftClickInteractions(blockEntity, level.getRecipeManager().getRecipe(recipeType, new Inventory(stack), level), level.getRecipeManager().getRecipe(recipeType, new Inventory(((TileEntityFurnace) blockEntity).getStackInSlot(0)), level), player, hand);
            }
        }
        return CommonUtils.PrivateInteractionResult.PASS;
    }

    public static CommonUtils.PrivateInteractionResult onRightClickBlock(EntityPlayer player, World level, EnumHand hand, BlockPos pos) {
        ItemStack stack = player.getHeldItem(hand);
        TileEntity blockEntity = level.getTileEntity(pos);
        boolean isSprintKeyDown = Minecraft.getInstance().gameSettings.keyBindSprint.isKeyDown();
        if (!level.isRemote() && isSprintKeyDown) {
            if (blockEntity instanceof TileEntityFurnace) {
                RecipeType<FurnaceRecipe> recipeType = VanillaRecipeTypes.SMELTING;
                return CommonUtils.doRightClickInteractions(blockEntity, level.getRecipeManager().getRecipe(recipeType, new Inventory(stack), level), player, hand);
            }
        }
        return CommonUtils.PrivateInteractionResult.PASS;
    }

}