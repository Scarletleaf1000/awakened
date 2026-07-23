package me.scarletleaf1000.awakened.command.triggers;

import me.scarletleaf1000.awakened.command.ActionType;
import me.scarletleaf1000.awakened.command.CommandContext;
import me.scarletleaf1000.awakened.command.Trigger;

import java.util.EnumSet;
import java.util.Set;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;

import java.util.Comparator;
import java.util.List;

public class OnProximityTrigger implements Trigger {
    private static final double RADIUS = 8.0;

    @Override
    public Set<ActionType> getSupportedActionTypes() {
        return EnumSet.of(ActionType.ENTITY);
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("On Proximity");
    }

    @Override
    public Component getDescription() {
        return Component.literal("Activates when a hostile entity is within 8 blocks.");
    }

    @Override
    public boolean check(CommandContext ctx) {
        Entity host = ctx.getHost();
        List<LivingEntity> nearby = ctx.getLevel().getEntitiesOfClass(
                LivingEntity.class,
                host.getBoundingBox().inflate(RADIUS),
                e -> e.isAlive() && e != host && e instanceof Enemy
        );
        if (nearby.isEmpty()) {
            return false;
        }

        nearby.sort(Comparator.comparingDouble(e -> e.distanceToSqr(host)));
        ctx.setTarget(nearby.get(0));
        return true;
    }
}
