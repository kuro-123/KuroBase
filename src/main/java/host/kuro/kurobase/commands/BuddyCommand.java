package host.kuro.kurobase.commands;

import host.kuro.kurobase.KuroBase;
import host.kuro.kurobase.database.DatabaseArgs;
import host.kuro.kurobase.lang.Language;
import host.kuro.kurobase.npc.KuroTrait;
import host.kuro.kurobase.utils.EntityUtils;
import host.kuro.kurobase.utils.ErrorUtils;
import host.kuro.kurobase.utils.PlayerUtils;
import host.kuro.kurobase.utils.SoundUtils;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.Age;
import net.citizensnpcs.trait.GameModeTrait;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
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
        }
        return true;
    }

    private boolean ActionList(Player player) {
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
            String name = "";
            String type = "";
            String mode = "";
            name = args[1];
            type = args[2];
            mode = args[3];

            if (EntityUtils.CheckNameEntity(name)) {
                player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.entity.exist.error"));
                SoundUtils.PlaySound(player,"cancel5", false);
                return false;
            }
            if (EntityUtils.ExistEntity(player, name)) {
                player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.entity.exist.error"));
                SoundUtils.PlaySound(player,"cancel5", false);
                return false;
            }

            if (type.length() <= 0) {
                type = "ウサギ";
            }
            EntityType settype;
            switch (type) {
                case "ウサギ": settype = EntityType.RABBIT; break;
                case "アヒル": settype = EntityType.CHICKEN; break;
                case "オウム": settype = EntityType.PARROT; break;
                case "イヌ": settype = EntityType.WOLF; break;
                case "ネコ": settype = EntityType.CAT; break;
                case "キツネ": settype = EntityType.FOX; break;
                case "ブタ": settype = EntityType.PIG; break;
                case "ヒツジ": settype = EntityType.SHEEP; break;
                case "ウシ": settype = EntityType.COW; break;
                case "ゾンビ": settype = EntityType.ZOMBIE; break;
                case "スケルトン": settype = EntityType.SKELETON; break;
                case "クリーパー": settype = EntityType.CREEPER; break;
                case "パンダ": settype = EntityType.PANDA; break;
                case "人間": settype = EntityType.PLAYER; break;
                default:
            }

            if (mode.length() <= 0) {
                switch (mode) {
                    case "ウサギ": mode = "大人"; break;
                    case "アヒル": mode = "大人"; break;
                    case "オウム": mode = "大人"; break;
                    case "イヌ": mode = "大人"; break;
                    case "ネコ": mode = "大人"; break;
                    case "キツネ": mode = "大人"; break;
                    case "ブタ": mode = "大人"; break;
                    case "ヒツジ": mode = "大人"; break;
                    case "ウシ": mode = "大人"; break;
                    case "ゾンビ": mode = "大人"; break;
                    case "スケルトン": mode = "大人"; break;
                    case "クリーパー": mode = "大人"; break;
                    case "パンダ": mode = "大人"; break;
                    case "人間": mode = "一般"; break;
                    default:
                }
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
            eargs.add(new DatabaseArgs("i", "" + 0)); // join
            int ret = plugin.getDB().ExecuteUpdate(Language.translate("SQL.INSERT.ENTITY"), eargs);
            eargs.clear();
            eargs = null;
            if (ret != 1) {
                player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.entity.regist.error"));
                SoundUtils.PlaySound(player,"cancel5", false);
                return false;
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
            String entity = args[1];

            // create
            NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, entity);
            npc.addTrait(SkinTrait.class);
            npc.addTrait(KuroTrait.class);

            // ゲームモード
            npc.getTrait(GameModeTrait.class).setGameMode(GameMode.SURVIVAL);
            // 年齢
            npc.getTrait(Age.class).setAge(17);
            // スキン
            String name = player.getName();
            String skin_name = "";
            String skin_data = "";
            String skin_signature = "";
            String status = "";
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
            } catch (Exception ex) {
                ErrorUtils.GetErrorMessage(ex);
            }
            if (status.equals("DEAD")) {
                player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.entity.join.dead"));
                SoundUtils.PlaySound(player,"cancel5", false);
                return false;
            }
            int rank = PlayerUtils.GetRank(plugin, player);
            if (rank < PlayerUtils.RANK_NUSHI) {
                if (status.equals("JOIN")) {
                    player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.entity.join.join"));
                    SoundUtils.PlaySound(player,"cancel5", false);
                    return false;
                }
            }

            if (skin_name != null && skin_data != null && skin_signature != null) {
                if (skin_name.length() > 0 && skin_data.length() > 0 && skin_signature.length() > 0) {
                    npc.getTrait(SkinTrait.class).setSkinPersistent(skin_name, skin_signature, skin_data);
                }
            }

            npc.getTrait(KuroTrait.class).setGameMode(GameMode.SURVIVAL);
            npc.getTrait(KuroTrait.class).setFollow(true);
            npc.getTrait(KuroTrait.class).setGuard(true);
            npc.getTrait(KuroTrait.class).setHealth(20);
            npc.getTrait(KuroTrait.class).setMaxHealth(20);
            npc.getTrait(KuroTrait.class).setName(skin_name);
            npc.getTrait(KuroTrait.class).setOwner(player);

            // UPDATE
            ArrayList<DatabaseArgs> eargs = new ArrayList<DatabaseArgs>();
            eargs.add(new DatabaseArgs("c", npc.getUniqueId().toString())); // npc uuid
            eargs.add(new DatabaseArgs("c", player.getUniqueId().toString())); // player uuid
            eargs.add(new DatabaseArgs("c", entity)); // name
            int ret = plugin.getDB().ExecuteUpdate(Language.translate("SQL.UPDATE.JOIN.ENTITY"), eargs);
            eargs.clear();
            eargs = null;
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

            player.sendMessage(ChatColor.DARK_GREEN + Language.translate("commands.entity.select.spawn"));
            SoundUtils.PlaySound(player,"switch1", false);
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
                npc.despawn();

                player.sendMessage(ChatColor.DARK_GREEN + Language.translate("commands.entity.select.despawn"));
                SoundUtils.PlaySound(player,"switch1", false);

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
}
