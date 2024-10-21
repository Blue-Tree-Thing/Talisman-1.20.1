package net.bluetree.talisman;

import net.bluetree.talisman.entities.ModEntities;
import net.bluetree.talisman.entities.custom.*;
import net.bluetree.talisman.items.ModItemGroup;
import net.bluetree.talisman.items.ModItems;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;

import software.bernie.geckolib.GeckoLib;

public class Talisman implements ModInitializer {
	public static final String MOD_ID = "talisman";



	@Override
	public void onInitialize() {
		ModItems.registerModItems();
		ModItemGroup.registerItemGroups();
		ModEntities.registerModEntities();

		GeckoLib.initialize();

		FabricDefaultAttributeRegistry.register(ModEntities.OOZE, OozeEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.SLUDGE, SludgeEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.SHADOW_HAND, ShadowHandEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.GUMGAR, GumgarEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.LOST_DILIGENCE, LostDiligenceEntity.setAttributes());


	}
}