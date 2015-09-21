package com.shawckz.ipractice.util.nametag;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.bukkit.Bukkit;

public class ReflectionUtils {
    private static final String PACKAGE_PREFIX = "org/bukkit/craftbukkit/v";
    private static String version;

    public static String getVersion() {
        return ReflectionUtils.version;
    }

    public static void putInPrivateStaticMap(final Class clazz, final String fieldName, final Object key, final Object value) throws Exception {
        final Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        final Map map = (Map) field.get(null);
        map.put(key, value);
        field.set(null, map);
    }

    public static String getCraftPlayerClasspath() {
        return "org.bukkit.craftbukkit." + getVersion() + ".entity.CraftPlayer";
    }

    public static String getPlayerConnectionClasspath() {
        return "net.minecraft.server." + getVersion() + ".PlayerConnection";
    }

    public static String getNMSPlayerClasspath() {
        return "net.minecraft.server." + getVersion() + ".EntityPlayer";
    }

    public static String getPacketClasspath() {
        return "net.minecraft.server." + getVersion() + ".Packet";
    }

    public static String getPacketTeamClasspath() {
        return "net.minecraft.server." + getVersion() + ".PacketPlayOutScoreboardTeam";
    }

    static {
        ReflectionUtils.version = "";
        try {
            final File file = new File(Bukkit.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
            final ZipInputStream zis = new ZipInputStream(new FileInputStream(file));
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                final String name = entry.getName().replace("\\", "/");
                if (name.startsWith("org/bukkit/craftbukkit/v")) {
                    String ver = "";
                    for (int t = "org/bukkit/craftbukkit/v".length(); t < name.length(); ++t) {
                        final char c = name.charAt(t);
                        if (c == '/') {
                            break;
                        }
                        ver += c;
                    }
                    ReflectionUtils.version = "v" + ver;
                    break;
                }
            }
            zis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}