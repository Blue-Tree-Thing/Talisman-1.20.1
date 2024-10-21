package net.bluetree.talisman.items;

import net.bluetree.talisman.Talisman;
import net.bluetree.talisman.entities.ModEntities;
import net.bluetree.talisman.items.custom.*;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {

    public static final Item BLANK_TALISMAN = registerItem("blank_talisman", new Item(new FabricItemSettings()));
    public static final Item ASPECT_OF_DILIGENCE = registerItem("aspect_of_diligence", new Item(new FabricItemSettings()));
    public static final Item FERROUS_HEART = registerItem("ferrous_heart", new Item(new FabricItemSettings()));
    public static final Item DARK_ESSENCE = registerItem("dark_essence", new Item(new FabricItemSettings()));
    public static final Item OOZE_TALISMAN = registerItem("ooze_talisman", new OozeTalismanItem(new FabricItemSettings()));
    public static final Item SLUDGE_TALISMAN = registerItem("sludge_talisman", new SludgeTalismanItem(new FabricItemSettings()));
    public static final Item SHADOW_HAND_TALISMAN = registerItem("shadow_hand_talisman", new ShadowHandTalismanItem(new FabricItemSettings()));
    public static final Item LOST_DILIGENCE_TALISMAN = registerItem("lost_diligence_talisman",
            new LostDiligenceTalismanItem(new FabricItemSettings(), ModEntities.LOST_DILIGENCE));

    public static final Item GUMGAR_SPAWN_EGG = registerItem("gumgar_spawn_egg",
            new GumgarSpawnItem(new FabricItemSettings()));

    // Register items for the custom item group
    private static void addItemsToTalismanGroup(FabricItemGroupEntries entries) {
        entries.add(BLANK_TALISMAN);
        entries.add(ASPECT_OF_DILIGENCE);
        entries.add(FERROUS_HEART);
        entries.add(DARK_ESSENCE);
        entries.add(OOZE_TALISMAN);
        entries.add(SLUDGE_TALISMAN);
        entries.add(SHADOW_HAND_TALISMAN);
        entries.add(GUMGAR_SPAWN_EGG);
        entries.add(LOST_DILIGENCE_TALISMAN);
    }

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(Talisman.MOD_ID, name), item);
    }

    public static void registerModItems() {
        // Register items to the custom item group using the RegistryKey
        ItemGroupEvents.modifyEntriesEvent(ModItemGroup.TALISMAN_GROUP).register(ModItems::addItemsToTalismanGroup);
    }
}