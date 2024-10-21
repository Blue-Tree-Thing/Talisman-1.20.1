package net.bluetree.talisman.blocks;

import net.bluetree.talisman.Talisman;
import net.bluetree.talisman.items.ModItemGroup;
import net.bluetree.talisman.blocks.custom.VirtueAltarBlock;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {

    // Register the block and its BlockItem together
    public static final Block VIRTUE_ALTAR = registerBlock("virtue_altar",
            new VirtueAltarBlock(FabricBlockSettings.create().strength(3.0f, 3.0f))); // Strength for stone-like properties

    // Register the block with a corresponding BlockItem for in-game access
    private static Block registerBlock(String name, Block block) {
        // Register the block
        Block registeredBlock = Registry.register(Registries.BLOCK, new Identifier(Talisman.MOD_ID, name), block);

        // Register the BlockItem
        Registry.register(Registries.ITEM, new Identifier(Talisman.MOD_ID, name),
                new BlockItem(registeredBlock, new Item.Settings()));

        return registeredBlock;
    }

    // Method to add blocks to the custom item group
    private static void addBlocksToTalismanGroup(FabricItemGroupEntries entries) {
        entries.add(VIRTUE_ALTAR);
    }

    public static void registerModBlocks() {
        // Register blocks to the custom item group
        ItemGroupEvents.modifyEntriesEvent(ModItemGroup.TALISMAN_GROUP).register(ModBlocks::addBlocksToTalismanGroup);
    }
}
