package me.scarletleaf1000.awakened.network;

import me.scarletleaf1000.awakened.client.ClientPacketHandlers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class EntityBreathSyncS2CPacket {
    private final int entityId;
    private final int breath;

    public EntityBreathSyncS2CPacket(int entityId, int breath) {
        this.entityId = entityId;
        this.breath = breath;
    }

    public EntityBreathSyncS2CPacket(FriendlyByteBuf buf) {
        this.entityId = buf.readInt();
        this.breath = buf.readInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeInt(breath);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> ClientPacketHandlers.handleEntityBreathSync(entityId, breath)));
        ctx.get().setPacketHandled(true);
    }
}
