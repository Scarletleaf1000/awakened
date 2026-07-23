package me.scarletleaf1000.awakened.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.UUID;

public final class AwakenedItemData {
    public static final String TAG_ROOT = "AwakenedCommand";
    public static final String TAG_TRIGGER = "Trigger";
    public static final String TAG_ACTION = "Action";
    public static final String TAG_TARGET = "Target";
    public static final String TAG_BREATH = "StoredBreath";
    public static final String TAG_OWNER = "OwnerUUID";
    private static final String TAG_DURABILITY_BONUS = "DurabilityBonus";
    private static final String TAG_WAS_UNBREAKABLE = "WasUnbreakable";

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
        if ("harden".equals(actionId.getPath()) || "resist".equals(actionId.getPath()) || "persist".equals(actionId.getPath())) {
            double multiplier = switch (actionId.getPath()) {
                case "harden" -> 0.20D;
                case "resist" -> 0.40D;
                default -> 0.65D;
            };
            int bonus = Math.max(1, (int) Math.ceil(stack.getMaxDamage() * multiplier));
            root.putInt(TAG_DURABILITY_BONUS, bonus);
            stack.setDamageValue(stack.getDamageValue() - bonus);
        } else if ("become_eternal".equals(actionId.getPath())) {
            root.putBoolean(TAG_WAS_UNBREAKABLE, stack.hasTag() && stack.getTag().getBoolean("Unbreakable"));
            stack.getOrCreateTag().putBoolean("Unbreakable", true);
        }
        stack.getOrCreateTag().put(TAG_ROOT, root);
        ItemBreathStorage.setStoredBreath(stack, storedBreath, owner);
    }

    public static int remove(ItemStack stack) {
        CompoundTag root = read(stack);
        if (root == null) {
            return 0;
        }
        int storedBreath = ItemBreathStorage.getStoredBreath(stack);
        if (storedBreath <= 0) {
            storedBreath = root.getInt(TAG_BREATH);
        }
        ItemBreathStorage.removeStoredBreath(stack);
        String action = root.getString(TAG_ACTION);
        if (root.contains(TAG_DURABILITY_BONUS)) {
            int bonus = root.getInt(TAG_DURABILITY_BONUS);
            int restoredDamage = Math.min(stack.getMaxDamage() - bonus - 1, stack.getDamageValue() + bonus);
            stack.setDamageValue(restoredDamage);
        } else if ("awakened:become_eternal".equals(action) && !root.getBoolean(TAG_WAS_UNBREAKABLE)) {
            stack.getTag().remove("Unbreakable");
        }
        stack.getTag().remove(TAG_ROOT);
        if (stack.getTag().isEmpty()) {
            stack.setTag(null);
        }
        return storedBreath;
    }

    public static int getDurabilityBonus(ItemStack stack) {
        CompoundTag root = read(stack);
        return root == null ? 0 : root.getInt(TAG_DURABILITY_BONUS);
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