package me.scarletleaf1000.awakened.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.UUID;

public class ItemBreathStorage {
    private static final String TAG_STORED_BREATH = "awakened:stored_breath";
    private static final String TAG_BREATH_OWNER = "awakened:breath_owner";

    public static int getStoredBreath(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return 0;
        }
        CompoundTag tag = stack.getTag();
        if (tag == null) {
            return 0;
        }
        return tag.getInt(TAG_STORED_BREATH);
    }

    public static void setStoredBreath(ItemStack stack, int amount) {
        if (stack == null || stack.isEmpty()) {
            return;
        }
        if (amount <= 0) {
            removeStoredBreath(stack);
            return;
        }
        stack.getOrCreateTag().putInt(TAG_STORED_BREATH, amount);
    }

    public static void setStoredBreath(ItemStack stack, int amount, @Nullable UUID owner) {
        setStoredBreath(stack, amount);
        if (amount > 0 && owner != null) {
            setOwner(stack, owner);
        } else if (amount <= 0) {
            removeStoredBreath(stack);
        }
    }

    public static boolean hasStoredBreath(ItemStack stack) {
        return getStoredBreath(stack) > 0;
    }

    public static void removeStoredBreath(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return;
        }
        CompoundTag tag = stack.getTag();
        if (tag == null) {
            return;
        }
        tag.remove(TAG_STORED_BREATH);
        tag.remove(TAG_BREATH_OWNER);
        if (tag.isEmpty()) {
            stack.setTag(null);
        }
    }

    public static boolean hasOwner(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        CompoundTag tag = stack.getTag();
        return tag != null && tag.contains(TAG_BREATH_OWNER, Tag.TAG_STRING);
    }

    @Nullable
    public static UUID getOwner(ItemStack stack) {
        if (!hasOwner(stack)) {
            return null;
        }
        try {
            return UUID.fromString(stack.getTag().getString(TAG_BREATH_OWNER));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static void setOwner(ItemStack stack, @Nullable UUID owner) {
        if (stack == null || stack.isEmpty()) {
            return;
        }
        if (owner == null) {
            CompoundTag tag = stack.getTag();
            if (tag != null) {
                tag.remove(TAG_BREATH_OWNER);
                if (tag.isEmpty()) {
                    stack.setTag(null);
                }
            }
            return;
        }
        stack.getOrCreateTag().putString(TAG_BREATH_OWNER, owner.toString());
    }

    public static boolean isOwnerOrPublic(ItemStack stack, Player player) {
        if (stack == null || stack.isEmpty() || player == null) {
            return false;
        }
        UUID owner = getOwner(stack);
        return owner == null || owner.equals(player.getUUID());
    }
}
