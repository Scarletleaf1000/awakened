package me.scarletleaf1000.awakened.command;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.List;
import java.util.Optional;

/**
 * Combinator that turns registry IDs into a validated {@link Command}.
 */
public final class CommandBuilder {
    private CommandBuilder() {
    }

    public static Command build(ResourceLocation triggerId, ResourceLocation actionId, int availableTier) {
        return build(triggerId, actionId, Optional.empty(), availableTier);
    }

    public static Command build(ResourceLocation triggerId, ResourceLocation actionId, Optional<ResourceLocation> conditionId, int availableTier) {
        Trigger trigger = getEntry(CommandRegistries.TRIGGER_REGISTRY.get(), triggerId);
        Action action = getEntry(CommandRegistries.ACTION_REGISTRY.get(), actionId);
        Optional<Condition> condition = conditionId.map(id -> getEntry(CommandRegistries.CONDITION_REGISTRY.get(), id));

        validateTier(trigger, triggerId, availableTier);
        validateTier(action, actionId, availableTier);
        condition.ifPresent(c -> validateTier(c, conditionId.get(), availableTier));

        return new Command(trigger, action, condition);
    }

    private static <T extends TieredEntry> T getEntry(IForgeRegistry<T> registry, ResourceLocation id) {
        T entry = registry.getValue(id);
        if (entry == null) {
            throw new CommandBuildException("Unknown registry entry: " + id);
        }
        return entry;
    }

    private static void validateTier(TieredEntry entry, ResourceLocation id, int availableTier) {
        if (entry.minTier() > availableTier) {
            throw new CommandBuildException("Entry " + id + " requires tier " + entry.minTier() + ", but available tier is " + availableTier);
        }
    }

    /**
     * Lists IDs from the given registry whose {@link TieredEntry#minTier()} is within the available tier.
     */
    public static <T extends TieredEntry> List<ResourceLocation> availableIds(IForgeRegistry<T> registry, int availableTier) {
        return registry.getKeys().stream()
                .filter(id -> {
                    T entry = registry.getValue(id);
                    return entry != null && entry.minTier() <= availableTier;
                })
                .toList();
    }
}
