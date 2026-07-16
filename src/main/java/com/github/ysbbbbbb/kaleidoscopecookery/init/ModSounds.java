package com.github.ysbbbbbb.kaleidoscopecookery.init;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public final class ModSounds {
    public static final SoundEvent BLOCK_RECIPE_BLOCK = variableRange("block.recipe_block");
    public static final SoundEvent BLOCK_MILLSTONE = variableRange("block.millstone");
    public static final SoundEvent BLOCK_STOCKPOT = fixedRange("block.stockpot");
    public static final SoundEvent BLOCK_TEAPOT_PROCESSING = fixedRange("block.teapot.processing");
    /** Compatibility alias used by the interim Fabric port. */
    @Deprecated(forRemoval = false)
    public static final SoundEvent FABRIC_BLOCK_TEAPOT_PROCESSING = fixedRange("block.teapot_processing");
    public static final SoundEvent BLOCK_PADDY = fixedRange("block.paddy");
    public static final SoundEvent TRASH_CAN = fixedRange("block.trash_can");
    public static final SoundEvent ENTITY_FART = fixedRange("entity.fart");
    public static final SoundEvent ITEM_DOUGH_TRANSFORM = fixedRange("item.dough_transform");

    private ModSounds() {
    }

    public static void registerSounds() {
        register("block.recipe_block", BLOCK_RECIPE_BLOCK);
        register("block.millstone", BLOCK_MILLSTONE);
        register("block.stockpot", BLOCK_STOCKPOT);
        register("block.teapot.processing", BLOCK_TEAPOT_PROCESSING);
        register("block.teapot_processing", FABRIC_BLOCK_TEAPOT_PROCESSING);
        register("block.paddy", BLOCK_PADDY);
        register("block.trash_can", TRASH_CAN);
        register("entity.fart", ENTITY_FART);
        register("item.dough_transform", ITEM_DOUGH_TRANSFORM);
    }

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, path);
    }

    private static SoundEvent variableRange(String path) {
        return SoundEvent.createVariableRangeEvent(id(path));
    }

    private static SoundEvent fixedRange(String path) {
        return SoundEvent.createFixedRangeEvent(id(path), 16.0F);
    }

    private static void register(String path, SoundEvent soundEvent) {
        Registry.register(BuiltInRegistries.SOUND_EVENT, id(path), soundEvent);
    }
}
