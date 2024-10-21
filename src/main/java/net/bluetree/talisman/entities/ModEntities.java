package net.bluetree.talisman.entities;

import net.bluetree.talisman.Talisman;
import net.bluetree.talisman.entities.custom.*;
import net.bluetree.talisman.entities.projectile.InkProjectileEntity;
import net.bluetree.talisman.entities.projectile.OozeTalismanEntity;
import net.bluetree.talisman.entities.projectile.ShadowHandTalismanEntity;
import net.bluetree.talisman.entities.projectile.SludgeTalismanEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {

    public static final EntityType<OozeEntity> OOZE = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(Talisman.MOD_ID, "ooze"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, OozeEntity::new)
                    .dimensions(EntityDimensions.fixed(0.8f, .8f))
                    .build()
    );

    public static final EntityType<SludgeEntity> SLUDGE = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(Talisman.MOD_ID, "sludge"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, SludgeEntity::new)
                    .dimensions(EntityDimensions.fixed(0.8f, .8f))
                    .build()
    );

    public static final EntityType<GumgarEntity> GUMGAR = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(Talisman.MOD_ID, "gumgar"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, GumgarEntity::new)
                    .dimensions(EntityDimensions.changing(1f, 2f))
                    .build()
    );

    public static final EntityType<LostDiligenceEntity> LOST_DILIGENCE = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(Talisman.MOD_ID, "lost_dilligence"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, LostDiligenceEntity::new)
                    .dimensions(EntityDimensions.changing(1f, 3f))
                    .build()
    );

    public static final EntityType<ShadowHandEntity> SHADOW_HAND = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(Talisman.MOD_ID, "shadow_hand"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, ShadowHandEntity::new)
                    .dimensions(EntityDimensions.fixed(0.8f, .8f))
                    .build()
    );

    public static final EntityType<DiligentGuardEntity> DILIGENT_GUARD = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(Talisman.MOD_ID, "diligent_guard"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, DiligentGuardEntity::new)
                    .dimensions(EntityDimensions.fixed(0.8f, .8f))
                    .build()
    );

    public static final EntityType<OozeTalismanEntity> OOZE_TALISMAN_ENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(Talisman.MOD_ID, "ooze_talisman_entity"),
            FabricEntityTypeBuilder.<OozeTalismanEntity>create(SpawnGroup.MISC, OozeTalismanEntity::new)
                    .dimensions(EntityDimensions.fixed(2f, 3f))
                    .trackRangeBlocks(4).trackedUpdateRate(10)
                    .build()
    );

    public static final EntityType<SludgeTalismanEntity> SLUDGE_TALISMAN_ENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(Talisman.MOD_ID, "sludge_talisman_entity"),
            FabricEntityTypeBuilder.<SludgeTalismanEntity>create(SpawnGroup.MISC, SludgeTalismanEntity::new)
                    .dimensions(EntityDimensions.fixed(1f, 1f))
                    .trackRangeBlocks(4).trackedUpdateRate(10)
                    .build()
    );

    public static final EntityType<ShadowHandTalismanEntity> SHADOW_HAND_TALISMAN_ENTITY= Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(Talisman.MOD_ID, "shadow_hand_talisman_entity"),
            FabricEntityTypeBuilder.<ShadowHandTalismanEntity>create(SpawnGroup.MISC, ShadowHandTalismanEntity::new)
                    .dimensions(EntityDimensions.fixed(1f, 1f))
                    .trackRangeBlocks(4).trackedUpdateRate(10)
                    .build()
    );

    public static final EntityType<InkProjectileEntity> INK_PROJECTILE_ENTITY= Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(Talisman.MOD_ID, "ink_projectile_entity"),
            FabricEntityTypeBuilder.<InkProjectileEntity>create(SpawnGroup.MISC, InkProjectileEntity::new)
                    .dimensions(EntityDimensions.fixed(3f, 3f)).build()
    );

    public static void registerModEntities(){
    }


}
