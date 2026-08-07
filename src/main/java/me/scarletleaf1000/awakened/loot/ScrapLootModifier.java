package me.scarletleaf1000.awakened.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.scarletleaf1000.awakened.Awakened;
import me.scarletleaf1000.awakened.item.ItemBreathStorage;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;

import javax.annotation.Nonnull;

public class ScrapLootModifier extends LootModifier {
    public static final Codec<ScrapLootModifier> CODEC = RecordCodecBuilder.create(inst ->
            LootModifier.codecStart(inst).and(inst.group(
                    Codec.INT.fieldOf("minBreath").forGetter(m -> m.minBreath),
                    Codec.INT.fieldOf("maxBreath").forGetter(m -> m.maxBreath)
            )).apply(inst, ScrapLootModifier::new));

    private final int minBreath;
    private final int maxBreath;

    public ScrapLootModifier(LootItemCondition[] conditionsIn, int minBreath, int maxBreath) {
        super(conditionsIn);
        this.minBreath = minBreath;
        this.maxBreath = maxBreath;
    }

    @Nonnull
    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        ItemStack stack = new ItemStack(Awakened.AWAKENED_SCRAP.get());
        int breath = minBreath + context.getRandom().nextInt(maxBreath - minBreath + 1);
        ItemBreathStorage.setStoredBreath(stack, breath);
        generatedLoot.add(stack);
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
