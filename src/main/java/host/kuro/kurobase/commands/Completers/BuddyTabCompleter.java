package host.kuro.kurobase.commands.Completers;

import host.kuro.kurobase.KuroBase;
import host.kuro.kurobase.database.DatabaseArgs;
import host.kuro.kurobase.lang.Language;
import host.kuro.kurobase.utils.ErrorUtils;
import host.kuro.kurobase.utils.PlayerUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class BuddyTabCompleter implements TabCompleter {

    List<String> cmds = new ArrayList<String>();

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        cmds.clear();
        Player player = (Player)sender;
        switch (args.length) {
            case 1:
                SetInputAction(player);
                return StringUtil.copyPartialMatches(args[args.length-1], cmds, new ArrayList<String>());
            case 2:
                switch (args[0].toLowerCase()) {
                    case "add":
                        SetInputName();
                        return StringUtil.copyPartialMatches(args[args.length-1], cmds, new ArrayList<String>());
                    case "join":
                        SetInputEntity(player, "ALIVE");
                        return StringUtil.copyPartialMatches(args[args.length-1], cmds, new ArrayList<String>());
                    case "quit":
                        SetInputEntity(player, "JOIN");
                        return StringUtil.copyPartialMatches(args[args.length-1], cmds, new ArrayList<String>());
                    case "revival":
                        SetInputEntity(player, "DEAD");
                        return StringUtil.copyPartialMatches(args[args.length-1], cmds, new ArrayList<String>());
                    case "url":
                    case "del":
                        SetInputEntity(player, "");
                        return StringUtil.copyPartialMatches(args[args.length-1], cmds, new ArrayList<String>());
                }
                return null;

            case 3:
                switch (args[0].toLowerCase()) {
                    case "add":
                    case "type":
                        SetInputType(player);
                        return StringUtil.copyPartialMatches(args[args.length-1], cmds, new ArrayList<String>());
                    case "mode":
                        SetInputModeByName(player, args[args.length-2]);
                        return StringUtil.copyPartialMatches(args[args.length-1], cmds, new ArrayList<String>());
                    case "url":
                        SetInputURL();
                        return StringUtil.copyPartialMatches(args[args.length-1], cmds, new ArrayList<String>());
                }
                return null;

            case 4:
                switch (args[0].toLowerCase()) {
                    case "add":
                        SetInputMode(player, args[args.length-2]);
                        return StringUtil.copyPartialMatches(args[args.length-1], cmds, new ArrayList<String>());
                }
                return null;
        }
        return null;
    }

    private void SetInputAction(Player player) {
        cmds.add("<アクションを選択>");
        cmds.add("join");
        cmds.add("quit");
        cmds.add("add");
        cmds.add("url");
        cmds.add("list");
        cmds.add("revival");
        cmds.add("del");
    }

    private void SetInputName() {
        cmds.add("<名前を入力>");
    }

    private void SetInputEntity(Player player, String kbn) {
        cmds.add("<エンティティを選択>");

        String sql_kbn = "";
        switch (kbn) {
            case "ALIVE": sql_kbn = "SQL.SELECT.ALIVE.ENTITY"; break;
            case "JOIN": sql_kbn = "SQL.SELECT.JOIN.ENTITY"; break;
            case "DEAD": sql_kbn = "SQL.SELECT.DEAD.ENTITY"; break;
            default: sql_kbn = "SQL.SELECT.ENTITY"; break;
        }

        try {
            PreparedStatement ps = KuroBase.getDB().getConnection().prepareStatement(Language.translate(sql_kbn));
            ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
            args.add(new DatabaseArgs("c", player.getUniqueId().toString()));
            ResultSet rs = KuroBase.getDB().ExecuteQuery(ps, args);
            args.clear();
            args = null;
            if (rs != null) {
                while(rs.next()){
                    cmds.add(rs.getString("name"));
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
            cmds.clear();
        }
    }

    private void SetInputType(Player player) {
        int level = player.getLevel();
        if (PlayerUtils.GetRank(KuroBase.GetInstance(), player) == PlayerUtils.RANK_NUSHI) {
            level = 1000;
        }
        cmds.add("<タイプを選択 ※現在は人型のみ>");
        cmds.add(Language.translate("buddy.type.human"));
    }

    private void SetInputModeByName(Player player, String name) {
        String type = "";
        try {
            PreparedStatement ps = KuroBase.getDB().getConnection().prepareStatement(Language.translate("SQL.SELECT.ENTITY.NAME"));
            ArrayList<DatabaseArgs> eargs = new ArrayList<DatabaseArgs>();
            eargs.add(new DatabaseArgs("c", player.getUniqueId().toString()));
            eargs.add(new DatabaseArgs("c", name));
            ResultSet rs = KuroBase.getDB().ExecuteQuery(ps, eargs);
            eargs.clear();
            eargs = null;
            if (rs != null) {
                while(rs.next()){
                    type = rs.getString("type");
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
        if (type.length() > 0) {
            SetInputMode(player, type);
        }
    }

    private void SetInputMode(Player player, String type) {
        int level = player.getLevel();
        if (PlayerUtils.GetRank(KuroBase.GetInstance(), player) == PlayerUtils.RANK_NUSHI) {
            level = 1000;
        }
        cmds.add("<モードを選択>");
        if (type.equals(Language.translate("buddy.type.human"))) {
            cmds.add(Language.translate("buddy.list.normal"));
            cmds.add(Language.translate("buddy.list.guard"));
            cmds.add(Language.translate("buddy.list.battle"));
            cmds.add(Language.translate("buddy.list.nijya"));
        }
    }

    private void SetInputURL() {
        cmds.add("<スキンURLを入力>");
    }
}