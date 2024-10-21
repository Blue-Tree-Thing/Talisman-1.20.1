package net.bluetree.talisman.blocks;

import net.bluetree.talisman.Talisman;
import net.bluetree.talisman.blocks.entity.VirtueAltarBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {

    public static BlockEntityType<VirtueAltarBlockEntity> VIRTUE_ALTAR_BLOCK_ENTITY;

    public static void registerBlockEntities() {
        // Create the block entity type
        VIRTUE_ALTAR_BLOCK_ENTITY = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                new Identifier(Talisman.MOD_ID, "virtue_altar_be"),
                FabricBlockEntityTypeBuilder.create(VirtueAltarBlockEntity::new, ModBlocks.VIRTUE_ALTAR).build()
        );
    }
}
