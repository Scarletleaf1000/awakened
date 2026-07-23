package me.scarletleaf1000.awakened.item;

import me.scarletleaf1000.awakened.Awakened;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.nio.charset.StandardCharsets;
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
                case "harm" -> addModifier(event, Attributes.ATTACK_DAMAGE, 0.10D, AttributeModifier.Operation.MULTIPLY_BASE, action);
                case "wound" -> addModifier(event, Attributes.ATTACK_DAMAGE, 0.40D, AttributeModifier.Operation.MULTIPLY_BASE, action);
                case "destroy" -> addModifier(event, Attributes.ATTACK_DAMAGE, 1.00D, AttributeModifier.Operation.MULTIPLY_BASE, action);
                case "reach" -> {
                    addModifier(event, ForgeMod.ENTITY_REACH.get(), 1.0D, AttributeModifier.Operation.ADDITION, action + ".entity");
                    addModifier(event, ForgeMod.BLOCK_REACH.get(), 1.0D, AttributeModifier.Operation.ADDITION, action + ".block");
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

        if (event.getSlotType().isArmor()) {
            switch (action) {
                case "defend" -> addModifier(event, Attributes.ARMOR, 2.0D, AttributeModifier.Operation.ADDITION, action);
                case "ward" -> addModifier(event, Attributes.ARMOR, 4.0D, AttributeModifier.Operation.ADDITION, action);
                case "protect" -> addModifier(event, Attributes.ARMOR, 8.0D, AttributeModifier.Operation.ADDITION, action);
                default -> {
                }
            }
        }
    }

    private static void addModifier(ItemAttributeModifierEvent event, net.minecraft.world.entity.ai.attributes.Attribute attribute, double amount, AttributeModifier.Operation operation, String name) {
        UUID id = UUID.nameUUIDFromBytes((Awakened.MOD_ID + ":" + name).getBytes(StandardCharsets.UTF_8));
        event.addModifier(attribute, new AttributeModifier(id, Awakened.MOD_ID + "." + name, amount, operation));
    }
}
