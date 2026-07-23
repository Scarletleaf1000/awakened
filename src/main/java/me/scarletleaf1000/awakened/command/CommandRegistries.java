package me.scarletleaf1000.awakened.command;

import me.scarletleaf1000.awakened.Awakened;
import me.scarletleaf1000.awakened.command.actions.PlaceholderItemAction;
import me.scarletleaf1000.awakened.command.targets.NoTarget;
import me.scarletleaf1000.awakened.command.triggers.PassiveTrigger;
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

    public static final RegistryObject<Trigger> PASSIVE = TRIGGERS.register("passive", PassiveTrigger::new);
    public static final RegistryObject<Action> HARDEN = ACTIONS.register("harden", () -> new PlaceholderItemAction("action.awakened.harden", 10, 2));
    public static final RegistryObject<Action> RESIST = ACTIONS.register("resist", () -> new PlaceholderItemAction("action.awakened.resist", 100, 5));
    public static final RegistryObject<Action> PERSIST = ACTIONS.register("persist", () -> new PlaceholderItemAction("action.awakened.persist", 300, 8));
    public static final RegistryObject<Action> BECOME_ETERNAL = ACTIONS.register("become_eternal", () -> new PlaceholderItemAction("action.awakened.become_eternal", 1000, 9));
    public static final RegistryObject<Action> HARM = ACTIONS.register("harm", () -> new PlaceholderItemAction("action.awakened.harm", 20, 2));
    public static final RegistryObject<Action> WOUND = ACTIONS.register("wound", () -> new PlaceholderItemAction("action.awakened.wound", 200, 4));
    public static final RegistryObject<Action> DESTROY = ACTIONS.register("destroy", () -> new PlaceholderItemAction("action.awakened.destroy", 500, 8));
    public static final RegistryObject<Action> REACH = ACTIONS.register("reach", () -> new PlaceholderItemAction("action.awakened.reach", 200, 4));
    public static final RegistryObject<Action> DEFEND = ACTIONS.register("defend", () -> new PlaceholderItemAction("action.awakened.defend", 50, 3));
    public static final RegistryObject<Action> WARD = ACTIONS.register("ward", () -> new PlaceholderItemAction("action.awakened.ward", 200, 5));
    public static final RegistryObject<Action> PROTECT = ACTIONS.register("protect", () -> new PlaceholderItemAction("action.awakened.protect", 500, 8));
    public static final ResourceLocation NO_TARGET_ID = new ResourceLocation(Awakened.MOD_ID, "no_target");
    public static final RegistryObject<Target> NO_TARGET = TARGETS.register(NO_TARGET_ID.getPath(), NoTarget::new);

    public static void register(IEventBus bus) {
        TRIGGERS.register(bus);
        ACTIONS.register(bus);
        CONDITIONS.register(bus);
        TARGETS.register(bus);
    }
}
