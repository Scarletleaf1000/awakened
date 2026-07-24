package me.scarletleaf1000.awakened.breath;

import me.scarletleaf1000.awakened.Awakened;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = Awakened.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BreathAttributes {
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(
            ForgeRegistries.ATTRIBUTES, Awakened.MOD_ID);

    public static final RegistryObject<Attribute> BREATH = ATTRIBUTES.register("breath",
            () -> new RangedAttribute("attribute.name.awakened.breath", 1.0D, 0.0D, Integer.MAX_VALUE)
                    .setSyncable(true));

    @SubscribeEvent
    public static void onEntityAttributeModification(EntityAttributeModificationEvent event) {
        event.getTypes().forEach(type -> {
            if (!event.has(type, BREATH.get())) {
                event.add(type, BREATH.get());
            }
        });
    }
}
