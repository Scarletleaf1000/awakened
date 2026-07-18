package me.scarletleaf1000.awakened.breath;

import me.scarletleaf1000.awakened.network.BreathNetwork;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

public class BreathCapability implements IBreath {
    private final LivingEntity entity;
    private int breath = 1;

    public BreathCapability(LivingEntity entity) {
        this.entity = entity;
    }

    @Override
    public int getBreath() {
        return breath;
    }

    @Override
    public void setBreath(int amount) {
        int previous = this.breath;
        this.breath = Math.max(0, amount);
        if (previous != this.breath && !entity.level().isClientSide() && entity instanceof ServerPlayer serverPlayer) {
            BreathNetwork.sendToPlayer(serverPlayer, this.breath);
        }
    }

    @Override
    public void addBreath(int amount) {
        setBreath(breath + amount);
    }

    @Override
    public void removeBreath(int amount) {
        setBreath(Math.max(0, breath - amount));
    }

    void setBreathInternal(int amount) {
        this.breath = Math.max(0, amount);
    }
}
