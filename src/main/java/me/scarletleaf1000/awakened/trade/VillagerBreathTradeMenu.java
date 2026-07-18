package me.scarletleaf1000.awakened.trade;

import me.scarletleaf1000.awakened.Awakened;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class VillagerBreathTradeMenu extends AbstractContainerMenu {
    private final int villagerId;
    private final int breathCount;
    private final int cost;

    public VillagerBreathTradeMenu(int id, Inventory playerInventory, FriendlyByteBuf data) {
        this(Awakened.VILLAGER_BREATH_TRADE_MENU.get(), id, playerInventory,
                data.readInt(), data.readInt(), data.readInt());
    }

    public VillagerBreathTradeMenu(MenuType<?> type, int id, Inventory playerInventory,
                                   int villagerId, int breathCount, int cost) {
        super(type, id);
        this.villagerId = villagerId;
        this.breathCount = breathCount;
        this.cost = cost;
        addInventorySlots(playerInventory);
    }

    private void addInventorySlots(Inventory playerInventory) {
        // Main inventory
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        // Hotbar
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return player.isAlive();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        Entity entity = player.level().getEntity(villagerId);
        if (entity instanceof Villager villager) {
            villager.setNoAi(false);
        }
    }

    public int getVillagerId() {
        return villagerId;
    }

    public int getBreathCount() {
        return breathCount;
    }

    public int getCost() {
        return cost;
    }
}
