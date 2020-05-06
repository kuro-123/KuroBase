package host.kuro.kurobase.utils;

import host.kuro.kurobase.database.DatabaseArgs;
import host.kuro.kurobase.database.DatabaseManager;
import host.kuro.kurobase.lang.Language;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class DataUtils {

    public static final boolean RefreshChestData(DatabaseManager db) {
        PreparedStatement ps;
        ResultSet rs;
        try {
            boolean deleted = false;
            while (true) {
                ps = db.getConnection().prepareStatement(Language.translate("SQL.CHECK.CHEST"));
                rs = db.ExecuteQuery(ps, null);
                if (rs == null) break;

                deleted = false;
                while (rs.next()) {
                    String world = rs.getString("world");
                    int x1 = rs.getInt("x1");
                    int y1 = rs.getInt("y1");
                    int z1 = rs.getInt("z1");
                    int x2 = rs.getInt("x2");
                    int y2 = rs.getInt("y2");
                    int z2 = rs.getInt("z2");
                    Location loc = new Location(Bukkit.getWorld(world), x1, y1, z1);
                    Block block = loc.getBlock();
                    String target = block.getType().toString();
                    if (target.toLowerCase().indexOf("chest") < 0) {
                        // DELETE
                        ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
                        args.add(new DatabaseArgs("c", world));
                        args.add(new DatabaseArgs("i", "" + x1));
                        args.add(new DatabaseArgs("i", "" + y1));
                        args.add(new DatabaseArgs("i", "" + z1));
                        args.add(new DatabaseArgs("i", "" + x2));
                        args.add(new DatabaseArgs("i", "" + y2));
                        args.add(new DatabaseArgs("i", "" + z2));
                        db.ExecuteUpdate(Language.translate("SQL.DELETE.CHECT"), args);
                        args.clear();
                        args = null;
                        deleted = true;
                    }
                    if (deleted) break;
                }
                if (rs != null) {
                    rs.close();
                    rs = null;
                }
                if (ps != null) {
                    ps.close();
                    ps = null;
                }
                if (!deleted) break;
            }
        } catch (Exception ex) {
            ErrorUtils.GetErrorMessage(ex);
            return false;
        }
        return true;
    }
}