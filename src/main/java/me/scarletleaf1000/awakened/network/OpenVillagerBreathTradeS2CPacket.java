package me.scarletleaf1000.awakened.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenVillagerBreathTradeS2CPacket {
    private final int villagerId;
    private final int breath;
    private final int cost;

    public OpenVillagerBreathTradeS2CPacket(int villagerId, int breath, int cost) {
        this.villagerId = villagerId;
        this.breath = breath;
        this.cost = cost;
    }

    public OpenVillagerBreathTradeS2CPacket(FriendlyByteBuf buf) {
        this.villagerId = buf.readInt();
        this.breath = buf.readInt();
        this.cost = buf.readInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(villagerId);
        buf.writeInt(breath);
        buf.writeInt(cost);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().setPacketHandled(true);
    }
}
