package me.scarletleaf1000.awakened.command;

import me.scarletleaf1000.awakened.Awakened;
import me.scarletleaf1000.awakened.command.actions.ItemStatAction;
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
    public static final RegistryObject<Action> BECOME_ETERNAL = ACTIONS.register("become_eternal", () -> new ItemStatAction("action.awakened.become_eternal", 1000, 9, ItemStatAction.Effect.UNBREAKABLE, 0));
    public static final RegistryObject<Action> HARM = ACTIONS.register("harm", () -> new ItemStatAction("action.awakened.harm", 20, 2, ItemStatAction.Effect.DAMAGE, 0.10D));
    public static final RegistryObject<Action> WOUND = ACTIONS.register("wound", () -> new ItemStatAction("action.awakened.wound", 200, 4, ItemStatAction.Effect.DAMAGE, 0.40D));
    public static final RegistryObject<Action> DESTROY = ACTIONS.register("destroy", () -> new ItemStatAction("action.awakened.destroy", 500, 8, ItemStatAction.Effect.DAMAGE, 1.00D));
    public static final RegistryObject<Action> REACH = ACTIONS.register("reach", () -> new ItemStatAction("action.awakened.reach", 200, 4, ItemStatAction.Effect.REACH, 1.0D));
    public static final RegistryObject<Action> DEFEND = ACTIONS.register("defend", () -> new ItemStatAction("action.awakened.defend", 50, 3, ItemStatAction.Effect.ARMOR, 2.0D));
    public static final RegistryObject<Action> WARD = ACTIONS.register("ward", () -> new ItemStatAction("action.awakened.ward", 200, 5, ItemStatAction.Effect.ARMOR, 4.0D));
    public static final RegistryObject<Action> PROTECT = ACTIONS.register("protect", () -> new ItemStatAction("action.awakened.protect", 500, 8, ItemStatAction.Effect.ARMOR, 8.0D));
    public static final RegistryObject<Action> JUMP = ACTIONS.register("jump", () -> new ItemStatAction("action.awakened.jump", 50, 3, ItemStatAction.Effect.JUMP, 1.5D));
    public static final RegistryObject<Action> LAUNCH = ACTIONS.register("launch", () -> new ItemStatAction("action.awakened.launch", 150, 5, ItemStatAction.Effect.JUMP, 3.0D));
    public static final ResourceLocation NO_TARGET_ID = new ResourceLocation(Awakened.MOD_ID, "no_target");
    public static final RegistryObject<Target> NO_TARGET = TARGETS.register(NO_TARGET_ID.getPath(), NoTarget::new);

    public static void register(IEventBus bus) {
        TRIGGERS.register(bus);
        ACTIONS.register(bus);
        CONDITIONS.register(bus);
        TARGETS.register(bus);
    }
}