package host.kuro.kurobase.listeners;

import host.kuro.kurobase.KuroBase;
import host.kuro.kurobase.database.DatabaseArgs;
import host.kuro.kurobase.lang.Language;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import java.util.ArrayList;

public class BlockListener implements Listener {

    KuroBase plugin = null;

    public BlockListener(KuroBase plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();

        // UPDATE
        ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
        args.add(new DatabaseArgs("c", player.getUniqueId().toString())); // UUID
        int ret = plugin.getDB().ExecuteUpdate(Language.translate("SQL.BREAK.UPDATE.PLAYER"), args);
        args.clear();
        args = null;

        Block block = e.getBlock();
        String material_name = block.getBlockData().getMaterial().toString();
        int material_id = block.getBlockData().getMaterial().getId();
        String simple_name = block.getBlockData().getMaterial().getData().getSimpleName();
        String type_name = block.getBlockData().getMaterial().getData().getTypeName();
        String world = block.getLocation().getWorld().getName();
        int x = block.getLocation().getBlockX();
        int y = block.getLocation().getBlockY();
        int z = block.getLocation().getBlockZ();
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();

        // UPDATE
        ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
        args.add(new DatabaseArgs("c", player.getUniqueId().toString())); // UUID
        int ret = plugin.getDB().ExecuteUpdate(Language.translate("SQL.PLACE.UPDATE.PLAYER"), args);
        args.clear();
        args = null;
    }
}