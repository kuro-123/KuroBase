package host.kuro.kurobase.shop;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class GuiItem {
    private ItemStack stack;
    private IClickEvent e;

    public GuiItem(ItemStack stack, IClickEvent e) {
        this.stack = stack;
        this.e = e;
    }

    public void click(ClickType type) {
        this.e.click(type);
    }

    public ItemStack getStack() {
        return this.stack;
    }
}
