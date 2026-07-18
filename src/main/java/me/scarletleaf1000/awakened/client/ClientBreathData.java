package me.scarletleaf1000.awakened.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Client-side cache of Breath values for nearby entities, populated by the server
 * for players with the First Heightening sight ability.
 */
public class ClientBreathData {
    private static final Map<Integer, Integer> ENTITY_BREATH = new ConcurrentHashMap<>();

    public static void set(int entityId, int breath) {
        ENTITY_BREATH.put(entityId, breath);
    }

    public static int get(int entityId) {
        return ENTITY_BREATH.getOrDefault(entityId, 1);
    }

    public static void remove(int entityId) {
        ENTITY_BREATH.remove(entityId);
    }

    public static void clear() {
        ENTITY_BREATH.clear();
    }
}
