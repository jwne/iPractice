package com.shawckz.ipractice.party;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.org.apache.commons.lang3.text.WordUtils;

import org.bukkit.Material;

@Getter
public enum PartyEvent {

    FFA(Material.GOLDEN_APPLE),
    TWO_TEAMS(Material.DIAMOND_CHESTPLATE);

    private final Material icon;
    private final String name;

    PartyEvent(Material icon) {
        this.icon = icon;
        this.name = WordUtils.capitalizeFully(toString().replaceAll("_"," "));
    }

    public static PartyEvent fromString(String s){
        for(PartyEvent event : values()){
            if(event.toString().equalsIgnoreCase(s)){
                return event;
            }
        }
        return null;
    }

}
