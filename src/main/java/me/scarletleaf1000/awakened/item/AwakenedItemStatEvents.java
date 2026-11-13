package me.scarletleaf1000.awakened.item;

import me.scarletleaf1000.awakened.Awakened;
import me.scarletleaf1000.awakened.attribute.ModAttributes;
import me.scarletleaf1000.awakened.command.Action;
import me.scarletleaf1000.awakened.command.CommandRegistries;
import me.scarletleaf1000.awakened.command.actions.ItemStatAction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = Awakened.MOD_ID)
public final class AwakenedItemStatEvents {
    private AwakenedItemStatEvents() {
    }

    @SubscribeEvent
    public static void onItemAttributeModifiers(ItemAttributeModifierEvent event) {
        ItemStack stack = event.getItemStack();
        ResourceLocation actionId = AwakenedItemData.getActionId(stack);
        if (actionId == null || !Awakened.MOD_ID.equals(actionId.getNamespace())) {
            return;
        }

        Action action = CommandRegistries.ACTION_REGISTRY.get().getValue(actionId);
        if (!(action instanceof ItemStatAction statAction)) {
            return;
        }

        String name = actionId.getPath();
        double amount = statAction.getAmount();
        double secondaryAmount = statAction.getSecondaryAmount();

        switch (statAction.getEffect()) {
            case DAMAGE -> {
                if (event.getSlotType() == EquipmentSlot.MAINHAND) {
                    addModifier(event, Attributes.ATTACK_DAMAGE, amount, AttributeModifier.Operation.ADDITION, name);
                }
            }
            case REACH -> {
                if (event.getSlotType() == EquipmentSlot.MAINHAND) {
                    addModifier(event, ForgeMod.ENTITY_REACH.get(), amount, AttributeModifier.Operation.ADDITION, name + ".entity");
                    addModifier(event, ForgeMod.BLOCK_REACH.get(), amount, AttributeModifier.Operation.ADDITION, name + ".block");
                }
            }
            case ATTACK_AND_MINING_SPEED -> {
                if (event.getSlotType() == EquipmentSlot.MAINHAND) {
                    addModifier(event, Attributes.ATTACK_SPEED, amount, AttributeModifier.Operation.ADDITION, name + ".attack_speed");
                    addModifier(event, ModAttributes.MINING_SPEED.get(), secondaryAmount, AttributeModifier.Operation.ADDITION, name + ".mining_speed");
                }
            }
            case JUMP -> {
                if (event.getSlotType() == EquipmentSlot.LEGS) {
                    addModifier(event, ForgeMod.STEP_HEIGHT_ADDITION.get(), secondaryAmount, AttributeModifier.Operation.ADDITION, name);
                }
            }
            case SPEED -> {
                if (event.getSlotType() == EquipmentSlot.FEET) {
                    addModifier(event, Attributes.MOVEMENT_SPEED, amount, AttributeModifier.Operation.MULTIPLY_BASE, name);
                }
            }
            case ARMOR -> {
                if (isOwnArmorSlot(event, stack)) {
                    addModifier(event, Attributes.ARMOR, amount, AttributeModifier.Operation.ADDITION, name);
                }
            }
            case TOUGHNESS -> {
                if (isOwnArmorSlot(event, stack)) {
                    addModifier(event, Attributes.ARMOR_TOUGHNESS, amount, AttributeModifier.Operation.ADDITION, name);
                }
            }
            default -> {
            }
        }
    }

    private static boolean isOwnArmorSlot(ItemAttributeModifierEvent event, ItemStack stack) {
        return event.getSlotType().isArmor()
                && stack.getItem() instanceof ArmorItem armorItem
                && event.getSlotType() == armorItem.getEquipmentSlot();
    }

    private static void addModifier(ItemAttributeModifierEvent event, Attribute attribute, double amount, AttributeModifier.Operation operation, String name) {
        UUID id = UUID.nameUUIDFromBytes((Awakened.MOD_ID + "." + name + "." + event.getSlotType().getName()).getBytes(java.nio.charset.StandardCharsets.UTF_8));
        event.addModifier(attribute, new AttributeModifier(id, Awakened.MOD_ID + "." + name, amount, operation));
    }
}
