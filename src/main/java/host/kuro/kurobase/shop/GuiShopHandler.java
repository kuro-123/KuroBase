package host.kuro.kurobase.shop;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public class GuiShopHandler implements Listener {

    @EventHandler
    public void click(InventoryClickEvent e) {
        Inventory inv = e.getInventory();
        //if(e.getInventory().getName().equals(Util.invName())) {
            GuiShop shop = GuiHandler.get((Player) e.getWhoClicked());
            shop.click(e);
        //}
    }

    @EventHandler
    public void close(InventoryCloseEvent e) {
        //if(e.getInventory().getName().equals(Util.invName())) {
            Player p = (Player) e.getPlayer();
            if(GuiHandler.isOpen(p)) {
                GuiHandler.close(p);
            }
        //}
    }
}