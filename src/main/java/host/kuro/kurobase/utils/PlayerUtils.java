package host.kuro.kurobase.utils;

import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import host.kuro.kurobase.KuroBase;
import host.kuro.kurobase.database.DatabaseArgs;
import host.kuro.kurobase.database.DatabaseManager;
import host.kuro.kurobase.database.SkinData;
import host.kuro.kurobase.lang.Language;
import host.kuro.kurodiscord.DiscordMessage;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

import com.mojang.authlib.*;
import org.bukkit.inventory.ItemStack;

public class PlayerUtils {
    // rank number
    public static final int RANK_MINARAI = 0;
    public static final int RANK_JYUMIN  = 1;
    public static final int RANK_KANRI = 2;
    public static final int RANK_NUSHI = 3;

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

    public static final int GetBreakPlace(DatabaseManager db, Player player) {
        int ret = 0;
        try {
            PreparedStatement ps = db.getConnection().prepareStatement(Language.translate("SQL.CHEST.LOCK.PERM"));
            ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
            args.add(new DatabaseArgs("c", player.getUniqueId().toString()));
            ResultSet rs = db.ExecuteQuery(ps, args);
            args.clear();
            args = null;
            if (rs != null) {
                while(rs.next()){
                    ret = rs.getInt("cnt");
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

    public static final int GetRank(KuroBase plugin, Player player) {
        int ret = RANK_MINARAI;
        if (plugin.GetRank().containsKey(player)) {
            return plugin.GetRank().get(player);
        } else {
            try {
                PreparedStatement ps = plugin.getDB().getConnection().prepareStatement(Language.translate("SQL.PLAYER.RANK"));
                ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
                args.add(new DatabaseArgs("c", player.getUniqueId().toString()));
                ResultSet rs = plugin.getDB().ExecuteQuery(ps, args);
                args.clear();
                args = null;
                if (rs != null) {
                    while(rs.next()){
                        ret = rs.getInt("rank");
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
                return ret;
            }
        }
        plugin.GetRank().put(player, ret);
        return ret;
    }

    public static final int GetMoney(DatabaseManager db, Player player) {
        int ret = -1;
        try {
            PreparedStatement ps = db.getConnection().prepareStatement(Language.translate("SQL.PLAYER.MONEY"));
            ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
            args.add(new DatabaseArgs("c", player.getUniqueId().toString()));
            ResultSet rs = db.ExecuteQuery(ps, args);
            args.clear();
            args = null;
            if (rs != null) {
                while(rs.next()){
                    ret = rs.getInt("money");
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

    public static final int PayMoney(DatabaseManager db, Player player, int pay) {
        int ret = 0;
        try {
            String uuid = player.getUniqueId().toString();
            if (uuid.length() > 0) {
                ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
                args.add(new DatabaseArgs("i", ""+pay));
                args.add(new DatabaseArgs("c", uuid));
                ret = db.ExecuteUpdate(Language.translate("SQL.PLAYER.PAYMODEY"), args);
                args.clear();
                args = null;
            }
        } catch (Exception ex) {
            ErrorUtils.GetErrorMessage(ex);
        }
        return ret;
    }
    public static final int AddMoney(DatabaseManager db, Player player, int pay) {
        int ret = 0;
        try {
            String uuid = player.getUniqueId().toString();
            if (uuid.length() > 0) {
                ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
                args.add(new DatabaseArgs("i", ""+pay));
                args.add(new DatabaseArgs("c", uuid));
                ret = db.ExecuteUpdate(Language.translate("SQL.PLAYER.ADDMODEY"), args);
                args.clear();
                args = null;
            }
        } catch (Exception ex) {
            ErrorUtils.GetErrorMessage(ex);
        }
        return ret;
    }

    public static final boolean CheckCommandRank(KuroBase plugin, Player player, String cmd) {
        int data_rank = 9;
        String usage = "";
        try {
            cmd = cmd.trim().replace("/", "").toLowerCase();
            PreparedStatement ps = plugin.getDB().getConnection().prepareStatement(Language.translate("SQL.COMMAND.CHECKRANK"));
            ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
            args.add(new DatabaseArgs("c", cmd));
            args.add(new DatabaseArgs("c", cmd));
            ResultSet rs = plugin.getDB().ExecuteQuery(ps, args);
            args.clear();
            args = null;
            if (rs != null) {
                while(rs.next()){
                    usage = rs.getString("useage");
                    data_rank = rs.getInt("rank");
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
            return false;
        }

        int rank = GetRank(plugin, player);
        if (rank >= data_rank) {
            return true;
        }
        player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.error.rank"));
        player.sendMessage(ChatColor.YELLOW + usage);
        SoundUtils.PlaySound(player,"cancel5", false);
        return false;
    }

    public static void SendActionBar(Player player, String message) {
        TextComponent component = new TextComponent();
        component.setText(message);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component);
    }

    public static double getDistance(Player player, Entity entity) {
        try {
            Location ploc = player.getLocation().clone();
            Location eloc = entity.getLocation().clone();
            return ploc.distance(eloc);

        } catch (Exception ex) {
            ErrorUtils.GetErrorMessage(ex);
            return -1;
        }
    }

    public static void BroadcastMessage(String message) {
        BroadcastMessage(message, false);
    }
    public static void BroadcastMessage(String message, boolean discord) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(message);
        }
        if (discord) {
            // discord
            DiscordMessage dm = KuroBase.getDiscord().getDiscordMessage();
            if (dm != null) {
                dm.SendDiscordGreenMessage(message);
            }
        }
    }
    public static void BroadcastActionBar(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            SendActionBar(player, message);
        }
    }

    public static void UpdateJyumin(KuroBase plugin, DatabaseManager db, Player player) {
        int play_time = 0;
        int rank = 99;
        try {
            PreparedStatement ps = db.getConnection().prepareStatement(Language.translate("SQL.PLAYER.TIME"));
            ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
            args.add(new DatabaseArgs("c", player.getUniqueId().toString()));
            ResultSet rs = db.ExecuteQuery(ps, args);
            args.clear();
            args = null;
            if (rs != null) {
                while(rs.next()){
                    rank = rs.getInt("rank");
                    play_time = rs.getInt("play_time");
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

            int rank_time = plugin.getConfig().getInt("Game.rank", 3600);
            if (rank == 0 && play_time >= rank_time) {
                // UPDATE
                ArrayList<DatabaseArgs> rargs = new ArrayList<DatabaseArgs>();
                rargs.add(new DatabaseArgs("c", player.getUniqueId().toString())); // UUID
                int ret = plugin.getDB().ExecuteUpdate(Language.translate("SQL.PLAYER.UPDATE.JYUMIN"), rargs);
                rargs.clear();
                rargs = null;

                StringBuilder sb = new StringBuilder();
                sb.append(ChatColor.GOLD);
                sb.append("[ランクアップ] ");
                sb.append(ChatColor.WHITE);
                sb.append("<");
                sb.append(player.getDisplayName());
                sb.append("さん> ");
                sb.append(ChatColor.GOLD);
                sb.append("が住民ランクへと昇格しました！昇格者はリログで反映されます！");
                String message = new String(sb);
                // broadcast chat
                PlayerUtils.BroadcastMessage(message, true);
                // broadcast sound
                SoundUtils.BroadcastSound("shine3", false);
                player.kickPlayer(message);
            }

        } catch (Exception ex) {
            ErrorUtils.GetErrorMessage(ex);
        }
        return;
    }

    public static final int AddLogPlayerPay(Player player, String kind, int pay, Player target) {
        int ret = 0;
        try {
            ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
            args.add(new DatabaseArgs("c", player.getName())); // src
            args.add(new DatabaseArgs("c", target.getName())); // dst
            args.add(new DatabaseArgs("c", kind)); // kind
            args.add(new DatabaseArgs("i", ""+pay)); // price
            args.add(new DatabaseArgs("c", "")); // result
            ret = KuroBase.getDB().ExecuteUpdate(Language.translate("SQL.INSERT.LOG.PAY"), args);
            args.clear();
            args = null;
        } catch (Exception ex) {
            ErrorUtils.GetErrorMessage(ex);
        }
        return ret;
    }

    public static final int AddLogAreaPay(Player player, String kind, int pay) {
        int ret = 0;
        try {
            ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
            args.add(new DatabaseArgs("c", player.getName())); // src
            args.add(new DatabaseArgs("c", "PROTECT")); // dst
            args.add(new DatabaseArgs("c", kind)); // kind
            args.add(new DatabaseArgs("i", ""+pay)); // price
            args.add(new DatabaseArgs("c", "")); // result
            ret = KuroBase.getDB().ExecuteUpdate(Language.translate("SQL.INSERT.LOG.PAY"), args);
            args.clear();
            args = null;
        } catch (Exception ex) {
            ErrorUtils.GetErrorMessage(ex);
        }
        return ret;
    }

    public static final int AddLogWarpPay(Player player, String kind, int pay) {
        int ret = 0;
        try {
            ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
            args.add(new DatabaseArgs("c", player.getName())); // src
            args.add(new DatabaseArgs("c", "HOME")); // dst
            args.add(new DatabaseArgs("c", kind)); // kind
            args.add(new DatabaseArgs("i", ""+pay)); // price
            args.add(new DatabaseArgs("c", "")); // result
            ret = KuroBase.getDB().ExecuteUpdate(Language.translate("SQL.INSERT.LOG.PAY"), args);
            args.clear();
            args = null;
        } catch (Exception ex) {
            ErrorUtils.GetErrorMessage(ex);
        }
        return ret;
    }

    public static final boolean IsSurvivalWorld(KuroBase plugin, Player player) {
        String name = plugin.getConfig().getString("Game.creative", "city").toLowerCase();
        String now = player.getLocation().getWorld().getName().toLowerCase();
        if (now.equals(name)) {
            return false;
        } else if (now.indexOf("nether") >= 0) {
            return false;
        } else if (now.indexOf("the_end") >= 0) {
            return false;
        }
        return true;
    }

    public static final boolean IsCityWorld(KuroBase plugin, Player player) {
        String name = plugin.getConfig().getString("Game.creative", "city").toLowerCase();
        String now = player.getLocation().getWorld().getName().toLowerCase();
        if (now.equals(name)) {
            return true;
        }
        return false;
    }

    public static final void RemoveAllItems(Player player) {
        player.getInventory().setItemInMainHand(new ItemStack(Material.AIR, 1));
        player.getInventory().setItemInOffHand(new ItemStack(Material.AIR, 1));
        player.getInventory().setChestplate(new ItemStack(Material.AIR, 1));
        player.getInventory().setBoots(new ItemStack(Material.AIR, 1));
        player.getInventory().setHelmet(new ItemStack(Material.AIR, 1));
        player.getInventory().setLeggings(new ItemStack(Material.AIR, 1));
        player.getInventory().setHelmet(new ItemStack(Material.AIR, 1));
        for(ItemStack item : player.getInventory().getContents())
        {
            if (item == null) continue; // null check
            player.getInventory().remove(item);
        }
        player.updateInventory();
    }

    public static final void ForceSurvival(Player player) {
        GameMode mode = player.getGameMode();
        if (mode != GameMode.SURVIVAL) {
            player.setGameMode(GameMode.SURVIVAL);
            RemoveAllItems(player);
        }
    }

    public static final int GetPlayTime(KuroBase plugin, Player player) {
        int play_time = 0;
        try {
            PreparedStatement ps = plugin.getDB().getConnection().prepareStatement(Language.translate("SQL.PLAYER.TIME"));
            ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
            args.add(new DatabaseArgs("c", player.getUniqueId().toString()));
            ResultSet rs = plugin.getDB().ExecuteQuery(ps, args);
            args.clear();
            args = null;
            if (rs != null) {
                while(rs.next()){
                    play_time = rs.getInt("play_time");
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
        return play_time;
    }
}
