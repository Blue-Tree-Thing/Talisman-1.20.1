package net.bluetree.talisman.blocks.custom;

import net.bluetree.talisman.blocks.entity.VirtueAltarBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class VirtueAltarBlock extends Block implements BlockEntityProvider {
    private static final VoxelShape BASE_SHAPE = Block.createCuboidShape(0, 0, 0, 16, 4, 16);
    private static final VoxelShape TOP_SHAPE = Block.createCuboidShape(1, 4, 1, 15, 6, 15);
    private static final VoxelShape FULL_SHAPE = VoxelShapes.union(BASE_SHAPE, TOP_SHAPE);

    public VirtueAltarBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new VirtueAltarBlockEntity(pos, state);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof VirtueAltarBlockEntity virtueAltar) {
                virtueAltar.markForDespawn();
                // Drop inventory contents
                if (world instanceof net.minecraft.world.World) {
                    Block.dropStacks(state, world, pos, blockEntity);
                }
            }
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof VirtueAltarBlockEntity) {
                player.openHandledScreen((VirtueAltarBlockEntity) blockEntity);
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return FULL_SHAPE;
    }
}