package com.christofmeg.fastentitytransfer.network;

import com.christofmeg.fastentitytransfer.CommonConstants;
import com.christofmeg.fastentitytransfer.FastEntityTransfer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SprintKeyPacket(Boolean isSprintKeyDown) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(CommonConstants.MOD_ID, "sprinket_ket_packet");
    private final boolean isSprintKeyDown;

    public static PacketHitToServer create(FriendlyByteBuf buf) {
        return new PacketHitToServer ();
    }

    @Override
    public void write(@NotNull FriendlyByteBuf buffer) {
        buffer.writeBoolean(isSprintKeyDown);
    }

    public static SprintKeyPacket decode(FriendlyByteBuf buffer) {
        boolean isSprintKeyDown = buffer.readBoolean();
        return new SprintKeyPacket(isSprintKeyDown);
    }

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }

    public void handle(SprintKeyPacket packet, PlayPayloadContext context) {
        context.workHandler().submitAsync(() -> {
            FastEntityTransfer.isCtrlKeyDown = packet.isSprintKeyDown;
        });
    }
}