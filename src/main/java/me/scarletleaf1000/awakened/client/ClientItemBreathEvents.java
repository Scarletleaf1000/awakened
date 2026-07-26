package me.scarletleaf1000.awakened.client;

import java.util.UUID;

import me.scarletleaf1000.awakened.Awakened;
import me.scarletleaf1000.awakened.breath.BreathProvider;
import me.scarletleaf1000.awakened.heightening.Heightening;
import me.scarletleaf1000.awakened.item.ItemBreathStorage;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Awakened.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientItemBreathEvents {

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (!ItemBreathStorage.hasStoredBreath(stack)) {
            return;
        }

        Player player = event.getEntity();
        if (player == null) {
            return;
        }

        int ownBreath = player.getCapability(BreathProvider.BREATH)
                .map(b -> b.getBreath())
                .orElse(0);
        boolean hasSeventhHeightening = Heightening.fromBreath(ownBreath).ordinal() >= Heightening.SEVENTH.ordinal();
        boolean canSeeCount = ItemBreathStorage.isOwnerOrPublic(stack, player) || hasSeventhHeightening;

        if (canSeeCount) {
            MutableComponent storedBreaths = Component.literal(String.valueOf(ItemBreathStorage.getStoredBreath(stack)))
                    .withStyle(ChatFormatting.BLUE);
            event.getToolTip().add(Component.translatable("tooltip.awakened.stored_breaths", storedBreaths)
                    .withStyle(ChatFormatting.GRAY));
        } else {
            event.getToolTip().add(Component.translatable("tooltip.awakened.contains_breath")
                    .withStyle(ChatFormatting.GRAY));
        }

        UUID owner = ItemBreathStorage.getOwner(stack);
        Component identityValue;
        if (owner == null) {
            identityValue = Component.translatable("tooltip.awakened.identity.unkeyed");
        } else if (owner.equals(player.getUUID())) {
            identityValue = player.getName();
        } else {
            identityValue = Component.translatable("tooltip.awakened.identity.unknown")
                    .withStyle(ChatFormatting.OBFUSCATED);
        }
        event.getToolTip().add(Component.translatable("tooltip.awakened.identity", identityValue)
                .withStyle(ChatFormatting.GRAY));
    }
}
