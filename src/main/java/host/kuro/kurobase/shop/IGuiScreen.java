package host.kuro.kurobase.shop;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public interface IGuiScreen {
    void open();
    void click(InventoryClickEvent event);
    ItemStack atPos(int x, int y);
    Player getOpener();
}
