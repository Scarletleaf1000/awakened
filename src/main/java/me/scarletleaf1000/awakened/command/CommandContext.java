package me.scarletleaf1000.awakened.command;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

/**
 * Mutable execution context shared between a {@link Trigger}, optional {@link Condition},
 * and {@link Action} for one evaluation of a {@link Command}.
 */
public class CommandContext {
    private final Entity host;
    private final Level level;
    private Entity target;

    public CommandContext(Entity host, Level level) {
        this.host = host;
        this.level = level;
    }

    public Entity getHost() {
        return host;
    }

    public Level getLevel() {
        return level;
    }

    public Entity getTarget() {
        return target;
    }

    public void setTarget(Entity target) {
        this.target = target;
    }

}
