package host.kuro.kurobase.commands;

import host.kuro.kurobase.KuroBase;
import host.kuro.kurobase.database.DatabaseArgs;
import host.kuro.kurobase.lang.Language;
import host.kuro.kurobase.npc.BuddyMasterTrait;
import host.kuro.kurobase.npc.KuroTrait;
import host.kuro.kurobase.utils.*;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.trait.*;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.UUID;

public class BuddyCommand implements CommandExecutor {

    private KuroBase plugin;
    public BuddyCommand(KuroBase plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if(!(sender instanceof Player)){
            // console
            plugin.getLogger().warning(Language.translate("plugin.console.error"));
            return false;
        }
        Player player = (Player)sender;

        // check city world
        if (PlayerUtils.IsCityWorld(plugin, player)) {
            int rank = PlayerUtils.GetRank(plugin, player);
            if (rank < PlayerUtils.RANK_NUSHI) {
                player.sendMessage(ChatColor.DARK_RED + Language.translate("plugin.error.world"));
                SoundUtils.PlaySound(player, "cancel5", false);
                return false;
            }
        }

        switch(args[0].toLowerCase()) {
            case "list": return ActionList(player);
            case "add": return ActionAdd(player, args);
            //case "type": return ActionType(player, args);
            //case "mode": return ActionMode(player, args);
            case "url": return ActionUrl(player, args);
            case "del": return ActionDel(player, args);
            case "join": return ActionJoin(player, args);
            case "quit": return ActionQuit(player, args);
            case "revival": return ActionRevival(player, args);
            case "buddyshop": return ActionBuddyShop(player, args);
        }
        return true;
    }

