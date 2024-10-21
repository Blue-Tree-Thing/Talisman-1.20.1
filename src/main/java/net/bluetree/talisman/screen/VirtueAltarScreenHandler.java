package net.bluetree.talisman.screen;

import net.bluetree.talisman.blocks.entity.VirtueAltarBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

import static net.bluetree.talisman.items.ModItems.ASPECT_OF_DILIGENCE;

public class VirtueAltarScreenHandler extends ScreenHandler {

    private final Inventory inventory;

    // Custom slot class to only allow ASPECT_OF_DILIGENCE
    private static class AltarSlot extends Slot {
        public AltarSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            // Only allow ASPECT_OF_DILIGENCE to be inserted
            return stack.getItem() == ASPECT_OF_DILIGENCE;
        }

        @Override
        public void setStack(ItemStack stack) {
            super.setStack(stack);
            // Debug message when ASPECT_OF_DILIGENCE is inserted
            if (stack.getItem() == ASPECT_OF_DILIGENCE) {
                System.out.println("ASPECT_OF_DILIGENCE inserted into Altar slot.");
            }
        }

        @Override
        public void onTakeItem(PlayerEntity player, ItemStack stack) {
            super.onTakeItem(player, stack);
            // Debug message when item is removed from the slot
            System.out.println("ASPECT_OF_DILIGENCE removed from Altar slot.");
        }
    }

    // Primary constructor with VirtueAltarBlockEntity, used in gameplay
    public VirtueAltarScreenHandler(int syncId, PlayerInventory playerInventory, VirtueAltarBlockEntity blockEntity) {
        super(ModScreenHandlers.VIRTUE_ALTAR_SCREEN_HANDLER, syncId);
        this.inventory = blockEntity;

        // Use the custom AltarSlot for the top slot
        this.addSlot(new AltarSlot(inventory, 0, 80, 4)); // Adjusted to move up by a third of a slot

        // Move player inventory slots up by a third of a slot (6 pixels)
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18)); // Adjusted to start at y: 84
            }
        }

        // Move player hotbar slots up by a third of a slot (6 pixels)
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142)); // Adjusted to y: 142
        }
    }

    // Secondary constructor for ScreenHandlerType registration only
    public VirtueAltarScreenHandler(int syncId, PlayerInventory playerInventory) {
        super(ModScreenHandlers.VIRTUE_ALTAR_SCREEN_HANDLER, syncId);
        this.inventory = new SimpleInventory(1); // Minimal setup for registration
        this.addSlot(new AltarSlot(this.inventory, 0, 80, 4)); // Adjusted altar slot

        // Adjusted player inventory slots
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        // Adjusted player hotbar slots
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasStack()) {
            return ItemStack.EMPTY;
        }

        ItemStack newStack = slot.getStack();
        ItemStack originalStack = newStack.copy();

        if (index == 0) { // If moving from altar slot to player inventory
            if (!this.insertItem(newStack, 1, 37, true)) {
                return ItemStack.EMPTY;
            }
        } else { // If moving from player inventory to altar slot
            if (!this.insertItem(newStack, 0, 1, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (newStack.isEmpty()) {
            slot.setStack(ItemStack.EMPTY);
        } else {
            slot.markDirty();
        }

        if (newStack.getCount() == originalStack.getCount()) {
            return ItemStack.EMPTY;
        }

        slot.onTakeItem(player, newStack);
        return originalStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }
}
