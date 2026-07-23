package me.scarletleaf1000.awakened.network;

import me.scarletleaf1000.awakened.breath.BreathProvider;
import me.scarletleaf1000.awakened.command.Command;
import me.scarletleaf1000.awakened.command.CommandBuildException;
import me.scarletleaf1000.awakened.command.CommandBuilder;
import me.scarletleaf1000.awakened.command.CommandContext;
import me.scarletleaf1000.awakened.command.actions.ItemStatAction;
import me.scarletleaf1000.awakened.heightening.Heightening;
import me.scarletleaf1000.awakened.item.AwakenedItemData;
import me.scarletleaf1000.awakened.item.ItemBreathStorage;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class AwakeningCommandUseC2SPacket {
    private final ResourceLocation triggerId;
    private final ResourceLocation actionId;
    private final ResourceLocation targetId;

    public AwakeningCommandUseC2SPacket(ResourceLocation triggerId, ResourceLocation actionId, ResourceLocation targetId) {
        this.triggerId = triggerId;
        this.actionId = actionId;
        this.targetId = targetId;
    }

    public AwakeningCommandUseC2SPacket(FriendlyByteBuf buf) {
        this.triggerId = buf.readResourceLocation();
        this.actionId = buf.readResourceLocation();
        this.targetId = buf.readResourceLocation();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceLocation(triggerId);
        buf.writeResourceLocation(actionId);
        buf.writeResourceLocation(targetId);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) {
                return;
            }
            player.getCapability(BreathProvider.BREATH).ifPresent(breath -> {
                int tier = Heightening.fromBreath(breath.getBreath()).ordinal();
                try {
                    Command command = CommandBuilder.build(triggerId, actionId, targetId, tier);
                    int storedBreath = command.trigger().cost() + command.action().cost() + command.target().cost();

                    if (command.action().appliesToItem()) {
                        ItemStack held = player.getMainHandItem();
                        if (!(command.action() instanceof ItemStatAction itemAction) || held.isEmpty() || held.getCount() != 1 || AwakenedItemData.isAwakened(held) || ItemBreathStorage.hasStoredBreath(held)) {
                            throw new CommandBuildException(Component.translatable("message.awakened.command.invalid_item"));
                        }
                        if (!itemAction.canApplyTo(held)) {
                            throw new CommandBuildException(Component.translatable("message.awakened.command.invalid_item_for_action", itemAction.getDisplayName()));
                        }
                        if (breath.getBreath() < storedBreath) {
                            throw new CommandBuildException(Component.translatable("message.awakened.command.insufficient_breath", storedBreath));
                        }
                        breath.removeBreath(storedBreath);
                        AwakenedItemData.write(held, triggerId, actionId, targetId, storedBreath, player.getUUID());
                    } else {
                        CommandContext commandCtx = new CommandContext(player, player.level());
                        command.evaluateAndExecute(commandCtx);
                    }

                    Component commandName = getCommandName(command);
                    Component announcement = Component.translatable("chat.awakened.announcement", player.getName(), commandName);

                    if (Heightening.fromBreath(breath.getBreath()) == Heightening.TENTH) {
                        player.sendSystemMessage(announcement.copy().withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
                    } else {
                        player.server.getPlayerList().broadcastSystemMessage(announcement, false);
                    }
                } catch (CommandBuildException e) {
                    player.sendSystemMessage(e.getComponent());
                }
            });
        });
        ctx.get().setPacketHandled(true);
    }

    private static Component getCommandName(Command command) {
        MutableComponent commandName = Component.empty();
        boolean first = true;
        Component[] parts = {command.trigger().getDisplayName(), command.action().getDisplayName(), command.target().getDisplayName()};
        for (Component part : parts) {
            if (part == null || part.getString().isBlank()) {
                continue;
            }
            if (!first) {
                commandName = commandName.append(" ");
            }
            first = false;
            commandName = commandName.append(part);
        }
        return commandName;
    }
}