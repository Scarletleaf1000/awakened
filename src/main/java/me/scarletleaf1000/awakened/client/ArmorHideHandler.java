package me.scarletleaf1000.awakened.client;

import me.scarletleaf1000.awakened.Awakened;
import me.scarletleaf1000.awakened.item.AwakenedItemData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = Awakened.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ArmorHideHandler {
    private ArmorHideHandler() {
    }

    @SubscribeEvent
    public static void onRenderLivingPre(RenderLivingEvent.Pre<?, ?> event) {
        LivingEntity entity = event.getEntity();
        Map<EquipmentSlot, ItemStack> hiddenArmor = new HashMap<>();
        
        // Check each armor slot for items with the "hide" command
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (!slot.isArmor()) {
                continue;
            }
            
            ItemStack armorStack = entity.getItemBySlot(slot);
            if (armorStack.isEmpty()) {
                continue;
            }
            
            ResourceLocation actionId = AwakenedItemData.getActionId(armorStack);
            if (actionId != null && Awakened.MOD_ID.equals(actionId.getNamespace()) && "hide".equals(actionId.getPath())) {
                // Store the armor piece and temporarily remove it from the entity
                hiddenArmor.put(slot, armorStack.copy());
                entity.setItemSlot(slot, ItemStack.EMPTY);
            }
        }
        
        // Store the hidden armor in the entity's persistent data for restoration in Post event
        if (!hiddenArmor.isEmpty()) {
            entity.getPersistentData().putIntArray("AwakenedHiddenArmorSlots", 
                hiddenArmor.keySet().stream().mapToInt(EquipmentSlot::ordinal).toArray());
            
            // Store each hidden armor piece
            int index = 0;
            for (Map.Entry<EquipmentSlot, ItemStack> entry : hiddenArmor.entrySet()) {
                entity.getPersistentData().put("AwakenedHiddenArmor_" + index, entry.getValue().save(new net.minecraft.nbt.CompoundTag()));
                index++;
            }
        }
    }

    @SubscribeEvent
    public static void onRenderLivingPost(RenderLivingEvent.Post<?, ?> event) {
        LivingEntity entity = event.getEntity();
        
        // Restore hidden armor pieces
        int[] hiddenSlots = entity.getPersistentData().getIntArray("AwakenedHiddenArmorSlots");
        if (hiddenSlots.length == 0) {
            return;
        }
        
        for (int i = 0; i < hiddenSlots.length; i++) {
            EquipmentSlot slot = EquipmentSlot.values()[hiddenSlots[i]];
            net.minecraft.nbt.CompoundTag tag = entity.getPersistentData().getCompound("AwakenedHiddenArmor_" + i);
            if (!tag.isEmpty()) {
                ItemStack restoredStack = ItemStack.of(tag);
                entity.setItemSlot(slot, restoredStack);
                entity.getPersistentData().remove("AwakenedHiddenArmor_" + i);
            }
        }
        
        entity.getPersistentData().remove("AwakenedHiddenArmorSlots");
    }
}
