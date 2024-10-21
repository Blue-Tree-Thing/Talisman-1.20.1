package net.bluetree.talisman;

import net.bluetree.talisman.blocks.ModBlockEntities;
import net.bluetree.talisman.blocks.ModBlocks;
import net.bluetree.talisman.entities.ModEntities;
import net.bluetree.talisman.entities.custom.*;
import net.bluetree.talisman.items.ModItemGroup;
import net.bluetree.talisman.items.ModItems;
import net.bluetree.talisman.screen.ModScreenHandlers;
import net.bluetree.talisman.sounds.ModSounds;
import net.bluetree.talisman.world.gen.GumgarEntitySpawn;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;

import software.bernie.geckolib.GeckoLib;

public class Talisman implements ModInitializer {
	public static final String MOD_ID = "talisman";



	@Override
	public void onInitialize() {
		ModItems.registerModItems();
		ModBlocks.registerModBlocks();
		ModBlockEntities.registerBlockEntities();
		ModScreenHandlers.registerAllScreenHandlers();
		ModItemGroup.registerItemGroups();
		ModEntities.registerModEntities();
		GumgarEntitySpawn.addGumgarEntitySpawn();
		ModSounds.registerModSounds();
		GeckoLib.initialize();

		FabricDefaultAttributeRegistry.register(ModEntities.OOZE, OozeEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.SLUDGE, SludgeEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.SHADOW_HAND, ShadowHandEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.GUMGAR, GumgarEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.LOST_DILIGENCE, LostDiligenceEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.DILIGENT_GUARD, DiligentGuardEntity.setAttributes());


	}
}