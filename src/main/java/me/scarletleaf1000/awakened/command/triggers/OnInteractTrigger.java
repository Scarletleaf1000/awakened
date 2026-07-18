package me.scarletleaf1000.awakened.command.triggers;

import me.scarletleaf1000.awakened.command.CommandContext;
import me.scarletleaf1000.awakened.command.Trigger;

/**
 * Fires when the host has been right-clicked. The interaction source is set on
 * the context by the caller (event handler) via {@link CommandContext#setInteractedBy}.
 */
public class OnInteractTrigger implements Trigger {
    @Override
    public boolean check(CommandContext ctx) {
        return ctx.getInteractedBy() != null;
    }
}
