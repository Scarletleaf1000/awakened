package me.scarletleaf1000.awakened.command.targets;

import me.scarletleaf1000.awakened.command.CommandContext;
import me.scarletleaf1000.awakened.command.Target;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.Comparator;
import java.util.List;

public class NearestEntityTarget implements Target {
    private static final double RADIUS = 8.0;

    @Override
    public Component getDisplayName() {
        return Component.literal("Nearest Entity");
    }

    @Override
    public Component getDescription() {
        return Component.literal("Targets the nearest living entity within 8 blocks.");
    }

    @Override
    public boolean select(CommandContext ctx) {
        Entity host = ctx.getHost();
        List<LivingEntity> nearby = ctx.getLevel().getEntitiesOfClass(
                LivingEntity.class,
                host.getBoundingBox().inflate(RADIUS),
                entity -> entity.isAlive() && entity != host
        );
        if (nearby.isEmpty()) {
            return false;
        }

        nearby.sort(Comparator.comparingDouble(entity -> entity.distanceToSqr(host)));
        ctx.setTarget(nearby.get(0));
        return true;
    }
}
