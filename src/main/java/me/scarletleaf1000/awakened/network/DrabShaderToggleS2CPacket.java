package me.scarletleaf1000.awakened.network;

import me.scarletleaf1000.awakened.client.ClientPacketHandlers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class DrabShaderToggleS2CPacket {
    private final boolean disabled;

    public DrabShaderToggleS2CPacket(boolean disabled) {
        this.disabled = disabled;
    }

    public DrabShaderToggleS2CPacket(FriendlyByteBuf buf) {
        this.disabled = buf.readBoolean();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(disabled);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> ClientPacketHandlers.handleDrabShaderToggle(disabled)));
        ctx.get().setPacketHandled(true);
    }
}
