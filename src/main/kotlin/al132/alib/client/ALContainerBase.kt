package al132.alib.client

import al132.alib.tiles.ALTile
import al132.alib.tiles.IGuiTile
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.SlotItemHandler

abstract class ALContainerBase<T>(playerInv: IInventory,
                                  open val tile: T) :
        Container() where T : IGuiTile, T : ALTile {

    init {
        addOwnSlots()
        addPlayerSlots(playerInv)
    }

    abstract fun addOwnSlots()

    fun addSlotArray(startingIndex: Int = 0, x: Int, y: Int, rows: Int = 1, columns: Int = 1, handler: IItemHandler) {
        val initialX = x
        var x = x
        var y = y

        var index = startingIndex
        for (row in 1..rows) {
            for (column in 1..columns) {
                this.addSlotToContainer(SlotItemHandler(handler, index, x, y))
                x += 18
                index++
            }
            x = initialX
            y += 18
        }
    }


    fun addPlayerSlots(playerInventory: IInventory) {
        for (row in 0..2) {
            for (col in 0..8) {
                val x = 8 + col * 18
                val y = row * 18 + tile.guiHeight - 82
                this.addSlotToContainer(Slot(playerInventory, col + row * 9 + 9, x, y))
            }
        }

        for (row in 0..8) {
            val x = 8 + row * 18
            val y = tile.guiHeight - 24
            this.addSlotToContainer(Slot(playerInventory, row, x, y))
        }
    }

    override fun canInteractWith(playerIn: EntityPlayer): Boolean = tile.canInteractWith(playerIn)


    override fun transferStackInSlot(playerIn: EntityPlayer?, index: Int): ItemStack {
        var itemstack = ItemStack.EMPTY
        val slot = this.inventorySlots[index]
        if (slot != null && slot.hasStack) {
            val itemstack1 = slot.stack
            itemstack = itemstack1.copy()

            if (index < tile.SIZE) {
                if (!this.mergeItemStack(itemstack1, tile.SIZE, this.inventorySlots.size, true)) {
                    return ItemStack.EMPTY!!
                }
            } else if (!this.mergeItemStack(itemstack1, 0, tile.SIZE, false)) return ItemStack.EMPTY!!

            if (itemstack1.count <= 0) slot.putStack(ItemStack.EMPTY)
            else slot.onSlotChanged()
        }
        return itemstack!!
    }
}