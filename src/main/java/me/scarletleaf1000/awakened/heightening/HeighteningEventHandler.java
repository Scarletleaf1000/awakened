package me.scarletleaf1000.awakened.heightening;

import me.scarletleaf1000.awakened.Awakened;
import me.scarletleaf1000.awakened.breath.BreathProvider;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Handles event-based heightening effects. Poison/Wither immunity is
 * handled every tick in {@link HeighteningEffects}.
 */
@Mod.EventBusSubscriber(modid = Awakened.MOD_ID)
public class HeighteningEventHandler {

    /**
     * Fifth Heightening+: allows eating food even while the hunger bar is full.
     */
    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        ItemStack stack = event.getItemStack();
        FoodProperties food = stack.getFoodProperties(player);
        if (food == null || food.canAlwaysEat() || player.canEat(false)) {
            return;
        }

        boolean canEatWhileFull = player.getCapability(BreathProvider.BREATH)
                .map(breath -> Heightening.fromBreath(breath.getBreath()).ordinal() >= Heightening.FIFTH.ordinal())
                .orElse(false);
        if (!canEatWhileFull) {
            return;
        }

        event.setCancellationResult(InteractionResult.CONSUME);
        event.setCanceled(true);
        player.startUsingItem(event.getHand());
    }
}
