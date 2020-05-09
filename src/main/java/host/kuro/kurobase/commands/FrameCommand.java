package host.kuro.kurobase.commands;

import host.kuro.kurobase.KuroBase;
import host.kuro.kurobase.database.DatabaseArgs;
import host.kuro.kurobase.lang.Language;
import host.kuro.kurobase.utils.ErrorUtils;
import host.kuro.kurobase.utils.SoundUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class FrameCommand implements CommandExecutor {

    private final KuroBase plugin;

    public FrameCommand(KuroBase plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if(!(sender instanceof Player)){
            // console check
            plugin.getLogger().warning(Language.translate("plugin.console.error"));
            return false;
        }
        Player player = (Player)sender;
        if (!(args.length == 1 || args.length == 2)) {
            // args check
            player.sendMessage(ChatColor.DARK_RED + Language.translate("plugin.args.error"));
            SoundUtils.PlaySound(player,"cancel5", false);
            return false;
        }
        switch (args[0].toLowerCase()) {
        case "list":
            return ActionList(player);
        case "add":
            return ActionAdd(player, args);
        case "set":
            return ActionSet(player, args);
        case "del":
            return ActionDel(player, args);
        default:
            player.sendMessage(ChatColor.DARK_RED + Language.translate("plugin.args.error"));
            SoundUtils.PlaySound(player,"cancel5", false);
            return false;
        }
    }

    private boolean ActionList(Player player) {
        plugin.GetClickMode().remove(player);
        try {
            PreparedStatement ps = plugin.getDB().getConnection().prepareStatement(Language.translate("SQL.FRAME.LIST"));
            ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
            args.add(new DatabaseArgs("c", player.getUniqueId().toString()));
            ResultSet rs = plugin.getDB().ExecuteQuery(ps, args);
            args.clear();
            args = null;
            if (rs != null) {
                int cnt = 0;
                StringBuilder sb = new StringBuilder();
                while(rs.next()){
                    sb.append(ChatColor.DARK_GREEN);
                    sb.append(String.format("[ フレーム:%s URL: %s ]"
                            , rs.getString("name")
                            , rs.getString("url")
                            ));
                    sb.append("\n");
                    sb.append(ChatColor.DARK_GREEN);
                    cnt++;
                }
                sb.append(String.format("フレーム数 : %d\n", cnt));
                player.sendMessage(new String(sb));
                SoundUtils.PlaySound(player,"switch1", false);
            }
            if (ps != null) {
                ps.close();
                ps = null;
            }
            if (rs != null) {
                rs.close();
                rs = null;
            }
            return true;

        } catch (Exception ex) {
            ErrorUtils.GetErrorMessage(ex);
        }
        SoundUtils.PlaySound(player, "cancel5", false);
        return false;
    }

    private boolean ActionAdd(Player player, String[] args) {
        plugin.GetClickMode().remove(player);
        BufferedImage bi =null;
        URLConnection urlcon;
        try {
            String sName = args[1];
            if (!IsLength(sName)) {
                player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.error.len"));
                SoundUtils.PlaySound(player,"cancel5", false);
                return false;
            }

            URI uri = new URI(args[0]);
            URL url = uri.toURL();
            urlcon = url.openConnection();
            bi = ImageIO.read(urlcon.getInputStream());

            String path = plugin.getConfig().getString("Web.framepath");
            File saveFile = null;
            if (plugin.IsLinux()) {
                if (path.length() > 0) {
                    new File(path).mkdirs();
                    saveFile = new File( path + "/" + player.getName().toLowerCase() + ".png");
                    ImageIO.write(bi, "png", saveFile);
                }

            } else {
                new File(plugin.getDataFolder() + "/frame/data/").mkdirs();
                saveFile = new File( plugin.getDataFolder() + "/frame/data/" + player.getName() + ".png");
                ImageIO.write(bi, "png", saveFile);
            }
            if (saveFile != null) {
                String sURL = url.getPath();
                String sPath = saveFile.getPath();

                // add
                ArrayList<DatabaseArgs> fargs = new ArrayList<DatabaseArgs>();
                fargs.add(new DatabaseArgs("c", player.getUniqueId().toString())); // uuid
                fargs.add(new DatabaseArgs("c", sName)); // name
                fargs.add(new DatabaseArgs("c", sURL)); // url
                fargs.add(new DatabaseArgs("c", sPath)); // path
                int ret = KuroBase.getDB().ExecuteUpdate(Language.translate("SQL.FRAME.ADD"), fargs);
                fargs.clear();
                fargs = null;
                if (ret != 1) {
                    player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.frame.add.error"));
                    SoundUtils.PlaySound(player, "cancel5", false);
                    return false;
                }
            } else {
                player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.frame.download.error "));
                SoundUtils.PlaySound(player, "cancel5", false);
                return false;
            }
            return true;

        } catch (URISyntaxException ex) {
            plugin.getLogger().warning(ErrorUtils.GetErrorMessage(ex));
        } catch (MalformedURLException ex) {
            plugin.getLogger().warning(ErrorUtils.GetErrorMessage(ex));
        } catch (IOException ex) {
            plugin.getLogger().warning(ErrorUtils.GetErrorMessage(ex));
        } catch (Exception ex) {
            plugin.getLogger().warning(ErrorUtils.GetErrorMessage(ex));
        }
        player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.frame.download.error "));
        SoundUtils.PlaySound(player, "cancel5", false);
        return false;
    }

    private boolean ActionSet(Player player, String[] args) {
        // change mode
        if (plugin.GetClickMode().containsKey(player)) {
            String click_mode = plugin.GetClickMode().get(player);
            if (click_mode.equals("frame")) {
                plugin.GetClickMode().remove(player);
                player.sendMessage(ChatColor.DARK_GREEN + Language.translate("commands.frame.modeoff"));
                SoundUtils.PlaySound(player,"switch1", false);
            } else {
                plugin.GetClickMode().remove(player);
                player.sendMessage(ChatColor.DARK_GREEN + Language.translate("commands.frame.modeon"));
                SoundUtils.PlaySound(player,"switch1", false);
                plugin.GetClickMode().put(player, "frame");
            }
        } else {
            plugin.GetClickMode().remove(player);
            player.sendMessage(ChatColor.DARK_GREEN + Language.translate("commands.frame.modeon"));
            SoundUtils.PlaySound(player,"switch1", false);
            plugin.GetClickMode().put(player, "frame");
        }
        return false;
    }

    private boolean ActionDel(Player player, String[] args) {
        plugin.GetClickMode().remove(player);
        try {
            String sName = args[1];
            if (!IsLength(sName)) {
                player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.error.len"));
                SoundUtils.PlaySound(player,"cancel5", false);
                return false;
            }
            // del
            ArrayList<DatabaseArgs> fargs = new ArrayList<DatabaseArgs>();
            fargs.add(new DatabaseArgs("c", player.getUniqueId().toString())); // uuid
            fargs.add(new DatabaseArgs("c", sName)); // name
            int ret = KuroBase.getDB().ExecuteUpdate(Language.translate("SQL.FRAME.DEL"), fargs);
            fargs.clear();
            fargs = null;
            if (ret != 1) {
                player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.frame.del.error"));
                SoundUtils.PlaySound(player, "cancel5", false);
                return false;
            }
            return true;

        } catch (Exception ex) {
            plugin.getLogger().warning(ErrorUtils.GetErrorMessage(ex));
        }
        player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.frame.del.error"));
        SoundUtils.PlaySound(player, "cancel5", false);
        return false;
    }

    private boolean IsLength(String target) {
        int len =target.length();
        if (len < 2 || len > 16) {
            return false;
        }
        return true;
    }
}
