package host.kuro.kurobase.utils;

import host.kuro.kurobase.KuroBase;
import host.kuro.kurobase.database.DatabaseArgs;
import host.kuro.kurobase.lang.Language;
import host.kuro.kurobase.trait.KuroTrait;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
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
        if (IsWeaponMaster(entity)) return true;
        if (IsArmorMaster(entity)) return true;
        if (IsItemMaster(entity)) return true;
        if (IsSpecialMaster(entity)) return true;
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

    public static boolean IsWeaponMaster(Entity entity) {
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
        if (strval.equals("WEAPONSMASTER")) return true;
        return false;
    }

    public static boolean IsArmorMaster(Entity entity) {
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
        if (strval.equals("ARMORMASTER")) return true;
        return false;
    }

    public static boolean IsItemMaster(Entity entity) {
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
        if (strval.equals("ITEMMASTER")) return true;
        return false;
    }

    public static boolean IsSpecialMaster(Entity entity) {
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
        if (strval.equals("SPECIALMASTER")) return true;
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
            args.add(new DatabaseArgs("c", name.replace("[ﾊﾞﾃﾞｨｰ] ", "").trim()));
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
                PlayerUtils.BroadcastMessage(String.format(ChatColor.AQUA + "%s が LevelUp!! -> Lv%d", name, level), false);
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

    public static boolean UpdateEquip(String kbn, int value, String name, NPC npc) {
        String exec_sql = "";
        switch (kbn) {
            case "SWORD":
                exec_sql = "SQL.UPDATE.SWORD.ENTITY";
                name = name + ":" + value;
                npc.getTrait(KuroTrait.class).setSword(name);
                break;
            case "HELMET":
                exec_sql = "SQL.UPDATE.HELMET.ENTITY";
                name = name + ":" + value;
                npc.getTrait(KuroTrait.class).setHelmet(name);
                break;
            case "CHESTPLATE":
                exec_sql = "SQL.UPDATE.CHESTPLATE.ENTITY";
                name = name + ":" + value;
                npc.getTrait(KuroTrait.class).setChestplate(name);
                break;
            case "LEGGINS":
                exec_sql = "SQL.UPDATE.LEGGINS.ENTITY";
                name = name + ":" + value;
                npc.getTrait(KuroTrait.class).setLeggins(name);
                break;
            case "BOOTS":
                exec_sql = "SQL.UPDATE.BOOTS.ENTITY";
                name = name + ":" + value;
                npc.getTrait(KuroTrait.class).setBoots(name);
                break;
        }
        // UPDATE
        ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
        args.add(new DatabaseArgs("c", ""+name));        // name
        args.add(new DatabaseArgs("c", npc.getUniqueId().toString()));   // uuid
        int ret = KuroBase.getDB().ExecuteUpdate(Language.translate(exec_sql), args);
        args.clear();
        args = null;
        if (ret == 1) return true;
        return false;
    }

    public static boolean Equip(NPC npc, String kbn, String arrayval) {
        String[] buff = arrayval.split(":");
        if (buff != null) {
            if (buff.length == 2) {
                String name = buff[0];
                String sval = buff[1];
                int val = Integer.parseInt(sval);
                BuddyEquip(npc, name, val, false);
                return true;
            }
        }
        return false;
    }

    public static ItemStack SetDulabity(ItemStack stack, int value) {
        if (value != 0) return stack;
        ItemMeta im = stack.getItemMeta();
        Damageable itemdmg = (Damageable)im;
        itemdmg.setDamage(value);
        stack.setItemMeta((ItemMeta)itemdmg);
        return stack;
    }

    public static boolean BuddyEquip(NPC npc, String display, int value, boolean update) {
        ItemStack item = null;
        if (display.equals(Language.translate("shop.item.sword.wood"))) {
            item = new ItemStack(Material.WOODEN_SWORD, 1);
            item = SetDulabity(item, value);
            item.addEnchantment(Enchantment.DAMAGE_ALL, 1);
            npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.HAND, item);
            if (update) UpdateEquip("SWORD", value, display, npc);
        } else if (display.equals(Language.translate("shop.item.sword.chain"))) {
            item = new ItemStack(Material.STONE_SWORD, 1);
            item = SetDulabity(item, value);
            item.addEnchantment(Enchantment.DAMAGE_ALL, 2);
            npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.HAND, item);
            if (update) UpdateEquip("SWORD", value, display, npc);
        } else if (display.equals(Language.translate("shop.item.sword.iron"))) {
            item = new ItemStack(Material.IRON_SWORD, 1);
            item = SetDulabity(item, value);
            item.addEnchantment(Enchantment.DAMAGE_ALL, 3);
            npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.HAND, item);
            if (update) UpdateEquip("SWORD", value, display, npc);
        } else if (display.equals(Language.translate("shop.item.sword.gold"))) {
            item = new ItemStack(Material.GOLDEN_SWORD, 1);
            item = SetDulabity(item, value);
            item.addEnchantment(Enchantment.DAMAGE_ALL, 4);
            npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.HAND, item);
            if (update) UpdateEquip("SWORD", value, display, npc);
        } else if (display.equals(Language.translate("shop.item.sword.dia"))) {
            item = new ItemStack(Material.DIAMOND_SWORD, 1);
            item = SetDulabity(item, value);
            item.addEnchantment(Enchantment.DAMAGE_ALL, 5);
            npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.HAND, item);
            if (update) UpdateEquip("SWORD", value, display, npc);

        } else if (display.equals(Language.translate("shop.item.helmet.wood"))) {
            item = new ItemStack(Material.LEATHER_HELMET, 1);
            item = SetDulabity(item, value);
            npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.HELMET, item);
            if (update) UpdateEquip("HELMET", value, display, npc);
        } else if (display.equals(Language.translate("shop.item.helmet.chain"))) {
            item = new ItemStack(Material.CHAINMAIL_HELMET, 1);
            item = SetDulabity(item, value);
            npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.HELMET, item);
            if (update) UpdateEquip("HELMET", value, display, npc);
        } else if (display.equals(Language.translate("shop.item.helmet.iron"))) {
            item = new ItemStack(Material.IRON_HELMET, 1);
            item = SetDulabity(item, value);
            npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.HELMET, item);
            if (update) UpdateEquip("HELMET", value, display, npc);
        } else if (display.equals(Language.translate("shop.item.helmet.gold"))) {
            item = new ItemStack(Material.GOLDEN_HELMET, 1);
            item = SetDulabity(item, value);
            npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.HELMET, item);
            if (update) UpdateEquip("HELMET", value, display, npc);
        } else if (display.equals(Language.translate("shop.item.helmet.dia"))) {
            item = new ItemStack(Material.DIAMOND_HELMET, 1);
            item = SetDulabity(item, value);
            npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.HELMET, item);
            if (update) UpdateEquip("HELMET", value, display, npc);

        } else if (display.equals(Language.translate("shop.item.chestplate.wood"))) {
            item = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
            item = SetDulabity(item, value);
            npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.CHESTPLATE, item);
            if (update) UpdateEquip("CHESTPLATE", value, display, npc);
        } else if (display.equals(Language.translate("shop.item.chestplate.chain"))) {
            item = new ItemStack(Material.CHAINMAIL_CHESTPLATE, 1);
            item = SetDulabity(item, value);
            npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.CHESTPLATE, item);
            if (update) UpdateEquip("CHESTPLATE", value, display, npc);
        } else if (display.equals(Language.translate("shop.item.chestplate.iron"))) {
            item = new ItemStack(Material.IRON_CHESTPLATE, 1);
            item = SetDulabity(item, value);
            npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.CHESTPLATE, item);
            if (update) UpdateEquip("CHESTPLATE", value, display, npc);
        } else if (display.equals(Language.translate("shop.item.chestplate.gold"))) {
            item = new ItemStack(Material.GOLDEN_CHESTPLATE, 1);
            item = SetDulabity(item, value);
            npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.CHESTPLATE, item);
            if (update) UpdateEquip("CHESTPLATE", value, display, npc);
        } else if (display.equals(Language.translate("shop.item.chestplate.dia"))) {
            item = new ItemStack(Material.DIAMOND_CHESTPLATE, 1);
            item = SetDulabity(item, value);
            npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.CHESTPLATE, item);
            if (update) UpdateEquip("CHESTPLATE", value, display, npc);

        } else if (display.equals(Language.translate("shop.item.leggins.wood"))) {
            item = new ItemStack(Material.LEATHER_LEGGINGS, 1);
            item = SetDulabity(item, value);
            npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.LEGGINGS, item);
            if (update) UpdateEquip("LEGGINS", value, display, npc);
        } else if (display.equals(Language.translate("shop.item.leggins.chain"))) {
            item = new ItemStack(Material.CHAINMAIL_LEGGINGS, 1);
            item = SetDulabity(item, value);
            npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.LEGGINGS, item);
            if (update) UpdateEquip("LEGGINS", value, display, npc);
        } else if (display.equals(Language.translate("shop.item.leggins.iron"))) {
            item = new ItemStack(Material.IRON_LEGGINGS, 1);
            item = SetDulabity(item, value);
            npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.LEGGINGS, item);
            if (update) UpdateEquip("LEGGINS", value, display, npc);
        } else if (display.equals(Language.translate("shop.item.leggins.gold"))) {
            item = new ItemStack(Material.GOLDEN_LEGGINGS, 1);
            item = SetDulabity(item, value);
            npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.LEGGINGS, item);
            if (update) UpdateEquip("LEGGINS", value, display, npc);
        } else if (display.equals(Language.translate("shop.item.leggins.dia"))) {
            item = new ItemStack(Material.DIAMOND_LEGGINGS, 1);
            item = SetDulabity(item, value);
            npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.LEGGINGS, item);
            if (update) UpdateEquip("LEGGINS", value, display, npc);

        } else if (display.equals(Language.translate("shop.item.boots.wood"))) {
            item = new ItemStack(Material.LEATHER_BOOTS, 1);
            item = SetDulabity(item, value);
            npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.BOOTS, item);
            if (update) UpdateEquip("BOOTS", value, display, npc);
        } else if (display.equals(Language.translate("shop.item.boots.chain"))) {
            item = new ItemStack(Material.CHAINMAIL_BOOTS, 1);
            item = SetDulabity(item, value);
            npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.BOOTS, item);
            if (update) UpdateEquip("BOOTS", value, display, npc);
        } else if (display.equals(Language.translate("shop.item.boots.iron"))) {
            item = new ItemStack(Material.IRON_BOOTS, 1);
            item = SetDulabity(item, value);
            npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.BOOTS, item);
            if (update) UpdateEquip("BOOTS", value, display, npc);
        } else if (display.equals(Language.translate("shop.item.boots.gold"))) {
            item = new ItemStack(Material.GOLDEN_BOOTS, 1);
            item = SetDulabity(item, value);
            npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.BOOTS, item);
            if (update) UpdateEquip("BOOTS", value, display, npc);
        } else if (display.equals(Language.translate("shop.item.boots.dia"))) {
            item = new ItemStack(Material.DIAMOND_BOOTS, 1);
            item = SetDulabity(item, value);
            npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.BOOTS, item);
            if (update) UpdateEquip("BOOTS", value, display, npc);
        } else {
            return false;
        }
        return true;
    }

    public static String GetEquipName(ItemStack stack, String value) {
        if (stack.getType() == Material.AIR) {
            return "";
        }
        String[] buff = value.split(":");
        if (buff != null) {
            if (buff.length == 2) {
                String name = buff[0];
                ItemMeta im;
                Damageable itemdmg;
                if (stack.hasItemMeta()) {
                    im = stack.getItemMeta();
                    itemdmg = (Damageable)im;
                    int val = itemdmg.getDamage();
                    return name + ":" + val;
                } else {
                    return name + ":0";
                }
            }
        }
        return "";
    }
}
