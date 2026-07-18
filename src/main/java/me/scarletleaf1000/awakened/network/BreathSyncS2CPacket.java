package me.scarletleaf1000.awakened.network;

import me.scarletleaf1000.awakened.client.ClientPacketHandlers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class BreathSyncS2CPacket {
    private final int breath;

    public BreathSyncS2CPacket(int breath) {
        this.breath = breath;
    }

    public BreathSyncS2CPacket(FriendlyByteBuf buf) {
        this.breath = buf.readInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(breath);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> ClientPacketHandlers.handleBreathSync(breath)));
        ctx.get().setPacketHandled(true);
    }
}
