package me.scarletleaf1000.awakened.data;

import me.scarletleaf1000.awakened.Awakened;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public class NightbloodCraftedData extends SavedData {
    private static final String NAME = Awakened.MOD_ID + "_nightblood_crafted";

    private int count;

    public NightbloodCraftedData() {
    }

    public static NightbloodCraftedData load(CompoundTag tag) {
        NightbloodCraftedData data = new NightbloodCraftedData();
        data.count = tag.getInt("Count");
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putInt("Count", count);
        return tag;
    }

    public int getCount() {
        return count;
    }

    public void increment() {
        count++;
        setDirty();
    }

    public static NightbloodCraftedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(NightbloodCraftedData::load, NightbloodCraftedData::new, NAME);
    }
}
