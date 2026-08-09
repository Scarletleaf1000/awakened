package me.scarletleaf1000.awakened.item;

import me.scarletleaf1000.awakened.Awakened;
import me.scarletleaf1000.awakened.attribute.ModAttributes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
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

        String action = actionId.getPath();
        if (event.getSlotType() == EquipmentSlot.MAINHAND) {
            switch (action) {
                case "harm" -> addModifier(event, Attributes.ATTACK_DAMAGE, 1.0D, AttributeModifier.Operation.ADDITION, action);
                case "wound" -> addModifier(event, Attributes.ATTACK_DAMAGE, 2.0D, AttributeModifier.Operation.ADDITION, action);
                case "destroy" -> addModifier(event, Attributes.ATTACK_DAMAGE, 5.0D, AttributeModifier.Operation.ADDITION, action);
                case "reach" -> {
                    addModifier(event, ForgeMod.ENTITY_REACH.get(), 1.0D, AttributeModifier.Operation.ADDITION, action + ".entity");
                    addModifier(event, ForgeMod.BLOCK_REACH.get(), 1.0D, AttributeModifier.Operation.ADDITION, action + ".block");
                }
                case "quicken" -> {
                    addModifier(event, Attributes.ATTACK_SPEED, 0.25D, AttributeModifier.Operation.ADDITION, action + ".attack_speed");
                    addModifier(event, ModAttributes.MINING_SPEED.get(), 0.35D, AttributeModifier.Operation.ADDITION, action + ".mining_speed");
                }
                case "hasten" -> {
                    addModifier(event, Attributes.ATTACK_SPEED, 0.5D, AttributeModifier.Operation.ADDITION, action + ".attack_speed");
                    addModifier(event, ModAttributes.MINING_SPEED.get(), 0.75D, AttributeModifier.Operation.ADDITION, action + ".mining_speed");
                }
                default -> {
                }
            }
        }

        if (event.getSlotType() == EquipmentSlot.LEGS) {
            switch (action) {
                case "jump" -> addModifier(event, ForgeMod.STEP_HEIGHT_ADDITION.get(), 0.5D, AttributeModifier.Operation.ADDITION, action);
                case "launch" -> addModifier(event, ForgeMod.STEP_HEIGHT_ADDITION.get(), 1.0D, AttributeModifier.Operation.ADDITION, action);
                default -> {
                }
            }
        }

        if (event.getSlotType() == EquipmentSlot.FEET) {
            switch (action) {
                case "flee" -> addModifier(event, Attributes.MOVEMENT_SPEED, 0.25D, AttributeModifier.Operation.MULTIPLY_BASE, action);
                default -> {
                }
            }
        }

        if (event.getSlotType().isArmor() && stack.getItem() instanceof net.minecraft.world.item.ArmorItem armorItem && event.getSlotType() == armorItem.getEquipmentSlot()) {
            switch (action) {
                case "defend" -> addModifier(event, Attributes.ARMOR, 2.0D, AttributeModifier.Operation.ADDITION, action);
                case "ward" -> addModifier(event, Attributes.ARMOR, 4.0D, AttributeModifier.Operation.ADDITION, action);
                case "protect" -> addModifier(event, Attributes.ARMOR, 8.0D, AttributeModifier.Operation.ADDITION, action);
                case "toughen" -> addModifier(event, Attributes.ARMOR_TOUGHNESS, 5.0D, AttributeModifier.Operation.ADDITION, action);
                default -> {
                }
            }
        }
    }

    private static void addModifier(ItemAttributeModifierEvent event, net.minecraft.world.entity.ai.attributes.Attribute attribute, double amount, AttributeModifier.Operation operation, String name) {
        UUID id = UUID.nameUUIDFromBytes((Awakened.MOD_ID + "." + name + "." + event.getSlotType().getName()).getBytes(java.nio.charset.StandardCharsets.UTF_8));
        event.addModifier(attribute, new AttributeModifier(id, Awakened.MOD_ID + "." + name, amount, operation));
    }
}
