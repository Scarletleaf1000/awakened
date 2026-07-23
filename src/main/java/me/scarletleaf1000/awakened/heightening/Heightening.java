package me.scarletleaf1000.awakened.heightening;

/**
 * Heightening tiers from Warbreaker. Tiers are unlocked by reaching the
 * required Breath threshold. Values 1-9 have no tier; 0 is Drab.
 */
import net.minecraft.network.chat.Component;

public enum Heightening {
    DRAB(0, "heightening.awakened.drab"),
    NO_HEIGHTENING(1, "heightening.awakened.none"),
    FIRST(10, "heightening.awakened.first"),
    SECOND(25, "heightening.awakened.second"),
    THIRD(50, "heightening.awakened.third"),
    FOURTH(100, "heightening.awakened.fourth"),
    FIFTH(150, "heightening.awakened.fifth"),
    SIXTH(250, "heightening.awakened.sixth"),
    SEVENTH(400, "heightening.awakened.seventh"),
    EIGHTH(600, "heightening.awakened.eighth"),
    NINTH(1000, "heightening.awakened.ninth"),
    TENTH(2000, "heightening.awakened.tenth");

    private final int threshold;
    private final String displayNameKey;

    Heightening(int threshold, String displayNameKey) {
        this.threshold = threshold;
        this.displayNameKey = displayNameKey;
    }

    public int getThreshold() {
        return threshold;
    }

    public Component getDisplayName() {
        return Component.translatable(displayNameKey);
    }

    /**
     * Returns the heightening for a given Breath count.
     * 0 = Drab, 1-9 = No Heightening, 10+ = the appropriate tier.
     */
    public static Heightening fromBreath(int breath) {
        if (breath <= 0) {
            return DRAB;
        }
        Heightening result = NO_HEIGHTENING;
        for (Heightening h : values()) {
            if (h.threshold > 0 && breath >= h.threshold) {
                result = h;
            }
        }
        return result;
    }
}
