package me.scarletleaf1000.awakened.network;

import me.scarletleaf1000.awakened.client.ClientPacketHandlers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class VillagerTradeResultS2CPacket {
    private final boolean success;

    public VillagerTradeResultS2CPacket(boolean success) {
        this.success = success;
    }

    public VillagerTradeResultS2CPacket(FriendlyByteBuf buf) {
        this.success = buf.readBoolean();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(success);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> ClientPacketHandlers.handleVillagerTradeResult(success)));
        ctx.get().setPacketHandled(true);
    }
}
