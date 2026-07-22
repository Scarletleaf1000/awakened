package me.scarletleaf1000.awakened.command;

import net.minecraft.network.chat.Component;

public interface TieredEntry {
    default int minTier() {
        return 0;
    }

    default int cost() {
        return 0;
    }

    default int minHeightening() {
        return 0;
    }

    default Component getDisplayName() {
        return Component.empty();
    }

    default Component getDescription() {
        return Component.empty();
    }
}
