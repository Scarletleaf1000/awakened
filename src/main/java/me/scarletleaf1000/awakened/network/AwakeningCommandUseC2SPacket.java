package me.scarletleaf1000.awakened.network;

import me.scarletleaf1000.awakened.breath.BreathProvider;
import me.scarletleaf1000.awakened.command.Command;
import me.scarletleaf1000.awakened.command.CommandBuilder;
import me.scarletleaf1000.awakened.command.CommandBuildException;
import me.scarletleaf1000.awakened.command.CommandContext;
import me.scarletleaf1000.awakened.heightening.Heightening;
import me.scarletleaf1000.awakened.item.AwakenedItemData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
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
                        if (!held.isEmpty()) {
                            AwakenedItemData.write(held, triggerId, actionId, targetId, storedBreath, player.getUUID());
                        }
                    } else {
                        CommandContext commandCtx = new CommandContext(player, player.level());
                        command.evaluateAndExecute(commandCtx);
                    }

                    Component announcement = Component.literal(
                            "<" + player.getName().getString() + "> [Awakening] " + getCommandName(command)
                    );

                    if (Heightening.fromBreath(breath.getBreath()) == Heightening.TENTH) {
                        player.sendSystemMessage(announcement.copy().withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
                    } else {
                        player.server.getPlayerList().broadcastSystemMessage(announcement, false);
                    }
                } catch (CommandBuildException e) {
                    player.sendSystemMessage(Component.literal(e.getMessage()));
                }
            });
        });
        ctx.get().setPacketHandled(true);
    }

    private static String getCommandName(Command command) {
        List<String> parts = new ArrayList<>();
        addIfNotBlank(parts, command.trigger().getDisplayName().getString());
        addIfNotBlank(parts, command.action().getDisplayName().getString());
        addIfNotBlank(parts, command.target().getDisplayName().getString());
        return String.join(" ", parts);
    }

    private static void addIfNotBlank(List<String> parts, String part) {
        if (part != null && !part.isBlank()) {
            parts.add(part);
        }
    }

}
