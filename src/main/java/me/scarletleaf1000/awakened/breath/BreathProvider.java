package me.scarletleaf1000.awakened.breath;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BreathProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static final Capability<IBreath> BREATH = CapabilityManager.get(new CapabilityToken<>() {});
    public static final ResourceLocation ID = new ResourceLocation("awakened", "breath");

    private final BreathCapability breath;
    private final LazyOptional<IBreath> lazyOptional;

    public BreathProvider(LivingEntity entity) {
        this.breath = new BreathCapability(entity);
        this.lazyOptional = LazyOptional.of(() -> breath);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == BREATH ? lazyOptional.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("breath", breath.getBreath());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        breath.setBreathInternal(tag.getInt("breath"));
    }
}
