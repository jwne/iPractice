/*
 * Copyright (c) Shawckz.com 2014.  All rights reserved.
 * You (the third party) may NOT modify, reuse, edit, change, decompile or use anything contained in any of this code, or in this project without written permission from Shawckz (Shockz__), Shawckz.com, MCDevs.net.
 * Thanks.
 */

package com.shawckz.ipractice.util;


import com.shawckz.ipractice.Practice;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by 360 on 8/12/2014.
 */
public class AutoRespawn {

    public static void autoRespawn(final EntityDeathEvent e){
        new BukkitRunnable()
        {
            public void run()
            {
                try
                {
                    Object nmsPlayer = e.getEntity().getClass().getMethod("getHandle").invoke(e.getEntity());
                    Object con = nmsPlayer.getClass().getDeclaredField("playerConnection").get(nmsPlayer);

                    Class< ? > EntityPlayer = Class.forName(nmsPlayer.getClass().getPackage().getName() + ".EntityPlayer");

                    Field minecraftServer = con.getClass().getDeclaredField("minecraftServer");
                    minecraftServer.setAccessible(true);
                    Object mcserver = minecraftServer.get(con);

                    Object playerlist = mcserver.getClass().getDeclaredMethod("getPlayerList").invoke(mcserver);
                    Method moveToWorld = playerlist.getClass().getMethod("moveToWorld" , EntityPlayer , int.class , boolean.class);
                    moveToWorld.invoke(playerlist , nmsPlayer , 0 , false);
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        }.runTaskLater(Practice.getPlugin() , 2);
    }

}
