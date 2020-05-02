package host.kuro.kurobase.utils;

import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import host.kuro.kurobase.database.DatabaseArgs;
import host.kuro.kurobase.database.DatabaseManager;
import host.kuro.kurobase.database.SkinData;
import host.kuro.kurobase.lang.Language;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

import com.mojang.authlib.*;
public class PlayerUtils {

    static Class<?> craftPlayer = PlayerUtils.getCBClass("entity.CraftPlayer");
    static Class<?> entityPlayer = PlayerUtils.getNMSClass("EntityPlayer");

    private static String getAPIVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().replace(".",",").split(",")[3];
    }
    private static Class<?> getCBClass(String name) {
        try {
            return Class.forName("org.bukkit.craftbukkit." + getAPIVersion() + "." + name);
        } catch(ClassNotFoundException ex) {
            //MidnightSkins.log("No CraftBukkit Class named " + name, Level.WARNING);
            return null;
        }
    }
    private static Class<?> getNMSClass(String name) {
        try {
            return Class.forName("net.minecraft.server." + getAPIVersion() + "." + name);
        } catch(ClassNotFoundException ex) {
            //MidnightSkins.log("No NMS Class named " + name, Level.WARNING);
            return null;
        }
    }
    private static GameProfile getPlayerProfile(Player p) {
        try {
            Object cp = craftPlayer.cast(p);
            Object ep = craftPlayer.getMethod("getHandle").invoke(cp);
            return (GameProfile) entityPlayer.getMethod("getProfile").invoke(ep);
        } catch(NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
            return null;
        }
    }
    private static SkinData getSkin(GameProfile g) {
        if(g == null) return null;
        return getSkin(g,g.getId());
    }
    private static SkinData getSkin(GameProfile g, UUID u) {
        if(g == null) return null;
        if(u == null) u = g.getId();
        PropertyMap properties = g.getProperties();
        if (properties.get("textures").size() > 0) {
            Iterator<Property> it = properties.get("textures").iterator();
            Property skin = it.next();
            return new SkinData(u, skin.getValue(), skin.getSignature());
        }
        return null;
    }
    private static void setSkin(GameProfile g, SkinData s) {
        if(g == null) return;
        PropertyMap properties = g.getProperties();
        properties.get("textures").clear();
        if(s != null) {
            properties.put("textures", new Property("textures", s.getBase64(), s.getSignedBase64()));
        }
    }
    public static SkinData getPlayerSkin(Player player) {
        GameProfile prof = getPlayerProfile(player);
        if (prof != null) {
            return getSkin(prof);
        }
        return null;
    }

    public static final String GetDisplayName(DatabaseManager db, Player player) {
        String disp_name = player.getName();
        try {
            PreparedStatement ps = db.getConnection().prepareStatement(Language.translate("SQL.DISPLAY.PLAYER.NAME"));
            ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
            args.add(new DatabaseArgs("c", player.getUniqueId().toString()));
            ResultSet rs = db.ExecuteQuery(ps, args);
            args.clear();
            args = null;
            if (rs != null) {
                while(rs.next()){
                    disp_name = rs.getString("dispname");
                    break;
                }
            }
            if (ps != null) {
                ps.close();
                ps = null;
            }
            if (rs != null) {
                rs.close();
                rs = null;
            }
        } catch (Exception ex) {
            ErrorUtils.GetErrorMessage(ex);
        }
        return disp_name;
    }

    public static final int GetElapsedDays(DatabaseManager db, Player player) {
        String disp_name = player.getName();
        int ret = 0;
        try {
            PreparedStatement ps = db.getConnection().prepareStatement(Language.translate("SQL.CHECK.ELAPSED_DAYS"));
            ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
            args.add(new DatabaseArgs("c", player.getUniqueId().toString()));
            ResultSet rs = db.ExecuteQuery(ps, args);
            args.clear();
            args = null;
            if (rs != null) {
                while(rs.next()){
                    ret = rs.getInt("elapse_days");
                    break;
                }
            }
            if (ps != null) {
                ps.close();
                ps = null;
            }
            if (rs != null) {
                rs.close();
                rs = null;
            }
        } catch (Exception ex) {
            ErrorUtils.GetErrorMessage(ex);
        }
        return ret;
    }

}
