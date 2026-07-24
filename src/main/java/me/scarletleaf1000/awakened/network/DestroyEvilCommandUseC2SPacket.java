package me.scarletleaf1000.awakened.network;

import me.scarletleaf1000.awakened.Awakened;
import me.scarletleaf1000.awakened.Config;
import me.scarletleaf1000.awakened.breath.BreathProvider;
import me.scarletleaf1000.awakened.command.SpecialCommand;
import me.scarletleaf1000.awakened.data.NightbloodCraftedData;
import me.scarletleaf1000.awakened.heightening.Heightening;
import me.scarletleaf1000.awakened.item.AwakenedItemData;
import me.scarletleaf1000.awakened.item.ItemBreathStorage;
import me.scarletleaf1000.awakened.item.NightbloodSwordItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class DestroyEvilCommandUseC2SPacket {

    public DestroyEvilCommandUseC2SPacket() {
    }

    public DestroyEvilCommandUseC2SPacket(FriendlyByteBuf buf) {
    }

    public void encode(FriendlyByteBuf buf) {
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) {
                return;
            }
            player.getCapability(BreathProvider.BREATH).ifPresent(breath -> {
                int heightening = Heightening.fromBreath(breath.getBreath()).ordinal();
                if (heightening < Heightening.NINTH.ordinal()) {
                    player.sendSystemMessage(Component.translatable("message.awakened.command.heightening_required",
                            SpecialCommand.DESTROY_EVIL.getDisplayName(), Heightening.NINTH.ordinal(), heightening));
                    return;
                }
                int cost = 1000;
                if (breath.getBreath() < cost) {
                    player.sendSystemMessage(Component.translatable("message.awakened.command.insufficient_breath", cost));
                    return;
                }
                ItemStack held = player.getMainHandItem();
                if (held.isEmpty() || held.getCount() != 1 || !held.is(Items.NETHERITE_SWORD)) {
                    player.sendSystemMessage(Component.translatable("message.awakened.command.invalid_item"));
                    return;
                }
                if (AwakenedItemData.isAwakened(held) || ItemBreathStorage.hasStoredBreath(held)) {
                    player.sendSystemMessage(Component.translatable("message.awakened.command.invalid_item"));
                    return;
                }

                NightbloodCraftedData craftedData = NightbloodCraftedData.get(player.server.overworld());
                if (Config.MAX_NIGHTBLOODS.get() != -1) {
                    if (craftedData.getCount() >= Config.MAX_NIGHTBLOODS.get()) {
                        player.sendSystemMessage(Component.translatable("message.awakened.destroy_evil.limit_reached"));
                        return;
                    }
                }

                breath.removeBreath(cost);

                ItemStack nightblood = new ItemStack(Awakened.NIGHTBLOOD.get());
                NightbloodSwordItem.setStoredBreath(nightblood, cost);
                nightblood.getOrCreateTag().putBoolean("Unbreakable", true);

                EnchantmentHelper.setEnchantments(EnchantmentHelper.getEnchantments(held), nightblood);
                if (held.hasCustomHoverName()) {
                    nightblood.setHoverName(held.getHoverName());
                }
                nightblood.setRepairCost(held.getBaseRepairCost());

                player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, nightblood);
                craftedData.increment();

                Component commandName = SpecialCommand.DESTROY_EVIL.getDisplayName();
                Component announcement = Component.translatable("chat.awakened.announcement", player.getName(), commandName);
                if (Heightening.fromBreath(breath.getBreath()) == Heightening.TENTH) {
                    player.sendSystemMessage(announcement.copy().withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
                } else {
                    player.server.getPlayerList().broadcastSystemMessage(announcement, false);
                }
            });
        });
        ctx.get().setPacketHandled(true);
    }
}
