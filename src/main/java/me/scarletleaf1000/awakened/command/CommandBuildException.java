package me.scarletleaf1000.awakened.command;

import net.minecraft.network.chat.Component;

/**
 * Thrown when a requested Command combination cannot be built, e.g. unknown registry ID
 * or required tier above what is available.
 */
public class CommandBuildException extends RuntimeException {
    private final Component component;

    public CommandBuildException(Component message) {
        super(message.getString());
        this.component = message;
    }

    public Component getComponent() {
        return component;
    }
}
