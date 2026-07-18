package me.scarletleaf1000.awakened.command;

import me.scarletleaf1000.awakened.Awakened;
import me.scarletleaf1000.awakened.command.actions.AttackTargetAction;
import me.scarletleaf1000.awakened.command.actions.FollowOwnerAction;
import me.scarletleaf1000.awakened.command.triggers.OnInteractTrigger;
import me.scarletleaf1000.awakened.command.triggers.OnProximityTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

/**
 * Deferred registers for the command subsystem. Custom Forge registries are created for
 * triggers, actions, and conditions so new entries can be added by the mod or datapacks
 * without touching core logic.
 */
public class CommandRegistries {
    public static final DeferredRegister<Trigger> TRIGGERS = DeferredRegister.create(new ResourceLocation(Awakened.MOD_ID, "triggers"), Awakened.MOD_ID);
    public static final DeferredRegister<Action> ACTIONS = DeferredRegister.create(new ResourceLocation(Awakened.MOD_ID, "actions"), Awakened.MOD_ID);
    public static final DeferredRegister<Condition> CONDITIONS = DeferredRegister.create(new ResourceLocation(Awakened.MOD_ID, "conditions"), Awakened.MOD_ID);

    public static final Supplier<IForgeRegistry<Trigger>> TRIGGER_REGISTRY = TRIGGERS.makeRegistry(RegistryBuilder::new);
    public static final Supplier<IForgeRegistry<Action>> ACTION_REGISTRY = ACTIONS.makeRegistry(RegistryBuilder::new);
    public static final Supplier<IForgeRegistry<Condition>> CONDITION_REGISTRY = CONDITIONS.makeRegistry(RegistryBuilder::new);

    public static final RegistryObject<Trigger> ON_PROXIMITY = TRIGGERS.register("on_proximity", OnProximityTrigger::new);
    public static final RegistryObject<Trigger> ON_INTERACT = TRIGGERS.register("on_interact", OnInteractTrigger::new);

    public static final RegistryObject<Action> FOLLOW_OWNER = ACTIONS.register("follow_owner", FollowOwnerAction::new);
    public static final RegistryObject<Action> ATTACK_TARGET = ACTIONS.register("attack_target", AttackTargetAction::new);

    public static void register(IEventBus bus) {
        TRIGGERS.register(bus);
        ACTIONS.register(bus);
        CONDITIONS.register(bus);
    }
}
