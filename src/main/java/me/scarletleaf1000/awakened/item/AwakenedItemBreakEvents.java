package me.scarletleaf1000.awakened.item;

import me.scarletleaf1000.awakened.Awakened;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import javax.annotation.Nullable;
import java.util.UUID;

public final class AwakenedItemBreakEvents {

    private AwakenedItemBreakEvents() {
    }

    public static void handleBreak(ItemStack stack, LivingEntity entity) {
        if (entity == null || stack == null || stack.isEmpty() || stack.is(Items.ELYTRA)) {
            return;
        }
        int breath = getStoredBreath(stack);
        if (breath <= 0) {
            return;
        }
        ItemStack scrap = createScrap(breath, getOwner(stack));
        if (entity instanceof Player player) {
            if (!player.addItem(scrap)) {
                player.drop(scrap, false);
            }
        } else {
            entity.spawnAtLocation(scrap);
        }
    }

    private static int getStoredBreath(ItemStack stack) {
        if (AwakenedItemData.isAwakened(stack)) {
            return AwakenedItemData.getStoredBreath(stack);
        }
        return ItemBreathStorage.getStoredBreath(stack);
    }

    @Nullable
    private static UUID getOwner(ItemStack stack) {
        if (AwakenedItemData.isAwakened(stack)) {
            return AwakenedItemData.getOwner(stack);
        }
        return ItemBreathStorage.getOwner(stack);
    }

    private static ItemStack createScrap(int breath, @Nullable UUID owner) {
        ItemStack scrap = new ItemStack(Awakened.AWAKENED_SCRAP.get());
        ItemBreathStorage.setStoredBreath(scrap, breath, owner);
        return scrap;
    }
}
