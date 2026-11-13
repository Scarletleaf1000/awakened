package me.scarletleaf1000.awakened.command.actions;

import me.scarletleaf1000.awakened.AwakenedServerConfig;
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
    private final AwakenedServerConfig.ActionTuning tuning;
    private final Effect effect;

    public ItemStatAction(AwakenedServerConfig.ActionTuning tuning, String translationKey, Effect effect) {
        this.translationKey = translationKey;
        this.tuning = tuning;
        this.effect = effect;
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
        return tuning.cost();
    }

    @Override
    public int minHeightening() {
        return tuning.minHeightening();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(translationKey);
    }

    @Override
    public Component getDescription() {
        return Component.translatable(translationKey + ".description", getDescriptionArgs());
    }

    /**
     * Builds the %s arguments for this action's description translation.
     * Percent-based strengths are formatted with a '%' suffix.
     */
    private Object[] getDescriptionArgs() {
        return switch (effect) {
            case UNBREAKABLE, HIDE -> new Object[0];
            case SPEED -> new Object[]{formatPercent(getAmount())};
            case ATTACK_AND_MINING_SPEED -> new Object[]{formatNumber(getAmount()), formatPercent(getSecondaryAmount())};
            case JUMP -> new Object[]{formatNumber(getAmount()), formatNumber(getSecondaryAmount())};
            default -> new Object[]{formatNumber(getAmount())};
        };
    }

    private static String formatPercent(double value) {
        return formatNumber(value * 100.0D) + "%";
    }

    private static String formatNumber(double value) {
        double rounded = Math.round(value * 100.0D) / 100.0D;
        return rounded == Math.floor(rounded) ? Long.toString((long) rounded) : Double.toString(rounded);
    }

    @Override
    public void execute(CommandContext ctx) {
    }

    public Effect getEffect() {
        return effect;
    }

    public double getAmount() {
        return tuning.strength();
    }

    public double getSecondaryAmount() {
        return tuning.secondaryStrength();
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
