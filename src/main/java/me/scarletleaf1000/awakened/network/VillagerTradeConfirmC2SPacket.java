package me.scarletleaf1000.awakened.network;

import me.scarletleaf1000.awakened.trade.VillagerBreathTradeHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class VillagerTradeConfirmC2SPacket {
    private final int villagerId;

    public VillagerTradeConfirmC2SPacket(int villagerId) {
        this.villagerId = villagerId;
    }

    public VillagerTradeConfirmC2SPacket(FriendlyByteBuf buf) {
        this.villagerId = buf.readInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(villagerId);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                VillagerBreathTradeHandler.handleTradeConfirm(player, villagerId);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
