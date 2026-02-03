package com.github.ysbbbbbb.kaleidoscopecookery.init;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.google.common.collect.Maps;
import net.minecraft.util.Util;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorMaterials;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAssets;


public class ModArmorMaterials {
    public static final Holder<ArmorMaterial> FARMER = Holder.direct(
            new ArmorMaterial(
                    ArmorMaterials.LEATHER.durability(),
                    Util.make(Maps.newEnumMap(ArmorType.class), map -> {
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
                    ResourceKey.create(EquipmentAssets.ROOT_ID, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "cookery_farmer"))
            )
    );

    public static void registerArmorMaterials() {
        // 注册方法，用于确保类被加载
    }
}
