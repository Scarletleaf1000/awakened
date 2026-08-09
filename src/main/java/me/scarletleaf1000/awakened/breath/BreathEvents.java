package me.scarletleaf1000.awakened.breath;

import me.scarletleaf1000.awakened.Awakened;
import me.scarletleaf1000.awakened.Config;
import me.scarletleaf1000.awakened.attribute.ModAttributes;
import me.scarletleaf1000.awakened.data.NightbloodCraftedData;
import me.scarletleaf1000.awakened.heightening.Heightening;
import me.scarletleaf1000.awakened.heightening.HeighteningEffects;
import me.scarletleaf1000.awakened.item.AwakenedItemData;
import me.scarletleaf1000.awakened.item.ItemBreathStorage;
import me.scarletleaf1000.awakened.item.NightbloodSwordItem;
import me.scarletleaf1000.awakened.network.BreathNetwork;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = Awakened.MOD_ID)
public class BreathEvents {
    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof LivingEntity living) {
            event.addCapability(BreathProvider.ID, new BreathProvider(living));
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        copyBreath(event.getOriginal(), event.getEntity());
        if (event.getOriginal().getPersistentData().getBoolean("awakened:breath_init")) {
            event.getEntity().getPersistentData().putBoolean("awakened:breath_init", true);
        }
    }

    private static void copyBreath(LivingEntity from, LivingEntity to) {
        AttributeInstance attr = from.getAttribute(BreathAttributes.BREATH.get());
        int breath = attr == null ? 1 : (int) attr.getBaseValue();
        to.getCapability(BreathProvider.BREATH).ifPresent(b -> b.setBreath(breath));
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            migrateFromLegacyNBT(player);
            if (!player.getPersistentData().getBoolean("awakened:breath_init")) {
                int breath = player.getRandom().nextInt(10); // 0-9
                player.getCapability(BreathProvider.BREATH).ifPresent(b -> b.setBreath(breath));
                player.getPersistentData().putBoolean("awakened:breath_init", true);
            }
            player.getCapability(BreathProvider.BREATH).ifPresent(breath -> {
                BreathNetwork.sendToPlayer(player, breath.getBreath());
            });
            ServerDrabShaderState.sync(player);
            NightbloodCraftedData craftedData = NightbloodCraftedData.get(player.server.overworld());
            BreathNetwork.sendNightbloodSync(player, craftedData.getCount(), Config.MAX_NIGHTBLOODS.get());
        }
    }

    @SubscribeEvent
    public static void onItemPickup(EntityItemPickupEvent event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }
        if (!event.getItem().getItem().is(Awakened.NIGHTBLOOD.get())) {
            return;
        }
        event.getEntity().sendSystemMessage(Component.translatable("message.awakened.nightblood.pickup_greeting")
                .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }
        if (!(event.getEntity() instanceof LivingEntity living) || living instanceof Player) {
            return;
        }
        migrateFromLegacyNBT(living);
        if (living.getPersistentData().getBoolean("awakened:breath_init")) {
            return;
        }
        int breath = determineInitialBreath(living);
        living.getCapability(BreathProvider.BREATH).ifPresent(b -> b.setBreath(breath));
        living.getPersistentData().putBoolean("awakened:breath_init", true);
    }

    private static void migrateFromLegacyNBT(LivingEntity entity) {
        CompoundTag nbt = entity.saveWithoutId(new CompoundTag());
        if (!nbt.contains("ForgeCaps", Tag.TAG_COMPOUND)) {
            return;
        }
        CompoundTag forgeCaps = nbt.getCompound("ForgeCaps");
        if (!forgeCaps.contains("awakened:breath", Tag.TAG_COMPOUND)) {
            return;
        }
        CompoundTag capTag = forgeCaps.getCompound("awakened:breath");
        if (!capTag.contains("breath", Tag.TAG_INT)) {
            return;
        }
        int legacyBreath = capTag.getInt("breath");
        entity.getCapability(BreathProvider.BREATH).ifPresent(b -> {
            if (b instanceof BreathCapability cap) {
                cap.setBreathInternal(legacyBreath);
            } else {
                b.setBreath(legacyBreath);
            }
        });
    }

    private static int determineInitialBreath(LivingEntity entity) {
        if (entity.getMobType() == MobType.UNDEAD) {
            return 0; // Drab
        }
        if (entity instanceof Animal || entity instanceof WaterAnimal) {
            return 0; // Drab
        }
        if (entity instanceof Evoker) {
            return 150; // Fifth Heightening
        }
        if (entity instanceof PiglinBrute) {
            return 100; // Fourth Heightening
        }
        if (canSpawnWithMultipleBreath(entity)) {
            double roll = entity.getRandom().nextDouble();
            if (roll < 0.01) {
                return 75;
            }
            if (roll < 0.01 + 0.60) {
                return entity.getRandom().nextInt(10); // 0-9
            }
            return 10 + entity.getRandom().nextInt(41); // 10-50
        }
        return 0; // Drab
    }

    public static boolean canSpawnWithMultipleBreath(LivingEntity entity) {
        return entity instanceof AbstractVillager
                || entity instanceof AbstractIllager
                || entity instanceof AbstractPiglin;
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }
        LivingEntity victim = event.getEntity();
        Entity killer = event.getSource().getEntity();

        if (victim instanceof Player) {
            int victimBreath = victim.getCapability(BreathProvider.BREATH)
                    .map(IBreath::getBreath)
                    .orElse(0);
            if (Heightening.fromBreath(victimBreath) == Heightening.DRAB) {
                return;
            }

            if (killer instanceof Player killerPlayer) {
                int transfer = Math.max(1, victimBreath / 5);
                ItemStack mainHand = killerPlayer.getMainHandItem();
                if (mainHand.is(Awakened.NIGHTBLOOD.get())) {
                    int totalStolen = Math.round(transfer * 1.5f);
                    int nightbloodShare = totalStolen - transfer;
                    victim.getCapability(BreathProvider.BREATH).ifPresent(b -> b.removeBreath(totalStolen));
                    killerPlayer.getCapability(BreathProvider.BREATH).ifPresent(b -> b.addBreath(transfer));
                    addStoredBreathToNightblood(mainHand, nightbloodShare);
                } else {
                    victim.getCapability(BreathProvider.BREATH).ifPresent(b -> b.removeBreath(transfer));
                    killerPlayer.getCapability(BreathProvider.BREATH).ifPresent(b -> b.addBreath(transfer));
                }
            } else {
                int loss = Math.min(25, Math.max(1, victimBreath / 20));
                victim.getCapability(BreathProvider.BREATH).ifPresent(b -> b.removeBreath(loss));
            }
        } else if (killer instanceof Player killerPlayer) {
            int victimBreath = victim.getCapability(BreathProvider.BREATH)
                    .map(IBreath::getBreath)
                    .orElse(0);
            int transfer = Math.round((victimBreath * (float) victimBreath) / 500f);
            if (transfer <= 0) {
                return;
            }
            ItemStack mainHand = killerPlayer.getMainHandItem();
            if (mainHand.is(Awakened.NIGHTBLOOD.get())) {
                int totalStolen = Math.round(transfer * 1.5f);
                int nightbloodShare = totalStolen - transfer;
                victim.getCapability(BreathProvider.BREATH).ifPresent(b -> b.removeBreath(totalStolen));
                killerPlayer.getCapability(BreathProvider.BREATH).ifPresent(b -> b.addBreath(transfer));
                addStoredBreathToNightblood(mainHand, nightbloodShare);
            } else {
                victim.getCapability(BreathProvider.BREATH).ifPresent(b -> b.removeBreath(transfer));
                killerPlayer.getCapability(BreathProvider.BREATH).ifPresent(b -> b.addBreath(transfer));
            }
        }
    }

    private static void addStoredBreathToNightblood(ItemStack nightblood, int amount) {
        int current = NightbloodSwordItem.getStoredBreath(nightblood);
        NightbloodSwordItem.setStoredBreath(nightblood, current + amount);
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.side == LogicalSide.SERVER && event.phase == TickEvent.Phase.END) {
            HeighteningEffects.tickPlayer(event.player);
        }
    }

    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        AttributeInstance attribute = event.getEntity().getAttribute(ModAttributes.MINING_SPEED.get());
        if (attribute != null) {
            event.setNewSpeed((float) (event.getNewSpeed() * attribute.getValue()));
        }
    }

    @SubscribeEvent
    public static void onRightClickItem(net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        if (!player.isShiftKeyDown()) {
            return;
        }
        ItemStack stack = event.getItemStack();
        if (stack.is(Awakened.NIGHTBLOOD.get())) {
            return;
        }
        if (AwakenedItemData.isAwakened(stack)) {
            UUID owner = AwakenedItemData.getOwner(stack);
            if (owner != null && !player.getUUID().equals(owner)) {
                return;
            }
            if (!event.getLevel().isClientSide()) {
                int breath = AwakenedItemData.remove(stack);
                player.getCapability(BreathProvider.BREATH).ifPresent(b -> b.addBreath(breath));
            }
        } else {
            if (!ItemBreathStorage.hasStoredBreath(stack) || !ItemBreathStorage.isOwnerOrPublic(stack, player)) {
                return;
            }
            if (!event.getLevel().isClientSide()) {
                int breath = ItemBreathStorage.getStoredBreath(stack);
                player.getCapability(BreathProvider.BREATH).ifPresent(b -> b.addBreath(breath));
                ItemBreathStorage.removeStoredBreath(stack);
            }
        }

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide()));
    }
}
