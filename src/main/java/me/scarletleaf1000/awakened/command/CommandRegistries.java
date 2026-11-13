package me.scarletleaf1000.awakened.command;

import me.scarletleaf1000.awakened.Awakened;
import me.scarletleaf1000.awakened.AwakenedServerConfig;
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
    public static final RegistryObject<Action> BECOME_ETERNAL = ACTIONS.register("become_eternal", () -> new ItemStatAction(AwakenedServerConfig.BECOME_ETERNAL, "action.awakened.become_eternal", ItemStatAction.Effect.UNBREAKABLE));
    public static final RegistryObject<Action> HARM = ACTIONS.register("harm", () -> new ItemStatAction(AwakenedServerConfig.HARM, "action.awakened.harm", ItemStatAction.Effect.DAMAGE));
    public static final RegistryObject<Action> WOUND = ACTIONS.register("wound", () -> new ItemStatAction(AwakenedServerConfig.WOUND, "action.awakened.wound", ItemStatAction.Effect.DAMAGE));
    public static final RegistryObject<Action> DESTROY = ACTIONS.register("destroy", () -> new ItemStatAction(AwakenedServerConfig.DESTROY, "action.awakened.destroy", ItemStatAction.Effect.DAMAGE));
    public static final RegistryObject<Action> REACH = ACTIONS.register("reach", () -> new ItemStatAction(AwakenedServerConfig.REACH, "action.awakened.reach", ItemStatAction.Effect.REACH));
    public static final RegistryObject<Action> DEFEND = ACTIONS.register("defend", () -> new ItemStatAction(AwakenedServerConfig.DEFEND, "action.awakened.defend", ItemStatAction.Effect.ARMOR));
    public static final RegistryObject<Action> WARD = ACTIONS.register("ward", () -> new ItemStatAction(AwakenedServerConfig.WARD, "action.awakened.ward", ItemStatAction.Effect.ARMOR));
    public static final RegistryObject<Action> PROTECT = ACTIONS.register("protect", () -> new ItemStatAction(AwakenedServerConfig.PROTECT, "action.awakened.protect", ItemStatAction.Effect.ARMOR));
    public static final RegistryObject<Action> JUMP = ACTIONS.register("jump", () -> new ItemStatAction(AwakenedServerConfig.JUMP, "action.awakened.jump", ItemStatAction.Effect.JUMP));
    public static final RegistryObject<Action> LAUNCH = ACTIONS.register("launch", () -> new ItemStatAction(AwakenedServerConfig.LAUNCH, "action.awakened.launch", ItemStatAction.Effect.JUMP));
    public static final RegistryObject<Action> FLEE = ACTIONS.register("flee", () -> new ItemStatAction(AwakenedServerConfig.FLEE, "action.awakened.flee", ItemStatAction.Effect.SPEED));
    public static final RegistryObject<Action> QUICKEN = ACTIONS.register("quicken", () -> new ItemStatAction(AwakenedServerConfig.QUICKEN, "action.awakened.quicken", ItemStatAction.Effect.ATTACK_AND_MINING_SPEED));
    public static final RegistryObject<Action> HASTEN = ACTIONS.register("hasten", () -> new ItemStatAction(AwakenedServerConfig.HASTEN, "action.awakened.hasten", ItemStatAction.Effect.ATTACK_AND_MINING_SPEED));
    public static final RegistryObject<Action> TOUGHEN = ACTIONS.register("toughen", () -> new ItemStatAction(AwakenedServerConfig.TOUGHEN, "action.awakened.toughen", ItemStatAction.Effect.TOUGHNESS));
    public static final RegistryObject<Action> HIDE = ACTIONS.register("hide", () -> new ItemStatAction(AwakenedServerConfig.HIDE, "action.awakened.hide", ItemStatAction.Effect.HIDE));
    public static final ResourceLocation NO_TARGET_ID = new ResourceLocation(Awakened.MOD_ID, "no_target");
    public static final RegistryObject<Target> NO_TARGET = TARGETS.register(NO_TARGET_ID.getPath(), NoTarget::new);

    public static void register(IEventBus bus) {
        TRIGGERS.register(bus);
        ACTIONS.register(bus);
        CONDITIONS.register(bus);
        TARGETS.register(bus);
    }
}