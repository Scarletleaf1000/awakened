package me.scarletleaf1000.awakened.command;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Comparator;
import java.util.List;

public final class CommandBuilder {
    private CommandBuilder() {
    }

    public static Command build(ResourceLocation triggerId, ResourceLocation actionId, ResourceLocation targetId, int availableTier) {
        Trigger trigger = getEntry(CommandRegistries.TRIGGER_REGISTRY.get(), triggerId);
        Action action = getEntry(CommandRegistries.ACTION_REGISTRY.get(), actionId);
        Target target = getEntry(CommandRegistries.TARGET_REGISTRY.get(), targetId);

        validateTier(trigger, triggerId, availableTier);
        validateTier(action, actionId, availableTier);
        validateTier(target, targetId, availableTier);

        validateHeightening(trigger, triggerId, availableTier);
        validateHeightening(action, actionId, availableTier);
        validateHeightening(target, targetId, availableTier);

        return new Command(trigger, action, target);
    }

    private static <T extends TieredEntry> T getEntry(IForgeRegistry<T> registry, ResourceLocation id) {
        T entry = registry.getValue(id);
        if (entry == null) {
            throw new CommandBuildException(Component.translatable("message.awakened.command.unknown_entry", id));
        }
        return entry;
    }

    private static void validateTier(TieredEntry entry, ResourceLocation id, int availableTier) {
        if (entry.minTier() > availableTier) {
            throw new CommandBuildException(Component.translatable("message.awakened.command.tier_required", id, entry.minTier(), availableTier));
        }
    }

    private static void validateHeightening(TieredEntry entry, ResourceLocation id, int availableTier) {
        if (entry.minHeightening() > availableTier) {
            throw new CommandBuildException(Component.translatable("message.awakened.command.heightening_required", id, entry.minHeightening(), availableTier));
        }
    }

    public static <T extends TieredEntry> List<ResourceLocation> availableIds(IForgeRegistry<T> registry, int availableTier) {
        return registry.getKeys().stream()
                .filter(id -> {
                    T entry = registry.getValue(id);
                    return entry != null && entry.minTier() <= availableTier;
                })
                .sorted(Comparator.comparing(ResourceLocation::toString))
                .toList();
    }
}
