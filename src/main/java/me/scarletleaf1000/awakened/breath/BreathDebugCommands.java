package me.scarletleaf1000.awakened.breath;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import me.scarletleaf1000.awakened.Awakened;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Awakened.MOD_ID)
public class BreathDebugCommands {
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("breath")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("set")
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("amount", IntegerArgumentType.integer(0))
                                        .executes(ctx -> {
                                            ServerPlayer target = EntityArgument.getPlayer(ctx, "player");
                                            int amount = IntegerArgumentType.getInteger(ctx, "amount");
                                            target.getCapability(BreathProvider.BREATH).ifPresent(b -> b.setBreath(amount));
                                            ctx.getSource().sendSuccess(() -> Component.literal("Set " + target.getName().getString() + "'s Breath to " + amount), false);
                                            return 1;
                                        }))))
                .then(Commands.literal("add")
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("amount", IntegerArgumentType.integer())
                                        .executes(ctx -> {
                                            ServerPlayer target = EntityArgument.getPlayer(ctx, "player");
                                            int amount = IntegerArgumentType.getInteger(ctx, "amount");
                                            target.getCapability(BreathProvider.BREATH).ifPresent(b -> b.addBreath(amount));
                                            ctx.getSource().sendSuccess(() -> Component.literal("Added " + amount + " Breath to " + target.getName().getString()), false);
                                            return 1;
                                        }))))
                .then(Commands.literal("setentity")
                        .then(Commands.argument("entities", EntityArgument.entities())
                                .then(Commands.argument("amount", IntegerArgumentType.integer(0))
                                        .executes(ctx -> {
                                            int amount = IntegerArgumentType.getInteger(ctx, "amount");
                                            int count = 0;
                                            for (Entity entity : EntityArgument.getEntities(ctx, "entities")) {
                                                if (entity instanceof LivingEntity living) {
                                                    living.getCapability(BreathProvider.BREATH).ifPresent(b -> b.setBreath(amount));
                                                    count++;
                                                }
                                            }
                                            final int finalCount = count;
                                            ctx.getSource().sendSuccess(() -> Component.literal("Set Breath to " + amount + " for " + finalCount + " living entities"), false);
                                            return 1;
                                        })))));
    }
}
