package net.bluetree.talisman.world.gen;

import net.bluetree.talisman.entities.ModEntities;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.BiomeKeys;

public class GumgarEntitySpawn {


    public static void addGumgarEntitySpawn(){
        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(BiomeKeys.LUSH_CAVES, BiomeKeys.DRIPSTONE_CAVES), SpawnGroup.MONSTER, ModEntities.GUMGAR, 60, 1,1);
        SpawnRestriction.register(ModEntities.GUMGAR, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
    }


}
