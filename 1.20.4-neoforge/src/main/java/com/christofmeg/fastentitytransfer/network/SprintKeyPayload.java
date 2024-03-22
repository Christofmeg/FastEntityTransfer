package com.christofmeg.fastentitytransfer.network;

import com.christofmeg.fastentitytransfer.CommonClickInteractions;
import com.christofmeg.fastentitytransfer.CommonConstants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record SprintKeyPayload(boolean isSprintKeyDown) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(CommonConstants.MOD_ID, "ctrl_key_is_down");

    public SprintKeyPayload(final FriendlyByteBuf buffer) {
        this(buffer.readBoolean());
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeBoolean(isSprintKeyDown);
    }

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }
}