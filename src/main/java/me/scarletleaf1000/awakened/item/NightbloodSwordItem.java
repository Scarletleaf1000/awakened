package me.scarletleaf1000.awakened.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import me.scarletleaf1000.awakened.breath.BreathProvider;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class NightbloodSwordItem extends SwordItem {
    private static final String TAG_STORED_BREATH = "awakened:nightblood_stored_breath";
    private static final int DRAIN_INTERVAL = 30; // 1.5 seconds
    private static final int WITHER_DURATION = 40; // 2 seconds
    private static final int WITHER_AMPLIFIER = 2; // Wither III
    private static final double DAMAGE_MULTIPLIER = 0.28D;
    private static final double DAMAGE_EXPONENT = 0.539D;

    public NightbloodSwordItem(Properties properties) {
        super(Tiers.NETHERITE, 0, -2.4F, properties);
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return false;
    }

    public static int getStoredBreath(ItemStack stack) {
        return stack.hasTag() ? stack.getTag().getInt(TAG_STORED_BREATH) : 0;
    }

    public static void setStoredBreath(ItemStack stack, int amount) {
        if (amount <= 0) {
            if (stack.hasTag()) {
                stack.getTag().remove(TAG_STORED_BREATH);
                if (stack.getTag().isEmpty()) {
                    stack.setTag(null);
                }
            }
            return;
        }
        stack.getOrCreateTag().putInt(TAG_STORED_BREATH, amount);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> modifiers = HashMultimap.create(super.getAttributeModifiers(slot, stack));
        if (slot == EquipmentSlot.MAINHAND) {
            int breath = getStoredBreath(stack);
            double bonusDamage = DAMAGE_MULTIPLIER * Math.pow(breath, DAMAGE_EXPONENT);
            modifiers.removeAll(Attributes.ATTACK_DAMAGE);
            modifiers.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_UUID, "Nightblood damage modifier", bonusDamage, AttributeModifier.Operation.ADDITION));
        }
        return modifiers;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, level, entity, slot, selected);
        if (!selected || !(entity instanceof Player player)) {
            return;
        }

        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            spawnSmokeParticles(serverLevel, player);
            if (level.getGameTime() % DRAIN_INTERVAL == 0) {
                player.getCapability(BreathProvider.BREATH).ifPresent(breath -> {
                    if (breath.getBreath() > 0) {
                        breath.removeBreath(1);
                        setStoredBreath(stack, getStoredBreath(stack) + 1);
                    } else {
                        player.addEffect(new MobEffectInstance(MobEffects.WITHER, WITHER_DURATION, WITHER_AMPLIFIER));
                    }
                });
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.awakened.nightblood.stored_breath", getStoredBreath(stack)));
    }

    private void spawnSmokeParticles(ServerLevel level, Player player) {
        double width = player.getBbWidth();
        double height = player.getBbHeight();
        double x = player.getX() + (level.random.nextDouble() - 0.5) * width;
        double y = player.getY() + level.random.nextDouble() * height;
        double z = player.getZ() + (level.random.nextDouble() - 0.5) * width;
        level.sendParticles(ParticleTypes.SMOKE, x, y, z, 1, 0.0D, 0.05D, 0.0D, 0.0D);
    }
}