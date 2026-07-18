package me.scarletleaf1000.awakened.command;

import java.util.Optional;

/**
 * A data-driven pairing of one {@link Trigger}, one {@link Action}, and an optional {@link Condition}.
 */
public record Command(Trigger trigger, Action action, Optional<Condition> condition) {
    /**
     * Runs the optional condition and trigger. If both pass, executes the action.
     *
     * @return true if the command fired this tick/event.
     */
    public boolean evaluateAndExecute(CommandContext ctx) {
        if (condition.isPresent() && !condition.get().test(ctx)) {
            return false;
        }
        if (trigger.check(ctx)) {
            action.execute(ctx);
            return true;
        }
        return false;
    }
}
