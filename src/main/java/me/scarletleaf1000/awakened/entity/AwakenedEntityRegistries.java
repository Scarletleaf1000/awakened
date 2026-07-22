package me.scarletleaf1000.awakened.entity;

import me.scarletleaf1000.awakened.Awakened;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AwakenedEntityRegistries {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Awakened.MOD_ID);

    public static final RegistryObject<EntityType<AwakenedItemEntity>> AWAKENED_ITEM = ENTITIES.register("awakened_item",
            () -> EntityType.Builder.<AwakenedItemEntity>of(AwakenedItemEntity::new, MobCategory.MISC)
                    .sized(0.75f, 0.75f)
                    .clientTrackingRange(8)
                    .updateInterval(20)
                    .build("awakened_item"));

    public static void register(IEventBus bus) {
        ENTITIES.register(bus);
    }
}
