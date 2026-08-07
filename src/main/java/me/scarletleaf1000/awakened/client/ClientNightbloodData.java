package me.scarletleaf1000.awakened.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientNightbloodData {
    private static int crafted = 0;
    private static int max = -1;

    private ClientNightbloodData() {
    }

    public static void set(int craftedCount, int maxCount) {
        crafted = craftedCount;
        max = maxCount;
    }

    public static boolean isLimitReached() {
        return max != -1 && crafted >= max;
    }
}
