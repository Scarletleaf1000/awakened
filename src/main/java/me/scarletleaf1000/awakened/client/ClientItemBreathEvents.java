package me.scarletleaf1000.awakened.client;

import me.scarletleaf1000.awakened.Awakened;
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

        if (ItemBreathStorage.isOwnerOrPublic(stack, player)) {
            MutableComponent storedBreaths = Component.literal(String.valueOf(ItemBreathStorage.getStoredBreath(stack)))
                    .withStyle(ChatFormatting.BLUE);
            event.getToolTip().add(Component.translatable("tooltip.awakened.stored_breaths", storedBreaths)
                    .withStyle(ChatFormatting.GRAY));
        } else {
            event.getToolTip().add(Component.translatable("tooltip.awakened.contains_breath")
                    .withStyle(ChatFormatting.GRAY));
        }
    }
}
