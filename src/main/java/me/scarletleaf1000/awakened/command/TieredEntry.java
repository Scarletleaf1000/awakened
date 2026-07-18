package me.scarletleaf1000.awakened.command;

/**
 * Anything that can be gated by a Heightening tier.
 */
public interface TieredEntry {
    default int minTier() {
        return 0;
    }
}
