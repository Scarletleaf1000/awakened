package me.scarletleaf1000.awakened.command;

public record Command(Trigger trigger, Action action, Target target) {
    public boolean evaluateAndExecute(CommandContext ctx) {
        if (!target.select(ctx)) {
            return false;
        }
        if (trigger.check(ctx)) {
            action.execute(ctx);
            return true;
        }
        return false;
    }
}
