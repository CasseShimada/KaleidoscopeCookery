package com.github.ysbbbbbb.kaleidoscopecookery.init;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import net.minecraft.util.Util;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorMaterials;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAssets;

import java.util.EnumMap;

public final class ModArmorMaterials {
    public static final ArmorMaterial FARMER = new ArmorMaterial(
            ArmorMaterials.LEATHER.durability(),
            Util.make(new EnumMap<>(ArmorType.class), map -> {
                map.put(ArmorType.HELMET, 1);
                map.put(ArmorType.CHESTPLATE, 4);
                map.put(ArmorType.LEGGINGS, 5);
                map.put(ArmorType.BOOTS, 2);
            }),
            12,
            SoundEvents.ARMOR_EQUIP_LEATHER,
            0.0F,
            0.0F,
            ItemTags.REPAIRS_LEATHER_ARMOR,
            ResourceKey.create(EquipmentAssets.ROOT_ID, id("cookery_farmer"))
    );

    private ModArmorMaterials() {
    }

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, path);
    }
}
