package me.scarletleaf1000.awakened.heightening;

import me.scarletleaf1000.awakened.breath.BreathProvider;
import me.scarletleaf1000.awakened.network.BreathNetwork;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.UUID;

/**
 * Applies heightening benefits and sends entity-breath sync data to players who can see them.
 */
public final class HeighteningEffects {
    private static final int SYNC_INTERVAL = 20;
    private static final int PASSIVE_INTERVAL = 100; // 5 seconds
    private static final double SIGHT_RANGE = 64.0;
    private static final UUID HEALTH_BOOST_MODIFIER_UUID = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");

    private HeighteningEffects() {
    }

    public static void tickPlayer(Player player) {
        if (player.level().isClientSide()) {
            return;
        }
        player.getCapability(BreathProvider.BREATH).ifPresent(breath -> {
            int amount = breath.getBreath();
            Heightening heightening = Heightening.fromBreath(amount);

            updateHealthBoostAttribute(player, heightening);

            if (player.tickCount % PASSIVE_INTERVAL == 0) {
                applyPotionEffects(player, heightening);
                if (heightening.ordinal() >= Heightening.FIFTH.ordinal()) {
                    removePoisonAndWither(player);
                    feedPlayer(player);
                }
            }

            syncEntityBreathToPlayer(player, heightening);
        });
    }

    private static void applyPotionEffects(Player player, Heightening heightening) {
        // Second Heightening: Regeneration 1
        if (heightening.ordinal() >= Heightening.SECOND.ordinal()) {
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 220, 0, false, false));
        }
    }

    private static void updateHealthBoostAttribute(Player player, Heightening heightening) {
        AttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth == null) {
            return;
        }
        maxHealth.removeModifier(HEALTH_BOOST_MODIFIER_UUID);

        double bonus = 0;
        if (heightening.ordinal() >= Heightening.TENTH.ordinal()) {
            bonus = 12;
        } else if (heightening.ordinal() >= Heightening.EIGHTH.ordinal()) {
            bonus = 8;
        } else if (heightening.ordinal() >= Heightening.THIRD.ordinal()) {
            bonus = 4;
        }

        if (bonus > 0) {
            AttributeModifier modifier = new AttributeModifier(
                    HEALTH_BOOST_MODIFIER_UUID,
                    "awakened.heightening.health_boost",
                    bonus,
                    AttributeModifier.Operation.ADDITION
            );
            maxHealth.addTransientModifier(modifier);
        }
    }

    private static void removePoisonAndWither(Player player) {
        if (player.hasEffect(MobEffects.POISON)) {
            player.removeEffect(MobEffects.POISON);
        }
        if (player.hasEffect(MobEffects.WITHER)) {
            player.removeEffect(MobEffects.WITHER);
        }
    }

    private static void feedPlayer(Player player) {
        if (player.getFoodData().getFoodLevel() >= 20 && player.getFoodData().getSaturationLevel() >= 2.0f) {
            return;
        }
        int newFood = Math.min(20, player.getFoodData().getFoodLevel() + 1);
        float newSaturation = Math.min(2.0f, player.getFoodData().getSaturationLevel() + 0.5f);
        player.getFoodData().setFoodLevel(newFood);
        player.getFoodData().setSaturation(newSaturation);
    }

    private static void syncEntityBreathToPlayer(Player player, Heightening heightening) {
        // First Heightening+: ability to see the heightening of others
        if (heightening.ordinal() < Heightening.FIRST.ordinal()) {
            return;
        }
        if (player.tickCount % SYNC_INTERVAL != 0) {
            return;
        }
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        AABB box = player.getBoundingBox().inflate(SIGHT_RANGE);
        List<LivingEntity> nearby = player.level().getEntitiesOfClass(LivingEntity.class, box, e -> e != player && e.isAlive());
        for (LivingEntity target : nearby) {
            target.getCapability(BreathProvider.BREATH).ifPresent(b -> {
                BreathNetwork.sendEntityBreath(serverPlayer, target.getId(), b.getBreath());
            });
        }
    }
}
