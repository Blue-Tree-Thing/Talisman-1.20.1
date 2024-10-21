package net.bluetree.talisman.blocks.entity;

import net.bluetree.talisman.blocks.ModBlockEntities;
import net.bluetree.talisman.entities.ModEntities;
import net.bluetree.talisman.entities.custom.DiligentGuardEntity;
import net.bluetree.talisman.screen.VirtueAltarScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static net.bluetree.talisman.items.ModItems.ASPECT_OF_DILIGENCE;

public class VirtueAltarBlockEntity extends BlockEntity implements NamedScreenHandlerFactory, Inventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
    private boolean guardSpawned;
    private UUID boundGuardUUID;

    public VirtueAltarBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.VIRTUE_ALTAR_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, this.inventory);
        this.guardSpawned = nbt.getBoolean("GuardSpawned");
        if (nbt.containsUuid("BoundGuardUUID")) {
            this.boundGuardUUID = nbt.getUuid("BoundGuardUUID");
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, this.inventory);
        nbt.putBoolean("GuardSpawned", this.guardSpawned);
        if (boundGuardUUID != null) {
            nbt.putUuid("BoundGuardUUID", boundGuardUUID);
        }
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("Virtue Altar");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, net.minecraft.entity.player.PlayerInventory inv, PlayerEntity player) {
        return new VirtueAltarScreenHandler(syncId, inv, this);
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return player.squaredDistanceTo(Vec3d.ofCenter(this.pos)) < 64.0;
    }

    @Override
    public int size() {
        return inventory.size();
    }

    @Override
    public boolean isEmpty() {
        return inventory.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getStack(int slot) {
        return inventory.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack removedStack = Inventories.splitStack(this.inventory, slot, amount);
        handleItemChange();
        return removedStack;
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStack removedStack = Inventories.removeStack(this.inventory, slot);
        handleItemChange();
        return removedStack;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        inventory.set(slot, stack);
        if (stack.getCount() > this.getMaxCountPerStack()) {
            stack.setCount(this.getMaxCountPerStack());
        }
        handleItemChange();
    }

    @Override
    public void clear() {
        inventory.clear();
        handleItemChange();
    }

    private void handleItemChange() {
        if (this.world instanceof ServerWorld serverWorld) {
            ItemStack stack = inventory.get(0);

            if (!stack.isEmpty() && stack.getItem() == ASPECT_OF_DILIGENCE && !guardSpawned) {
                spawnDiligentGuard(serverWorld);
                guardSpawned = true;
                markDirty();
            } else if (stack.isEmpty() && guardSpawned) {
                despawnDiligentGuard();
                guardSpawned = false;
                markDirty();
            }
        }
    }

    private void spawnDiligentGuard(ServerWorld world) {
        DiligentGuardEntity guard = new DiligentGuardEntity(ModEntities.DILIGENT_GUARD, world);
        guard.refreshPositionAndAngles(
                this.pos.getX() + 0.5,
                this.pos.getY() + 1.0,
                this.pos.getZ() + 0.5,
                0.0F,
                0.0F
        );
        guard.setHomePos(this.pos);
        world.spawnEntity(guard);
        this.boundGuardUUID = guard.getUuid();
        System.out.println("DEBUG: Diligent Guard spawned.");
    }

    private void despawnDiligentGuard() {
        if (this.world instanceof ServerWorld serverWorld) {
            if (boundGuardUUID != null) {
                var entity = serverWorld.getEntity(boundGuardUUID);
                if (entity != null) {
                    entity.discard();
                    boundGuardUUID = null;
                    System.out.println("DEBUG: Diligent Guard despawned via UUID.");
                    return;
                }
            }

            // Fallback to position-based check if UUID lookup fails
            for (DiligentGuardEntity entity : serverWorld.getEntitiesByType(
                    ModEntities.DILIGENT_GUARD,
                    e -> e.isAlive() && e.getHomePos().equals(this.pos)
            )) {
                entity.discard();
                System.out.println("DEBUG: Diligent Guard despawned via position check.");
                break;
            }
        }
    }

    public void markForDespawn() {
        assert this.world != null;
        if (!this.world.isClient) {
            despawnDiligentGuard();
            this.guardSpawned = false;
            this.markDirty();
        }
    }
}