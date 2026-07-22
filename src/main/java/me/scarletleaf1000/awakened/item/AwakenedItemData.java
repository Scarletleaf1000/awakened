package me.scarletleaf1000.awakened.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Helpers for reading and writing awakening command data on {@link ItemStack}s.
 */
public final class AwakenedItemData {
    public static final String TAG_ROOT = "AwakenedCommand";
    public static final String TAG_TRIGGER = "Trigger";
    public static final String TAG_ACTION = "Action";
    public static final String TAG_TARGET = "Target";
    public static final String TAG_BREATH = "StoredBreath";
    public static final String TAG_OWNER = "OwnerUUID";

    private AwakenedItemData() {
    }

    public static boolean isAwakened(ItemStack stack) {
        return !stack.isEmpty() && stack.hasTag() && stack.getTag().contains(TAG_ROOT, CompoundTag.TAG_COMPOUND);
    }

    public static void write(ItemStack stack, ResourceLocation triggerId, ResourceLocation actionId, ResourceLocation targetId, int storedBreath, UUID owner) {
        CompoundTag root = new CompoundTag();
        root.putString(TAG_TRIGGER, triggerId.toString());
        root.putString(TAG_ACTION, actionId.toString());
        root.putString(TAG_TARGET, targetId.toString());
        root.putInt(TAG_BREATH, storedBreath);
        root.putUUID(TAG_OWNER, owner);
        stack.getOrCreateTag().put(TAG_ROOT, root);
    }

    @Nullable
    public static CompoundTag read(ItemStack stack) {
        if (!isAwakened(stack)) {
            return null;
        }
        return stack.getTag().getCompound(TAG_ROOT);
    }

    @Nullable
    public static ResourceLocation getTriggerId(ItemStack stack) {
        return getId(read(stack), TAG_TRIGGER);
    }

    @Nullable
    public static ResourceLocation getActionId(ItemStack stack) {
        return getId(read(stack), TAG_ACTION);
    }

    @Nullable
    public static ResourceLocation getTargetId(ItemStack stack) {
        return getId(read(stack), TAG_TARGET);
    }

    public static int getStoredBreath(ItemStack stack) {
        CompoundTag root = read(stack);
        return root == null ? 0 : root.getInt(TAG_BREATH);
    }

    @Nullable
    public static UUID getOwner(ItemStack stack) {
        CompoundTag root = read(stack);
        if (root == null || !root.hasUUID(TAG_OWNER)) {
            return null;
        }
        return root.getUUID(TAG_OWNER);
    }

    @Nullable
    private static ResourceLocation getId(@Nullable CompoundTag root, String key) {
        if (root == null) {
            return null;
        }
        try {
            return new ResourceLocation(root.getString(key));
        } catch (Exception e) {
            return null;
        }
    }
}
