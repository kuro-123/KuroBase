package host.kuro.kurobase.shop;

import host.kuro.kurobase.utils.ErrorUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public class GuiShopHandler implements Listener {

    @EventHandler
    public void click(InventoryClickEvent e) {
        try {
            Inventory inv = e.getInventory();
            GuiShop shop = GuiHandler.get((Player) e.getWhoClicked());
            if (shop != null && e != null) {
                shop.click(e);
            }

        } catch (Exception ex) {
            ErrorUtils.GetErrorMessage(ex);
        }
    }

    @EventHandler
    public void close(InventoryCloseEvent e) {
        try {
            Player p = (Player) e.getPlayer();
            if(GuiHandler.isOpen(p)) {
                GuiHandler.close(p);
            }

        } catch (Exception ex) {
            ErrorUtils.GetErrorMessage(ex);
        }
    }
}