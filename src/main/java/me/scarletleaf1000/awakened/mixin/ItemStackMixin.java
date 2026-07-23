package me.scarletleaf1000.awakened.mixin;

import me.scarletleaf1000.awakened.item.AwakenedItemData;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Inject(method = "getMaxDamage", at = @At("RETURN"), cancellable = true)
    private void awakened$addDurabilityBonus(CallbackInfoReturnable<Integer> callback) {
        int bonus = AwakenedItemData.getDurabilityBonus((ItemStack) (Object) this);
        if (bonus > 0) {
            callback.setReturnValue(callback.getReturnValue() + bonus);
        }
    }
}
