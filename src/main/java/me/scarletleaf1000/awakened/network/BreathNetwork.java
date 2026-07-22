package me.scarletleaf1000.awakened.network;

import me.scarletleaf1000.awakened.Awakened;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class BreathNetwork {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Awakened.MOD_ID, "breath"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        int id = 0;
        CHANNEL.registerMessage(id++, BreathSyncS2CPacket.class, BreathSyncS2CPacket::encode,
                BreathSyncS2CPacket::new, BreathSyncS2CPacket::handle);
        CHANNEL.registerMessage(id++, EntityBreathSyncS2CPacket.class, EntityBreathSyncS2CPacket::encode,
                EntityBreathSyncS2CPacket::new, EntityBreathSyncS2CPacket::handle);
        CHANNEL.registerMessage(id++, VillagerTradeConfirmC2SPacket.class, VillagerTradeConfirmC2SPacket::encode,
                VillagerTradeConfirmC2SPacket::new, VillagerTradeConfirmC2SPacket::handle);
        CHANNEL.registerMessage(id++, VillagerTradeResultS2CPacket.class, VillagerTradeResultS2CPacket::encode,
                VillagerTradeResultS2CPacket::new, VillagerTradeResultS2CPacket::handle);
        CHANNEL.registerMessage(id++, AwakeningCommandUseC2SPacket.class, AwakeningCommandUseC2SPacket::encode,
                AwakeningCommandUseC2SPacket::new, AwakeningCommandUseC2SPacket::handle);
    }

    public static void sendToPlayer(ServerPlayer player, int breath) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new BreathSyncS2CPacket(breath));
    }

    public static void sendEntityBreath(ServerPlayer player, int entityId, int breath) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new EntityBreathSyncS2CPacket(entityId, breath));
    }

    public static void sendVillagerTradeResult(ServerPlayer player, boolean success) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new VillagerTradeResultS2CPacket(success));
    }
}
