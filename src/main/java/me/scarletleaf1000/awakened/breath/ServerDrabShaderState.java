package me.scarletleaf1000.awakened.breath;

import me.scarletleaf1000.awakened.network.BreathNetwork;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public final class ServerDrabShaderState {
    private static boolean disabled = false;

    private ServerDrabShaderState() {
    }

    public static boolean isDisabled() {
        return disabled;
    }

    public static void toggle(MinecraftServer server) {
        disabled = !disabled;
        sync(server);
    }

    public static void sync(ServerPlayer player) {
        BreathNetwork.sendDrabShaderToggle(player, disabled);
    }

    public static void sync(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            sync(player);
        }
    }
}
