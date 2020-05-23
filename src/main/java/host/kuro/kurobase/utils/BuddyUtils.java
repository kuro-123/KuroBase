package host.kuro.kurobase.utils;

import host.kuro.kurobase.KuroBase;
import host.kuro.kurobase.database.DatabaseArgs;
import host.kuro.kurobase.lang.Language;
import host.kuro.kurobase.trait.KuroTrait;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BuddyUtils {

    private final static String DATA_KEY = "NPCTYPE";

    public static boolean IsNpc(Entity entity) {
        if (entity == null) return false;
        if (!entity.hasMetadata(DATA_KEY)) return false;
        if (IsBuddy(entity)) return true;
        if (IsBuddyMaster(entity)) return true;
        if (IsExplaner(entity)) return true;
        return false;
    }

    public static boolean IsBuddy(Entity entity) {
        if (entity == null) return false;
        String name = entity.getCustomName();
        if (name == null) return false;
        if (name.length() <= 0) return false;
        if (!entity.hasMetadata(DATA_KEY)) return false;
        String strval = "";
        List<MetadataValue> values = entity.getMetadata(DATA_KEY);
        for (MetadataValue v : values) {
            if (v.getOwningPlugin().getName().equals(KuroBase.GetInstance().getName())) {
                strval = v.asString();
                break;
            }
        }
        if (strval.equals("BUDDY")) return true;
        return false;
    }

    public static boolean IsBuddyMaster(Entity entity) {
        if (entity == null) return false;
        String name = entity.getCustomName();
        if (name == null) return false;
        if (name.length() <= 0) return false;
        if (!entity.hasMetadata(DATA_KEY)) return false;
        String strval = "";
        List<MetadataValue> values = entity.getMetadata(DATA_KEY);
        for (MetadataValue v : values) {
            if (v.getOwningPlugin().getName().equals(KuroBase.GetInstance().getName())) {
                strval = v.asString();
                break;
            }
        }
        if (strval.equals("BUDDYMASTER")) return true;
        return false;
    }

    public static boolean IsExplaner(Entity entity) {
        if (entity == null) return false;
        String name = entity.getCustomName();
        if (name == null) return false;
        if (name.length() <= 0) return false;
        String strval = "";
        List<MetadataValue> values = entity.getMetadata(DATA_KEY);
        for (MetadataValue v : values) {
            if (v.getOwningPlugin().getName().equals(KuroBase.GetInstance().getName())) {
                strval = v.asString();
                break;
            }
        }
        if (strval.equals("EXPLANER")) return true;
        return false;
    }

    public static boolean ExistEntity(Player player, String name) {
        boolean ret = false;
        try {
            PreparedStatement ps = KuroBase.getDB().getConnection().prepareStatement(Language.translate("SQL.SELECT.ENTITY.NAME"));
            ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
            args.add(new DatabaseArgs("c", player.getUniqueId().toString()));
            args.add(new DatabaseArgs("c", name));
            ResultSet rs = KuroBase.getDB().ExecuteQuery(ps, args);
            args.clear();
            args = null;
            if (rs != null) {
                while (rs.next()) {
                    ret = true;
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

    public static boolean CheckNameEntity(String name) {
        boolean ret = false;
        try {
            PreparedStatement ps = KuroBase.getDB().getConnection().prepareStatement(Language.translate("SQL.NAME.CHECK.NAME"));
            ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
            args.add(new DatabaseArgs("c", name.toLowerCase()));
            args.add(new DatabaseArgs("c", name.toLowerCase()));
            ResultSet rs = KuroBase.getDB().ExecuteQuery(ps, args);
            args.clear();
            args = null;
            if (rs != null) {
                while (rs.next()) {
                    ret = true;
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

    public static boolean GetJoinEntity(Player player) {
        boolean ret = false;
        try {
            PreparedStatement ps = KuroBase.getDB().getConnection().prepareStatement(Language.translate("SQL.SELECT.JOIN.ENTITY"));
            ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
            args.add(new DatabaseArgs("c", player.getUniqueId().toString()));
            ResultSet rs = KuroBase.getDB().ExecuteQuery(ps, args);
            args.clear();
            args = null;
            if (rs != null) {
                while (rs.next()) {
                    ret = true;
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
    public static boolean CheckDeadEntity(Player player, String name) {
        boolean ret = false;
        try {
            PreparedStatement ps = KuroBase.getDB().getConnection().prepareStatement(Language.translate("SQL.SELECT.CHECK.DEAD.ENTITY"));
            ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
            args.add(new DatabaseArgs("c", player.getUniqueId().toString()));
            args.add(new DatabaseArgs("c", name));
            ResultSet rs = KuroBase.getDB().ExecuteQuery(ps, args);
            args.clear();
            args = null;
            if (rs != null) {
                while (rs.next()) {
                    ret = true;
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
    public static boolean SetNpcExprience(String name, int addexp) {
        String uuid = "";
        String type = "";
        String mode = "";
        int level = -1;
        int exp = -1;
        try {
            PreparedStatement ps = KuroBase.getDB().getConnection().prepareStatement(Language.translate("SQL.SELECT.ENTITY.BYNAME"));
            ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
            args.add(new DatabaseArgs("c", name));
            ResultSet rs = KuroBase.getDB().ExecuteQuery(ps, args);
            args.clear();
            args = null;
            if (rs != null) {
                while(rs.next()){
                    uuid = rs.getString("uuid");
                    level = rs.getInt("level");
                    exp = rs.getInt("exp");
                    type = rs.getString("type");
                    mode = rs.getString("mode");
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
        if (level == -1 && exp == -1) {
            return false;
        }

        UUID id = UUID.fromString(uuid);
        NPC npc = CitizensAPI.getNPCRegistry().getByUniqueId(id);
        if (npc != null) {
            // lebel換算
            exp = exp+addexp;
            npc.getTrait(KuroTrait.class).setExp(exp);

            int calc_level = calculateLevelForExp(exp);
            if (level < calc_level) {
                // level up
                level = calc_level;
                PlayerUtils.BroadcastMessage(String.format(ChatColor.AQUA + "[ﾊﾞﾃﾞｨｰ] %s が LevelUp!! -> Lv%d", name, level), false);
                SoundUtils.BroadcastSound("shine3", false);
                // update status
                npc.getTrait(KuroTrait.class).setLevel(level);
                npc.getTrait(KuroTrait.class).UpdateStatus();

                // UPDATE ADD SKILLPOINTS
                ArrayList<DatabaseArgs> largs = new ArrayList<DatabaseArgs>();
                largs.add(new DatabaseArgs("c", uuid)); // uuid
                int ret = KuroBase.getDB().ExecuteUpdate(Language.translate("SQL.UPDATE.LEVEL.ENTITY"), largs);
                largs.clear();
                largs = null;
            }
            // UPDATE
            ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
            args.add(new DatabaseArgs("i", ""+exp)); // exp
            args.add(new DatabaseArgs("i", ""+level)); // level
            args.add(new DatabaseArgs("c", uuid)); // uuid
            int ret = KuroBase.getDB().ExecuteUpdate(Language.translate("SQL.UPDATE.KILLMOB.ENTITY"), args);
            args.clear();
            args = null;
        }
        return true;
    }

    private static int calculateLevelForExp(int exp) {
        int level = 0;
        int curExp = 7; // level 1
        int incr = 10;
        while (curExp <= exp) {
            curExp += incr;
            level++;
            incr += (level % 2 == 0) ? 3 : 4;
        }
        return level;
    }

    public static void InitBuddy(Player player) {
        // UPDATE
        ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
        args.add(new DatabaseArgs("c", player.getUniqueId().toString())); // UUID
        int ret = KuroBase.getDB().ExecuteUpdate(Language.translate("SQL.UPDATE.INIT.ENTITY"), args);
        args.clear();
        args = null;
    }
}
