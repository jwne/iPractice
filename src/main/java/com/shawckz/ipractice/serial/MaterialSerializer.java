package com.shawckz.ipractice.serial;

import com.shawckz.ipractice.configuration.AbstractSerializer;
import org.bukkit.Material;

public class MaterialSerializer extends AbstractSerializer<Material> {

    @Override
    public String toString(Material data) {
        return data.toString();
    }

    @Override
    public Material fromString(Object data) {
        return Material.valueOf(((String)data).toUpperCase());
    }
}
