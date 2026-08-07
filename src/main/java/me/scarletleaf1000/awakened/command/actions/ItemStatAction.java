package me.scarletleaf1000.awakened.command.actions;

import me.scarletleaf1000.awakened.command.Action;
import me.scarletleaf1000.awakened.command.ActionType;
import me.scarletleaf1000.awakened.command.CommandContext;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

public class ItemStatAction implements Action {
    public enum Effect {
        UNBREAKABLE,
        DAMAGE,
        REACH,
        ARMOR,
        TOUGHNESS,
        JUMP,
        SPEED,
        ATTACK_AND_MINING_SPEED,
        HIDE
    }

    private final String translationKey;
    private final int cost;
    private final int minHeightening;
    private final Effect effect;
    private final double amount;
    private final double secondaryAmount;

    public ItemStatAction(String translationKey, int cost, int minHeightening, Effect effect, double amount) {
        this(translationKey, cost, minHeightening, effect, amount, 0.0D);
    }

    public ItemStatAction(String translationKey, int cost, int minHeightening, Effect effect, double amount, double secondaryAmount) {
        this.translationKey = translationKey;
        this.cost = cost;
        this.minHeightening = minHeightening;
        this.effect = effect;
        this.amount = amount;
        this.secondaryAmount = secondaryAmount;
    }

    @Override
    public ActionType getActionType() {
        return ActionType.ITEM;
    }

    @Override
    public boolean appliesToItem() {
        return true;
    }

    @Override
    public int cost() {
        return cost;
    }

    @Override
    public int minHeightening() {
        return minHeightening;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(translationKey);
    }

    @Override
    public Component getDescription() {
        return Component.translatable(translationKey + ".description");
    }

    @Override
    public void execute(CommandContext ctx) {
    }

    public Effect getEffect() {
        return effect;
    }

    public double getAmount() {
        return amount;
    }

    public double getSecondaryAmount() {
        return secondaryAmount;
    }

    public boolean canApplyTo(ItemStack stack) {
        return switch (effect) {
            case UNBREAKABLE -> stack.isDamageableItem();
            case ARMOR, TOUGHNESS, HIDE -> stack.getItem() instanceof ArmorItem;
            case JUMP -> stack.getItem() instanceof ArmorItem armor && armor.getEquipmentSlot() == net.minecraft.world.entity.EquipmentSlot.LEGS;
            case SPEED -> stack.getItem() instanceof ArmorItem armor && armor.getEquipmentSlot() == net.minecraft.world.entity.EquipmentSlot.FEET;
            case DAMAGE, REACH, ATTACK_AND_MINING_SPEED -> true;
        };
    }
}
