package me.scarletleaf1000.awakened.item;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class AwakenedScrapItem extends Item {
    public AwakenedScrapItem(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        if (level.isClientSide() || stack.isEmpty() || !stack.is(this)) {
            return;
        }
        if (ItemBreathStorage.hasStoredBreath(stack)) {
            return;
        }

        int count = stack.getCount();
        stack.shrink(count);

        level.playSound(null, entity.getX(), entity.getY() + entity.getEyeHeight(), entity.getZ(),
                SoundEvents.ITEM_BREAK, entity.getSoundSource(), 0.8F,
                0.8F + level.random.nextFloat() * 0.4F);

        if (entity instanceof LivingEntity living) {
            for (int i = 0; i < 5; i++) {
                double offsetX = (level.random.nextDouble() - 0.5) * 0.5;
                double offsetY = level.random.nextDouble() * living.getBbHeight();
                double offsetZ = (level.random.nextDouble() - 0.5) * 0.5;
                level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, stack),
                        entity.getX() + offsetX, entity.getY() + offsetY, entity.getZ() + offsetZ,
                        0.0D, 0.0D, 0.0D);
            }
        }
    }
}
