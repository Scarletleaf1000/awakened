package me.scarletleaf1000.awakened.item;

import me.scarletleaf1000.awakened.Awakened;
import me.scarletleaf1000.awakened.command.Action;
import me.scarletleaf1000.awakened.command.CommandRegistries;
import me.scarletleaf1000.awakened.command.actions.ItemStatAction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Awakened.MOD_ID)
public final class AwakenedJumpEvents {
    private AwakenedJumpEvents() {
    }

    @SubscribeEvent
    public static void onLivingJump(LivingEvent.LivingJumpEvent event) {
        LivingEntity entity = event.getEntity();
        ItemStack leggings = entity.getItemBySlot(EquipmentSlot.LEGS);
        ResourceLocation actionId = AwakenedItemData.getActionId(leggings);
        if (actionId == null || !Awakened.MOD_ID.equals(actionId.getNamespace())) {
            return;
        }

        Action action = CommandRegistries.ACTION_REGISTRY.get().getValue(actionId);
        if (!(action instanceof ItemStatAction statAction) || statAction.getEffect() != ItemStatAction.Effect.JUMP) {
            return;
        }
        double boost = statAction.getAmount();
        if (boost > 0) {
            entity.setDeltaMovement(entity.getDeltaMovement().add(0.0D, boost, 0.0D));
        }
    }

    @SubscribeEvent
    public static void onLivingFall(LivingFallEvent event) {
        LivingEntity entity = event.getEntity();
        ResourceLocation actionId = AwakenedItemData.getActionId(entity.getItemBySlot(EquipmentSlot.LEGS));
        if (actionId == null || !Awakened.MOD_ID.equals(actionId.getNamespace())) {
            return;
        }

        float reduction = switch (actionId.getPath()) {
            case "jump" -> 2.0F;
            case "launch" -> 3.0F;
            default -> 0.0F;
        };
        if (reduction > 0.0F) {
            event.setDistance(Math.max(0.0F, event.getDistance() - reduction));
        }
    }
}