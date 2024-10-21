package net.bluetree.talisman.items;

import net.bluetree.talisman.Talisman;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroup {

    // Create the registry key for the item group
    public static final RegistryKey<ItemGroup> TALISMAN_GROUP = RegistryKey.of(RegistryKeys.ITEM_GROUP, new Identifier(Talisman.MOD_ID, "talisman_group"));

    // Register the item group
    public static void registerItemGroups() {
        Registry.register(Registries.ITEM_GROUP, TALISMAN_GROUP,
                FabricItemGroup.builder()
                        .displayName(Text.translatable("itemGroup.talisman"))
                        .icon(() -> new ItemStack(ModItems.FERROUS_HEART)) // Icon for the group
                        .build());
    }
}
