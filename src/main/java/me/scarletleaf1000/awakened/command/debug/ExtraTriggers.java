package me.scarletleaf1000.awakened.command.debug;

import me.scarletleaf1000.awakened.command.CommandRegistries;
import me.scarletleaf1000.awakened.command.Trigger;
import net.minecraftforge.registries.RegistryObject;

/**
 * Demonstrates extending the command system from a separate class.
 * Registering new entries only requires referencing {@link CommandRegistries#TRIGGERS}
 * or {@link CommandRegistries#ACTIONS}; no changes to {@link me.scarletleaf1000.awakened.command.Command},
 * {@link me.scarletleaf1000.awakened.command.CommandBuilder}, or the starter implementations are needed.
 */
public class ExtraTriggers {
    public static final RegistryObject<Trigger> MOCK_TRIGGER = CommandRegistries.TRIGGERS.register("mock_trigger", () -> ctx -> true);

    public static void init() {
        // Static fields above register themselves when this class is loaded.
    }
}
