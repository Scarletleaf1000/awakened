package me.scarletleaf1000.awakened.network;

import me.scarletleaf1000.awakened.client.ClientPacketHandlers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class NightbloodSyncS2CPacket {
    private final int crafted;
    private final int max;

    public NightbloodSyncS2CPacket(int crafted, int max) {
        this.crafted = crafted;
        this.max = max;
    }

    public NightbloodSyncS2CPacket(FriendlyByteBuf buf) {
        this.crafted = buf.readVarInt();
        this.max = buf.readVarInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(crafted);
        buf.writeVarInt(max);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> ClientPacketHandlers.handleNightbloodSync(crafted, max)));
        ctx.get().setPacketHandled(true);
    }
}
