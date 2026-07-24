package me.scarletleaf1000.awakened.breath;

import me.scarletleaf1000.awakened.network.BreathNetwork;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;

public class BreathCapability implements IBreath {
    private final LivingEntity entity;

    public BreathCapability(LivingEntity entity) {
        this.entity = entity;
    }

    @Override
    public int getBreath() {
        AttributeInstance attr = entity.getAttribute(BreathAttributes.BREATH.get());
        return attr == null ? 1 : (int) attr.getBaseValue();
    }

    @Override
    public void setBreath(int amount) {
        int previous = getBreath();
        setBreathInternal(amount);
        int current = getBreath();
        if (previous != current && entity.isAlive() && !entity.level().isClientSide() && entity instanceof ServerPlayer serverPlayer) {
            BreathNetwork.sendToPlayer(serverPlayer, current);
        }
    }

    @Override
    public void addBreath(int amount) {
        setBreath(getBreath() + amount);
    }

    @Override
    public void removeBreath(int amount) {
        setBreath(Math.max(0, getBreath() - amount));
    }

    void setBreathInternal(int amount) {
        AttributeInstance attr = entity.getAttribute(BreathAttributes.BREATH.get());
        if (attr != null) {
            attr.setBaseValue(Math.max(0, amount));
        }
    }
}
