package me.scarletleaf1000.awakened.client;

import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class AwakenedKeyMappings {
    public static final KeyMapping OPEN_AWAKENING = new KeyMapping(
            "key.awakened.open_awakening",
            GLFW.GLFW_KEY_APOSTROPHE,
            "key.categories.awakened"
    );
}
