package me.scarletleaf1000.awakened.command.debug;

import com.mojang.brigadier.CommandDispatcher;
import me.scarletleaf1000.awakened.Awakened;
import me.scarletleaf1000.awakened.command.Command;
import me.scarletleaf1000.awakened.command.CommandBuilder;
import me.scarletleaf1000.awakened.command.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


/**
 * In-game debug command that builds a sample Command from registry IDs and evaluates it.
 * Run {@code /awakenedcommandtest} as an op/permission level 2+ player.
 */
@Mod.EventBusSubscriber(modid = Awakened.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommandDebugCommand {
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("awakenedcommandtest")
                .requires(source -> source.hasPermission(2))
                .executes(CommandDebugCommand::run));
    }

    private static int run(com.mojang.brigadier.context.CommandContext<CommandSourceStack> ctx) {
        try {
            Command command = CommandBuilder.build(
                    new ResourceLocation(Awakened.MOD_ID, "on_proximity"),
                    new ResourceLocation(Awakened.MOD_ID, "attack_target"),
                    new ResourceLocation(Awakened.MOD_ID, "nearest_hostile"),
                    0
            );
            Entity executor = ctx.getSource().getEntityOrException();
            CommandContext commandCtx = new CommandContext(executor, executor.level());
            boolean fired = command.evaluateAndExecute(commandCtx);
            ctx.getSource().sendSuccess(() -> Component.translatable("message.awakened.debug.command_test.success", fired), false);
            return 1;
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.translatable("message.awakened.debug.command_test.failure", e.getMessage()));
            return 0;
        }
    }
}
