package com.smashingmods.alchemylib.api.blockentity.container;

import com.mojang.datafixers.util.Function4;
import com.smashingmods.alchemylib.AlchemyLib;
import com.smashingmods.alchemylib.api.blockentity.processing.AbstractProcessingBlockEntity;
import com.smashingmods.alchemylib.api.network.BlockEntityPacket;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

@SuppressWarnings("unused")
public abstract class AbstractProcessingMenu extends AbstractContainerMenu {

    private final AbstractProcessingBlockEntity blockEntity;
    private final Level level;
    private final int inputSlots;
    private final int outputSlots;

    protected AbstractProcessingMenu(MenuType<?> pMenuType, int pContainerId, Inventory pInventory, BlockEntity pBlockEntity, int pInputSlots, int pOutputSlots) {
        super(pMenuType, pContainerId);

        this.inputSlots = pInputSlots;
        this.outputSlots = pOutputSlots;
        this.blockEntity = ((AbstractProcessingBlockEntity) pBlockEntity);
        this.level = pInventory.player.level;

        addPlayerInventorySlots(pInventory);
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        AlchemyLib.getPacketHandler().sendToTrackingChunk(new BlockEntityPacket(getBlockEntity().getBlockPos(), getBlockEntity().getUpdateTag()), getLevel(), getBlockEntity().getBlockPos());
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        int blockEntitySlots = inputSlots + outputSlots;
        Slot sourceSlot = slots.get(pIndex);
        if (!sourceSlot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyStack = sourceStack.copy();

        if (pIndex < 36) {
            if (!moveItemStackTo(sourceStack, 36, 36 + inputSlots, false)) {
                return ItemStack.EMPTY;
            }
        } else if (pIndex < 36 + inputSlots) {
            if (!moveItemStackTo(sourceStack, 0, 36, false))  {
                return ItemStack.EMPTY;
            }
        } else if (pIndex >= 36 + inputSlots && pIndex < 36 + blockEntitySlots) {
            if (!moveItemStackTo(sourceStack, 0, 36, true)) {
                return ItemStack.EMPTY;
            }
        } else {
            return ItemStack.EMPTY;
        }

        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(pPlayer, sourceStack);
        return copyStack;
    }

    protected <T> void addSlots(Function4<T, Integer, Integer, Integer, Slot> pSlotType, T pContainer, int pXOrigin, int pYOrigin) {
        addSlots(pSlotType, pContainer, 1, 1, 0, 1, pXOrigin, pYOrigin);
    }

    protected <T> void addSlots(Function4<T, Integer, Integer, Integer, Slot> pSlotType, T pContainer, int pStartIndex, int pTotalSlots, int pXOrigin, int pYOrigin) {
        addSlots(pSlotType, pContainer, 1, 1, pStartIndex, pTotalSlots, pXOrigin, pYOrigin);
    }

    protected <T> void addSlots(Function4<T, Integer, Integer, Integer, Slot> pSlotType, T pContainer, int pRows, int pColumns, int pStartIndex, int pTotalSlots, int pXOrigin, int pYOrigin) {

        for (int row = 0; row < pRows; row++) {
            for (int column = 0; column < pColumns; column++) {
                int slotIndex = column + row * pColumns + pStartIndex;
                int x = pXOrigin + column * 18;
                int y = pYOrigin + row * 18;

                if (slotIndex < pStartIndex + pTotalSlots) {
                    this.addSlot(pSlotType.apply(pContainer, slotIndex, x, y));
                }
            }
        }
    }

    public void addPlayerInventorySlots(Inventory pInventory) {
        // player main inventory
        addSlots(Slot::new, pInventory, 3, 9, 9, 27,12, 76);
        // player hotbar
        addSlots(Slot::new, pInventory, 1, 9, 0, 9,12, 134);
    }

    public AbstractProcessingBlockEntity getBlockEntity() {
        return blockEntity;
    }

    public Level getLevel() {
        return level;
    }
}
