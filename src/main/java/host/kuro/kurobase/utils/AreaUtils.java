package host.kuro.kurobase.utils;

import host.kuro.kurobase.KuroBase;
import host.kuro.kurobase.database.AreaData;
import host.kuro.kurobase.lang.Language;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AreaUtils {

    public static final void SetupProtectData() {
        try {
            KuroBase.GetProtect().clear();
            PreparedStatement ps = KuroBase.getDB().getConnection().prepareStatement(Language.translate("SQL.AREAS"));
            ResultSet rs = KuroBase.getDB().ExecuteQuery(ps, null);
            if (rs != null) {
                while(rs.next()){
                    AreaData area = new AreaData();
                    area.owner = rs.getString("owner");
                    area.name = rs.getString("name");
                    area.x1 = rs.getInt("x1");
                    area.y1 = rs.getInt("y1");
                    area.z1 = rs.getInt("z1");
                    area.x2 = rs.getInt("x2");
                    area.y2 = rs.getInt("y2");
                    area.z2 = rs.getInt("z2");
                    KuroBase.GetProtect().add(area);
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
    }

    public static final void DeleteAreaData(String owner, String name) {
        while (true) {
            int i=0;
            boolean hit = false;
            for (AreaData area : KuroBase.GetProtect()) {
                if (area.owner.equals(owner) && area.name.equals(name)) {
                    KuroBase.GetProtect().remove(i);
                    hit = true;
                    break;
                }
                i++;
            }
            if (!hit) break;
        }
        return;
    }

    public static final AreaData CheckInsideProtect(Player player, int x, int y, int z) {
        if (KuroBase.GetProtect().size() <= 0) return null;
        for (AreaData area : KuroBase.GetProtect()) {
            if (player != null) {
                if (area.owner.toLowerCase().equals(player.getName().toLowerCase())) continue;
            }
            int x1 = Math.min(area.x1, area.x2);
            int x2 = Math.max(area.x1, area.x2);
            int y1 = Math.min(area.y1, area.y2);
            int y2 = Math.max(area.y1, area.y2);
            int z1 = Math.min(area.z1, area.z2);
            int z2 = Math.max(area.z1, area.z2);
            if ((x1 <= x && x <= x2) &&
                (y1 <= y && y <= y2) &&
                (z1 <= z && z <= z2)) {
                return area;
            }
        }
        return null;
    }

    public static final int GetAreaCount(AreaData area) {
        int count = 0;
        int x1 = Math.min(area.x1, area.x2);
        int x2 = Math.max(area.x1, area.x2);
        int y1 = Math.min(area.y1, area.y2);
        int y2 = Math.max(area.y1, area.y2);
        int z1 = Math.min(area.z1, area.z2);
        int z2 = Math.max(area.z1, area.z2);
        for (int i=x1; i<=x2; i++) {
            for (int j=y1; j<=y2; j++) {
                for (int k=z1; k<=z2; k++) {
                    count++;
                }
            }
        }
        return count;
    }

    public static final AreaData ReplacePos(AreaData area) {
        if (area.x1 < area.x2) {
            return area;
        }
        if (area.x1 == area.x2) {
            if (area.y1 < area.y2) {
                return area;
            }
            if (area.y1 == area.y2) {
                if (area.z1 < area.z2) {
                    return area;
                }
            }
        }
        int temp_x = area.x1;
        int temp_y = area.y1;
        int temp_z = area.z1;
        area.x1 = area.x2;
        area.y1 = area.y2;
        area.z1 = area.z2;
        area.x2 = temp_x;
        area.y2 = temp_y;
        area.z2 = temp_z;
        return area;
    }
}
