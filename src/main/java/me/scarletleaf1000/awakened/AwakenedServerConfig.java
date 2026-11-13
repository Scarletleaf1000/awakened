package me.scarletleaf1000.awakened;

import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nullable;

/**
 * Per-world server config stored in saves/<world>/serverconfig/awakened-server.toml.
 * Contains a category for each Awakening action exposing its breath cost,
 * minimum heightening, and strength values.
 */
public class AwakenedServerConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ActionTuning BECOME_ETERNAL;
    public static final ActionTuning HARM;
    public static final ActionTuning WOUND;
    public static final ActionTuning DESTROY;
    public static final ActionTuning REACH;
    public static final ActionTuning DEFEND;
    public static final ActionTuning WARD;
    public static final ActionTuning PROTECT;
    public static final ActionTuning JUMP;
    public static final ActionTuning LAUNCH;
    public static final ActionTuning FLEE;
    public static final ActionTuning QUICKEN;
    public static final ActionTuning HASTEN;
    public static final ActionTuning TOUGHEN;
    public static final ActionTuning HIDE;

    public static final ForgeConfigSpec SPEC;

    static {
        BUILDER.comment(
                "Tuning for each Awakening action.",
                "'cost' is the amount of Breath consumed to awaken the item.",
                "'minHeightening' is the minimum Heightening tier required to use the action",
                "(0 = Drab, 1 = No Heightening, 2 = First, ..., 11 = Tenth).",
                "'strength' and 'secondaryStrength' control the magnitude of the effect;",
                "their exact meaning is described per action."
        ).push("actions");

        BECOME_ETERNAL = action("become_eternal", 1000, 9);
        HARM = action("harm", 20, 2, 1.0D, "Attack damage added to the item.");
        WOUND = action("wound", 200, 4, 2.0D, "Attack damage added to the item.");
        DESTROY = action("destroy", 1000, 10, 5.0D, "Attack damage added to the item.");
        REACH = action("reach", 200, 4, 1.0D, "Extra entity and block reach distance.");
        DEFEND = action("defend", 50, 3, 2.0D, "Armor points added.");
        WARD = action("ward", 200, 5, 4.0D, "Armor points added.");
        PROTECT = action("protect", 500, 8, 8.0D, "Armor points added.");
        JUMP = action("jump", 50, 3, 0.20D, "Upward velocity added when jumping.",
                0.5D, "Step height added.");
        LAUNCH = action("launch", 150, 5, 0.35D, "Upward velocity added when jumping.",
                1.0D, "Step height added.");
        FLEE = action("flee", 200, 6, 0.25D, "Movement speed multiplier (0.25 = +25%).");
        QUICKEN = action("quicken", 200, 6, 0.25D, "Attack speed added.",
                0.35D, "Mining speed added.");
        HASTEN = action("hasten", 500, 8, 0.5D, "Attack speed added.",
                0.75D, "Mining speed added.");
        TOUGHEN = action("toughen", 350, 7, 5.0D, "Armor toughness added.");
        HIDE = action("hide", 100, 10);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    private AwakenedServerConfig() {
    }

    private static ActionTuning action(String name, int cost, int minHeightening) {
        return action(name, cost, minHeightening, null, null, null, null);
    }

    private static ActionTuning action(String name, int cost, int minHeightening,
                                       double strength, String strengthDescription) {
        return action(name, cost, minHeightening, strength, strengthDescription, null, null);
    }

    private static ActionTuning action(String name, int cost, int minHeightening,
                                       double strength, String strengthDescription,
                                       double secondaryStrength, String secondaryDescription) {
        return action(name, cost, minHeightening,
                (Double) strength, strengthDescription,
                (Double) secondaryStrength, secondaryDescription);
    }

    private static ActionTuning action(String name, int cost, int minHeightening,
                                       @Nullable Double strength, @Nullable String strengthDescription,
                                       @Nullable Double secondaryStrength, @Nullable String secondaryDescription) {
        BUILDER.push(name);
        ForgeConfigSpec.IntValue costValue = BUILDER
                .comment("Breath cost to awaken an item with this action.")
                .defineInRange("cost", cost, 0, Integer.MAX_VALUE);
        ForgeConfigSpec.IntValue minHeighteningValue = BUILDER
                .comment("Minimum Heightening tier required (0 = Drab, 11 = Tenth).")
                .defineInRange("minHeightening", minHeightening, 0, 11);
        ForgeConfigSpec.DoubleValue strengthValue = strength == null ? null : BUILDER
                .comment(strengthDescription)
                .defineInRange("strength", strength, 0.0D, Double.MAX_VALUE);
        ForgeConfigSpec.DoubleValue secondaryStrengthValue = secondaryStrength == null ? null : BUILDER
                .comment(secondaryDescription)
                .defineInRange("secondaryStrength", secondaryStrength, 0.0D, Double.MAX_VALUE);
        BUILDER.pop();
        return new ActionTuning(costValue, minHeighteningValue, strengthValue, secondaryStrengthValue,
                strength == null ? 0.0D : strength, secondaryStrength == null ? 0.0D : secondaryStrength);
    }

    /**
     * Live config-backed values for a single action. Values are read on access so
     * edits to the server config take effect without restarting.
     */
    public static class ActionTuning {
        private final ForgeConfigSpec.IntValue cost;
        private final ForgeConfigSpec.IntValue minHeightening;
        @Nullable
        private final ForgeConfigSpec.DoubleValue strength;
        @Nullable
        private final ForgeConfigSpec.DoubleValue secondaryStrength;
        private final double defaultStrength;
        private final double defaultSecondaryStrength;

        private ActionTuning(ForgeConfigSpec.IntValue cost, ForgeConfigSpec.IntValue minHeightening,
                             @Nullable ForgeConfigSpec.DoubleValue strength,
                             @Nullable ForgeConfigSpec.DoubleValue secondaryStrength,
                             double defaultStrength, double defaultSecondaryStrength) {
            this.cost = cost;
            this.minHeightening = minHeightening;
            this.strength = strength;
            this.secondaryStrength = secondaryStrength;
            this.defaultStrength = defaultStrength;
            this.defaultSecondaryStrength = defaultSecondaryStrength;
        }

        public int cost() {
            return cost.get();
        }

        public int minHeightening() {
            return minHeightening.get();
        }

        public double strength() {
            return strength == null ? defaultStrength : strength.get();
        }

        public double secondaryStrength() {
            return secondaryStrength == null ? defaultSecondaryStrength : secondaryStrength.get();
        }
    }
}
