package me.scarletleaf1000.awakened.entity;

import me.scarletleaf1000.awakened.breath.BreathProvider;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * A physical awakened item. Looks like the item it was created from and stores the
 * command used to awaken it, the breath invested, and the original owner.
 *
 * This is a stripped scaffold; behavior is being rebuilt from the ground up.
 */
public class AwakenedItemEntity extends Entity {
    public static final String TAG_ROOT = "AwakenedCommand";
    public static final String TAG_TRIGGER = "Trigger";
    public static final String TAG_ACTION = "Action";
    public static final String TAG_TARGET = "Target";
    public static final String TAG_BREATH = "StoredBreath";
    public static final String TAG_OWNER = "OwnerUUID";

    private static final EntityDataAccessor<ItemStack> DATA_ITEM = SynchedEntityData.defineId(AwakenedItemEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<CompoundTag> DATA_COMMAND = SynchedEntityData.defineId(AwakenedItemEntity.class, EntityDataSerializers.COMPOUND_TAG);

    public AwakenedItemEntity(EntityType<? extends AwakenedItemEntity> type, Level level) {
        super(type, level);
        this.setInvulnerable(true);
    }

    public void setCommandData(ResourceLocation triggerId, ResourceLocation actionId, ResourceLocation targetId, int storedBreath, UUID owner) {
        CompoundTag root = new CompoundTag();
        root.putString(TAG_TRIGGER, triggerId.toString());
        root.putString(TAG_ACTION, actionId.toString());
        root.putString(TAG_TARGET, targetId.toString());
        root.putInt(TAG_BREATH, storedBreath);
        root.putUUID(TAG_OWNER, owner);
        this.entityData.set(DATA_COMMAND, root);
    }

    @Nullable
    public CompoundTag getCommandData() {
        return this.entityData.get(DATA_COMMAND);
    }

    @Nullable
    public ResourceLocation getTriggerId() {
        return getId(getCommandData(), TAG_TRIGGER);
    }

    @Nullable
    public ResourceLocation getActionId() {
        return getId(getCommandData(), TAG_ACTION);
    }

    @Nullable
    public ResourceLocation getTargetId() {
        return getCommandData() == null ? null : getId(getCommandData(), TAG_TARGET);
    }

    public int getStoredBreath() {
        CompoundTag root = getCommandData();
        return root == null ? 0 : root.getInt(TAG_BREATH);
    }

    @Nullable
    public UUID getOwnerUUID() {
        CompoundTag root = getCommandData();
        if (root == null || !root.hasUUID(TAG_OWNER)) {
            return null;
        }
        return root.getUUID(TAG_OWNER);
    }

    public ItemStack getItem() {
        return this.entityData.get(DATA_ITEM);
    }

    public void setItem(ItemStack stack) {
        this.entityData.set(DATA_ITEM, stack.copy());
        this.refreshDimensions();
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return EntityDimensions.scalable(1.0f, 1.0f);
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public void playerTouch(Player player) {
        // Awakened items cannot be collected by walking into them.
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (player.isShiftKeyDown() && player.getUUID().equals(getOwnerUUID())) {
            if (!this.level().isClientSide() && this.level() instanceof ServerLevel serverLevel) {
                int breath = getStoredBreath();
                if (breath > 0) {
                    player.getCapability(BreathProvider.BREATH).ifPresent(b -> b.addBreath(breath));
                }

                serverLevel.sendParticles(ParticleTypes.WITCH, this.getX(), this.getY() + this.getBbHeight() / 2.0, this.getZ(), 12, 0.25, 0.25, 0.25, 0.05);
                serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, this.getX(), this.getY() + this.getBbHeight() / 2.0, this.getZ(), 8, 0.2, 0.2, 0.2, 0.05);
                serverLevel.sendParticles(ParticleTypes.ENTITY_EFFECT, this.getX(), this.getY() + this.getBbHeight() / 2.0, this.getZ(), 6, 0.15, 0.15, 0.15, 0.0);

                this.discard();
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide());
        }

        return InteractionResult.PASS;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_ITEM, ItemStack.EMPTY);
        this.entityData.define(DATA_COMMAND, new CompoundTag());
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        ItemStack item = this.getItem();
        if (!item.isEmpty()) {
            compound.put("Item", item.save(new CompoundTag()));
        }
        CompoundTag root = getCommandData();
        if (root != null && !root.isEmpty()) {
            compound.put(TAG_ROOT, root.copy());
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        if (compound.contains("Item", CompoundTag.TAG_COMPOUND)) {
            this.setItem(ItemStack.of(compound.getCompound("Item")));
        }
        if (compound.contains(TAG_ROOT, CompoundTag.TAG_COMPOUND)) {
            this.entityData.set(DATA_COMMAND, compound.getCompound(TAG_ROOT));
        }
    }

    @Override
    public void tick() {
        if (this.getItem().isEmpty()) {
            this.discard();
            return;
        }
        super.tick();
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
