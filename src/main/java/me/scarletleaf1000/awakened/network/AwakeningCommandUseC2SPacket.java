package me.scarletleaf1000.awakened.network;

import me.scarletleaf1000.awakened.breath.BreathProvider;
import me.scarletleaf1000.awakened.command.Command;
import me.scarletleaf1000.awakened.command.CommandBuilder;
import me.scarletleaf1000.awakened.command.CommandBuildException;
import me.scarletleaf1000.awakened.command.CommandContext;
import me.scarletleaf1000.awakened.entity.AwakenedEntityRegistries;
import me.scarletleaf1000.awakened.entity.AwakenedItemEntity;
import me.scarletleaf1000.awakened.heightening.Heightening;
import me.scarletleaf1000.awakened.item.AwakenedItemData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
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
                        if (!held.isEmpty()) {
                            AwakenedItemData.write(held, triggerId, actionId, targetId, storedBreath, player.getUUID());
                        }
                    } else if (command.action().transformsToEntity()) {
                        ItemStack source = findItemToTransform(player);
                        if (source == null || source.isEmpty()) {
                            player.sendSystemMessage(Component.literal("You need an item in your inventory to summon an awakened entity."));
                            return;
                        }

                        ItemStack visual = source.copy();
                        visual.setCount(1);

                        AwakenedItemEntity entity = new AwakenedItemEntity(AwakenedEntityRegistries.AWAKENED_ITEM.get(), player.level());
                        entity.moveTo(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());
                        entity.setItem(visual);
                        entity.setCommandData(triggerId, actionId, targetId, storedBreath, player.getUUID());
                        player.level().addFreshEntity(entity);

                        source.shrink(1);
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
        return command.trigger().getDisplayName().getString() + " "
                + command.action().getDisplayName().getString() + " "
                + command.target().getDisplayName().getString();
    }

    @javax.annotation.Nullable
    private static ItemStack findItemToTransform(ServerPlayer player) {
        ItemStack main = player.getMainHandItem();
        if (!main.isEmpty()) {
            return main;
        }

        Inventory inventory = player.getInventory();
        ItemStack offhand = inventory.offhand.get(0);
        if (!offhand.isEmpty()) {
            return offhand;
        }

        for (ItemStack stack : inventory.items) {
            if (!stack.isEmpty()) {
                return stack;
            }
        }

        return null;
    }
}
