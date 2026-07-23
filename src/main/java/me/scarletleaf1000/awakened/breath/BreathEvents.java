package me.scarletleaf1000.awakened.breath;

import me.scarletleaf1000.awakened.Awakened;
import me.scarletleaf1000.awakened.heightening.Heightening;
import me.scarletleaf1000.awakened.heightening.HeighteningEffects;
import me.scarletleaf1000.awakened.item.AwakenedItemData;
import me.scarletleaf1000.awakened.item.ItemBreathStorage;
import me.scarletleaf1000.awakened.network.BreathNetwork;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

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
        event.getOriginal().getCapability(BreathProvider.BREATH).ifPresent(old -> {
            event.getEntity().getCapability(BreathProvider.BREATH).ifPresent(newCap -> {
                newCap.setBreath(old.getBreath());
            });
        });
        if (event.getOriginal().getPersistentData().getBoolean("awakened:breath_init")) {
            event.getEntity().getPersistentData().putBoolean("awakened:breath_init", true);
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (!player.getPersistentData().getBoolean("awakened:breath_init")) {
                int breath = player.getRandom().nextInt(10); // 0-9
                player.getCapability(BreathProvider.BREATH).ifPresent(b -> b.setBreath(breath));
                player.getPersistentData().putBoolean("awakened:breath_init", true);
            }
            player.getCapability(BreathProvider.BREATH).ifPresent(breath -> {
                BreathNetwork.sendToPlayer(player, breath.getBreath());
            });
        }
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }
        if (!(event.getEntity() instanceof LivingEntity living) || living instanceof Player) {
            return;
        }
        if (living.getPersistentData().getBoolean("awakened:breath_init")) {
            return;
        }
        int breath = determineInitialBreath(living);
        living.getCapability(BreathProvider.BREATH).ifPresent(b -> b.setBreath(breath));
        living.getPersistentData().putBoolean("awakened:breath_init", true);
    }

    private static int determineInitialBreath(LivingEntity entity) {
        if (entity.getMobType() == MobType.UNDEAD) {
            return 0; // Drab
        }
        if (entity instanceof Animal || entity instanceof WaterAnimal) {
            return 1;
        }
        if (entity instanceof Evoker) {
            return 150; // Fifth Heightening
        }
        if (entity instanceof AbstractVillager || entity instanceof AbstractIllager) {
            double roll = entity.getRandom().nextDouble();
            if (roll < 0.01) {
                return 75;
            }
            if (roll < 0.01 + 0.60) {
                return entity.getRandom().nextInt(10); // 0-9
            }
            return 10 + entity.getRandom().nextInt(41); // 10-50
        }
        return entity.getRandom().nextInt(2); // 0 or 1
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
                victim.getCapability(BreathProvider.BREATH).ifPresent(b -> b.removeBreath(transfer));
                killerPlayer.getCapability(BreathProvider.BREATH).ifPresent(b -> b.addBreath(transfer));
            } else {
                int loss = Math.min(25, Math.max(1, victimBreath / 10));
                victim.getCapability(BreathProvider.BREATH).ifPresent(b -> b.removeBreath(loss));
            }
        } else if (killer instanceof Player killerPlayer) {
            int victimBreath = victim.getCapability(BreathProvider.BREATH)
                    .map(IBreath::getBreath)
                    .orElse(0);
            if (victimBreath < 20) {
                return;
            }
            int transfer = victimBreath / 20;
            victim.getCapability(BreathProvider.BREATH).ifPresent(b -> b.removeBreath(transfer));
            killerPlayer.getCapability(BreathProvider.BREATH).ifPresent(b -> b.addBreath(transfer));
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.side == LogicalSide.SERVER && event.phase == TickEvent.Phase.END) {
            HeighteningEffects.tickPlayer(event.player);
        }
    }

    @SubscribeEvent
    public static void onRightClickItem(net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        if (!player.isShiftKeyDown()) {
            return;
        }
        ItemStack stack = event.getItemStack();
        if (AwakenedItemData.isAwakened(stack)) {
            if (!player.getUUID().equals(AwakenedItemData.getOwner(stack))) {
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
