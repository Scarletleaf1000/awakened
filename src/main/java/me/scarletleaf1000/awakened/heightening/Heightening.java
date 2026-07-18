package me.scarletleaf1000.awakened.heightening;

/**
 * Heightening tiers from Warbreaker. Tiers are unlocked by reaching the
 * required Breath threshold. Values 1-9 have no tier; 0 is Drab.
 */
public enum Heightening {
    DRAB(0, "Drab"),
    NO_HEIGHTENING(1, "No Heightening"),
    FIRST(10, "First Heightening"),
    SECOND(25, "Second Heightening"),
    THIRD(50, "Third Heightening"),
    FOURTH(100, "Fourth Heightening"),
    FIFTH(150, "Fifth Heightening"),
    SIXTH(250, "Sixth Heightening"),
    SEVENTH(400, "Seventh Heightening"),
    EIGHTH(600, "Eighth Heightening"),
    NINTH(1000, "Ninth Heightening"),
    TENTH(2000, "Tenth Heightening");

    private final int threshold;
    private final String displayName;

    Heightening(int threshold, String displayName) {
        this.threshold = threshold;
        this.displayName = displayName;
    }

    public int getThreshold() {
        return threshold;
    }

    public String getDisplayName() {
        return displayName;
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
