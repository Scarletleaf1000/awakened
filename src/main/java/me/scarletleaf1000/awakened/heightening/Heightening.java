package me.scarletleaf1000.awakened.heightening;

/**
 * Heightening tiers from Warbreaker. Tiers are unlocked by reaching the
 * required Breath threshold. Values 1-9 have no tier; 0 is Drab.
 */
import me.scarletleaf1000.awakened.Config;
import net.minecraft.network.chat.Component;

public enum Heightening {
    DRAB(0, "heightening.awakened.drab"),
    NO_HEIGHTENING(1, "heightening.awakened.none"),
    FIRST(getThresholdFromConfig(1), "heightening.awakened.first"),
    SECOND(getThresholdFromConfig(2), "heightening.awakened.second"),
    THIRD(getThresholdFromConfig(3), "heightening.awakened.third"),
    FOURTH(getThresholdFromConfig(4), "heightening.awakened.fourth"),
    FIFTH(getThresholdFromConfig(5), "heightening.awakened.fifth"),
    SIXTH(getThresholdFromConfig(6), "heightening.awakened.sixth"),
    SEVENTH(getThresholdFromConfig(7), "heightening.awakened.seventh"),
    EIGHTH(getThresholdFromConfig(8), "heightening.awakened.eighth"),
    NINTH(getThresholdFromConfig(9), "heightening.awakened.ninth"),
    TENTH(getThresholdFromConfig(10), "heightening.awakened.tenth");

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

    public static int getThresholdFromConfig(int heightening) {
        return switch (heightening) {
            default -> Integer.MAX_VALUE;
            case 1 -> Config.HEIGHTENING_THRESHOLD_FIRST.get();
            case 2 -> Config.HEIGHTENING_THRESHOLD_SECOND.get();
            case 3 -> Config.HEIGHTENING_THRESHOLD_THIRD.get();
            case 4 -> Config.HEIGHTENING_THRESHOLD_FOURTH.get();
            case 5 -> Config.HEIGHTENING_THRESHOLD_FIFTH.get();
            case 6 -> Config.HEIGHTENING_THRESHOLD_SIXTH.get();
            case 7 -> Config.HEIGHTENING_THRESHOLD_SEVENTH.get();
            case 8 -> Config.HEIGHTENING_THRESHOLD_EIGHTH.get();
            case 9 -> Config.HEIGHTENING_THRESHOLD_NINTH.get();
            case 10 -> Config.HEIGHTENING_THRESHOLD_TENTH.get();
        };
    }
}
