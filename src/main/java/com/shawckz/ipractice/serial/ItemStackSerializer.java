package com.shawckz.ipractice.serial;

import com.shawckz.ipractice.configuration.AbstractSerializer;
import com.shawckz.ipractice.util.ItemUtil;
import org.bukkit.inventory.ItemStack;

/**
 * ---------- GuruCraft ----------
 * Created by Fraser.Cumming on 06/04/2015.
 * © 2015 Fraser Cumming All Rights Reserved
 */
public class ItemStackSerializer extends AbstractSerializer<ItemStack> {

    @Override
    public String toString(ItemStack data) {
        return data == null ? null : ItemUtil.itemToString(data);
    }

    @Override
    public ItemStack fromString(Object data) {
        return data == null ? null : ItemUtil.stringToItem(data.toString());
    }
}
