package me.scarletleaf1000.awakened.trade;

import me.scarletleaf1000.awakened.Awakened;
import me.scarletleaf1000.awakened.breath.BreathProvider;
import me.scarletleaf1000.awakened.breath.IBreath;
import me.scarletleaf1000.awakened.network.BreathNetwork;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkHooks;

@Mod.EventBusSubscriber(modid = Awakened.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class VillagerBreathTradeHandler {
    private static final int MIN_COST_PER_BREATH = 32;
    private static final int MAX_COST_PER_BREATH = 64;
    private static final int MAX_TOTAL_COST = 64 * 36; // full inventory of emeralds
    private static final String COST_TAG = "awakened:breath_trade_costs";

    @SubscribeEvent(priority = EventPriority.HIGH, receiveCanceled = true)
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }
        tryOpenTrade(event);
    }

    private static void tryOpenTrade(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getTarget() instanceof Villager villager) || !event.getEntity().isShiftKeyDown()) {
            return;
        }

        if (event.getEntity().level().isClientSide()) {
            return;
        }
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        int breath = villager.getCapability(BreathProvider.BREATH)
                .map(IBreath::getBreath)
                .orElse(0);
        if (breath <= 0) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide()));
            return;
        }

        event.setCanceled(true);
        int cost = computeCost(villager, player);
        villager.getNavigation().stop();
        villager.setNoAi(true);

        NetworkHooks.openScreen(player, new SimpleMenuProvider(
                        (id, inv, p) -> new VillagerBreathTradeMenu(Awakened.VILLAGER_BREATH_TRADE_MENU.get(),
                                id, inv, villager.getId(), breath, cost),
                        Component.translatable("gui.awakened.trader.title")),
                buf -> {
                    buf.writeInt(villager.getId());
                    buf.writeInt(breath);
                    buf.writeInt(cost);
                });
        event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide()));
    }

    public static void handleTradeConfirm(ServerPlayer player, int villagerId) {
        Entity entity = player.serverLevel().getEntity(villagerId);
        if (!(entity instanceof Villager villager) || !villager.isAlive()) {
            BreathNetwork.sendVillagerTradeResult(player, false);
            return;
        }

        int breath = villager.getCapability(BreathProvider.BREATH)
                .map(IBreath::getBreath)
                .orElse(0);
        if (breath <= 0) {
            BreathNetwork.sendVillagerTradeResult(player, false);
            return;
        }

        int cost = computeCost(villager, player);
        if (!player.isCreative() && countEmeralds(player) < cost) {
            BreathNetwork.sendVillagerTradeResult(player, false);
            return;
        }

        removeEmeralds(player, cost);
        player.getCapability(BreathProvider.BREATH).ifPresent(b -> b.addBreath(breath));
        villager.getCapability(BreathProvider.BREATH).ifPresent(b -> b.setBreath(0));
        villager.getPersistentData().remove(COST_TAG);

        BreathNetwork.sendVillagerTradeResult(player, true);
    }

    public static int countEmeralds(Player player) {
        int count = 0;
        for (ItemStack stack : player.getInventory().items) {
            if (stack.is(Items.EMERALD)) {
                count += stack.getCount();
            }
        }
        ItemStack offhand = player.getOffhandItem();
        if (offhand.is(Items.EMERALD)) {
            count += offhand.getCount();
        }
        return count;
    }

    private static int computeCost(Villager villager, Player player) {
        int[] baseCosts = getOrCreateBaseCosts(villager);
        int discountLevel = 0;
        MobEffectInstance hero = player.getEffect(MobEffects.HERO_OF_THE_VILLAGE);
        if (hero != null) {
            discountLevel = hero.getAmplifier() + 1;
        }

        int total = 0;
        for (int base : baseCosts) {
            total += Math.max(12, base - 8 * discountLevel);
        }
        return Math.min(MAX_TOTAL_COST, total);
    }

    private static int[] getOrCreateBaseCosts(Villager villager) {
        int breath = villager.getCapability(BreathProvider.BREATH)
                .map(IBreath::getBreath)
                .orElse(0);

        CompoundTag tag = villager.getPersistentData();
        if (tag.contains(COST_TAG, Tag.TAG_INT_ARRAY)) {
            int[] existing = tag.getIntArray(COST_TAG);
            if (existing.length == breath) {
                return existing;
            }
        }

        int[] costs = new int[breath];
        for (int i = 0; i < breath; i++) {
            costs[i] = MIN_COST_PER_BREATH + villager.getRandom().nextInt(MAX_COST_PER_BREATH - MIN_COST_PER_BREATH + 1);
        }
        tag.putIntArray(COST_TAG, costs);
        return costs;
    }

    private static void removeEmeralds(Player player, int amount) {
        if (player.isCreative() || amount <= 0) {
            return;
        }
        amount = consumeEmeralds(player.getOffhandItem(), amount);
        if (amount <= 0) {
            return;
        }
        for (ItemStack stack : player.getInventory().items) {
            amount = consumeEmeralds(stack, amount);
            if (amount <= 0) {
                break;
            }
        }
    }

    private static int consumeEmeralds(ItemStack stack, int amount) {
        if (stack.isEmpty() || !stack.is(Items.EMERALD)) {
            return amount;
        }
        int remove = Math.min(amount, stack.getCount());
        stack.shrink(remove);
        return amount - remove;
    }
}
