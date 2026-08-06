package me.scarletleaf1000.awakened.network;

import me.scarletleaf1000.awakened.Awakened;
import me.scarletleaf1000.awakened.breath.BreathProvider;
import me.scarletleaf1000.awakened.heightening.Heightening;
import me.scarletleaf1000.awakened.item.AwakenedItemData;
import me.scarletleaf1000.awakened.item.ItemBreathStorage;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class BreathTransferC2SPacket {
    private final UUID targetPlayerId;
    private final boolean targetHeldItem;

    private BreathTransferC2SPacket(UUID targetPlayerId, boolean targetHeldItem) {
        this.targetPlayerId = targetPlayerId;
        this.targetHeldItem = targetHeldItem;
    }

    public static BreathTransferC2SPacket forPlayer(UUID targetPlayerId) {
        return new BreathTransferC2SPacket(targetPlayerId, false);
    }

    public static BreathTransferC2SPacket forHeldItem() {
        return new BreathTransferC2SPacket(null, true);
    }

    public BreathTransferC2SPacket(FriendlyByteBuf buf) {
        this.targetHeldItem = buf.readBoolean();
        this.targetPlayerId = targetHeldItem ? null : buf.readUUID();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(targetHeldItem);
        if (!targetHeldItem) {
            buf.writeUUID(targetPlayerId);
        }
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer source = ctx.get().getSender();
            if (source == null) {
                return;
            }
            if (targetHeldItem) {
                transferToHeldItem(source);
            } else {
                transferToPlayer(source);
            }
        });
        ctx.get().setPacketHandled(true);
    }

    private void transferToHeldItem(ServerPlayer source) {
        ItemStack held = source.getMainHandItem();
        if (held.isEmpty() || held.getCount() != 1 || held.is(Awakened.UNAWAKENABLE_TAG) || AwakenedItemData.isAwakened(held) || ItemBreathStorage.hasStoredBreath(held)) {
            return;
        }
        source.getCapability(BreathProvider.BREATH).ifPresent(breath -> {
            int amount = breath.getBreath();
            int heightening = Heightening.fromBreath(amount).ordinal();
            UUID owner = ItemBreathStorage.isIdentityBlanked(source) ? null : source.getUUID();
            ItemBreathStorage.setStoredBreath(held, amount, owner);
            breath.setBreath(0);
            announceTransfer(source, heightening);
        });
    }

    private void transferToPlayer(ServerPlayer source) {
        if (targetPlayerId == null) {
            return;
        }
        ServerPlayer target = source.server.getPlayerList().getPlayer(targetPlayerId);
        if (target == null || target == source || !target.isAlive() || target.level() != source.level() || target.distanceToSqr(source) > 100.0D) {
            return;
        }
        source.getCapability(BreathProvider.BREATH).ifPresent(sourceBreath -> target.getCapability(BreathProvider.BREATH).ifPresent(targetBreath -> {
            int amount = sourceBreath.getBreath();
            int heightening = Heightening.fromBreath(amount).ordinal();
            targetBreath.addBreath(amount);
            sourceBreath.setBreath(0);
            announceTransfer(source, heightening);
        }));
    }

    private void announceTransfer(ServerPlayer source, int heightening) {
        Component commandName = Component.translatable("chat.awakened.my_breath_to_yours");
        Component announcement = Component.translatable("chat.awakened.announcement", source.getName(), commandName);
        if (heightening == Heightening.TENTH.ordinal()) {
            source.sendSystemMessage(announcement.copy().withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        } else {
            source.server.getPlayerList().broadcastSystemMessage(announcement, false);
        }
    }
}
