package me.scarletleaf1000.awakened.command;

import me.scarletleaf1000.awakened.Awakened;
import me.scarletleaf1000.awakened.command.actions.AttackTargetAction;
import me.scarletleaf1000.awakened.command.actions.FollowOwnerAction;
import me.scarletleaf1000.awakened.command.actions.SummonEntityAction;
import me.scarletleaf1000.awakened.command.targets.InteractingEntityTarget;
import me.scarletleaf1000.awakened.command.targets.NearestHostileTarget;
import me.scarletleaf1000.awakened.command.triggers.OnInteractTrigger;
import me.scarletleaf1000.awakened.command.triggers.OnProximityTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class CommandRegistries {
    public static final DeferredRegister<Trigger> TRIGGERS = DeferredRegister.create(new ResourceLocation(Awakened.MOD_ID, "triggers"), Awakened.MOD_ID);
    public static final DeferredRegister<Action> ACTIONS = DeferredRegister.create(new ResourceLocation(Awakened.MOD_ID, "actions"), Awakened.MOD_ID);
    public static final DeferredRegister<Condition> CONDITIONS = DeferredRegister.create(new ResourceLocation(Awakened.MOD_ID, "conditions"), Awakened.MOD_ID);
    public static final DeferredRegister<Target> TARGETS = DeferredRegister.create(new ResourceLocation(Awakened.MOD_ID, "targets"), Awakened.MOD_ID);

    public static final Supplier<IForgeRegistry<Trigger>> TRIGGER_REGISTRY = TRIGGERS.makeRegistry(RegistryBuilder::new);
    public static final Supplier<IForgeRegistry<Action>> ACTION_REGISTRY = ACTIONS.makeRegistry(RegistryBuilder::new);
    public static final Supplier<IForgeRegistry<Condition>> CONDITION_REGISTRY = CONDITIONS.makeRegistry(RegistryBuilder::new);
    public static final Supplier<IForgeRegistry<Target>> TARGET_REGISTRY = TARGETS.makeRegistry(RegistryBuilder::new);

    public static final RegistryObject<Trigger> ON_PROXIMITY = TRIGGERS.register("on_proximity", OnProximityTrigger::new);
    public static final RegistryObject<Trigger> ON_INTERACT = TRIGGERS.register("on_interact", OnInteractTrigger::new);

    public static final RegistryObject<Action> FOLLOW_OWNER = ACTIONS.register("follow_owner", FollowOwnerAction::new);
    public static final RegistryObject<Action> ATTACK_TARGET = ACTIONS.register("attack_target", AttackTargetAction::new);
    public static final RegistryObject<Action> SUMMON_ENTITY = ACTIONS.register("summon_entity", SummonEntityAction::new);

    public static final RegistryObject<Target> INTERACTING_ENTITY = TARGETS.register("interacting_entity", InteractingEntityTarget::new);
    public static final RegistryObject<Target> NEAREST_HOSTILE = TARGETS.register("nearest_hostile", NearestHostileTarget::new);

    public static void register(IEventBus bus) {
        TRIGGERS.register(bus);
        ACTIONS.register(bus);
        CONDITIONS.register(bus);
        TARGETS.register(bus);
    }
}
