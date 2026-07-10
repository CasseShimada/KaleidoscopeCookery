package com.github.ysbbbbbb.kaleidoscopecookery.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;

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
        return properties.humanoidArmor(material, type);
    }
}
