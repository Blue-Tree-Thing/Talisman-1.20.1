package net.bluetree.talisman.sounds;

import net.bluetree.talisman.Talisman;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {

    public static SoundEvent LOST_DILIGENCE_AMBIENT = registerSoundEvent("lost_diligence_ambient");
    public static SoundEvent LOST_DILIGENCE_HURT = registerSoundEvent("lost_diligence_hurt");
    public static SoundEvent LOST_DILIGENCE_DEATH = registerSoundEvent("lost_diligence_death");
    public static SoundEvent GUMGAR_ATTACK = registerSoundEvent("gumgar_attack");
    public static SoundEvent DILLIGENT_GUARD_AMBIENT = registerSoundEvent("diligent_guard_ambient");
    public static SoundEvent DILLIGENT_GUARD_ATTACK = registerSoundEvent("diligent_guard_attack");
    public static SoundEvent GENERIC_OOZE_AMBIENT_1 = registerSoundEvent("generic_ooze_ambient");
    public static SoundEvent GENERIC_OOZE_AMBIENT_2 = registerSoundEvent("generic_ooze_ambient_2");

    private static SoundEvent registerSoundEvent(String name) {
        Identifier id = new Identifier(Talisman.MOD_ID, name);
        SoundEvent soundEvent = SoundEvent.of(id);
        return Registry.register(Registries.SOUND_EVENT, id, soundEvent);
    }

    public static void registerModSounds() {
    }
}
