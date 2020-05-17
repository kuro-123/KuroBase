package host.kuro.kurobase.commands.Completers;

import host.kuro.kurobase.KuroBase;
import host.kuro.kurobase.database.DatabaseArgs;
import host.kuro.kurobase.database.DatabaseManager;
import host.kuro.kurobase.lang.Language;
import host.kuro.kurobase.utils.ErrorUtils;
import host.kuro.kurobase.utils.PlayerUtils;
import host.kuro.kurobase.utils.SoundUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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
                SetInputAction();
                return StringUtil.copyPartialMatches(args[args.length-1], cmds, new ArrayList<String>());
            case 2:
                switch (args[0].toLowerCase()) {
                    case "add":
                        SetInputName();
                        return StringUtil.copyPartialMatches(args[args.length-1], cmds, new ArrayList<String>());
                    case "join":
                    case "quit":
                    case "type":
                    case "mode":
                    case "url":
                    case "del":
                        SetInputEntity(player);
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

    private void SetInputAction() {
        cmds.add("<アクションを選択>");
        cmds.add("join");
        cmds.add("quit");
        cmds.add("add");
        cmds.add("url");
        cmds.add("list");
        cmds.add("del");
        //cmds.add("type");
        //cmds.add("mode");
    }

    private void SetInputName() {
        cmds.add("<名前を入力>");
    }

    private void SetInputEntity(Player player) {
        cmds.add("<エンティティを選択>");
        try {
            PreparedStatement ps = KuroBase.getDB().getConnection().prepareStatement(Language.translate("SQL.SELECT.ENTITY"));
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
        cmds.add("<タイプを選択>");
        if (  0 <= level) cmds.add("ウサギ");
        if ( 11 <= level) cmds.add("アヒル");
        if ( 31 <= level) cmds.add("オウム");
        if ( 61 <= level) cmds.add("イヌ");
        if ( 91 <= level) cmds.add("ネコ");
        if (121 <= level) cmds.add("キツネ");
        if (151 <= level) cmds.add("ブタ");
        if (181 <= level) cmds.add("ヒツジ");
        if (211 <= level) cmds.add("ウシ");
        if (241 <= level) cmds.add("ゾンビ");
        if (271 <= level) cmds.add("スケルトン");
        if (301 <= level) cmds.add("クリーパー");
        if (331 <= level) cmds.add("パンダ");
        if (361 <= level) cmds.add("人間");
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
        switch (type) {
            case "ウサギ":
            case "アヒル":
            case "オウム":
            case "イヌ":
            case "ネコ":
            case "キツネ":
            case "ブタ":
            case "ヒツジ":
            case "ウシ":
            case "ゾンビ":
            case "スケルトン":
            case "クリーパー":
            case "パンダ":
                cmds.add("子供");
                cmds.add("大人");
                break;
            case "人間":
                if (level <= 390) cmds.add("一般");
                if (421 <= level) cmds.add("ボディーガード");
                if (451 <= level) cmds.add("土木");
                if (481 <= level) cmds.add("商人");
                if (511 <= level) cmds.add("傭兵");
                if (541 <= level) cmds.add("エキスパート");
        }
    }

    private void SetInputURL() {
        cmds.add("<スキンURLを入力>");
    }
}