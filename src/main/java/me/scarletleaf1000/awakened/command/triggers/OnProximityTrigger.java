package me.scarletleaf1000.awakened.command.triggers;

import me.scarletleaf1000.awakened.command.CommandContext;
import me.scarletleaf1000.awakened.command.Trigger;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;

import java.util.Comparator;
import java.util.List;

/**
 * Fires when a hostile entity is within {@code RADIUS} blocks of the host.
 * Sets the nearest valid target on the context.
 */
public class OnProximityTrigger implements Trigger {
    private static final double RADIUS = 8.0;

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
