package net.bluetree.talisman.items;

import net.bluetree.talisman.Talisman;
import net.bluetree.talisman.entities.ModEntities;
import net.bluetree.talisman.items.custom.OozeTalismanItem;
import net.bluetree.talisman.items.custom.ShadowHandTalismanItem;
import net.bluetree.talisman.items.custom.SludgeTalismanItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {

    public static final Item BLANK_TALISMAN = registerItem("blank_talisman", new Item(new FabricItemSettings()));
    public static final Item FERROUS_HEART = registerItem("ferrous_heart", new Item(new FabricItemSettings()));
    public static final Item DARK_ESSENCE = registerItem("dark_essence", new Item(new FabricItemSettings()));
    public static final Item OOZE_TALISMAN = registerItem("ooze_talisman", new OozeTalismanItem(new FabricItemSettings()));
    public static final Item SLUDGE_TALISMAN = registerItem("sludge_talisman", new SludgeTalismanItem(new FabricItemSettings()));
    public static final Item SHADOW_HAND_TALISMAN = registerItem("shadow_hand_talisman", new ShadowHandTalismanItem(new FabricItemSettings()));

    // Gumgar spawn egg
    public static final Item GUMGAR_SPAWN_EGG = registerItem("gumgar_spawn_egg",
            new SpawnEggItem(ModEntities.GUMGAR, 0x4A4A4A, 0xFFD700, new FabricItemSettings()));

    public static final Item LOST_DILIGENCE_SPAWN_EGG = registerItem("lost_diligence_spawn_egg",
            new SpawnEggItem(ModEntities.LOST_DILIGENCE, 0x4A4A4A, 0xFFD700, new FabricItemSettings()));


    // Register items for the custom item group
    private static void addItemsToTalismanGroup(FabricItemGroupEntries entries) {
        entries.add(BLANK_TALISMAN);
        entries.add(FERROUS_HEART);
        entries.add(DARK_ESSENCE);
        entries.add(OOZE_TALISMAN);
        entries.add(SLUDGE_TALISMAN);
        entries.add(SHADOW_HAND_TALISMAN);
        entries.add(GUMGAR_SPAWN_EGG);
        entries.add(LOST_DILIGENCE_SPAWN_EGG);
    }

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(Talisman.MOD_ID, name), item);
    }

    public static void registerModItems() {
        // Register items to the custom item group using the RegistryKey
        ItemGroupEvents.modifyEntriesEvent(ModItemGroup.TALISMAN_GROUP).register(ModItems::addItemsToTalismanGroup);
    }
}