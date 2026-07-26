package me.scarletleaf1000.awakened;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = Awakened.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.IntValue MAX_NIGHTBLOODS = BUILDER
            .comment("Maximum number of Nightblood swords that can be crafted per world. (-1 for infinite)")
            .defineInRange("maxNightbloods", 1, -1, Integer.MAX_VALUE);

    public static final ForgeConfigSpec.IntValue BREATH_MIN_PRICE = BUILDER
            .comment("Minimum emerald cost per Breath when trading with a villager.")
            .defineInRange("breathMinPrice", 16, 0, Integer.MAX_VALUE);

    public static final ForgeConfigSpec.IntValue BREATH_MAX_PRICE = BUILDER
            .comment("Maximum emerald cost per Breath when trading with a villager.",
                    "Each Breath a villager has will roll a random cost between breathMinPrice and breathMaxPrice.")
            .defineInRange("breathMaxPrice", 32, 0, Integer.MAX_VALUE);

    public static final ForgeConfigSpec.IntValue BREATH_MIN_DISCOUNT_PRICE = BUILDER
            .comment("The lowest emerald cost a single Breath can be reduced to by Hero of the Village discounts.")
            .defineInRange("breathMinDiscountPrice", 2, 0, Integer.MAX_VALUE);

    public static final ForgeConfigSpec.IntValue BREATH_DISCOUNT_PER_LEVEL = BUILDER
            .comment("Emerald discount applied per Hero of the Village level when purchasing Breath.")
            .defineInRange("breathDiscountPerLevel", 8, 0, Integer.MAX_VALUE);

    public static final ForgeConfigSpec SPEC = BUILDER.build();
}
