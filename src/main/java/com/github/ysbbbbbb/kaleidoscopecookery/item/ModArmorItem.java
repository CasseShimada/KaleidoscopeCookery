package com.github.ysbbbbbb.kaleidoscopecookery.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.Equippable;

public class ModArmorItem extends Item {
    private final ArmorMaterial material;
    private final ArmorType type;

    public ModArmorItem(ArmorMaterial material, ArmorType type, Properties properties) {
        super(applyArmorProperties(material, type, properties));
        this.material = material;
        this.type = type;
    }

    public ArmorMaterial getMaterial() {
        return material;
    }

    public ArmorType getType() {
        return type;
    }

    private static Properties applyArmorProperties(ArmorMaterial material, ArmorType type, Properties properties) {
        EquipmentSlot slot = type.getSlot();
        return properties
                .durability(type.getDurability(material.durability()))
                .component(DataComponents.EQUIPPABLE, Equippable.builder(slot)
                        .setEquipSound(material.equipSound())
                        .setAsset(material.assetId())
                        .setDamageOnHurt(true)
                        .build())
                .attributes(material.createAttributes(type));
    }
}
