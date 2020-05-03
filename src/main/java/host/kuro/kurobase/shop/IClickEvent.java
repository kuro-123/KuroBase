package host.kuro.kurobase.shop;

import org.bukkit.event.inventory.ClickType;

@FunctionalInterface
public interface IClickEvent {

    public abstract void click(ClickType click);

}