    private boolean ActionList(Player player) {
        StringBuilder sb = new StringBuilder();
        int i=0;
        try {
            PreparedStatement ps = KuroBase.getDB().getConnection().prepareStatement(Language.translate("SQL.SELECT.ENTITY"));
            ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
            args.add(new DatabaseArgs("c", player.getUniqueId().toString()));
            ResultSet rs = KuroBase.getDB().ExecuteQuery(ps, args);
            args.clear();
            args = null;
            if (rs != null) {
                while(rs.next()){
                    sb.append(String.format("名前: %s 状態:%s 討伐数:%4d LV:%3d EXP:%5d\n"
                            ,rs.getString("name")
                            ,rs.getString("status")
                            ,rs.getInt("level")
                            ,rs.getInt("exp")
                            ,rs.getInt("killmob")
                            ));
                    i++;
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
        sb.append(ChatColor.GREEN + "バディー数: " + i);
        player.sendMessage(new String(sb));
        return true;
    }

    private boolean ActionAdd(Player player, String[] args) {
        try {
            // args check
            if (args.length != 4) {
                player.sendMessage(ChatColor.DARK_RED + Language.translate("plugin.args.error"));
                SoundUtils.PlaySound(player,"cancel5", false);
                return false;
            }

            // check item
            ItemStack stack = player.getInventory().getItemInMainHand();
            if (stack != null) {
                ItemMeta data = stack.getItemMeta();
                if (data != null) {
                    String display = data.getDisplayName();
                    if (!display.equals("バディーの書")) {
                        player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.entity.add.item.error"));
                        SoundUtils.PlaySound(player,"cancel5", false);
                        return false;
                    }
                }
            }

            String name = args[1];
            if (BuddyUtils.CheckNameEntity(name)) {
                player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.entity.exist.error"));
                SoundUtils.PlaySound(player,"cancel5", false);
                return false;
            }

            if (BuddyUtils.ExistEntity(player, name)) {
                player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.entity.exist.error"));
                SoundUtils.PlaySound(player,"cancel5", false);
                return false;
            }

            boolean nuchi = false;
            int rank = PlayerUtils.GetRank(plugin, player);
            if (rank == PlayerUtils.RANK_NUSHI) nuchi = true;

            int level = player.getLevel();
            if (nuchi) level = 1000;

            String type = args[2];
            type = "人型";
            String mode = args[3];
            if (mode.equals(Language.translate("buddy.list.normal"))) {
                if (level < 200) {
                    player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.entity.add.error"));
                    SoundUtils.PlaySound(player,"cancel5", false);
                    return false;
                }
                mode = Language.translate("buddy.data.normal");
            } else if (mode.equals(Language.translate("buddy.list.guard"))) {
                if (level < 300) {
                    player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.entity.add.error"));
                    SoundUtils.PlaySound(player,"cancel5", false);
                    return false;
                }
                mode = Language.translate("buddy.data.guard");
            } else if (mode.equals(Language.translate("buddy.list.battle"))) {
                if (level < 400) {
                    player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.entity.add.error"));
                    SoundUtils.PlaySound(player,"cancel5", false);
                    return false;
                }
                mode = Language.translate("buddy.data.battle");
            } else if (mode.equals(Language.translate("buddy.list.nijya"))) {
                if (level < 500) {
                    player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.entity.add.error"));
                    SoundUtils.PlaySound(player,"cancel5", false);
                    return false;
                }
                mode = Language.translate("buddy.data.nijya");
            } else{
                if (level < 200) {
                    player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.entity.add.error"));
                    SoundUtils.PlaySound(player,"cancel5", false);
                    return false;
                }
                mode = Language.translate("buddy.data.normal");
            }

            // INSERT
            ArrayList<DatabaseArgs> eargs = new ArrayList<DatabaseArgs>();
            eargs.add(new DatabaseArgs("c", player.getUniqueId().toString())); // player uuid
            eargs.add(new DatabaseArgs("c", name)); // name
            eargs.add(new DatabaseArgs("c", type)); // type
            eargs.add(new DatabaseArgs("c", mode)); // mode
            eargs.add(new DatabaseArgs("i", "" + 0)); // exp
            eargs.add(new DatabaseArgs("i", "" + 0)); // level
            eargs.add(new DatabaseArgs("i", "" + 0)); // killmob
            eargs.add(new DatabaseArgs("i", "" + 0)); // kill
            eargs.add(new DatabaseArgs("i", "" + 0)); // death
            eargs.add(new DatabaseArgs("i", "" + 0)); // win
            eargs.add(new DatabaseArgs("i", "" + 0)); // lose
            eargs.add(new DatabaseArgs("i", "" + 0)); // break
            eargs.add(new DatabaseArgs("i", "" + 0)); // place
            eargs.add(new DatabaseArgs("i", "" + 0)); // heart
            eargs.add(new DatabaseArgs("i", "" + 0)); // spawn
            int ret = plugin.getDB().ExecuteUpdate(Language.translate("SQL.INSERT.ENTITY"), eargs);
            eargs.clear();
            eargs = null;
            if (ret != 1) {
                player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.entity.regist.error"));
                SoundUtils.PlaySound(player,"cancel5", false);
                return false;
            }

            int amount = stack.getAmount();
            amount--;
            if (amount <= 0) {
                player.getInventory().setItemInMainHand(new ItemStack(Material.AIR, 1));
            } else {
                stack.setAmount(amount);
                player.getInventory().setItemInMainHand(stack);
            }

            player.sendMessage(ChatColor.DARK_GREEN + Language.translate("commands.entity.regist.success"));
            SoundUtils.PlaySound(player,"switch1", false);

        } catch (Exception ex) {
            ErrorUtils.GetErrorMessage(ex);
            player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.entity.regist.error"));
            SoundUtils.PlaySound(player,"cancel5", false);
            return false;
        }
        return true;
    }
/*
    private boolean ActionType(Player player, String[] args) {
        try {
            // args check
            if (args.length != 3) {
                player.sendMessage(ChatColor.DARK_RED + Language.translate("plugin.args.error"));
                SoundUtils.PlaySound(player,"cancel5", false);
                return false;
            }
            String entity = args[1];
            String newtype = args[2];

            // UPDATE
            ArrayList<DatabaseArgs> eargs = new ArrayList<DatabaseArgs>();
            eargs.add(new DatabaseArgs("c", newtype)); // type
            eargs.add(new DatabaseArgs("c", player.getUniqueId().toString())); // player uuid
            eargs.add(new DatabaseArgs("c", entity)); // name
            int ret = plugin.getDB().ExecuteUpdate(Language.translate("SQL.UPDATE.TYPE.ENTITY"), eargs);
            eargs.clear();
            eargs = null;
            if (ret != 1) {
                player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.entity.regist.error"));
                SoundUtils.PlaySound(player,"cancel5", false);
                return false;
            }
            player.sendMessage(ChatColor.DARK_GREEN + Language.translate("commands.entity.type.success"));
            SoundUtils.PlaySound(player,"switch1", false);

        } catch (Exception ex) {
            ErrorUtils.GetErrorMessage(ex);
            return false;
        }
        return true;
    }

    private boolean ActionMode(Player player, String[] args) {
        try {
            // args check
            if (args.length != 3) {
                player.sendMessage(ChatColor.DARK_RED + Language.translate("plugin.args.error"));
                SoundUtils.PlaySound(player,"cancel5", false);
                return false;
            }
            String entity = args[1];
            String newmode = args[2];

            // UPDATE
            ArrayList<DatabaseArgs> eargs = new ArrayList<DatabaseArgs>();
            eargs.add(new DatabaseArgs("c", newmode)); // type
            eargs.add(new DatabaseArgs("c", player.getUniqueId().toString())); // player uuid
            eargs.add(new DatabaseArgs("c", entity)); // name
            int ret = plugin.getDB().ExecuteUpdate(Language.translate("SQL.UPDATE.MODE.ENTITY"), eargs);
            eargs.clear();
            eargs = null;
            if (ret != 1) {
                player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.entity.regist.error"));
                SoundUtils.PlaySound(player,"cancel5", false);
                return false;
            }
            player.sendMessage(ChatColor.DARK_GREEN + Language.translate("commands.entity.mode.success"));
            SoundUtils.PlaySound(player,"switch1", false);

        } catch (Exception ex) {
            ErrorUtils.GetErrorMessage(ex);
            return false;
        }
        return true;
    }
*/
    private boolean ActionUrl(Player player, String[] args) {
        try {
            // args check
            if (args.length != 3) {
                player.sendMessage(ChatColor.DARK_RED + Language.translate("plugin.args.error"));
                SoundUtils.PlaySound(player,"cancel5", false);
                return false;
            }
            final String url_entity = args[1];
            final String url_url = args[2];

            Bukkit.getScheduler().runTaskAsynchronously(KuroBase.GetCitizens(), new Runnable() {
                @Override
                public void run() {
                    DataOutputStream out = null;
                    BufferedReader reader = null;
                    try {
                        final URL target = new URL("https://api.mineskin.org/generate/url");
                        HttpURLConnection con = (HttpURLConnection) target.openConnection();
                        con.setRequestMethod("POST");
                        con.setDoOutput(true);
                        con.setConnectTimeout(1000);
                        con.setReadTimeout(30000);
                        out = new DataOutputStream(con.getOutputStream());
                        out.writeBytes("url=" + URLEncoder.encode(url_url, "UTF-8"));
                        out.close();
                        reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                        JSONObject output = (JSONObject) new JSONParser().parse(reader);
                        JSONObject data = (JSONObject) output.get("data");
                        String uuid = (String) data.get("uuid");
                        JSONObject texture = (JSONObject) data.get("texture");
                        String textureEncoded = (String) texture.get("value");
                        String signature = (String) texture.get("signature");
                        con.disconnect();
                        Bukkit.getScheduler().runTask(KuroBase.GetCitizens(), new Runnable() {
                            @Override
                            public void run() {
                                // UPDATE
                                ArrayList<DatabaseArgs> eargs = new ArrayList<DatabaseArgs>();
                                eargs.add(new DatabaseArgs("c", uuid)); // uuid
                                eargs.add(new DatabaseArgs("c", url_url)); // org_url
                                eargs.add(new DatabaseArgs("c", "https://api.mineskin.org/generate/url" + "//POST//" + url_entity)); // url
                                eargs.add(new DatabaseArgs("c", textureEncoded)); // data
                                eargs.add(new DatabaseArgs("c", signature)); // signature
                                eargs.add(new DatabaseArgs("c", player.getUniqueId().toString())); // player uuid
                                eargs.add(new DatabaseArgs("c", url_entity)); // name
                                int ret = plugin.getDB().ExecuteUpdate(Language.translate("SQL.UPDATE.URL.ENTITY"), eargs);
                                eargs.clear();
                                eargs = null;
                                if (ret != 1) {
                                    player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.entity.skin.fail"));
                                    SoundUtils.PlaySound(player,"cancel5", false);
                                    return;
                                }
                                player.sendMessage(ChatColor.DARK_GREEN + Language.translate("commands.entity.url.success"));
                                SoundUtils.PlaySound(player,"switch1", false);
                                //trait.setSkinPersistent(uuid, signature, textureEncoded);
                            }
                        });
                    } catch (Throwable t) {
                        Bukkit.getScheduler().runTask(CitizensAPI.getPlugin(), new Runnable() {
                            @Override
                            public void run() {
                                player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.entity.skin.fail"));
                                SoundUtils.PlaySound(player,"cancel5", false);
                            }
                        });
                    } finally {
                        if (out != null) {
                            try {
                                out.close();
                            } catch (IOException ex) {
                                ErrorUtils.GetErrorMessage(ex);
                                player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.entity.skin.fail"));
                                SoundUtils.PlaySound(player,"cancel5", false);
                            }
                        }
                        if (reader != null) {
                            try {
                                reader.close();
                            } catch (IOException ex) {
                                ErrorUtils.GetErrorMessage(ex);
                                player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.entity.skin.fail"));
                                SoundUtils.PlaySound(player,"cancel5", false);
                            }
                        }
                    }
                }
            });

        } catch (Exception ex) {
            ErrorUtils.GetErrorMessage(ex);
            player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.entity.skin.fail"));
            SoundUtils.PlaySound(player,"cancel5", false);
            return false;
        }
        return true;
    }

    private boolean ActionDel(Player player, String[] args) {
        try {
            // args check
            if (args.length != 2) {
                player.sendMessage(ChatColor.DARK_RED + Language.translate("plugin.args.error"));
                SoundUtils.PlaySound(player,"cancel5", false);
                return false;
            }
            String entity = args[1];

            // DELETE
            ArrayList<DatabaseArgs> eargs = new ArrayList<DatabaseArgs>();
            eargs.add(new DatabaseArgs("c", player.getUniqueId().toString())); // player uuid
            eargs.add(new DatabaseArgs("c", entity)); // name
            int ret = plugin.getDB().ExecuteUpdate(Language.translate("SQL.DELETE.ENTITY"), eargs);
            eargs.clear();
            eargs = null;
            if (ret != 1) {
                player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.entity.delete.error"));
                SoundUtils.PlaySound(player,"cancel5", false);
                return false;
            }
            player.sendMessage(ChatColor.DARK_GREEN + Language.translate("commands.entity.delete.success"));
            SoundUtils.PlaySound(player,"switch1", false);

        } catch (Exception ex) {
            ErrorUtils.GetErrorMessage(ex);
            player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.entity.delete.error"));
            SoundUtils.PlaySound(player,"cancel5", false);
            return false;
        }
        return true;
    }

    private boolean ActionJoin(Player player, String[] args) {
        try {
            // args check
            if (args.length != 2) {
                player.sendMessage(ChatColor.DARK_RED + Language.translate("plugin.args.error"));
                SoundUtils.PlaySound(player,"cancel5", false);
                return false;
            }
            int rank = PlayerUtils.GetRank(plugin, player);
            if (rank < PlayerUtils.RANK_NUSHI) {
                if (BuddyUtils.GetJoinEntity(player)) {
                    player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.entity.join.already"));
                    SoundUtils.PlaySound(player,"cancel5", false);
                    return false;
                }
            }
            String buddy_name = args[1];
            String name = player.getName();
            String type = "";
            String mode = "";
            int level = -1;
            int exp = -1;
            String skin_name = "";
            String skin_data = "";
            String skin_signature = "";
            String status = "";

            PreparedStatement ps = KuroBase.getDB().getConnection().prepareStatement(Language.translate("SQL.SELECT.ENTITY.NAME"));
            ArrayList<DatabaseArgs> eargs = new ArrayList<DatabaseArgs>();
            eargs.add(new DatabaseArgs("c", player.getUniqueId().toString()));
            eargs.add(new DatabaseArgs("c", buddy_name));
            ResultSet rs = KuroBase.getDB().ExecuteQuery(ps, eargs);
            eargs.clear();
            eargs = null;
            if (rs != null) {
                while(rs.next()){
                    type = rs.getString("type");
                    mode = rs.getString("mode");
                    level = rs.getInt("level");
                    exp = rs.getInt("exp");
                    status = rs.getString("status");
                    skin_name = rs.getString("name");
                    skin_data = rs.getString("skin_data");
                    skin_signature = rs.getString("skin_signature");
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
            if (level == -1) {
                player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.entity.join.error"));
                SoundUtils.PlaySound(player,"cancel5", false);
                return false;
            }

            if (status.equals("DEAD")) {
                player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.entity.join.dead"));
                SoundUtils.PlaySound(player,"cancel5", false);
                return false;
            }
            if (rank < PlayerUtils.RANK_NUSHI) {
                if (status.equals("JOIN")) {
                    player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.entity.join.join"));
                    SoundUtils.PlaySound(player,"cancel5", false);
                    return false;
                }
            }

            // create
            NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, buddy_name);
            // trait
            npc.getTrait(Age.class).setAge(20);
            npc.getTrait(GameModeTrait.class).setGameMode(GameMode.SURVIVAL);
            npc.addTrait(SkinTrait.class);
            if (skin_name != null && skin_data != null && skin_signature != null) {
                if (skin_name.length() > 0 && skin_data.length() > 0 && skin_signature.length() > 0) {
                    if (mode.equals(Language.translate("buddy.list.normal"))) {
                        skin_name = "people";
                        skin_data = "ewogICJ0aW1lc3RhbXAiIDogMTU4OTcxNjE3NDU3NiwKICAicHJvZmlsZUlkIiA6ICIyM2YxYTU5ZjQ2OWI0M2RkYmRiNTM3YmZlYzEwNDcxZiIsCiAgInByb2ZpbGVOYW1lIiA6ICIyODA3IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzZjZDEyOTRmNjMxNTQ5NWQ0NDAwYmY4NzdhMzkyZDgxODRjZmE4Nzk5MGZhMDgzNDEyODhlNDQxMTUxZTExNjkiCiAgICB9CiAgfQp9";
                        skin_signature = "D8StUSKrjWWeFB7kjK0Dn50tgOv6jCuJtMHf2zI++PBEEKeqw+AnVOwE81evaDxU6P9Plqz8EoeUFubCKhwU4OFmtdR36lNXNd8rP/U3BCZ0CNmrulY2tnBtnz4cXQY2V7w+5LiTgOQFz5mV2QYQZXFba7ieMxvcOU0f5WiHpVtZkvcTL3Uz7T2ZNjrpip8ReT/4Q8C8ulYYO2Xf5qjkvhl4eegwryMSYhzT2lmIXXmeY5TNvITe8xAVP1QCfrYuAI/ZRGuSb5rFhBCg+Y6U4w/cHw+UhXTJK3GdfYNQiLpxgtN+RQ1eONSNZ9u1lqqoKjT7r4hKv12g1Vgb5BXUYzCzPuLF/92TPAjABQIefnNAcAmoE7gz5UPDBxn1a+mVknynYB4ycqIqrlz5+yJ2zOxgTvyypzYLaO/gcNSD1lVAraCXOfi+dKbj4JAuS5ep3IAkJo7/h3EKNOGGmVYLsTzvU1lHEPCatGopExTZYVK1oTiWgj4begItyN4hyBdzETUCVMfvWZu49TmdawUYXkuCHpMSz6xT5Pe5GyYDEsNx4QeOE3D1iLLcytaAauZ0kmBAI2shTpS0t628Y09dahV81BA8JZQL7kKt6QORxmTPt9vPX//O3vIF9FEDiMl37lp7c67suARAmXbqVa4ASpLbFpkRPfOpN22NH7ugq2Y=";
                    } else if (mode.equals(Language.translate("buddy.list.guard"))) {
                        skin_name = "guard";
                        skin_data = "eyJ0aW1lc3RhbXAiOjE1NzM0MjIxNDA3MjAsInByb2ZpbGVJZCI6IjdkYTJhYjNhOTNjYTQ4ZWU4MzA0OGFmYzNiODBlNjhlIiwicHJvZmlsZU5hbWUiOiJHb2xkYXBmZWwiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzRlZDFkZWY0ZDZhMjk4YWQ5YmU1NmEwYTMwMzhjMzVmYjAwMWM1ZTRmZDI2YzgzM2IyNjZkZDEwZDEyYzdmOTAifX19";
                        skin_signature = "ke4/us8bKdy9NOUZ8o1l46cC0ATNE0cVpiUzD5HspK8vCfX/3/5l4ugMovyd9VfwU0e5e6il0gPMteYYLueoxiu44WaicMSim13u0MwP4wAABoDuwafajVi6WOUK7IuBe8ahtSDcddPtTEfrOtUIFiDx0ERLpijQoxQEwIlrZgtjmIjdCSv5cQJ/LI5gzi51RZMhV1FiISvnANTrCl/YVmLd0WkOtE6nCLV4ppky3VI2JMMYonS58BCVsP/LsVncDINcxWMWbEy7he8sDnP3ANkcWi15Wo+hhlDWgmFE0KqRpryCPpXueQDSYspk8Meus4PidwjO3XdnQcBtAcDe/PfvZvGkL7nG6fTQ2OicQrrRi00y9LZG8OD2kaaRgGRXJrTMR/Lq5gzlaBW0vOM+H4TLCPMafFh32GAEVvXpMvhFJPtVrchPwaThntm5L85kCyJ3DDstsAW9HKOYlrHZGxiGVLDYKiKFXlTWEt4T+Voh7ywcjbLnElqOPsXno7kfFyx95kYLepg8HLHgQB5WTmX067n5AVpz+0Yx1yUugZ7f76vwmKxk4u2oyPg0srOZzm+LxrgqDupvTJ9Sb1VN7tzfGXpKqIcsY5kqhoyL4GgfqZejtvgTnh4+MSL/IJ5ba+j7+MeK0FYxFvkRhgSbm7bBpEvFCdIZKoRF+oENVYY=";
                    } else if (mode.equals(Language.translate("buddy.list.battle"))) {
                        skin_name = "youhei";
                        skin_data = "ewogICJ0aW1lc3RhbXAiIDogMTU4OTcxNjM5MTQ1NiwKICAicHJvZmlsZUlkIiA6ICJiNzQ3OWJhZTI5YzQ0YjIzYmE1NjI4MzM3OGYwZTNjNiIsCiAgInByb2ZpbGVOYW1lIiA6ICJTeWxlZXgiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjFkYTA5MzEwNTMyYjJjY2VmZDY2NDkzMmYxN2RkZDk2YTczNzE4ZDgwYzQxNzNjZjAxMzFlNzkxNjVkY2Y4MyIKICAgIH0KICB9Cn0=";
                        skin_signature = "F2AIuesRz0T99YzhSn7vXp7EC40jDs9I4eIS72jmu0aGei7s+UasaCE9o0KGmaLnnoihcRCXPFs48f5TIG+DUP54R8a5eMDvgk4ij15HJ/ErbWC+ZYAAP8vaOuh1ysjm+GO6DGvjWISVE/sROw9k/FKqlxXnmpc+jPHigZXIQohraE302Ct/tmdFDl6v1DfzXhaRY/Nx2haYyI1u50SAnT5xgkToTobGhPN+OgRouvVnDcQYJIhUx5dW7f19Zqr9mBOv7HgarCmkstnhryl4UOhbj37pco6IivtM7s4LXSQhebMOy85pa/ewRP+k+4STX5rYJBfKwIrqVjU2I+yZZldEaVC3pZcvsZ+uXkVHpcthFOkgWFiP2UKSR2vHmiugwUt3TlTzMBOYNj8jBQoX0gNdqWU/0uoAszQBl2OrqlZ3QxljMjDzuef0jeJprULgxYM1sT27i6LptU8iGTrT6uWxHJ86o56jNC+yOCPnH0ZhPK5whhodCAyRHy98RyWMIWqrUxa8WTsl7pQSq+95L6A3WWYD4MIENBMKnaH2B21Qfb0PXtjDPUlXAmYdNJBYDtRWahi9RaUw8w4fBocu+K9zMDKjZY0u5zQbiYnT2XnndWOjO3xDEVcqpxHibBGIgrxt6PYvGuFehPifjJxSYBoF8mLV1cI42pilF6QfZR0=";
                    } else if (mode.equals(Language.translate("buddy.list.nijya"))) {
                        skin_name = "ninjya";
                        skin_data = "ewogICJ0aW1lc3RhbXAiIDogMTU4OTcxNjMzMTMwMSwKICAicHJvZmlsZUlkIiA6ICJkNjBmMzQ3MzZhMTI0N2EyOWI4MmNjNzE1YjAwNDhkYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJCSl9EYW5pZWwiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGZmYjBiYTBjOTQyZGVmZGNiODlmNTI1NjNlZmE5OWNhMzZkZjAwNGNhMGNjYmE0MWRjMzA3ZmY0YmNkZmZhYSIKICAgIH0KICB9Cn0=";
                        skin_signature = "NzuvdTO78iHpbWDr5Kj0jHHkF5HceYaa1rQbHeLIwzzMXXzUyfPEc/zOLlZJy4GN4H2FmxM704Z0eRnhjrtdKQ2ayThwdn8NClzsTerzAr5PYSLlGTARBpkE7RcPZug6siUa2SK4QasEBv1w+b15TRyWyGWb9Lm6uNzRXHyGyFVBGhjDSx82oSwty2XVJgy1l9pWHxMb1ER9RuLSYEBwTSk7xff+ek7ywTADhTBjKE26cJtuwtvgEMcbJjPETbwNdrCHAfUA2l2bpnsNu3izLcdxBs9E7aWpweWpXwEBbFzqp0CkO6BidokZCYFfuV+KXBmKz+fTQSqyTEofJtFe/WXFH/X/ZdDpDLxTZi1N5pEruLmK3r8AgxpxxbQ3y93Q/SZzh5yIcZCsx5F/wVRbKeIt5J04oR3Xm+89Ka7XB7aD6g/YSfZQ3+mGuc9e8GY3En1pIrKX96lnEn11oDSVUDenBzTcDxNauvRzym21UjB1I6j0vAtT1PFQWgP2X6mIC5k9ouDX7Be1thjNHAQFqPIWozptPviLv2LfD/AveAsnMZqw2f14P0TgC7nv3eN6S5xiLQS78o7yMCgQR7Dwqh9h7gfDF5nRL1EgoKVWm9pwvfuuWv/UxCcywvNNtd0kn9wL7Db7+Qg8VjaQaaDr3BDOigVWwxHGmObZ+C9SPiA=";
                    } else{
                        skin_name = "people";
                        skin_data = "ewogICJ0aW1lc3RhbXAiIDogMTU4OTcxNjE3NDU3NiwKICAicHJvZmlsZUlkIiA6ICIyM2YxYTU5ZjQ2OWI0M2RkYmRiNTM3YmZlYzEwNDcxZiIsCiAgInByb2ZpbGVOYW1lIiA6ICIyODA3IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzZjZDEyOTRmNjMxNTQ5NWQ0NDAwYmY4NzdhMzkyZDgxODRjZmE4Nzk5MGZhMDgzNDEyODhlNDQxMTUxZTExNjkiCiAgICB9CiAgfQp9";
                        skin_signature = "D8StUSKrjWWeFB7kjK0Dn50tgOv6jCuJtMHf2zI++PBEEKeqw+AnVOwE81evaDxU6P9Plqz8EoeUFubCKhwU4OFmtdR36lNXNd8rP/U3BCZ0CNmrulY2tnBtnz4cXQY2V7w+5LiTgOQFz5mV2QYQZXFba7ieMxvcOU0f5WiHpVtZkvcTL3Uz7T2ZNjrpip8ReT/4Q8C8ulYYO2Xf5qjkvhl4eegwryMSYhzT2lmIXXmeY5TNvITe8xAVP1QCfrYuAI/ZRGuSb5rFhBCg+Y6U4w/cHw+UhXTJK3GdfYNQiLpxgtN+RQ1eONSNZ9u1lqqoKjT7r4hKv12g1Vgb5BXUYzCzPuLF/92TPAjABQIefnNAcAmoE7gz5UPDBxn1a+mVknynYB4ycqIqrlz5+yJ2zOxgTvyypzYLaO/gcNSD1lVAraCXOfi+dKbj4JAuS5ep3IAkJo7/h3EKNOGGmVYLsTzvU1lHEPCatGopExTZYVK1oTiWgj4begItyN4hyBdzETUCVMfvWZu49TmdawUYXkuCHpMSz6xT5Pe5GyYDEsNx4QeOE3D1iLLcytaAauZ0kmBAI2shTpS0t628Y09dahV81BA8JZQL7kKt6QORxmTPt9vPX//O3vIF9FEDiMl37lp7c67suARAmXbqVa4ASpLbFpkRPfOpN22NH7ugq2Y=";
                    }
                    npc.getTrait(SkinTrait.class).setSkinPersistent(skin_name, skin_signature, skin_data);
                }
            }
            npc.addTrait(LookClose.class);
            npc.getTrait(LookClose.class).lookClose(true);

            npc.addTrait(KuroTrait.class);
            npc.getTrait(KuroTrait.class).setLevel(level);
            npc.getTrait(KuroTrait.class).setExp(exp);
            npc.getTrait(KuroTrait.class).setName(buddy_name);
            npc.getTrait(KuroTrait.class).setType(type);
            npc.getTrait(KuroTrait.class).setMode(mode);
            npc.getTrait(KuroTrait.class).setFollow(true);
            npc.getTrait(KuroTrait.class).setGuard(true);
            npc.getTrait(KuroTrait.class).setOwner(player);

            // UPDATE
            ArrayList<DatabaseArgs> uargs = new ArrayList<DatabaseArgs>();
            uargs.add(new DatabaseArgs("c", npc.getUniqueId().toString())); // npc uuid
            uargs.add(new DatabaseArgs("c", player.getUniqueId().toString())); // player uuid
            uargs.add(new DatabaseArgs("c", buddy_name)); // name
            int ret = plugin.getDB().ExecuteUpdate(Language.translate("SQL.UPDATE.SPAWN.ENTITY"), uargs);
            uargs.clear();
            uargs = null;
            if (ret != 1) {
                npc.destroy();
                player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.entity.join.error"));
                SoundUtils.PlaySound(player,"cancel5", false);
                return false;
            }

            Location loc = player.getLocation();
            loc.setX(loc.getX()+1.0D);
            loc.setZ(loc.getZ()+1.0D);

            npc.setFlyable(false);
            npc.setProtected(false);
            npc.data().setPersistent(NPC.DEFAULT_PROTECTED_METADATA, false);
            npc.data().setPersistent(NPC.DAMAGE_OTHERS_METADATA, true);
            npc.spawn(loc);

            // particle
            ParticleUtils.CrownParticle(npc.getEntity(), Particle.DRIP_LAVA, 50); // particle
            SoundUtils.BroadcastSound("typewriter-2", false);

            player.sendMessage(ChatColor.DARK_GREEN + Language.translate("commands.entity.select.spawn"));
            //SoundUtils.PlaySound(player,"switch1", false);
            //npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.BOOTS, new ItemStack(Material.LEATHER_BOOTS, 1));

        } catch (Exception ex) {
            ErrorUtils.GetErrorMessage(ex);
            player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.entity.join.error"));
            SoundUtils.PlaySound(player,"cancel5", false);
            return false;
        }
        return true;
    }

    private boolean ActionQuit(Player player, String[] args) {
        try {
            // args check
            if (args.length != 2) {
                player.sendMessage(ChatColor.DARK_RED + Language.translate("plugin.args.error"));
                SoundUtils.PlaySound(player,"cancel5", false);
                return false;
            }
            String entity = args[1];
            String uuid_str = "";
            try {
                PreparedStatement ps = KuroBase.getDB().getConnection().prepareStatement(Language.translate("SQL.SELECT.ENTITY.NAME"));
                ArrayList<DatabaseArgs> eargs = new ArrayList<DatabaseArgs>();
                eargs.add(new DatabaseArgs("c", player.getUniqueId().toString()));
                eargs.add(new DatabaseArgs("c", entity));
                ResultSet rs = KuroBase.getDB().ExecuteQuery(ps, eargs);
                eargs.clear();
                eargs = null;
                if (rs != null) {
                    while(rs.next()){
                        uuid_str = rs.getString("uuid");
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

            if (uuid_str.length() > 0) {
                UUID uuid = UUID.fromString(uuid_str);
                NPC npc = CitizensAPI.getNPCRegistry().getByUniqueId(uuid);
                if (npc == null) {
                    player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.entity.select.error"));
                    SoundUtils.PlaySound(player,"cancel5", false);
                    return false;
                }

                // particle
                ParticleUtils.CrownParticle(npc.getEntity(), Particle.DRIP_LAVA, 50); // particle
                SoundUtils.BroadcastSound("typewriter-2", false);

                npc.getTrait(KuroTrait.class).Close();
                player.sendMessage(ChatColor.DARK_GREEN + Language.translate("commands.entity.select.despawn"));

            } else {
                player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.entity.select.error"));
                SoundUtils.PlaySound(player,"cancel5", false);
                return false;
            }

        } catch (Exception ex) {
            ErrorUtils.GetErrorMessage(ex);
            player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.entity.select.error"));
            SoundUtils.PlaySound(player,"cancel5", false);
            return false;
        }
        return true;
    }

    private boolean ActionRevival(Player player, String[] args) {
        try {
            // args check
            if (args.length != 2) {
                player.sendMessage(ChatColor.DARK_RED + Language.translate("plugin.args.error"));
                SoundUtils.PlaySound(player,"cancel5", false);
                return false;
            }
            String entity = args[1];

            // check dead
            if (!BuddyUtils.CheckDeadEntity(player, entity)) {
                player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.entity.revival.dead.error"));
                SoundUtils.PlaySound(player,"cancel5", false);
                return false;
            }

            // check item
            ItemStack stack = player.getInventory().getItemInMainHand();
            if (stack != null) {
                ItemMeta data = stack.getItemMeta();
                if (data != null) {
                    String display = data.getDisplayName();
                    if (!display.equals("復活の書")) {
                        player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.entity.revival.item.error"));
                        SoundUtils.PlaySound(player,"cancel5", false);
                        return false;
                    }
                }
            }

            // UPDATE
            ArrayList<DatabaseArgs> eargs = new ArrayList<DatabaseArgs>();
            eargs.add(new DatabaseArgs("c", player.getUniqueId().toString())); // player uuid
            eargs.add(new DatabaseArgs("c", entity)); // name
            int ret = plugin.getDB().ExecuteUpdate(Language.translate("SQL.UPDATE.ALIVE.ENTITY"), eargs);
            eargs.clear();
            eargs = null;
            if (ret != 1) {
                player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.entity.revival.error"));
                SoundUtils.PlaySound(player,"cancel5", false);
                return false;
            }

            int amount = stack.getAmount();
            amount--;
            if (amount <= 0) {
                player.getInventory().setItemInMainHand(new ItemStack(Material.AIR, 1));
            } else {
                stack.setAmount(amount);
                player.getInventory().setItemInMainHand(stack);
            }
            player.sendMessage(ChatColor.DARK_GREEN + Language.translate("commands.entity.revival.success"));
            SoundUtils.PlaySound(player,"switch1", false);

        } catch (Exception ex) {
            ErrorUtils.GetErrorMessage(ex);
            player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.entity.select.error"));
            SoundUtils.PlaySound(player,"cancel5", false);
            return false;
        }
        return true;
    }

    private boolean ActionBuddyShop(Player player, String[] args) {
        try {
            String skin_name = "";
            String skin_data = "";
            String skin_signature = "";

            // create
            NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "バディーマスター");

            // trait
            npc.getTrait(Age.class).setAge(20);
            npc.addTrait(SkinTrait.class);
            skin_name = "master";
            skin_data = "eeyJ0aW1lc3RhbXAiOjE1NjMxMTE1MTcxODcsInByb2ZpbGVJZCI6ImIwZDRiMjhiYzFkNzQ4ODlhZjBlODY2MWNlZTk2YWFiIiwicHJvZmlsZU5hbWUiOiJ4RmFpaUxlUiIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjVlNDY1YzA3YTgyMjI0ODE5MjYxMjE5N2YyNzcxZTcxZDMzMjYzNDViZDc3OTA3MzEzOTllYWUxM2Y2OWNhOCJ9fX0=";
            skin_signature = "YFbFMaC5/02UGEHV1wYU2Eo6/Cz4ybnAnIjyAbU5tp13G3Nl/X11j52njzNL82O+YrwldxRL7HEn3/K9+VPp9zY7KTg+Hzh2a4ps4AxwDoXUkqmyhVm5r2UDtZkckPJ+pd08KzIzWjM1/CDhCZ2fLPL0MOeeYNt1IO5uR1aEJe2b/46nhKzArwZ3p/vl5lgN1atfETsLnK9Xi6nTdck8J9jzsqvNJpDek87Y1/p6QFPu9gSWVN15tfv/0DbURvK++0CBx0OR93O/ftsS90KpM08fmfjxWde7dTATKmLcnbJY0QpHZyQ3ohe61uHW4cyB1kU0gS3mbJ/eTrMNKHWUYkAh2us+CDkUXbx6oau/GkmR0LyjRER/wyEczDfVbIMvMqE+h0HkdZSblCzkJsIOYBmapom8G7uDT88bKtQZAWzgPpoNyI8BZmImTA9J5YbudUaLnkN8RVANoED1juG4ilAJO6sXHpeURVnoDbkPXyRVo+8gB/2cHGugLSgjZqCt78KMHbo0yFefNPjeQYMSRfXm9IFLNCANK8rfv3K1Sck/Jwc6LaqGuDl7UZZXKEvIjL/B2hc1FUvcC41MXvEyIFB5Fycm0fqClhR9kdlMfl7NLL/W8dA8mooZlrk/CcC4OlGRKFuM+71jOPmU6sSJGWzkUk2qvTcIlSSEYTCF9yU=";
            npc.getTrait(SkinTrait.class).setSkinPersistent(skin_name, skin_signature, skin_data);

            Location loc = player.getLocation();

            // trait
            npc.addTrait(LookClose.class);
            npc.getTrait(LookClose.class).lookClose(true);

            npc.addTrait(BuddyMasterTrait.class);
            npc.getTrait(BuddyMasterTrait.class).setLocation(loc);
            npc.setFlyable(false);
            npc.setProtected(true);
            npc.data().setPersistent(NPC.DEFAULT_PROTECTED_METADATA, true);
            npc.data().setPersistent(NPC.DAMAGE_OTHERS_METADATA, true);
            npc.spawn(loc);
            npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.HAND, new ItemStack(Material.DIAMOND_SWORD, 1));

            // particle
            ParticleUtils.CrownParticle(npc.getEntity(), Particle.DRIP_LAVA, 50); // particle
            SoundUtils.BroadcastSound("typewriter-2", false);

        } catch (Exception ex) {
            ErrorUtils.GetErrorMessage(ex);
            SoundUtils.PlaySound(player,"cancel5", false);
            return false;
        }
        return true;
    }
}
