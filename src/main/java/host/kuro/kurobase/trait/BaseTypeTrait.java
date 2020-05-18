package host.kuro.kurobase.trait;

import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;

public class BaseTypeTrait extends Trait {
    @Persist
    private String type = "";

    public BaseTypeTrait() {
        super("BaseTypeTrait");
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    @Override
    public void onSpawn() {
        npc.getEntity().setCustomName(type);
        npc.getEntity().setCustomNameVisible(false);
    }
}