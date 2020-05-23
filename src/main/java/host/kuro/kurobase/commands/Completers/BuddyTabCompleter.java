package host.kuro.kurobase.commands.Completers;

import host.kuro.kurobase.KuroBase;
import host.kuro.kurobase.database.DatabaseArgs;
import host.kuro.kurobase.lang.Language;
import host.kuro.kurobase.utils.ErrorUtils;
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
                    case "mode":
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
                    case "url":
                        SetInputURL();
                        return StringUtil.copyPartialMatches(args[args.length-1], cmds, new ArrayList<String>());
                    case "mode":
                        cmds.add("autobattle");
                        cmds.add("follow");
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
        cmds.add("mode");
        cmds.add("add");
        cmds.add("url");
        cmds.add("list");
        cmds.add("equip");
        cmds.add("revival");
        cmds.add("del");
    }

    private void SetInputName() {
        cmds.add("<名前を入力>");
    }

    private void SetInputEntity(Player player, String kbn) {
        cmds.add("<バディーを選択>");
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
    private void SetInputURL() {
        cmds.add("<スキンURLを入力>");
    }
}